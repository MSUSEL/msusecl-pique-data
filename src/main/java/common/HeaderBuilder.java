package common;


import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeaderBuilder {
    private final ArrayList<Header> headers = new ArrayList<>();

    public HeaderBuilder addHeader(String name, String value) {
        headers.add(new BasicHeader( name, value));
        return this;
    }

    public Header[] build() {
        return headers.toArray(new Header[headers.size()]);
    }
}
