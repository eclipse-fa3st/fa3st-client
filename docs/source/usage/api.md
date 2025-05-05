# API

There are several classes that are central to the library.
An instance of them represents an AAS service. All of these interfaces can be instantiated using a `baseUri`.
This `baseUri` should include the domain and end on `api/v3.0`. For example: `https://www.example.org/api/v3.0`.
Optionally, the interfaces can be instantiated with strings for user and password for basic authentication or with a user defined http-client for more advanced customization.

## Repository Interfaces
These repository interfaces can be used for managing AAS (`AASRepositoryInterface`), Submodels (`SubmodelRepositoryInterface`) and Concept Descriptions (`ConceptDescriptionRepositoryInterface`) on an AAS server.
For interacting with individual AAS, Submodels or Concept Descriptions, the classes `AASInterface`, `SubmodelInterface` and `DescriptionInterface` should be instantiated from the respective repository interfaces.

## Registry Interfaces
The registry interfaces can be used for managing AAS descriptors and Submodel descriptors on registries.

## Description Interface
The description interface can be used to request a self-description of the features of the AAS server.

## CRUD operations

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

## Searching and paging results

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

## Retrieving entities in different formats

The AAS API allows for retrieving entities in different formats.
Most of these formats are used to change the serialization of Submodel Elements and Submodels.
AAS can only be returned as a reference or in the standard serialization.
These formats include:
- Metadata: Returns all metadata without values as a `Submodel` or `SubmodelElement`.
- Value: Includes all values without the metadata of an entity as a `JsonNode` in the case of a Submodel or `ElementValue` in the case of a Submodel Element.
- Reference: Returns the entity as a `Reference`.
- Path: Returns the IdShortPath of an entity as a `String`.

For each format there exist separate methods because of the differing return types.


## Invoking Operations

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