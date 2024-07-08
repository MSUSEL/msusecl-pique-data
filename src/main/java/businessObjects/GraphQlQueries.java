package businessObjects;

public final class GraphQlQueries {
    public static final String GHSA_SECURITY_ADVISORY_QUERY = "query { securityAdvisory(ghsaId: \"%s\") { ghsaId summary cwes(first : 1) { nodes { cweId } } } }";
}
