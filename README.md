# Eclipse FA³ST Client

![FA³ST Client Logo Light](./docs/images/fa3st-client-positive.svg/#gh-light-mode-only "FA³ST Client Logo")
![FA³ST Client Logo Dark](./docs/images/fa3st-client-negative.svg/#gh-dark-mode-only "FA³ST CLient Logo")


The Eclipse FA³ST Client is a Java-based client library for version 3.0.1 of the [AAS API](https://github.com/admin-shell-io/aas-specs-api/tree/v3.0.1) and
aims to simplify development of AAS client applications.
See also the [OpenAPI](https://app.swaggerhub.com/apis/Plattform_i40/Entire-API-Collection/V3.0.1) Documentation.
For detailed API documentation see [JavaDoc](https://javadoc.io/doc/org.eclipse.digitaltwin.fa3st.client/core/latest/index.html).

> [!TIP]
> For more details on FA³ST Client see the [:blue_book: **full documenation**](https://fa3st-client.readthedocs.io).

## Usage

### Maven

```xml
<dependency>
	<groupId>org.eclipse.digitaltwin.fa3st.client</groupId>
	<artifactId>fa3st-client</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```
### Gradle

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


## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions are **greatly appreciated**.
You can find our contribution guidelines [here](CONTRIBUTING.md)

## License

Distributed under the Apache 2.0 License. See `LICENSE` for more information.

Copyright (C) 2025 the Eclipse FA³ST Authors.

You should have received a copy of the Apache 2.0 License along with this program. If not, see https://www.apache.org/licenses/LICENSE-2.0.html.
