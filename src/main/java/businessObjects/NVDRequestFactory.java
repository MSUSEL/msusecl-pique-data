package businessObjects;

import common.Constants;
import common.ParameterBuilder;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class NVDRequestFactory {

    // Constructors
    public static NVDRequest createNVDRequest(String httpMethod, String baseURI, Header[] headers, int startIndex, int resultsPerPage) {
        ParameterBuilder pb = new ParameterBuilder();
        List<NameValuePair> params = pb.addParameter(Constants.START_INDEX_PARAM_NAME, Integer.toString(startIndex))
                .addParameter(Constants.RESULTS_PER_PAGE_PARAM_NAME, Integer.toString(resultsPerPage))
                .build();

        return new NVDRequest(httpMethod, baseURI, headers, params);
    }

    public static NVDRequest createNVDRequest(String httpMethod, String baseURI, Header[] headers, int startIndex, int resultsPerPage, String lastModStartDate, String lastModEndDate) {
        return new NVDRequest(httpMethod, baseURI, headers, configureUpdateParams(startIndex, resultsPerPage, lastModStartDate, lastModEndDate));
    }

    // helper methods
    private static List<NameValuePair> configureBasicParams(int startIndex, int resultsPerPage) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("startIndex", Integer.toString(startIndex)));
        params.add(new BasicNameValuePair("resultsPerPage", Integer.toString(resultsPerPage)));

        return params;
    }

    private static List<NameValuePair> configureUpdateParams(int startIndex, int resultsPerPage, String lastModStartDate, String lastModEndDate) {
        List<NameValuePair> params = configureBasicParams(startIndex, resultsPerPage);
        params.add(new BasicNameValuePair("lastModStartDate", lastModStartDate));
        params.add(new BasicNameValuePair("lastModEndDate", lastModEndDate));

        return params;
    }
}
