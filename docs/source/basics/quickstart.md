# Quickstart

```java
AASRepositoryInterface aasRepository = new AASRepositoryInterface(new URI("https://www.example.org/api/v3.0"));
AASInterface aasInterface = aasRepository.getAASInterface("globalUniqueId");
AssetAdministrationShell aas = aasInterface.get();
aas.getAssetInformation().getSpecificAssetIds().add(
    new DefaultSpecificAssetId.Builder()
        .name("specificAssetId")
        .value("serialNumber")
        .build());
aasInterface.put(aas);
```