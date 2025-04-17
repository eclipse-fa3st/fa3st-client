/**
 * Copyright (c) 2025 the Eclipse FA³ST Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.digitaltwin.fa3st.client.interfaces;

import static org.eclipse.digitaltwin.fa3st.client.util.Constants.DEFAULT_CHARSET;
import static org.eclipse.digitaltwin.fa3st.client.util.Constants.URI_PATH_SEPERATOR;

import com.fasterxml.jackson.databind.type.TypeFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.digitaltwin.fa3st.client.exception.BadRequestException;
import org.eclipse.digitaltwin.fa3st.client.exception.ConflictException;
import org.eclipse.digitaltwin.fa3st.client.exception.ConnectivityException;
import org.eclipse.digitaltwin.fa3st.client.exception.ForbiddenException;
import org.eclipse.digitaltwin.fa3st.client.exception.InternalServerErrorException;
import org.eclipse.digitaltwin.fa3st.client.exception.InvalidPayloadException;
import org.eclipse.digitaltwin.fa3st.client.exception.MethodNotAllowedException;
import org.eclipse.digitaltwin.fa3st.client.exception.NotFoundException;
import org.eclipse.digitaltwin.fa3st.client.exception.StatusCodeException;
import org.eclipse.digitaltwin.fa3st.client.exception.UnauthorizedException;
import org.eclipse.digitaltwin.fa3st.client.exception.UnsupportedStatusCodeException;
import org.eclipse.digitaltwin.fa3st.client.query.SearchCriteria;
import org.eclipse.digitaltwin.fa3st.client.util.HttpHelper;
import org.eclipse.digitaltwin.fa3st.client.util.QueryHelper;
import org.eclipse.digitaltwin.fa3st.common.dataformat.DeserializationException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.SerializationException;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiDeserializer;
import org.eclipse.digitaltwin.fa3st.common.dataformat.json.JsonApiSerializer;
import org.eclipse.digitaltwin.fa3st.common.exception.UnsupportedModifierException;
import org.eclipse.digitaltwin.fa3st.common.model.InMemoryFile;
import org.eclipse.digitaltwin.fa3st.common.model.TypedInMemoryFile;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.Content;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.OutputModifier;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.QueryModifier;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingInfo;
import org.eclipse.digitaltwin.fa3st.common.model.http.HttpMethod;
import org.eclipse.digitaltwin.fa3st.common.model.http.HttpStatus;
import org.eclipse.digitaltwin.fa3st.common.util.EncodingHelper;
import org.eclipse.digitaltwin.fa3st.common.util.StringHelper;
import org.springframework.web.util.UriUtils;


/**
 * Abstract base class providing core functionality for sending HTTP requests and handling API responses.
 * Supports GET, POST, PUT, PATCH and DELETE operations, deserialization of responses, and throws exceptions based on
 * status codes.
 * Subclasses extend these methods to interact with specific APIs.
 */
public abstract class BaseInterface {

    private static final List<HttpStatus> SUPPORTED_DEFAULT_HTTP_STATUS = List.of(
            HttpStatus.BAD_REQUEST,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.FORBIDDEN,
            HttpStatus.NOT_FOUND,
            HttpStatus.INTERNAL_SERVER_ERROR);

    protected final JsonApiSerializer serializer = new JsonApiSerializer();
    protected final JsonApiDeserializer deserializer = new JsonApiDeserializer();
    protected final HttpClient httpClient;
    protected final URI endpoint;

    /**
     * Creates a new instance.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param httpClient Allows user to specify custom http-client
     */
    protected BaseInterface(URI endpoint, HttpClient httpClient) {
        this.endpoint = sanitizeEndpoint(endpoint);
        this.httpClient = httpClient;
    }


