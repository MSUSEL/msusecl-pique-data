package handlers;

import businessObjects.cve.NvdMirrorMetaData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaDataMarshaller implements IJsonMarshaller<NvdMirrorMetaData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataMarshaller.class);
    private final Gson gson = new Gson();

    @Override
    public NvdMirrorMetaData unmarshalJson(String json) {
        try {
            return gson.fromJson(json, NvdMirrorMetaData.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error(Constants.MALFORMED_JSON_SYNTAX_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String marshalJson(NvdMirrorMetaData metaData) {
        return gson.toJson(metaData);
    }
}
