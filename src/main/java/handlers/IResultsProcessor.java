package handlers;

public interface IResultsProcessor<T, U> {
    T processResults(U);
}
