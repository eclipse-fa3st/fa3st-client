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

import static org.eclipse.digitaltwin.fa3st.client.interfaces.BaseInterface.validateStatusCode;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import javax.xml.datatype.Duration;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationResult;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationRequest;
import org.eclipse.digitaltwin.fa3st.client.exception.ConnectivityException;
import org.eclipse.digitaltwin.fa3st.client.exception.InvalidPayloadException;
import org.eclipse.digitaltwin.fa3st.client.exception.StatusCodeException;
import org.eclipse.digitaltwin.fa3st.client.query.SearchCriteria;
import org.eclipse.digitaltwin.fa3st.client.util.HttpHelper;
import org.eclipse.digitaltwin.fa3st.client.util.QueryHelper;
import org.eclipse.digitaltwin.fa3st.common.dataformat.DeserializationException;
import org.eclipse.digitaltwin.fa3st.common.exception.InvalidRequestException;
import org.eclipse.digitaltwin.fa3st.common.model.IdShortPath;
import org.eclipse.digitaltwin.fa3st.common.model.InMemoryFile;
import org.eclipse.digitaltwin.fa3st.common.model.TypedInMemoryFile;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.Content;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.Extent;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.Level;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.OutputModifier;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.QueryModifier;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingInfo;
import org.eclipse.digitaltwin.fa3st.common.model.http.HttpMethod;
import org.eclipse.digitaltwin.fa3st.common.model.http.HttpStatus;
import org.eclipse.digitaltwin.fa3st.common.model.value.ElementValue;
import org.eclipse.digitaltwin.fa3st.common.typing.ElementValueTypeInfo;


