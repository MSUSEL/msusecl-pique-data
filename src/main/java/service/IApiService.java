package service;

import exceptions.ApiCallException;

public interface IApiService<T> {
    T handleGetEntity(String id) throws ApiCallException;
}
