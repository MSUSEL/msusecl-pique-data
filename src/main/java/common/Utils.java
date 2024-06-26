package common;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 *  Utility class for helper methods related to Data Access
 */
public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    // constants for use with data access
    public static final String NVD_BASE_URI = "https://services.nvd.nist.gov/rest/json/cves/2.0";
    public static final String GHSA_URI = "https://api.github.com/graphql";
    public static final int DEFAULT_START_INDEX = 0;
    public static final int NVD_MAX_PAGE_SIZE = 2000;
    public static final String DB_CONTEXT_LOCAL = "local";
    public static final String DB_CONTEXT_PERSISTENT = "persistent";
    public static final String MONGO_NVD_METADATA_ID = "nvd_metadata";

    // constants for log messages
    public static final String FAILED_TO_READ_FILE = "Failed to read file";
    public static final String MALFORMED_JSON = "Malformed JSON";

    /**
     * Headers need to be formatted into an array of Header Objects.
     * The constructor for BaseRequest passes headers as strings for ease of use.
     * This method resolves those strings to Header objects
     *
     * @param headerStrings List of header key,value pairs as strings
     * @return array of Header objects
     */
    public static Header[] resolveHeaders(List<String> headerStrings) {
        Header[] headers = new Header[0];
        int size = headerStrings.size() / 2;

        if (size % 2 == 0) {
            headers = new Header[size];
            for (int i = 0; i < headerStrings.size() - 1; i += 2) {
                headers[i / 2] = new BasicHeader(headerStrings.get(i), headerStrings.get(i + 1));
            }
        } else {
            // TODO throw custom Exception here instead?
            LOGGER.error("Incorrect format in headers list: Headers should always be key value pairs.");
        }

        return headers;
    }

    /**
     * Reads a given file paths contents into a string and returns the results.
     *
     * @param filePath - Path of file to be read
     * @return the text output of the file content.
     * @throws IOException
     */
    public static String readFileContent(Path filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines( filePath, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) {
            LOGGER.error(FAILED_TO_READ_FILE, e);
            throw e;
        }

        return contentBuilder.toString();
    }

    /**
     * Gets the actual GitHub token from the given filepath
     *
     * @param authTokenPath path to github token
     * @return the token as a String literal
     */
    public static String getAuthToken(String authTokenPath) {
        try {
            return readFileContent(Paths.get(authTokenPath.substring(1)));
        } catch (IOException e) {
            LOGGER.error(FAILED_TO_READ_FILE, e);
            throw new RuntimeException(e);
        }
    }



}
