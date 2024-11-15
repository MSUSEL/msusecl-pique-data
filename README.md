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

## General Setup
### Necessary Software
* java development kit with a language level of 11+
* docker


### Configuration
#### Environment Variables
PiqueData can use environment variables to handle configuration values. To get the best out of this library,
it is recommended to set up an [NVD api key](https://nvd.nist.gov/developers/request-an-api-key). This will prevent
rate limits from interrupting calls. Additionally API calls to GitHub Security Advisories require a Personal Access
Token from [GitHub](https://docs.GitHub.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens).
No scope needs to be assigned to this token, but it must exist on the user's GitHub profile. To use these tokens with Pique Data,
create the following environment variables:

```bash
export NVD_KEY=<Your key>
export GITHUB_PAT=<Your Personal Access Token>
```
*Note: extra steps may be required to make these environment variables persistent between sessions.*



-----------------
### Database Setup
The NVD Mirror created by this library uses postgres. The user will need to set up postgres on their hardware with one of two methods.

1. Docker Image
    * Be sure docker is installed on your system and the docker daemon is running
    * Download this [docker compose](https://raw.githubusercontent.com/MSUSEL/msusecl-data-utility/refs/heads/remove-mongo-and-improve-postgres/src/main/resources/docker-compose.yml) file or run the following command in bash
        ```bash
        curl -o https://raw.githubusercontent.com/MSUSEL/msusecl-data-utility/refs/heads/remove-mongo-and-improve-postgres/src/main/resources/docker-compose.yml
        ``````
    * Run the following command to download and configure a postgres instance in a docker container.
        ```bash
        docker-compose up -d
        ```
    * This docker compose file bundles Adminer, a simple, graphical database management tool which will run containerized over localhost. To start it, naviagate to the following url in a browser.  *Note that the port number can be customized in the docker-compose.yml file.*
        ```bash
        localhost:8080
        ```

2. Bare Metal Installation

    * Follow instructions on the postgres [website](https://www.postgresql.org/) to install postgres on your system.
    * Configure a DBMS of your choice.

#### On-Prem NVD Mirror
The PiqueData library supports two different database systems and is architected to easily incorporate
new data sources. If you are developing a pique model in the SECL, and that pique model depends on information from
the NVD, the on-prem (or persistent) database is likely the right data source. The on-prem NVD mirror is updated
at least daily through the official NVD API so that the official NVD and the SECL mirror are in sync.
#### Steps to Configure Connection:
1. Obtain a username, password, and connection information for the on-prem database from Ryan or Derek
2. Set the following environment variables on your development machine.
```
PG_DRIVER=jdbc:postgresql
PG_HOSTNAME=<hostname or ip provided>
PG_PORT=<port provided>
PG_DBNAME=<database name provided>
PG_USERNAME=<your username>
PG_PASS=<your password>
```
*Note: extra steps may be required to make these environment variables persistent between sessions.
You may also need to start your IDE from a terminal session depending on your system configuration.*

3. Pass the String `"persistent"` as the `dbContext` parameter for relevant methods. It is recommended to store this
as a static constant, in a properties file, or in some sort of credential injection service.

If you would like a graphical program to explore the NVD Mirror, [pgadmin](https://www.pgadmin.org/) and
[adminer](https://www.adminer.org/) are free, open source RDBMS programs that provide an intuitive,
graphical environments.


### Local NVD Mirror
If you are not a member of the SECL or you wish to build your own mirror, PiqueData supports the local creation
of a dockerized MongoDB instance. No credentials are needed to use the local database, however, some setup is
still necessary.

#### Steps to Configure Connection:

1. Run the following command.
```
docker run -v nvd-mirror:/data/db --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:latest
```
This uses the [official docker image of mongodb community edition](https://www.mongodb.com/resources/products/compatibilities/docker)
and sets up a persistent volume on your local machine. If the docker container shuts down, the nvd data will remain on your system
accessible to another docker container.


2. Pass the String `"local"` as the `dbContext` parameter for relevant methods. It is recommended to store this
   as a static constant, in a properties file, or in some sort of credential injection service.

3. Hydrate the local database with data from the NVD. This should take between 10 and 25 minutes based on your network
connection and will require approximately 1 GB of storage.

```java
String local = "local";

try {
    NvdMirror.buildNvdMirror(local);
} catch (DataAccessException e) {
    throw new RuntimeException(e);
}
```

4. If the volume is not removed, the user can update this database with the latest information from the NVD by starting
a container with the above docker run command and running the `NvdMirror.updateNvdMirror(local)` method.

5. To remove the data after you are finished, delete the docker volume with the following command:
```dockerfile
docker volume rm nvd-mirror
```

Alternatively, you can omit the volume setup in the docker run command. With the volume, stopping the container will
destroy the data.

*Note: If you attempt pass "persistent" for the dbContext parameter on any methods/queries that mutate either the data or schema,
you will receive a permissions error.*

-----------------

## Interacting with PiqueData

Two classes offer the user-facing functionality of this library.
* __PiqueDataFactory__ provides methods to create instances of PiqueData, NvdMirror, and NvdRequestBuilder.
* __PiqueData__ provides methods for interacting with third-party data. This includes methods to interact with
on-prem/ephemeral databases as well as third-party sources like the NVD.
* __NvdMirror__ provides methods to manage a permanent mirror of the NVD


*Note: Other classes can be used, extended, implemented, or overridden. More documentation on advanced usage
will be included in subsequent releases.*


Example usages are included below.

### Accessing On-Prem or Local Database
This is the preferred way to interact with CVE data in the SECL. Direct access to a database whether over localhost
or to our on-prem server is much faster than hitting public API endpoints. The following examples will use the persistent
dbContext. For a local mirror, simply pass the "local" string for the dbContext parameter.

```java
class ExampleClass {
   private final String cveId = "CVE-1999-0095";
   private final String dbContext = "persistent";

   // Gets a list of CWE's associated with a particular CVE
   public String[] exampleGetCweMethod() {
      try {
          return PiqueData.getCwes(dbContext, cveId);
      } catch (DataAccessException e) {
          throw new RuntimeException(e);
      }
   }

   // Gets a Descriptions value from a given CVE
   public String getFirstDescriptionsValue() {
      try {
          Cve cve = PiqueData.getCveById(dbContext, cveId);
          return cveFromLocalMirror.getDescriptions().get(0).getValue();
      } catch (DataAccessException e) {
          throw new RuntimeException(e);
      }
   }
}
```

-----------------

### Consuming Third-party APIs
The PiqueData class provides static methods for interacting with third-party APIs. Currently, PiqueData is configured to interact
with the National Vulnerability Database and GitHub Security Advisories. The NVD offers only REST endpoints and PiqueData deserializes
the responses to POJOs (These are typically accessed through the Cve class). This library also provides tools to marshal and unmarshal json
representations of the Cve objects.

GHSAs are only offered via a GraphQL endpoint. Currently, PiqueData can consume this API, but it is optimized for a very small subset
of the GHSA graph. A future release will contain a full GraphQL library with type-safe queries.

#### Example

```java
class ExampleClass {
   private final String cveId = "CVE-1999-0095";
   private final String dbContext = "persistent";


   // Get a CVE from the National Vulnerability Database
   public String getACveFromTheNvd() {
      try {
          Cve cve = PiqueData.getCveFromNvd(dbContext, cveId);
      } catch (ApiCallException e) {
          throw new RuntimeException(e);
      }
   }
}
```

-----------------

### A Note on Exception Handling

The PiqueData library uses two custom checked exceptions.
`DataAccessException` is thrown when there is an error interacting with a database.  `ApiCallException` is thrown when there is an error
interacting with a third-party API.  Because these are both checked exceptions, the user must handle them when calling certain methods
from PiqueData. This gives the user the option to either halt or continue application execution in the event of an error retrieving
data.



-----------------


### More info
For a complete picture of available methods, it is recommended to read through the PiqueData and NvdMirror classes in the
`presentation` package. The methods and classes are extensively documented there. In subsequent releases, this will be
replaced by javadocs.

