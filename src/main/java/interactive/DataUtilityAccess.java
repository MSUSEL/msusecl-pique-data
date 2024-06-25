package interactive;

import api.HTTPMethod;
import api.NVDRequest;
import api.NVDRequestFactory;
import api.NVDResponse;
import com.google.gson.Gson;
import common.DataProperties;
import common.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DataUtilityAccess {
    private static final Properties prop = DataProperties.getProperties();
    private static final String nvdKeyPath = prop.getProperty("nvd-api-key-path");
    private static final List<String> apiKeyHeader = Arrays.asList("apiKey", Utils.getAuthToken(prop.getProperty("nvd-api-key-path")));


    public static String getCveById(boolean local, String cveId) {
        int startIndex = 0;
        int stopIndex = 1;
        if (local) {
            // Get from Mongo
            NVDRequest nvdRequest = NVDRequestFactory.createNVDRequest(HTTPMethod.GET, Utils.NVD_BASE_URI, apiKeyHeader, startIndex, stopIndex);
            NVDResponse nvdResponse = nvdRequest.executeRequest();

            return new Gson().toJson(nvdResponse.getCveResponse().getVulnerabilities().get(0).getCve());

        } else {
            // Get from postgres
            System.out.println("Postgres not implemented yet");
        }

        return "";
    }

    public static String getCwe(boolean local, String cveId) {
        // TODO get just the CWE from the given CVE
        return "";
    }
}
