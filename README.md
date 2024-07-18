# PiqueData Library

PiqueData (on github as "msusecl-data-utility") is a java library primarily intended for the Software Engineering and Cybersercutiy
Laboratory, Montana State University - Bozeman (SECL). While members of this research lab are the intended users, anyone developing a PIQUE
model may find this library useful for accessing third-party APIs or managing a local mirror of the National Vulnerability Database (NVD).
__Please note that at this time, the SECL does not offer public support for this library, does not guarantee functionality, and 
it is "use at your own risk".__ The original intent of this project was to provide opinionated access to the NVD's CVE2.0 API.
The official NVD APIs provide limited functionality. If greater expressiveness or flexibility is required, users are encouraged to [mirror the 
database](https://nvd.nist.gov/developers/api-workflows). Some PIQUE models which depend on the NVD, already build a mirror of the NVD at startup. 
However, this complicates the setup, benchmarking and evaluation phases of PIQUE. As such, this project evolved from simply
accessing the NVD through API calls to maintaining an on-prem mirror at the lab. **Again, please note that this mirror is for use only
by members of the SECL.** Recognizing that not all users of PiqueData will be members of SECL, this library provides flexibility to
build ephemeral mirrors with MongoDB and Docker. Outside the lab, this is the recommended approach.  More instructions follow on
how to build permanent and ephemeral mirrors and interact with 3rd-party APIs. Finally, this is a work in progress. The developers
will attempt to avoid breaking changes but stability is not currently guaranteed.



-----------------

### Installation
This project requires java 8+ and only supports the maven build system.
To install, add the following to your project's pom.xml file. Alternatively, you can clone the 
git repository and compile from source using java language level 8.
```
<dependency>
    <groupId>[PiqueData_group_id]</groupId>
    <artifactId>[PiqueData_artifact_id]</artifactId>
    <version>0.0.1</version>
</dependency>
```
-----------------
## Database Context and Setup

### On-Prem NVD Mirror
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
Note that you may need to adjust your settings to make these environment variables persistent between sessions.
You may also need to start your IDE from a terminal session depending on your system configuration.

3. Pass the String `"persistent"` as the `dbContext` parameter for relevant methods. It is recommended to store this 
as a static constant, in a properties file, or in some sort of credential injection service.

If you would like a graphical program to explore the NVD Mirror, [pgadmin](https://www.pgadmin.org/) and 
[adminer](https://www.adminer.org/) are free, open source RDBMS programs that provide an intuitive, 
graphical environment.


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

3. Hydrate the local database with data from the NVD.

```java
String local = "local";

try {
    NvdMirror.buildNvdMirror(local);
} catch (DataAccessException e) {
    throw new RuntimeException(e);
}
```

-----------------

## Interacting with PiqueData

Three classes offer the user-facing functionality of this library.
* __PiqueData__ provides static methods for interacting with third-party data. This includes methods to interact with
on-prem/ephemeral databases as well as third-party sources like the NVD.
* __NvdMirror__ provides static methods to manage a permanent mirror of the NVD
* __CveReponseProcessor__ provides an easy way to extract any field from a Cve object.

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

   // Future work will include methods to extract each field from a Cve object 
   // in the CveResponseProcessor class. In the meantime you can access any field 
   // in the Cve object and nested objects with normal java getters as with the following.
   
   // Gets a Descriptions value from a given CVE
   public String getDescriptionsValue() {
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

### Consuming 3rd-party APIs
The PiqueData class provides static methods for interacting with third-party APIs. Currently, PiqueData is configured to interact
with the National Vulnerability Database and GitHub Security Advisories. The NVD offers only RESTful endpoints and PiqueData deserializes
the responses to POJOs (These are typically accessed through the Cve class). This library also provides tools to marshal and unmarshal json
representations of the Cve objects.  

GHSA's are only offered via a GraphQL endpoint. Currently, PiqueData can consume this API, but it is optimized for only a very small subset
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

### More info
For a complete picture of available methods, it is recommended to read through the PiqueData and NvdMirror classes in the 
`presentation` package. The methods and classes are extensively documented there. In subsequent releases, this will be 
replaced by javadocs.

