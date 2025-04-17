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
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.fa3st.client.exception.ConnectivityException;
import org.eclipse.digitaltwin.fa3st.client.exception.StatusCodeException;
import org.eclipse.digitaltwin.fa3st.client.query.SearchCriteria;
import org.eclipse.digitaltwin.fa3st.client.query.SubmodelSearchCriteria;
import org.eclipse.digitaltwin.fa3st.client.util.HttpHelper;
import org.eclipse.digitaltwin.fa3st.common.model.IdShortPath;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.Content;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.Extent;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.Level;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.OutputModifier;
import org.eclipse.digitaltwin.fa3st.common.model.api.modifier.QueryModifier;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.Page;
import org.eclipse.digitaltwin.fa3st.common.model.api.paging.PagingInfo;


/**
 * Interface for managing Submodels. It further provides access to the data of these elements through
 * the Submodel Interface. A repository can host multiple entities.
 *
 * <p>
 * Communication is handled via HTTP requests to a specified service URI.
 * </p>
 */
public class SubmodelRepositoryInterface extends BaseInterface {

    private static final String API_PATH = "/submodels";

    /**
     * Creates a new Submodel Repository API.
     *
     * @param endpoint uri used to communicate with the FA³ST service
     * @param httpClient Allows user to specify custom http-client
     */
    public SubmodelRepositoryInterface(URI endpoint, HttpClient httpClient) {
        super(resolve(endpoint, API_PATH), httpClient);
    }


    /**
     * Creates a new Submodel Repository Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     */
    public SubmodelRepositoryInterface(URI endpoint) {
        super(resolve(endpoint, API_PATH));
    }


    /**
     * Creates a new Submodel Repository Interface with basic authentication.
     *
     * @param endpoint uri used to communicate with the FA³ST service
     * @param user String for basic authentication
     * @param password String for basic authentication
     */
    public SubmodelRepositoryInterface(URI endpoint, String user, String password) {
        super(resolve(endpoint, API_PATH), user, password);
    }


    /**
     * Creates a new Submodel Repository Interface.
     *
     * @param endpoint Uri used to communicate with the FA³ST service
     * @param trustAllCertificates Allows user to specify if all certificates (including self-signed) are trusted
     */
    public SubmodelRepositoryInterface(URI endpoint, boolean trustAllCertificates) {
        super(resolve(endpoint, API_PATH), trustAllCertificates ? HttpHelper.newTrustAllCertificatesClient() : HttpHelper.newDefaultClient());
    }


