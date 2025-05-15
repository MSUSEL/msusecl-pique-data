import businessObjects.cve.Cve;
import com.google.gson.Gson;
import handlers.NvdSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TestUtils {

    protected String readCveJsonFile(Path path) {
        StringBuilder builder = new StringBuilder();

        try(Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.forEach(s -> builder.append(s).append("\n"));

            return builder.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Cve deserializeTestData(String json) {
        NvdSerializer nvdSerializer = new NvdSerializer(new Gson());

        return nvdSerializer.deserialize(json, Cve.class);
    }
}
