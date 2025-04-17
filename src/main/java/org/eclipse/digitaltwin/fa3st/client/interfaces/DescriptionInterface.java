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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.eclipse.digitaltwin.fa3st.client.exception.ConnectivityException;
import org.eclipse.digitaltwin.fa3st.client.exception.StatusCodeException;
import org.eclipse.digitaltwin.fa3st.client.util.HttpHelper;
import org.eclipse.digitaltwin.fa3st.common.model.ServiceDescription;
import org.eclipse.digitaltwin.fa3st.common.model.http.HttpMethod;
import org.eclipse.digitaltwin.fa3st.common.model.http.HttpStatus;


/**
 * Interface for communicating the description of a server.
 * This includes the capabilities and supported features of the server.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class DescriptionInterface extends BaseInterface {

    private static final String API_PATH = "/description";

    /**
     * Creates a new Description Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param httpClient custom http-client in case the user wants to set specific attributes
     */
    public DescriptionInterface(URI endpoint, HttpClient httpClient) {
        super(endpoint, httpClient);
    }


    /**
     * Creates a new Description Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    public DescriptionInterface(URI endpoint) {
        super(resolve(endpoint, API_PATH));
    }


    /**
     * Creates a new Description Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    public DescriptionInterface(URI endpoint, String user, String password) {
        super(resolve(endpoint, API_PATH), user, password);
    }


    /**
     * Creates a new Description Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public DescriptionInterface(URI endpoint, boolean trustAllCertificates) {
        super(resolve(endpoint, API_PATH), trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Retrieves the self-describing information of a network resource (ServiceDescription) as a List of Strings.
     *
     * @return Requested self-describing information
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
    public ServiceDescription get() throws StatusCodeException, ConnectivityException {
        HttpRequest request = HttpHelper.createGetRequest(endpoint);
        HttpResponse<String> response = HttpHelper.send(httpClient, request);
        validateStatusCode(HttpMethod.GET, response, HttpStatus.OK);
        return parseBody(response, ServiceDescription.class);
    }

}
