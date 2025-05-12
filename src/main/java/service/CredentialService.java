/*
 * MIT License
 *
 * Copyright (c) 2024 Montana State University Software Engineering and Cybersecurity Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@Setter
public class CredentialService {
    private String driver;
    private String hostname;
    private String port;
    private String dbname;
    private Optional<String> username;
    private Optional<String> password;

    public CredentialService() {
        this.driver = System.getenv("PG_DRIVER");
        this.hostname = System.getenv("PG_HOSTNAME");
        this.port = System.getenv("PG_PORT");
        this.dbname = System.getenv("PG_DBNAME");
        this.username = Optional.ofNullable(System.getenv("PG_USERNAME"));
        this.password = Optional.ofNullable(System.getenv("PG_PASS"));
    }

    public CredentialService(String filepath) {
        if (filepath.contains("json")) {
            processJsonFile(filepath);
        } else {
            String message = "Please format your credential file using credentialsTemplate.json in the " +
                    "resources directory or set the appropriate environment variables on your system";
            System.out.println(message);
        }
    }

    private void processJsonFile(String filepath) {
        JsonObject creds = JsonParser.parseString(readJsonFile(Paths.get(filepath))).getAsJsonObject();

        this.driver= creds.get("driver").getAsString();
        this.hostname = creds.get("hostname").getAsString();
        this.port = creds.get("port").getAsString();
        this.dbname = creds.get("dbname").getAsString();
        this.username = getOptionalString(creds, "username");
        this.password= getOptionalString(creds, "password");
    }

    private Optional<String> getOptionalString(JsonObject json, String key) {
        return Optional.ofNullable(json.get(key))
                .filter(e -> !e.isJsonNull())
                .map(JsonElement::getAsString);
    }

    private String readJsonFile(Path filepath) {
        StringBuilder builder = new StringBuilder();

        try(Stream<String> stream = Files.lines(filepath, StandardCharsets.UTF_8)) {
            stream.forEach(s -> builder.append(s).append("\n"));

            return builder.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}