/**
 * Interface for accessing the elements of a Submodel via a standardized API.
 * This interface allows operations such as retrieving, updating, and deleting various aspects of the Submodel,
 * including its Submodel Elements. Submodels and Submodel Elements can be deserialized in different ways:
 * as metadata, value, reference or path.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class SubmodelInterface extends BaseInterface {

    /**
     * Creates a new Submodel API.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param httpClient the httpClient to use
     */
    public SubmodelInterface(URI endpoint, HttpClient httpClient) {
        super(endpoint, httpClient);
    }


    /**
     * Creates a new Submodel API.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    public SubmodelInterface(URI endpoint) {
        super(endpoint);
    }


    /**
     * Creates a new Submodel API.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    public SubmodelInterface(URI endpoint, String user, String password) {
        super(endpoint, user, password);
    }


    /**
     * Creates a new Submodel API.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public SubmodelInterface(URI endpoint, boolean trustAllCertificates) {
        super(endpoint, trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Retrieves the Submodel from the server.
     *
     * @return The requested Submodel object in standard format: deep structural depth and without blob value
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Submodel get() throws StatusCodeException, ConnectivityException {
        return get(QueryModifier.DEFAULT);
    }


    /**
     * Retrieves the Submodel formatted according to query modifier.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @return Requested Submodel object formatted according to query modifier
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Submodel get(QueryModifier modifier) throws StatusCodeException, ConnectivityException {
        return get(null, OutputModifier.with(Content.NORMAL, modifier.getLevel(), modifier.getExtent()), Submodel.class);
    }


    /**
     * Retrieves the Submodel formatted according to query modifier.
     *
     * @param level The level to use
     * @param extent The extent to use
     * @return Requested Submodel object formatted according to query modifier
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Submodel get(Level level, Extent extent) throws StatusCodeException, ConnectivityException {
        return get(null, OutputModifier.with(Content.NORMAL, level, extent), Submodel.class);
    }


    /**
     * Replaces the current Submodel with a new one.
     *
     * @param submodel The new Submodel object to replace the current one
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void put(Submodel submodel) throws StatusCodeException, ConnectivityException {
        put(submodel, new QueryModifier.Builder().level(Level.DEEP).build());
    }


    /**
     * Updates the Submodel.
     *
     * @param submodel The new Submodel object to patch the current one
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void patch(Submodel submodel)
            throws StatusCodeException, ConnectivityException {
        patch(submodel, new QueryModifier.Builder().level(Level.CORE).build());
    }


    /**
     * Retrieves the metadata attributes of a specific Submodel.
     *
     * @param level The level to use
     * @return Requested Submodel object containing only metadata
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Submodel getMetadata(Level level) throws StatusCodeException, ConnectivityException {
        return get(null, OutputModifier.with(Content.METADATA), Submodel.class);
    }


    /**
     * Updates the metadata attributes of a specific Submodel.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param submodel The new Submodel object to patch the current one
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void patchMetadata(QueryModifier modifier, Submodel submodel) throws StatusCodeException, ConnectivityException {
        patch(submodel, Content.METADATA, modifier);
    }


    /**
     * Retrieves a specific Submodel in the value-only serialization.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @return JsonNode containing only the values of a Submodel object
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Submodel getValue(QueryModifier modifier) throws StatusCodeException, ConnectivityException {
        return get(null, OutputModifier.with(Content.VALUE), Submodel.class);
    }


    /**
     * Updates the values of a specific Submodel.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param jsonNode JsonNode containing the new values of the Submodel to update the current ones
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void patchValue(QueryModifier modifier, JsonNode jsonNode) throws StatusCodeException, ConnectivityException {
        patch(jsonNode, Content.VALUE, modifier);
    }


    /**
     * Retrieves the reference of a specific Submodel.
     *
     * @return The reference of the requested Submodel object
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Reference getReference() throws StatusCodeException, ConnectivityException {
        return get(null, OutputModifier.with(Content.REFERENCE, Level.CORE, Extent.WITHOUT_BLOB_VALUE), Reference.class);
    }


    /**
     * Retrieves the path of a specific Submodel.
     *
     * @param level The level to use
     * @return The path of the requested Submodel object
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public List<IdShortPath> getPath(Level level) throws StatusCodeException, ConnectivityException {
        return getAll(
                null,
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.PATH, level, Extent.DEFAULT),
                IdShortPath.class);
    }


    /**
     * Retrieves a list of all Submodel Elements including their hierarchy.
     *
     * @return A List of all submodel elements
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public List<SubmodelElement> getAllElements() throws StatusCodeException, ConnectivityException {
        return getAll(submodelElementsPath(), SearchCriteria.DEFAULT, OutputModifier.DEFAULT, SubmodelElement.class);
    }


    /**
     * Retrieves a list of all Submodel Elements including their hierarchy formatted according to query modifier.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @return A List of all submodel elements
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public List<SubmodelElement> getAllElements(QueryModifier modifier) throws StatusCodeException, ConnectivityException {
        return getAll(
                submodelElementsPath(),
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.NORMAL, modifier.getLevel(), modifier.getExtent()),
                SubmodelElement.class);
    }


    /**
     * Retrieves a list of all Submodel Elements including their hierarchy formatted according to query modifier.
     *
     * @param level The level to use
     * @param extent The extent to use
     * @return A List of all submodel elements
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public List<SubmodelElement> getAllElements(Level level, Extent extent) throws StatusCodeException, ConnectivityException {
        return getAll(
                submodelElementsPath(),
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.NORMAL, level, extent),
                SubmodelElement.class);
    }


    /**
     * Retrieves a Page of Submodel Elements including their hierarchy.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodel Elements
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Page<SubmodelElement> getElements(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getElements(pagingInfo, QueryModifier.DEFAULT);
    }


    /**
     * Retrieves a Page of Submodel Elements including their hierarchy.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @return A page of Submodel elements
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Page<SubmodelElement> getElements(PagingInfo pagingInfo, QueryModifier modifier) throws StatusCodeException, ConnectivityException {
        return getPage(
                submodelElementsPath(),
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.NORMAL, modifier.getLevel(), modifier.getExtent()),
                pagingInfo,
                SubmodelElement.class);
    }


    /**
     * Retrieves a Page of Submodel Elements including their hierarchy.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param level The level to use
     * @param extent The extent to use
     * @return A page of Submodel elements
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Page<SubmodelElement> getElements(PagingInfo pagingInfo, Level level, Extent extent) throws StatusCodeException, ConnectivityException {
        return getPage(
                submodelElementsPath(),
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.NORMAL, level, extent),
                pagingInfo,
                SubmodelElement.class);
    }


    /**
     * Creates a new Submodel Element as a child of the submodel. The idShort of the new Submodel Element must be set in the
     * payload.
     *
     * @param submodelElement The new Submodel Element object
     * @return Created Submodel Element object
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>409: ConflictException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public SubmodelElement postElement(SubmodelElement submodelElement) throws StatusCodeException, ConnectivityException {
        return post(submodelElementsPath(), submodelElement, Content.NORMAL, SubmodelElement.class);
    }


    /**
     * Retrieves the metadata attributes of multiple Submodel Elements.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param level The level to use
     * @return A page of Submodel Element Metadata
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Page<SubmodelElement> getElementMetadata(PagingInfo pagingInfo, Level level) throws StatusCodeException, ConnectivityException {
        return getPage(
                submodelElementsPath(),
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.METADATA, level, Extent.WITHOUT_BLOB_VALUE),
                pagingInfo,
                SubmodelElement.class);
    }


    /**
     * Retrieves the references of multiple Submodel Elements.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodel Element references
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Page<Reference> getElementReference(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(submodelElementsPath(), SearchCriteria.DEFAULT, OutputModifier.DEFAULT, pagingInfo, Reference.class);
    }


    /**
     * Retrieves the path of multiple Submodel Elements.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @param level The level to use
     * @return A page of Submodel Element paths
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Page<String> getElementPath(PagingInfo pagingInfo, Level level) throws StatusCodeException, ConnectivityException {
        return getPage(
                submodelElementsPath(),
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.PATH, level, Extent.WITHOUT_BLOB_VALUE),
                pagingInfo,
                String.class);
    }


    /**
     * Retrieves a specific submodel element from the Submodel at a specified path.
     *
     * @param idShortPath The path of the Submodel Element
     * @return The requested Submodel Element object
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public SubmodelElement getElement(IdShortPath idShortPath) throws StatusCodeException, ConnectivityException {
        return getElement(idShortPath, QueryModifier.DEFAULT);
    }


    /**
     * Retrieves a specific Submodel Element from the Submodel at a specified path.
     *
     * @param idShortPath The path of the Submodel Element
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @return The requested submodel element object
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public SubmodelElement getElement(IdShortPath idShortPath, QueryModifier modifier) throws StatusCodeException, ConnectivityException {
        return get(
                submodelElementIdPath(idShortPath),
                OutputModifier.with(Content.NORMAL, modifier.getLevel(), modifier.getExtent()),
                SubmodelElement.class);
    }


    /**
     * Retrieves a specific Submodel Element from the Submodel at a specified path.
     *
     * @param idShortPath The path of the Submodel Element
     * @param level The level to use
     * @param extent The extent to use
     * @return The requested submodel element object
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public SubmodelElement getElement(IdShortPath idShortPath, Level level, Extent extent) throws StatusCodeException, ConnectivityException {
        return get(
                submodelElementIdPath(idShortPath),
                OutputModifier.with(Content.NORMAL, level, extent),
                SubmodelElement.class);
    }


    /**
     * Creates a new submodel element at a specified path within the submodel element hierarchy.
     * If the PostSubmodelElementByPath is executed towards a SubmodelElementList, the new SubmodelElement is added to the
     * end of the list.
     *
     * @param idShortPath The path under which the new SubmodelElement shall be added
     * @param submodelElement The new Submodel Element object
     * @return The new Submodel Element object as hosted on the server
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>409: ConflictException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public SubmodelElement postElement(IdShortPath idShortPath, SubmodelElement submodelElement) throws StatusCodeException, ConnectivityException {
        return post(submodelElementIdPath(idShortPath), submodelElement, Content.NORMAL, SubmodelElement.class);
    }


    /**
     * Replaces an existing Submodel Element at a specified path within the submodel element hierarchy.
     *
     * @param idShortPath The path to the Submodel Element which shall be replaced
     * @param submodelElement The new Submodel Element object to replace the current one
     *
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void putElement(IdShortPath idShortPath, SubmodelElement submodelElement) throws StatusCodeException, ConnectivityException {
        put(submodelElementIdPath(idShortPath), submodelElement);
    }


    /**
     * Updates an existing Submodel Element at a specified path within the submodel element hierarchy.
     *
     * @param idShortPath The path to the Submodel Element which shall be replaced
     * @param submodelElement The new Submodel Element object to update the current one
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void patchElement(IdShortPath idShortPath, SubmodelElement submodelElement) throws StatusCodeException, ConnectivityException {
        patch(submodelElementIdPath(idShortPath), submodelElement);
    }


    /**
     * Deletes a Submodel Element at a specified path within the submodel elements hierarchy.
     *
     * @param idShortPath The path to the Submodel Element which shall be replaced
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void deleteElement(IdShortPath idShortPath) throws StatusCodeException, ConnectivityException {
        delete(submodelElementIdPath(idShortPath));
    }


    /**
     * Retrieves the metadata attributes of a specific Submodel Element.
     *
     * @param idShortPath The path to the Submodel Element
     * @return The Submodel Element metadata
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public SubmodelElement getElementMetadata(IdShortPath idShortPath) throws StatusCodeException, ConnectivityException {
        return get(submodelElementIdPath(idShortPath), OutputModifier.with(Content.METADATA), SubmodelElement.class);
    }


    /**
     * Updates the metadata attributes of a specific Submodel Element.
     *
     * @param idShortPath The path to the Submodel Element
     * @param submodelElement The new Submodel Element metadata to patch the current one
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void patchElementMetadata(IdShortPath idShortPath, SubmodelElement submodelElement) throws StatusCodeException, ConnectivityException {
        patch(submodelElementIdPath(idShortPath), submodelElement, new QueryModifier.Builder().level(Level.CORE).build());
    }


    /**
     * Returns a specific Submodel Element value from the Submodel at a specified path.
     *
     * @param <T> the return type
     * @param idShortPath The path to the Submodel Element
     * @param typeInfo Information specifying how the value should be deserialized. Requires type and datatype to be set
     * @return The requested submodel element value
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public <T extends ElementValue> T getElementValue(IdShortPath idShortPath, ElementValueTypeInfo typeInfo)
            throws StatusCodeException, ConnectivityException {
        return getElementValue(idShortPath, typeInfo, Level.DEFAULT);
    }


    /**
     * Returns a specific Submodel Element value from the Submodel at a specified path according to query modifier.
     *
     * @param <T> the return type
     * @param idShortPath The path to the Submodel Element
     * @param typeInfo Information specifying how the value should be deserialized. Requires type and datatype to be set
     * @param level The level to use
     * @return The requested submodel element value according to query parameter
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public <T extends ElementValue> T getElementValue(IdShortPath idShortPath, ElementValueTypeInfo typeInfo, Level level)
            throws StatusCodeException, ConnectivityException {
        String path = submodelElementIdPath(idShortPath);
        HttpRequest request = HttpHelper.createGetRequest(resolve(QueryHelper.apply(path, OutputModifier.with(Content.VALUE, level, Extent.DEFAULT))));
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        try {
            return deserializer.readValue(response.body(), typeInfo);
        }
        catch (DeserializationException e) {
            throw new InvalidPayloadException(e);
        }
    }


    /**
     * Updates an existing Submodel Element value at a specified path within the submodel element hierarchy.
     *
     * @param idShortPath The path to the Submodel Element which shall be updated
     * @param value The new Submodel Element value object to replace the current one
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void patchElementValue(IdShortPath idShortPath, Object value) throws StatusCodeException, ConnectivityException {
        patchValue(submodelElementIdPath(idShortPath), value, new QueryModifier.Builder().level(Level.DEFAULT).build());
    }


    /**
     * Retrieves a specific Submodel Element reference from the server.
     *
     * @param idShortPath The path to the Submodel Element
     * @return The reference of the requested Submodel Element
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public Reference getElementReference(IdShortPath idShortPath) throws StatusCodeException, ConnectivityException {
        return get(
                submodelElementIdPath(idShortPath),
                OutputModifier.with(Content.REFERENCE, Level.CORE, Extent.DEFAULT),
                Reference.class);
    }


    /**
     * Retrieves a specific Submodel Element path from the server.
     *
     * @param idShortPath The path to the Submodel Element
     * @param level The level to use
     * @return The path of the requested Submodel Element
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public String getElementPath(IdShortPath idShortPath, Level level) throws StatusCodeException, ConnectivityException {
        return get(
                submodelElementIdPath(idShortPath),
                OutputModifier.with(Content.PATH, level, Extent.DEFAULT),
                String.class);
    }


    /**
     * Returns a specific file from the Submodel at a specified path.
     *
     * @param idShortPath The path to the Submodel Element
     * @return The requested file
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>405: MethodNotAllowedException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public InMemoryFile getAttachment(IdShortPath idShortPath) throws StatusCodeException, ConnectivityException, InvalidRequestException {
        return getFile(attachmentPath(idShortPath));
    }


    /**
     * Replaces the file at a specified path within the submodel element hierarchy.
     *
     * @param idShortPath The path to the Submodel Element
     * @param attachment The new file to replace the current one
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>405: MethodNotAllowedException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void putAttachment(IdShortPath idShortPath, TypedInMemoryFile attachment) throws StatusCodeException, ConnectivityException {
        putFile(attachmentPath(idShortPath), attachment);
    }


    /**
     * Deletes the file of an existing submodel element at a specified path within the submodel element hierarchy.
     *
     * @param idShortPath The path to the Submodel Element
     * @throws StatusCodeException if the request fails
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public void deleteAttachment(IdShortPath idShortPath) throws StatusCodeException, ConnectivityException {
        delete(attachmentPath(idShortPath), HttpStatus.OK);
    }


    /**
     * Invokes a synchronous Operation at a specified path.
     *
     * @param idShortPath The path to the Submodel Element
     * @param input List of input variables
     * @param timeout Timeout for client in java xml duration format
     * @return The returned result of an operation’s invocation
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>405: MethodNotAllowedException</li>
     *             <li>409: ConflictException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public OperationResult invokeOperationSync(IdShortPath idShortPath, List<OperationVariable> input, Duration timeout) throws StatusCodeException, ConnectivityException {
        return invokeOperationSync(idShortPath, input, List.of(), timeout);
    }


    /**
     * Invokes a synchronous Operation at a specified path.
     *
     * @param idShortPath The path to the Submodel Element
     * @param input List of input variables
     * @param inoutput List of inoutput variables
     * @param timeout Timeout for client in java xml duration format
     * @return The returned result of an operation’s invocation
     * @throws StatusCodeException if the server responds with an error. Possible Exceptions:
     *             <div>
     *             <ul>
     *             <li>400: BadRequestException</li>
     *             <li>401: UnauthorizedException</li>
     *             <li>403: ForbiddenException</li>
     *             <li>404: NotFoundException</li>
     *             <li>405: MethodNotAllowedException</li>
     *             <li>409: ConflictException</li>
     *             <li>500: InternalServerErrorException</li>
     *             </ul>
     *             </div>
     * @throws ConnectivityException if the connection to the server cannot be established
     */
    public OperationResult invokeOperationSync(IdShortPath idShortPath, List<OperationVariable> input, List<OperationVariable> inoutput, Duration timeout)
            throws StatusCodeException, ConnectivityException {
        return post(
                invokePath(idShortPath),
                new DefaultOperationRequest.Builder()
                        .inputArguments(input)
                        .inoutputArguments(inoutput)
                        .clientTimeoutDuration(timeout)
                        .build(),
                Content.NORMAL,
                HttpStatus.OK,
                OperationResult.class);
    }


    private static String submodelElementsPath() {
        return "/submodel-elements";
    }


    private static String submodelElementIdPath(IdShortPath idShortPath) {
        return String.format("%s/%s", submodelElementsPath(), idShortPath);
    }


    private static String attachmentPath(IdShortPath idShortPath) {
        return submodelElementIdPath(idShortPath) + "/attachment";
    }


    private static String invokePath(IdShortPath idShortPath) {
        return submodelElementIdPath(idShortPath) + "/invoke";
    }
}