    /**
     * Retrieves all Submodels.
     *
     * @return A List of all Submodels
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
    public List<Submodel> getAll() throws StatusCodeException, ConnectivityException {
        return getAll(null, SearchCriteria.DEFAULT, OutputModifier.DEFAULT, Submodel.class);
    }


    /**
     * Retrieves all Submodels according to output modifier.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @return List of all Submodels
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
    public List<Submodel> getAll(QueryModifier modifier) throws StatusCodeException, ConnectivityException {
        return getAll(
                null,
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.NORMAL, modifier.getLevel(), modifier.getExtent()),
                Submodel.class);
    }


    /**
     * Retrieves all Submodels according to output modifier.
     *
     * @param level The level to use
     * @param extent The extent to use
     * @return List of all Submodels
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
    public List<Submodel> getAll(Level level, Extent extent) throws StatusCodeException, ConnectivityException {
        return getAll(
                null,
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.NORMAL, level, extent),
                Submodel.class);
    }


    /**
     * Retrieves all Submodels that match specific search criteria.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return List of all submodels matching the search criteria
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
    public List<Submodel> getAll(SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(null, submodelSearchCriteria, OutputModifier.DEFAULT, Submodel.class);
    }


    /**
     * Retrieves all Submodels that match specific search criteria according to query modifiers.
     * 
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @return List of Submodels
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
    public List<Submodel> getAll(SubmodelSearchCriteria submodelSearchCriteria, QueryModifier modifier) throws StatusCodeException, ConnectivityException {
        return getAll(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.NORMAL, modifier.getLevel(), modifier.getExtent()),
                Submodel.class);
    }


    /**
     * Retrieves all Submodels that match specific search criteria according to query modifiers.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param level The level to use
     * @param extent The extent to use
     * @return List of Submodels
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
    public List<Submodel> getAll(SubmodelSearchCriteria submodelSearchCriteria, Level level, Extent extent) throws StatusCodeException, ConnectivityException {
        return getAll(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.NORMAL, level, extent),
                Submodel.class);
    }


    /**
     * Retrieves a page of Submodels.
     *
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of submodels
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
    public Page<Submodel> get(PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(null, SearchCriteria.DEFAULT, OutputModifier.DEFAULT, pagingInfo, Submodel.class);
    }


    /**
     * Retrieves a page of Submodels that match specific search criteria.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodels
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
    public Page<Submodel> get(SubmodelSearchCriteria submodelSearchCriteria, PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(null, submodelSearchCriteria, OutputModifier.DEFAULT, pagingInfo, Submodel.class);
    }


    /**
     * Retrieves a page of Submodels according to query modifier.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodels
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
    public Page<Submodel> get(QueryModifier modifier, PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(
                null,
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.NORMAL, modifier.getLevel(), modifier.getExtent()),
                pagingInfo,
                Submodel.class);
    }


    /**
     * Retrieves a page of Submodels according to query modifier.
     *
     * @param level The level to use
     * @param extent The extent to use
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodels
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
    public Page<Submodel> get(Level level, Extent extent, PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(
                null,
                SearchCriteria.DEFAULT,
                OutputModifier.with(Content.NORMAL, level, extent),
                pagingInfo,
                Submodel.class);
    }


    /**
     * Retrieves a page of Submodels matching specific search criteria according to query modifier.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodels
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
    public Page<Submodel> get(SubmodelSearchCriteria submodelSearchCriteria, QueryModifier modifier, PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.NORMAL, modifier.getLevel(), modifier.getExtent()),
                pagingInfo,
                Submodel.class);
    }


    /**
     * Retrieves a page of Submodels matching specific search criteria according to query modifier.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param level The level to use
     * @param extent The extent to use
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodels
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
    public Page<Submodel> get(SubmodelSearchCriteria submodelSearchCriteria, Level level, Extent extent, PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.NORMAL, level, extent),
                pagingInfo,
                Submodel.class);
    }


    /**
     * Retrieves all Submodel metadata matching specific search criteria.
     *
     * @param level The level to use
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A List containing all submodels serialised as metadata
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
    public List<Submodel> getAllMetadata(Level level, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.METADATA, level, Extent.DEFAULT),
                Submodel.class);
    }


    /**
     * Retrieves a page of Submodel metadata matching specific search criteria.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param level The level to use
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodel metadata
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
    public Page<Submodel> getMetadata(SubmodelSearchCriteria submodelSearchCriteria, Level level, PagingInfo pagingInfo)
            throws StatusCodeException, ConnectivityException {
        return getPage(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.METADATA, level, Extent.DEFAULT),
                pagingInfo,
                Submodel.class);
    }


    /**
     * Retrieves a List containing all Submodels matching specific search criteria in value only serialisation.
     *
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A list of Submodels
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
    public List<Submodel> getAllValues(QueryModifier modifier, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.VALUE, modifier.getLevel(), modifier.getExtent()),
                Submodel.class);
    }


    /**
     * Retrieves a List containing all Submodels matching specific search criteria in value only serialisation.
     *
     * @param level The level to use
     * @param extent The extentto use
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A list of Submodels
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
    public List<Submodel> getAllValues(Level level, Extent extent, SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.VALUE, level, extent),
                Submodel.class);
    }


    /**
     * Retrieves a page containing Submodels matching specific search criteria in value only serialisation.
     * 
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param modifier The query modifier specifies the structural depth and resource serialization of the submodel
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodels in value only serialisation
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
    public Page<Submodel> getValue(SubmodelSearchCriteria submodelSearchCriteria, QueryModifier modifier, PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.VALUE, modifier.getLevel(), modifier.getExtent()),
                pagingInfo,
                Submodel.class);
    }


    /**
     * Retrieves a page containing Submodels matching specific search criteria in value only serialisation.
     * 
     * @param level The level to use
     * @param extent The extent to use
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of Submodels in value only serialisation
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
    public Page<Submodel> getValue(SubmodelSearchCriteria submodelSearchCriteria, Level level, Extent extent, PagingInfo pagingInfo)
            throws StatusCodeException, ConnectivityException {
        return getPage(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.VALUE, level, extent),
                pagingInfo,
                Submodel.class);
    }


    /**
     * Retrieves a list of references to Submodels matching specific search criteria.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @return A List of References
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
    public List<Reference> getAllReferences(SubmodelSearchCriteria submodelSearchCriteria) throws StatusCodeException, ConnectivityException {
        return getAll(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.REFERENCE, Level.CORE, Extent.DEFAULT),
                Reference.class);
    }


    /**
     * Retrieves a page of references to Submodels matching specific search criteria.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of References
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
    public Page<Reference> getReference(SubmodelSearchCriteria submodelSearchCriteria, PagingInfo pagingInfo) throws StatusCodeException, ConnectivityException {
        return getPage(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.REFERENCE, Level.CORE, Extent.DEFAULT),
                pagingInfo,
                Reference.class);
    }


    /**
     * Retrieves a list of paths to Submodels matching specific search criteria.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param level The level to use
     * @return A list of paths
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
    public List<IdShortPath> getAllPaths(SubmodelSearchCriteria submodelSearchCriteria, Level level) throws StatusCodeException, ConnectivityException {
        return getAll(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.PATH, level, Extent.DEFAULT),
                IdShortPath.class);
    }


    /**
     * Retrieves a page of paths to Submodels matching specific search criteria.
     *
     * @param submodelSearchCriteria Search criteria to filter Submodels based on IdShort and semanticId
     * @param level The level to use
     * @param pagingInfo Metadata for controlling the pagination of results
     * @return A page of paths
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
    public Page<IdShortPath> getSubmodelsPath(SubmodelSearchCriteria submodelSearchCriteria, Level level, PagingInfo pagingInfo)
            throws StatusCodeException, ConnectivityException {
        return getPage(
                null,
                submodelSearchCriteria,
                OutputModifier.with(Content.PATH, level, Extent.DEFAULT),
                pagingInfo,
                IdShortPath.class);
    }


    /**
     * Creates a new Submodel. The unique if of the new submodel must be set in the payload.
     *
     * @param submodel Submodel object
     * @return The created Submodel
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
    public Submodel post(Submodel submodel) throws StatusCodeException, ConnectivityException {
        return post(null, submodel, Content.NORMAL, Submodel.class);
    }


    /**
     * Deletes a Submodel.
     *
     * @param submodelIdentifier The unique identifier of the Submodel to be deleted
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


    /**
     * Returns a Submodel Interface for use of Interface Methods.
     *
     * @param submodelId The Submodels’ unique id
     * @return The requested Submodel Interface
     */
    public SubmodelInterface getSubmodelInterface(String submodelId) {
        return new SubmodelInterface(resolve(idPath(submodelId)), httpClient);
    }
}