    /**
     * Creates a new instance.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    protected BaseInterface(URI endpoint) {
        this(endpoint, HttpClient.newHttpClient());
    }


    /**
     * Creates a new instance.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    protected BaseInterface(URI endpoint, String user, String password) {
        this(endpoint, HttpHelper.newUsernamePasswordClient(user, password));
    }


    /**
     * Creates a new instance.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    protected BaseInterface(URI endpoint, boolean trustAllCertificates) {
        this(endpoint, trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param modifier the output modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T get(String path, OutputModifier modifier, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createGetRequest(resolve(QueryHelper.apply(path, modifier)));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        return parseBody(response, responseType);
    }


    /**
     * Executes a HTTP GET and parses the response body as {@code responseType} using valueOnly serialization.
     *
     * @param path the URL path relative to the current endpoint
     * @return the file name and content
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected InMemoryFile getFile(String path) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createGetRequest(resolve(QueryHelper.apply(path, OutputModifier.DEFAULT)));
        HttpResponse<byte[]> response = HttpHelper.sendFileRequest(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);

        return HttpHelper.parseBody(response);
    }


    /**
     * Executes a HTTP GET and parses the response body as a list of {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param searchCriteria the search criteria
     * @param modifier the output modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> List<T> getAll(String path, SearchCriteria searchCriteria, OutputModifier modifier, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createGetRequest(resolve(QueryHelper.apply(path, modifier, PagingInfo.ALL, searchCriteria)));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        try {
            return deserializePage(response.body(), responseType).getContent();
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    /**
     * Executes a HTTP GET and parses the response body as a page of {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param searchCriteria the search criteria
     * @param modifier the output modifier
     * @param pagingInfo the paging information
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> Page<T> getPage(String path, SearchCriteria searchCriteria, OutputModifier modifier, PagingInfo pagingInfo, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createGetRequest(resolve(QueryHelper.apply(path, modifier, pagingInfo, searchCriteria)));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        try {
            return deserializePage(response.body(), responseType);
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    /**
     * Executes a HTTP POST and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the POST body
     * @param content the content modifier
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T post(String path, Object entity, Content content, Class<T> responseType) throws ConnectivityException, StatusCodeException {
        return post(path, entity, content, HttpStatus.CREATED, responseType);
    }


    /**
     * Executes a HTTP POST and parses the response body as {@code responseType}.
     *
     * @param <T> the result type
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the POST body
     * @param content the content modifier
     * @param expectedStatusCode the expected HTTP status code
     * @param responseType the result type
     * @return the parsed HTTP response
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     * @throws InvalidPayloadException if deserializing the payload fails
     */
    protected <T> T post(String path, Object entity, Content content, HttpStatus expectedStatusCode, Class<T> responseType)
            throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createPostRequest(
                resolve(QueryHelper.apply(path, content, QueryModifier.DEFAULT)),
                serialize(entity, content, QueryModifier.DEFAULT));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.POST, response, expectedStatusCode);
        return parseBody(response, responseType);
    }


    /**
     * Executes a HTTP PUT.
     *
     * @param entity the payload to send in the body
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void put(Object entity) throws ConnectivityException, StatusCodeException {
        put(null, entity, QueryModifier.DEFAULT);
    }


    /**
     * Executes a HTTP PUT.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void put(String path, Object entity) throws ConnectivityException, StatusCodeException {
        put(path, entity, QueryModifier.DEFAULT);
    }


    /**
     * Executes a HTTP PUT.
     *
     * @param entity the payload to send in the body
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void put(Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        put(null, entity, Content.DEFAULT, modifier);
    }


    /**
     * Executes a HTTP PUT.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void put(String path, Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        put(path, entity, Content.DEFAULT, modifier);
    }


    /**
     * Executes a HTTP PUT.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @param content the content modifier
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void put(String path, Object entity, Content content, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createPutRequest(
                resolve(QueryHelper.apply(path, content, modifier)),
                serialize(entity, content, modifier));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.PUT, response, HttpStatus.NO_CONTENT);
    }


    /**
     * Executes an HTTP PUT for files.
     *
     * @param path the URL path relative to the current endpoint
     * @param file the file
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void putFile(String path, TypedInMemoryFile file) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createPutFileRequest(resolve(QueryHelper.apply(path, Content.DEFAULT, QueryModifier.DEFAULT)), file);
        HttpResponse<byte[]> response = HttpHelper.sendFileRequest(httpClient, request);
        validateStatusCode(HttpMethod.PUT, response, HttpStatus.NO_CONTENT);
    }


    /**
     * Executes a HTTP PATCH.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void patch(String path, Object entity) throws ConnectivityException, StatusCodeException {
        patch(path, entity, QueryModifier.DEFAULT);
    }


    /**
     * Executes a HTTP PATCH.
     *
     * @param entity the payload to send in the body
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void patch(Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        patch(null, entity, Content.DEFAULT, modifier);
    }


    /**
     * Executes a HTTP PATCH.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid stats code
     */
    protected void patch(String path, Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        patch(path, entity, Content.DEFAULT, modifier);
    }


    /**
     * Executes a HTTP PATCH.
     *
     * @param entity the payload to send in the body
     * @param content the content modifier
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void patch(Object entity, Content content, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        patch(null, entity, content, modifier);
    }


    /**
     * Executes a HTTP PATCH.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @param content the content modifier
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void patch(String path, Object entity, Content content, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createPatchRequest(
                resolve(QueryHelper.apply(path, content, modifier)),
                serialize(entity, content, modifier));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.PATCH, response, HttpStatus.NO_CONTENT);
    }


    /**
     * Executes a HTTP PATCH with valueOnly serialization.
     *
     * @param path the URL path relative to the current endpoint
     * @param entity the payload to send in the body
     * @param modifier the query modifier
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void patchValue(String path, Object entity, QueryModifier modifier) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createPatchRequest(
                resolve(QueryHelper.apply(path, Content.VALUE, modifier)),
                serializeEntity(entity));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.PATCH, response, HttpStatus.NO_CONTENT);
    }


    /**
     * Executes a HTTP DELETE.
     *
     * @param path the URL path relative to the current endpoint
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void delete(String path) throws ConnectivityException, StatusCodeException {
        delete(path, HttpStatus.NO_CONTENT);
    }


    /**
     * Executes a HTTP DELETE.
     *
     * @param path the URL path relative to the current endpoint
     * @param expectedStatus the expected HTTP status code
     * @throws ConnectivityException if connection to the server fails
     * @throws StatusCodeException if HTTP request returns invalid status code
     */
    protected void delete(String path, HttpStatus expectedStatus) throws ConnectivityException, StatusCodeException {
        HttpRequest request = HttpHelper.createDeleteRequest(resolve(path));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.DELETE, response, expectedStatus);
    }


    /**
     * Creates a URL path for an id in the form of "/{base64URL-encoded id}".
     *
     * @param id the id
     * @return the URL path with the encoded id
     */
    protected String idPath(String id) {
        return "/" + EncodingHelper.base64UrlEncode(id);
    }


    /**
     * Resolves a path to the current {@code endpoint}.
     *
     * @param path the path to resolve
     * @return the resolved path relative to the current {@code endpoint}
     */
    protected URI resolve(String path) {
        return resolve(endpoint, path);
    }


    /**
     * Resolves a path to a given {@code baseUri}.
     *
     * @param baseUri the URI to resolve the path to
     * @param path the path to resolve
     * @return the resolved path relative to the current {@code baseUri}
     */
    protected static URI resolve(URI baseUri, String path) {
        if (Objects.isNull(path) || path.isBlank()) {
            return baseUri;
        }
        try {
            String base = baseUri.toString();
            String[] parts = path.split("\\?", 2);

            String actualPath = parts[0];
            if (actualPath.startsWith(URI_PATH_SEPERATOR)) {
                actualPath = actualPath.substring(1);
            }
            if (actualPath.endsWith(URI_PATH_SEPERATOR)) {
                actualPath = actualPath.substring(0, base.length() - 1);
            }
            if (!StringHelper.isBlank(actualPath) && !base.endsWith(URI_PATH_SEPERATOR)) {
                base += URI_PATH_SEPERATOR;
            }
            if (parts.length == 1) {
                return new URI(base + UriUtils.encodePath(actualPath, DEFAULT_CHARSET));
            }
            String query = parts[1];
            return updateQuery(new URI(base + UriUtils.encodePath(actualPath, DEFAULT_CHARSET)), UriUtils.encodeQuery(query, DEFAULT_CHARSET));

        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(
                    String.format(
                            "error resolving path (endpoint: %s, path: %s)",
                            baseUri,
                            path),
                    e);
        }
    }


    private static URI updateQuery(URI uri, String query) throws URISyntaxException {
        return new URI(
                uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                query.isBlank() ? uri.getQuery() : query,
                uri.getFragment());
    }


    private static URI sanitizeEndpoint(URI endpoint) {
        URI result = endpoint;
        if (endpoint.getPath().endsWith(URI_PATH_SEPERATOR)) {
            try {
                result = new URI(endpoint.toString().substring(0, endpoint.toString().length() - 1));
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException(String.format("error sanitizing endpoint URI (endpoint: %s", endpoint), e);
            }
        }
        return result;
    }


    /**
     * Parses body of HTTP response.
     *
     * @param <T> result type
     * @param response the response
     * @param responseType the type of the payload to parse
     * @return parsed body of response
     */
    protected <T> T parseBody(HttpResponse<String> response, Class<T> responseType) {
        try {
            return deserializer.read(response.body(), responseType);
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    private String serialize(Object entity, Content content, QueryModifier queryModifier) {
        try {
            OutputModifier outputModifier = new OutputModifier.Builder()
                    .level(queryModifier.getLevel())
                    .extent(queryModifier.getExtent())
                    .content(content).build();
            return serializer.write(entity, outputModifier);
        }
        catch (SerializationException | UnsupportedModifierException e) {
            throw new InvalidPayloadException("Serialization Failed", e);
        }
    }


    private String serializeEntity(Object entity) {
        try {
            return serializer.write(entity, OutputModifier.DEFAULT);
        }
        catch (SerializationException | UnsupportedModifierException e) {
            throw new InvalidPayloadException("Serialization Failed", e);
        }
    }


    private <T> Page<T> deserializePage(String responseBody, Class<T> responseType) throws DeserializationException {
        return deserializer.read(responseBody, TypeFactory.defaultInstance().constructParametricType(Page.class, responseType));
    }


    /**
     * Checks if a given response matches the expected HTTP status code.
     *
     * @param method the HTTP method
     * @param response the response to check
     * @param expected the expected HTTP status code
     * @throws StatusCodeException if the HTTP status code of the response is invlid/not supported
     */
    protected static void validateStatusCode(HttpMethod method, HttpResponse<?> response, HttpStatus expected) throws StatusCodeException {
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("response must be non-null");
        }
        if (Objects.equals(expected.getCode(), response.statusCode())) {
            return;
        }
        List<HttpStatus> supported = new ArrayList<>(SUPPORTED_DEFAULT_HTTP_STATUS);
        if (Objects.equals(method, HttpMethod.POST)) {
            supported.add(HttpStatus.METHOD_NOT_ALLOWED);
            supported.add(HttpStatus.CONFLICT);
        }

        try {
            HttpStatus status = HttpStatus.from(response.statusCode());
            if (!supported.contains(status)) {
                throw new UnsupportedStatusCodeException(response);
            }
            throw switch (status) {
                case BAD_REQUEST -> new BadRequestException(response);
                case UNAUTHORIZED -> new UnauthorizedException(response);
                case FORBIDDEN -> new ForbiddenException(response);
                case NOT_FOUND -> new NotFoundException(response);
                case METHOD_NOT_ALLOWED -> new MethodNotAllowedException(response);
                case CONFLICT -> new ConflictException(response);
                case INTERNAL_SERVER_ERROR -> new InternalServerErrorException(response);
                default -> throw new UnsupportedStatusCodeException(response);
            };
        }
        catch (IllegalArgumentException e) {
            throw new UnsupportedStatusCodeException(response);
        }
    }

}
