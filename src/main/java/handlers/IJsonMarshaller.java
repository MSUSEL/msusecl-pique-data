package handlers;

public interface IJsonMarshaller {
    Object unmarshalJson(String json);
    String marshalJson(Object obj);
}
