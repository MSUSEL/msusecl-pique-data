package handlers;

public interface IJsonSerializer {
    <T> String serialize(T pojo);
    <T> T deserialize(String json, Class<T> clazz);
}
