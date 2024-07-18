# PiqueData Library

### Introduction and Background

PiqueData (on github as "msusecl-data-utility") is a java library primarily intended for the Software Engineering and Cybersercutiy
Laboratory, Montana State University - Bozeman (SECL). While members of this research lab are the intended users, anyone developing a PIQUE
model may find this library useful for accessing third-party APIs or managing a local mirror of the National Vulnerability Database (NVD).
**Please note that at this time, the SECL does not offer public support for this library, does not guarantee functionality, and 
it is "use at your own risk".** The original intent of this project was to provide opinionated access to the NVD's CVE2.0 API.
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

-------------- Eplanation of on-prem database for SECL members --------------

-------------- Explanation of "local" db using mongodb and docker --------------

## Interacting with PiqueData

Three classes offer the user-facing functionality of this library.
* PiqueData provides static methods for interacting with third-party data. This includes methods to interact with
on-prem/ephemeral databases as well as third-party sources like the NVD.
* NvdMirror provides static methods to manage a permanent mirror of the NVD
* CveReponseProcessor provides an easy way to extract any field from a Cve object.

Example usages are included below.
### Consuming 3rd-party APIs
The PiqueData class provides static methods for interacting with third-party APIs. Currently, PiqueData is configured to interact
with the National Vulnerability Database and GitHub Security Advisories. The NVD offers only RESTful endpoints and PiqueData deserializes
the response to POJOs (These are typically accessed through the Cve class). This library also provides tools to marshal and unmarshal json
representations of the Cve objects.  

GHSA's are only offered via a GraphQL endpoint. Currently, PiqueData can consume this API, but it is optimized for only a very small subset
of the GHSA graph. A future release will contain a full GraphQL library with type-safe queries.

#### Examples

```java
class exampleClass() {
    private final String cveId = "CVE-1999-0095";
    private final String dbContextLocal = "local";
    private final String dbConextPersistent = "persistent";
    
    // Get a CVE from a local or permanent NVD mirror
    Cve cveFromLocalMirror = PiqueData.getCveById(dbContextLocal);
                                // or
    Cve cveFromPersistentMirror = PiqueData.getCveById(dbConextPersistent);
                                // or
    // Get a CVE from the National Vulnerability Database
    Cve cveFromNvd = PiqueData.getCveFromNvd(cveId);

    // Interact with the retrieved Cve objects:
    String[] weaknesses = CveResponseProcessor.extractCwes(cveFromPersistentMirror);
    
    // Future work will include methods to extract each field from a Cve object 
    // in the CveResponseProcessor class. In the meantime you can access any field 
    // in the Cve object and nested objects with normal java getters as with the following.
    String descriptionsValue = cveFromLocalMirror.getDescriptions().get(0).getValue();
    
}
```