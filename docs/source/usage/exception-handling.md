# Exception Handling

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
