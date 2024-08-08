package service;

import businessObjects.cve.Cve;
import businessObjects.cve.Metrics;
import businessObjects.cve.NvdMirrorMetaData;
import exceptions.DataAccessException;

import java.util.List;
import java.util.Map;

public interface INvdMirrorService {
    Cve handleGetCveById(String cveId) throws DataAccessException;
    List<Cve> handleGetCveById(List<String> cveIds) throws DataAccessException;
    List<String> handleGetNvdCweDescriptions(String cveId) throws DataAccessException;
    NvdMirrorMetaData handleGetCurrentMetaData() throws DataAccessException;
    void handleInsertSingleCve(Cve cve) throws DataAccessException;
    void handleDeleteSingleCve(String cveId) throws DataAccessException;
    Map<String, Metrics> handleGetCvssMetrics(List<String> cveIds) throws DataAccessException;
    String handleDumpNvdToJson();
}
