# Pique Data (msusecl-pique-data)

Pique Data is a java library, intended for creators of [PIQUE](https://GitHub.com/MSUSEL/msusel-pique) models, that provides features related to accessing
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
    <version>2.0.1</version>
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


*NOTE: If you are using a pre-built docker image or connecting to the on-prem mirror at the SECL, you don't need an NVD API key. If you make any calls to the CVE2.0 API,
you WILL want to set up a key first.*



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

Default values for a containerized nvd mirror follow as an example. These can be customized to suit your needs.
```bash
PG_DRIVER=jdbc:postgresql
PG_HOSTNAME=localhost
PG_PORT=5433
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
    2. Download this [docker compose](https://github.com/MSUSEL/msusecl-pique-data/blob/master/src/main/resources/docker-compose.yml) file or run the following command in your shell
        ```bash
        curl -o docker-compose.yml https://raw.githubusercontent.com/MSUSEL/msusecl-pique-data/refs/heads/master/src/main/resources/docker-compose.yml
        ``````
    3. Download and configure a postgres instance in a docker container:
        ```bash
        docker-compose up -d
        ```

2. __Interacting With The NVD Mirror__

    1. This docker compose file bundles Adminer, a simple, graphical database management tool which will run containerized over localhost. To start it, naviagate to the following url in a browser.  *Note that the port number can be customized in the docker-compose.yml file.*
        ```bash
        localhost:8080
        ```
    2. Log in to Adminer with the following credentials
        ```
        System: PostgreSQL
        Server: nvd_mirror
        Username: postgres
        Password: postgres
        Database: nvd_mirror
        ```

    3. Once logged in, you may need to navigate to the "nvd" Schema using the Schema drop-down menu.


2. __Bare Metal Installation__ (Not recommended unless there's a very good reason)
    1. Follow instructions on the postgres [website](https://www.postgresql.org/) to install and configure postgres on your system.
    2. Configure a DBMS of your choice.
    3. Add your database username and password to your configuration file or environment variables.
    <br><br>
3. __Build Relations and Hydrate Tables__
    1. To build table relations and hydrate the data, clone this repository, set up your credentials, and call testBuildAndHydrateMirror().
    2. To update the mirror with the latest from the NVD use the `updateNvdMirror()` method in your project or `testUpdateNvdMirror()` natively in PiqueData.
    <br><br>

-----------------

### Interacting with PiqueData

Two classes offer the user-facing functionality of this library.
* __PiqueDataFactory__ provides methods to create instances of PiqueData, NvdMirror, and NvdRequestBuilder.
* __PiqueData__ provides methods for interacting with third-party data. This includes methods to interact with
on-prem/ephemeral databases as well as third-party sources like the NVD. This is probably the right class to instantiate for most needs.
* __NvdMirror__ provides methods to manage a mirror of the NVD. With the options of the lab nvd mirror or portable dockerized nvd mirrors,
this class is not needed for most use cases.


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
   public Optional<Cve> exampleGetCveMethod() {
       try {
           Optional<Cve> result = piqueData.getCveById(cveId);
           if (result.isPresent()) {
               return result.get();
           } else {
               // handle any logical implications of no result from db
               return result // returns Optional.empty() object
           }
       } catch (DataAccessException e) {
           // Log error
           throw new RuntimeException(e);
       }
   }

   // Gets a list of CWE's associated with a particular CVE
   public List<String> exampleGetCweMethod() {
      try {
          List<String> result = piqueData.getCweName(cveId);
          if (result.isEmpty) {
              // handle any logical implications of no result from db
              // beware of Out of Bounds exceptions on empty lists
          }
          return result

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

### Working with the NVD CVE2.0 API
The NVD API offers features not covered in PiqueData. The advantage of the SECL NVD Mirror is that you can customize how you query the database easily with SQL.
However, if you want to work with the NVD directly or use any of their parameters, PiqueData makes it easy. This is the purpose of the NvdRequestBuilder class in
the presentation package. You can use this class to call the NVD with any parameter offered by the CVE2.0 API. A builder pattern is used and
so you can simply add as many parameters as you like by instantiating the NvdRequestBuilder class from the PiqueDataFactory class, then include parameters using "
.withParamName(paramValue)." As of January 2025, all legal CVE2.0 paramaeters are included in the NvdRequestBuilder. An example follows.

```java
class ExampleCallWithParams {
    PiqueDataFactory piqueDataFactory = new PiqueDataFactory(<optional_path_to_credentials_file>);

    try {
        CveEntity entity = piqueDataFactory.getNvdRequestBuilder()
            .withApiKey(System.getenv("NVD_KEY"))
            .withCpeName("cpe:2.3:a:eric_allman:sendmail:5.58:*:*:*:*:*:*:*")
            .build().executeRequest().getEntity();
    } catch (ApiCallException e) {
        // Log error
        throw new RuntimeException(e);
    }
}
```

A list of parameters can be found [here](https://nvd.nist.gov/developers/vulnerabilities).


-----------------

### Exception Handling

The PiqueData library uses two custom exceptions. It is recommended to wrap calls in a try-catch block and properly log exceptions in your pique models.

`DataAccessException` is thrown when there is an error interacting with a database.

`ApiCallException` is thrown when there is an error interacting with a third-party API. It is probably best to allow this exception to halt program execution. (Though there
may be reasons not to halt) Again the simple approach is to use try-catch blocks with logging. ApiCallException is used to indicate a problem with the API call rather
than a problem with the returned data.

Note that both of these extend the java RuntimeException class. As such, no try-catch blocks are necessary, but they are not caught, these will halt program execution if thrown.

-----------------


### More info
For a complete picture of available methods, it is recommended to read through the PiqueData and NvdMirror classes in the
`presentation` package. The methods and classes are extensively documented there. In subsequent releases, this will be
replaced by javadocs.

