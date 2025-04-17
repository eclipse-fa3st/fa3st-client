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
import java.util.List;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelDescriptor;
import org.eclipse.digitaltwin.fa3st.client.exception.ConnectivityException;
import org.eclipse.digitaltwin.fa3st.client.exception.StatusCodeException;
import org.eclipse.digitaltwin.fa3st.client.query.SearchCriteria;
import org.eclipse.digitaltwin.fa3st.client.util.HttpHelper;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.Content;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.OutputModifier;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingInfo;


/**
 * Interface for interacting with a Submodel Registry via a standardized API.
 * This interface allows to register and unregister descriptors of Submodels.
 * The descriptors contain the information needed to access the Submodel interface.
 * This required information includes the endpoint in the dedicated environment.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class SubmodelRegistryInterface extends BaseInterface {

    private static final String API_PATH = "/submodel-descriptors";

    /**
     * Creates a new Submodel Registry Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param httpClient Allows the user to specify a custom httpClient
     */
    public SubmodelRegistryInterface(URI endpoint, HttpClient httpClient) {
        super(resolve(endpoint, API_PATH), httpClient);
    }


    /**
     * Creates a new Submodel Registry Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     */
    public SubmodelRegistryInterface(URI endpoint) {
        super(resolve(endpoint, API_PATH));
    }


    /**
     * Creates a new Submodel Registry Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST Service
     * @param user String to allow for basic authentication
     * @param password String to allow for basic authentication
     */
    public SubmodelRegistryInterface(URI endpoint, String user, String password) {
        super(resolve(endpoint, API_PATH), user, password);
    }


    /**
     * Creates a new Submodel Registry Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public SubmodelRegistryInterface(URI endpoint, boolean trustAllCertificates) {
        super(resolve(endpoint, API_PATH), trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Retrieves a list of all Submodel Descriptors.
     *
     * @return A list containing all Submodel Descriptors
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
    public List<SubmodelDescriptor> getAll() throws StatusCodeException, ConnectivityException {
        return getAll(null, SearchCriteria.DEFAULT, OutputModifier.DEFAULT, SubmodelDescriptor.class);
    }


    /**
     * Returns a page of Submodel Descriptors.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodel Descriptors
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
    public Page<SubmodelDescriptor> get(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(null, SearchCriteria.DEFAULT, OutputModifier.DEFAULT, pagingInfo, SubmodelDescriptor.class);
    }


    /**
     * Creates a new Submodel Descriptor, i.e. registers a Submodel.
     *
     * @param submodelDescriptor Object containing the Submodel’s identification and endpoint information
     * @return Created Submodel Descriptor
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
    public SubmodelDescriptor post(SubmodelDescriptor submodelDescriptor) throws StatusCodeException, ConnectivityException {
        return post(null, submodelDescriptor, Content.NORMAL, SubmodelDescriptor.class);
    }


    /**
     * Returns a specific Submodel Descriptor.
     *
     * @param submodelIdentifier The Submodel’s unique id
     * @return Requested Submodel Descriptor
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
    public SubmodelDescriptor get(String submodelIdentifier) throws StatusCodeException, ConnectivityException {
        return get(idPath(submodelIdentifier), OutputModifier.DEFAULT, SubmodelDescriptor.class);
    }


    /**
     * Replaces an existing Submodel Descriptor, i.e. replaces registration information.
     *
     * @param submodelDescriptor Object containing the Submodel’s identification and endpoint information
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
    public void put(SubmodelDescriptor submodelDescriptor) throws StatusCodeException, ConnectivityException {
        super.put(idPath(submodelDescriptor.getId()), submodelDescriptor);
    }


    /**
     * Deletes a Submodel Descriptor, i.e. de-registers a Submodel.
     *
     * @param submodelIdentifier The Submodel’s unique id
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
    @Override
    public void delete(String submodelIdentifier) throws StatusCodeException, ConnectivityException {
        super.delete(idPath(submodelIdentifier));
    }
}
