# Eclipse FA³ST Client

![FA³ST Client Logo Light](./docs/images/fa3st-client-positive.svg/#gh-light-mode-only "FA³ST Client Logo")
![FA³ST Client Logo Dark](./docs/images/fa3st-client-negative.svg/#gh-dark-mode-only "FA³ST CLient Logo")


The Eclipse FA³ST Client is a Java-based client library for version 3.0.1 of the [AAS API](https://github.com/admin-shell-io/aas-specs-api/tree/v3.0.1) and
aims to simplify development of AAS client applications.
See also the [OpenAPI](https://app.swaggerhub.com/apis/Plattform_i40/Entire-API-Collection/V3.0.1) Documentation.
For detailed documentation see [JavaDoc](https://javadoc.io/doc/org.eclipse.digitaltwin.fa3st.client/core/latest/index.html).

## Usage

### Requirements
Java Runtime 17 or newer.

### with maven

Add the dependency:
```xml
<dependency>
	<groupId>org.eclipse.digitaltwin.fa3st.client</groupId>
	<artifactId>fa3st-client</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>

```
### with gradle

Add the dependency:
```gradle
implementation 'org.eclipse.digitaltwin.fa3st.client:fa3st-client:1.0.0-SNAPSHOT'
```

### Quick start
```java
URI serviceUri = new URI("https://www.example.org/api/v3.0");
AASRepositoryInterface aasRepository = new AASRepositoryInterface(serviceUri);
// Retrieve the AAS from the server.
AssetAdministrationShell aas = aasRepository.getAASInterface("globalUniqueId").get();
// Add a specific asset id to the asset information.
List<SpecificAssetId> specificAssetIds = new ArrayList<>();
specificAssetIds.add(new DefaultSpecificAssetId.Builder().name("specificAssetId").value("serialNumber").build());
AssetInformation updatedAssetInformation = aas.getAssetInformation();
updatedAssetInformation.setSpecificAssetIds(specificAssetIds);
aas.setAssetInformation(updatedAssetInformation);
// Update the AAS on the server.
aasRepository.getAASInterface("globalUniqueId").put(aas);
```


## Supported Features
*   CRUD operations for the major interfaces:
	* Asset Administration Shell API, Submodel API
	* Asset Administration Shell Repository API, Submodel Repository API, Concept Description Repository API
	* Asset Administration Shell Registry API, Submodel Registry API
	* Description API
*   Searching for specific identifiables in repositories
*   Paging
*   Synchronous Operations
*   Basic Authentication


## API

There are several classes that are central to the library.
An instance of them represents an AAS service. All of these interfaces can be instantiated using a `baseUri`.
This `baseUri` should include the domain and end on `api/v3.0`. For example: `https://www.example.org/api/v3.0`.
Optionally, the interfaces can be instantiated with strings for user and password for basic authentication or with a user defined http-client for more advanced customization.

### Repository Interfaces
These repository interfaces can be used for managing AAS (`AASRepositoryInterface`), Submodels (`SubmodelRepositoryInterface`) and Concept Descriptions (`ConceptDescriptionRepositoryInterface`) on an AAS server.
For interacting with individual AAS, Submodels or Concept Descriptions, the classes `AASInterface`, `SubmodelInterface` and `DescriptionInterface` should be instantiated from the respective repository interfaces.

### Registry Interfaces
The registry interfaces can be used for managing AAS descriptors and Submodel descriptors on registries.

### Description Interface
The description interface can be used to request a self-description of the features of the AAS server.

### CRUD operations

The source code below demonstrates the CRUD operations for AAS objects.
Operations for other entities work similarly.

```java
URI serviceUri = new URI("https://www.example.org/api/v3.0");
AASRepositoryInterface aasRepository = new AASRepositoryInterface(serviceUri);

AssetAdministrationShell aas = new DefaultAssetAdministrationShell.Builder().setId("uniqueId").build();

// Create an AAS in the AAS repository
AssetAdministrationShell registeredAas = aasRepository.post(aas);

// Retrieve all AAS from the repository
List<AssetAdministrationShell> aasList = aasRepository.getAll(searchCriteria);

// Retrieve one AAS with unique global id from the repository
AssetAdministrationShell aas = aasRepository.getAASInterface("globalUniqueId").get();

// Update the retrieved AAS
aas.setIdShort("newIdShort");
aasRepository.getAASInterface("globalUniqueId").patch(aas);

// Delete AAS
aasRepository.delete(aas.getId());
```

### Searching and paging results

When querying repositories for entities it is possible to search the results based on search criteria and limit the results using paging.

The `AASSearchCriteria` class allows for filtering AAS based on `idShort` and `assetIds`.

The `SubmodelSearchCriteria` class allows for filtering Submodels based on `idShort` and `semanticId`.

The `ConceptDescriptionSearchCriteria` class allows for filtering Concept Descriptions based on `idShort`, `isCaseOf` and `dataSpecification`.

The `AASDescriptorSearchCriteria` class allows for filtering AAS Descriptors based on `assetKind` and `assetType`.

The source code below demonstrates filtering AAS using the `AASSearchCriteria` class. Filtering based on other search criteria works similarly.

```java
import org.eclipse.digitaltwin.fa3st.common.model.asset.GlobalAssetIdentification;
import org.eclipse.digitaltwin.fa3st.common.model.asset.SpecificAssetIdentification;

// Find all AAS based on idShort "defaultIdShort", global asset id "globalAssetLink" and specific asset id "specificId" from the repository
GlobalAssetIdentification globalAssetId = new GlobalAssetIdentification.Builder().value("globalAssetLink").build();
SpecificAssetIdentification specificAssetId = new SpecificAssetIdentification.Builder().key("specificId").value("specificAssetLink").build();

AASSearchCriteria searchCriteria = new AASSearchCriteria.Builder()
		.idShort("defaultIdShort")
		.assetIds(globalAssetId)
		.assetIds(specificAssetId)
		.build();
List<AssetAdministrationShell> aasList = aasRepository.getAll(searchCriteria);
```

Additionally, the resulting entities can be received in a paged format instead of a list containing all entities.
Lists containing all entities can be very long, so paging is a way to gain control over the used bandwidth and speed of the transfer.

The source code below demonstrates paging the results from an AAS repository using the `PagingInfo` class.

```java
// Retrieve individual pages of entities.
PagingInfo pagingInfo = new PagingInfo.Builder()
		.limit(10)
		.build();
Page<AssetAdministrationShell> aasPage1 = aasRepository.get(pagingInfo);
```
The resulting page contains a `PagingMetadata` class that saves the `cursor` of the paging.
The `cursor` can be used to update the `pagingInfo` in order to continue with the next page.

```java
// Use the cursor parameter to continue with the next page.
Page<AssetAdministrationShell> aasPage2 = aasRepository.get(PagingInfo.builder().of(aasPage1.getMetadata().getCursor(), 10));
```

### Retrieving entities in different formats

The AAS API allows for retrieving entities in different formats.
Most of these formats are used to change the serialization of Submodel Elements and Submodels.
AAS can only be returned as a reference or in the standard serialization.
These formats include:
- Metadata: Returns all metadata without values as a `Submodel` or `SubmodelElement`.
- Value: Includes all values without the metadata of an entity as a `JsonNode` in the case of a Submodel or `ElementValue` in the case of a Submodel Element.
- Reference: Returns the entity as a `Reference`.
- Path: Returns the IdShortPath of an entity as a `String`.

For each format there exist separate methods because of the differing return types.


### Invoking Operations

The AAS API allows for invoking operations on the server. So far, only synchronous operations are supported but the support of asynchronous operations is planned for the future.

The code below demonstrates how to invoke an operation and store the operation result.

```java
// Invoke an operation and store the result.
OperationVariable operationVariable = new DefaultOperationVariable.Builder()
		.value(new DefaultProperty()).build();
Operation operation = new DefaultOperation.Builder()
		.inputVariables(operationVariable).build();
OperationResult responseOperationResult = submodelInterface.invokeOperationSync(
		IdShortPath.parse(operation.getIdShort()), operation);
```

## Exception Handling
FA³ST Client throws two different checked exceptions for most public methods.
There is a `ConnectivityException` if the connection to the server cannot be established and a `StatusCodeException` if the server responds with an error e.g., `UnauthorizedException` or `NotFoundException`.
It is good practice to handle the two exceptions separately.
For detailed exception handling the status code exception can be split up for all possible exceptions.
Which exceptions can occur is documented in the javadoc of the method.

```java
// Detailed exception handling
try {
AssetAdministrationShell responseAas = AASInterface.get();
} catch (StatusCodeException e) {
		// Handle different status code exceptions
		if (e instanceof BadRequestException) {
		System.err.println("Bad Request: " + e.getMessage());
		} else if (e instanceof UnauthorizedException) {
		System.err.println("Unauthorized: " + e.getMessage());
		} else if (e instanceof ForbiddenException) {
		System.err.println("Forbidden: " + e.getMessage());
		} else if (e instanceof NotFoundException) {
		System.err.println("Not Found: " + e.getMessage());
		} else (e instanceof InternalServerErrorException) {
		System.err.println("Internal Server Error: " + e.getMessage());
		}
} catch (ConnectivityException e) {
		// Handle connectivity issues
		System.err.println("Connectivity issue: " + e.getMessage());
}

```

Alternatively the super class `ClientException` can be used to handle all exceptions together.

## Security
FA³ST Client can handle security on AAS servers.
For basic authentication using username and password the interfaces can be instantiated with these parameters.
For more complex security a custom http client (with the java.net library) can be used to instantiate an interface for connection with that server.

## Roadmap
*   Asynchronous Operations
*   Value-only Operations
*   Serialization API
*   Service Specifications and Profiles
*   Page Scrolling

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions are **greatly appreciated**.
You can find our contribution guidelines [here](CONTRIBUTING.md)

## License

Distributed under the Apache 2.0 License. See `LICENSE` for more information.

Copyright (C) 2025 the Eclipse FA³ST Authors.

You should have received a copy of the Apache 2.0 License along with this program. If not, see https://www.apache.org/licenses/LICENSE-2.0.html.
