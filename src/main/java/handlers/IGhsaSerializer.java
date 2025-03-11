package handlers;

public interface IGhsaSerializer<T> {
    T deserialize(String json);
    String serialize(T ghsaSubgraph);
}
