package common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 *  Utility class for helper methods related to Data Access
 */
public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);


    /**
     * Headers need to be formatted into an array of Header Objects.
     * The constructor for BaseRequest passes headers as strings for ease of use.
     * This method resolves those strings to Header objects
     *
     * @param headerStrings List of header key,value pairs as strings
     * @return array of Header objects
     */
//    public static Header[] resolveHeaders(List<String> headerStrings) {
//        Header[] headers = new Header[0];
//        int size = headerStrings.size();
//
//        if (size % 2 == 0) {
//            headers = new Header[size / 2];
//            for (int i = 0; i < headerStrings.size() - 1; i += 2) {
//                headers[i / 2] = new BasicHeader(headerStrings.get(i), headerStrings.get(i + 1));
//            }
//        } else {
//            // TODO throw custom Exception here instead?
//            LOGGER.error("Incorrect format in headers list: Headers should always be key value pairs.");
//        }
//
//        return headers;
//    }

    /**
     * Reads a given file paths contents into a string and returns the results.
     *
     * @param filePath - Path of file to be read
     * @return the text output of the file content.
     * @throws IOException
     */
    public static String readFileContent(Path filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(filePath, StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) {
            LOGGER.error(Constants.FAILED_TO_READ_FILE, e);
            throw e;
        }

        return contentBuilder.toString();
    }

    public static String readFileWithBufferedReader(String path) {
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the actual GitHub token from the given filepath
     *
     * @param authTokenPath path to github token
     * @return the token as a String literal
     */
    public static String getAuthToken(String authTokenPath) {
        try {
            return readFileContent(Paths.get(authTokenPath));
        } catch (IOException e) {
            LOGGER.error(Constants.FAILED_TO_READ_FILE, e);
            throw new RuntimeException(e);
        }
    }
}
