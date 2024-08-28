package common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class HelperFunctions {
    public static String readJsonFile(Path filepath) {
        StringBuilder builder = new StringBuilder();
        try(Stream<String> stream = Files.lines(filepath, StandardCharsets.UTF_8)) {
            stream.forEach(s -> builder.append(s).append("\n"));
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
