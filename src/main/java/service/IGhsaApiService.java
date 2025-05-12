package service;

import businessObjects.ghsa.SecurityAdvisory;

import java.util.List;

public interface IGhsaApiService extends IApiService<SecurityAdvisory> {
    List<String> handleGetCweIds(String ghsaId);
}
