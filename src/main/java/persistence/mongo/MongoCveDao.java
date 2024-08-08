package persistence.mongo;

import businessObjects.cve.Cve;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import common.Constants;
import exceptions.DataAccessException;
import handlers.IJsonMarshaller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.apache.commons.lang3.NotImplementedException;
import persistence.IDao;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class MongoCveDao implements IDao<Cve> {
    private final MongoCollection<Document> vulnerabilities;
    private final IJsonMarshaller marshaller;
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoCveDao.class);

    public MongoCveDao(IDataSource<MongoClient> dataSource, IJsonMarshaller marshaller) {
        MongoClient client = dataSource.getConnection();
        MongoDatabase db = client.getDatabase("nvdMirror");
        this.vulnerabilities = db.getCollection("vulnerabilities");
        this.marshaller = marshaller;
    }

    @Override
    public List<Cve> fetch(List<String> ids) {
        List<Cve> results;

        if (ids.size() > 1) {
            results = performBulkFetch(ids);
        } else {
            results = performFetch(ids.get(0));
        }

        return results;
    }

    // TODO deal with empty cve list?
    @Override
    public void insert(List<Cve> cves) throws DataAccessException {
        if (cves.size() == 1) {
            performSingleInsert(cves);
        } else {
            performBulkInsert(cves);
        }
    }

    @Override
    public void update(List<Cve> cveDetails) {
        throw new NotImplementedException(Constants.METHOD_NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public void delete(List<String> cveId) {
        throw new NotImplementedException(Constants.METHOD_NOT_IMPLEMENTED_MESSAGE);
    }

    private List<Cve> performBulkFetch(List<String> ids) {
        List<Cve> results = new ArrayList<>();

        for (String id : ids) {
            results.add(performFetch(id).get(0));
        }

        return results;
    }

    private List<Cve> performFetch(String id) {
        Cve cve = new Cve();
        Document retrievedDoc = vulnerabilities.find(Filters.eq("id", id)).first();
        if (retrievedDoc != null) {
            cve = (Cve) marshaller.unmarshalJson(retrievedDoc.toJson());
        } else {
            LOGGER.info("Requested data is not in the collection");
        }

        return Collections.singletonList(cve);
    }

    private void performSingleInsert(List<Cve> cves) throws DataAccessException {
        String cveDetails = marshaller.marshalJson(cves.get(0));
        Document filter = new Document("id", cves.get(0).getId());
        long documentCount = vulnerabilities.countDocuments(filter);
        System.out.println(documentCount);

        if(vulnerabilities.countDocuments(filter) == 0) {
            vulnerabilities.insertOne(Document.parse(cveDetails));
        } else {
            LOGGER.info("Document already exists");
            throw new DataAccessException("Document already exists");
        }
    }

    private void performBulkInsert(List<Cve> cves) throws DataAccessException {
        List<WriteModel<Document>> bulkDocuments = new ArrayList<>();
        try {
            for (Cve cve : cves) {
                bulkDocuments.add(new InsertOneModel<>(Document.parse(marshaller.marshalJson(cve))));
            }
            vulnerabilities.bulkWrite(bulkDocuments);
        } catch (MongoBulkWriteException e) {
            throw new DataAccessException(e);
        }
    }

}
