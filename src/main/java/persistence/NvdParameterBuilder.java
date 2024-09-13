package persistence;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public final class NvdParameterBuilder {
    private final List<NameValuePair> params = new ArrayList<>();

    public NvdParameterBuilder addParameter(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
        return this;
    }

    public List<NameValuePair> build() {
        return params;
    }
}
