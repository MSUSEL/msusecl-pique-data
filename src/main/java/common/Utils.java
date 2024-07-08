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
public final class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

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

    /**
     * Buffered file reader mainly used to read in json files for unit testing
     * @param path is the path to the file
     * @return contents of the file as a String object
     */
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
     * Gets the actual token from the given filepath
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
