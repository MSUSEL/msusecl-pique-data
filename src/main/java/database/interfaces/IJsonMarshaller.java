package database.interfaces;

public interface IJsonMarshaller<T> {
    T unmarshalJson(String json);
    String marshalJson(T obj);
}
