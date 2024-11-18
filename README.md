# Pique Data (msusecl-pique-data)

Pique Data is a java library, intended for users of [PIQUE](https://GitHub.com/MSUSEL/msusel-pique), that provides features related to accessing
public repositories of cyber vulnerabilities. In particular, it provides methods to access the the [National Vulnerability
Database](https://nvd.nist.gov/developers/api-workflows) (NVD) CVE 2.0 API and the [GitHub Security Advisory Database](https://GitHub.com/advisories).
The NVD has a limited feature set and recommends that heavy users of its API's create a mirror of the database. As such, this library provides
methods to create and interact with a local mirror of the NVD. This local mirror can be maintained on a dev machine or permanently stored on a
database server. Also this mirror can be updated as often as necessary to maintain parity with the current NVD. The performance improvements
of a local NVD mirror over the use of the API make it the recommended way to interact with the NVD when using Pique Data.

This library is versioned and developers will attempt to avoid breaking changes. However, backwards compatibility is not guaranteed.
View the Changelog for information on breaking changes. __Please note there is no warranty of any kind and this library is "use at your own risk"__



-----------------

### Installation
This project requires java 11+ and only supports the maven build system.
To install, add the following to your project's pom.xml file. Alternatively, you can clone the
git repository and compile from source using java language level 11.
```
<dependency>
    <groupId>edu.montana.gsoc.msusel</groupId>
    <artifactId>msusecl-pique-data</artifactId>
    <version>1.0.0</version>
</dependency>
```

-----------------

### General Setup
#### Necessary Software
* java development kit with a language level of 11+
* docker


### Configuration
#### Environment Variables
PiqueData can use environment variables to handle configuration values. To get the best out of this library,
it is recommended to set up an [NVD API key](https://nvd.nist.gov/developers/request-an-api-key). This will prevent
rate limits from interrupting calls. Additionally API calls to GitHub Security Advisories require a Personal Access
Token from [GitHub](https://docs.GitHub.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens).
No scope needs to be assigned to this token, but it must exist on the user's GitHub profile. To use these tokens with Pique Data,
create the following environment variables:

```bash
export NVD_KEY=<your key>
export GITHUB_PAT=<your personal access token>
```
The following values are needed to configure an NVD Mirror. Note that these database configuration values can also be set with a
configuration file as shown in the "Configuration File" section.

```bash
export PG_DRIVER=<driver_name>
export PG_HOSTNAME=<hostname or ip of database server>
export PG_PORT=<port>
export PG_DBNAME=<database name>
export PG_USERNAME=<your username>
export PG_PASS=<your password>
```

Default values for the docker container version of the nvd mirror follow as an example. These can be
customized to suit your needs.
```bash
PG_DRIVER=jdbc:postgresql
PG_HOSTNAME=postgres_network
PG_PORT=5432
PG_DBNAME=nvd_mirror
PG_USERNAME=postgres
PG_PASS=postgres
```

*Note: extra steps may be required to make these environment variables persistent between sessions. Check your operating system's
documentation on custom environment variables for more information*

#### Configuration File
PiqueData can use a configuration file instead of environment variables if desired. __Be sure to gitignore your configuration
file to avoid committing secrets to your respository.__ The configuration file must conform to a particular json schema. In `/src/main/resources`
is a skeleton configuration file. You can easily download that file with the following command.
```bash
curl -o https://raw.githubusercontent.com/MSUSEL/msusecl-data-utility/refs/heads/master/src/main/resources/credentialsTemplate.json
```

__*Important:*__ To work properly, the file must be named `configuration.json`. The recommended filepath is `/src/main/resources/configuration.json`, but this can be customized.

*Note: Only one of the above methods is necessary to configure your project to use Pique Data*


-----------------

### Database Setup
The NVD Mirror created by this library uses postgres. The user will need to set up postgres on their hardware with one of two methods.

1. __Docker Container Installation__ (Recommended)
    1. Be sure docker is installed on your system and the docker daemon is running
    2. Download this [docker compose](https://raw.githubusercontent.com/MSUSEL/msusecl-data-utility/refs/heads/remove-mongo-and-improve-postgres/src/main/resources/docker-compose.yml) file or run the following command in bash
        ```bash
        curl -o https://raw.githubusercontent.com/MSUSEL/msusecl-data-utility/refs/heads/remove-mongo-and-improve-postgres/src/main/resources/docker-compose.yml
        ``````
    3. Download and configure a postgres instance in a docker container:
        ```bash
        docker-compose up -d
        ```
    4. This docker compose file bundles Adminer, a simple, graphical database management tool which will run containerized over localhost. To start it, naviagate to the following url in a browser.  *Note that the port number can be customized in the docker-compose.yml file.*
        ```bash
        localhost:8080
        ```

2. __Bare Metal Installation__
    1. Follow instructions on the postgres [website](https://www.postgresql.org/) to install and configure postgres on your system.
    2. Configure a DBMS of your choice.
    3. Add your database username and password to your configuration file or environment variables.
    <br><br>
3. __Build Relations and Hydrate Tables__
    1. To build table relations and hydrate the data __programmatically__, call `buildAndHydrateMirror()` on an object of the NvdMirror class.
    2. To table relations and hydrate the data __interactively__, clone this repo and run the `testBuildAndHydrateMirror()` test from the NvdMirrorIntegrationTests class.
    3. To update the mirror with the latest from the NVD use the `updateNvdMirror()` and `testUpdateNvdMirror()` methods from the same classes respectively.
    <br><br>

-----------------

### Interacting with PiqueData

Two classes offer the user-facing functionality of this library.
* __PiqueDataFactory__ provides methods to create instances of PiqueData, NvdMirror, and NvdRequestBuilder.
* __PiqueData__ provides methods for interacting with third-party data. This includes methods to interact with
on-prem/ephemeral databases as well as third-party sources like the NVD.
* __NvdMirror__ provides methods to manage a permanent mirror of the NVD


*Note: Other classes can be used, extended, implemented, or overridden. More documentation on advanced usage
will be included in subsequent releases.*


Example usages are included below.

### Obtaining data from an NVD Mirror
This is the preferred way to interact with CVE data in the SECL. Direct access to a database whether over localhost or via a database server.

```java
class ExampleClass {
   private final String cveId = "CVE-1999-0095";

   // If configured with environment variables
   PiqueDataFactory piqueDataFactory = new PiqueDataFactory();

   // Else if configured with a credentials file:
   PiqueDataFactory piqueDataFactory = new PiqueDataFactory(path_to_credentials_file);

   PiqueData piqueData = piqueDataFactory.getPiqueData();

   // Gets a cve object
   public Cve exampleGetCveMethod() {
       try {
           return piqueData.getCveById(cveId);
       } catch (DataAccessException e) {
           // Log error
           throw new RuntimeException(e);
       }
   }

   // Gets a list of CWE's associated with a particular CVE
   public String[] exampleGetCweMethod() {
      try {
          return piqueData.getCwes(cveId);
      } catch (DataAccessException e) {
          throw new RuntimeException(e);
      }
   }
}
```

-----------------

### Consuming Third-party APIs
The PiqueData class provides methods for interacting with third-party APIs. Currently, PiqueData is configured to interact
with the National Vulnerability Database and GitHub Security Advisories. The NVD offers only REST endpoints and PiqueData deserializes
the responses to POJOs (These are typically accessed through the Cve class). This library also provides tools to serialize and deserialize json
representations of the Cve objects.

GHSAs are only offered via a GraphQL endpoint. Currently, PiqueData can consume this API, but it is optimized for a very small subset
of the GHSA graph. A future release will contain a full GraphQL library with type-safe queries.

#### Example

```java
class ExampleClass {
   private final String cveId = "CVE-1999-0095";
   private final String dbContext = "persistent";

   // If configured with environment variables
   PiqueDataFactory piqueDataFactory = new PiqueDataFactory();

   // Else if configured with a credentials file:
   PiqueDataFactory piqueDataFactory = new PiqueDataFactory(path_to_credentials_file);

   PiqueData piqueData = piqueDataFactory.getPiqueData();

   // Get a CVE from the National Vulnerability Database
   public String getACveFromTheNvd() {
      try {
          Cve cve = piqueData.getCveFromNvd(dbContext, cveId);
      } catch (ApiCallException e) {
          // Log error
          throw new RuntimeException(e);
      }
   }
}
```

-----------------

### A Note on Exception Handling

The PiqueData library uses two custom exceptions.
`DataAccessException` is thrown when there is an error interacting with a database.  `ApiCallException` is thrown when there is an error
interacting with a third-party API.

-----------------


### More info
For a complete picture of available methods, it is recommended to read through the PiqueData and NvdMirror classes in the
`presentation` package. The methods and classes are extensively documented there. In subsequent releases, this will be
replaced by javadocs.

