package api.handlers;

public interface IJsonMarshaller<T> {
    T unmarshalJson(String json);
    String marshalJson(T obj);
}
