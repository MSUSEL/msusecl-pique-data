package handlers;

import businessObjects.cve.CveEntity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NvdCveMarshaller implements IJsonMarshaller<CveEntity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdCveMarshaller.class);

    @Override
    public CveEntity unmarshalJson(String json) {
        try {
            return new Gson().fromJson(json, CveEntity.class);
        } catch (JsonSyntaxException e) {
            LOGGER.error("Incorrect JSON syntax - unable to parse to object", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String marshalJson(CveEntity cveEntity) {
        String json = new Gson().toJson(cveEntity);
        System.out.println(json);
        return new Gson().toJson(cveEntity);
    }
}
