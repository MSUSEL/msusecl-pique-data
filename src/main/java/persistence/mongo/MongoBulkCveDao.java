package persistence.mongo;

import businessObjects.cve.Cve;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

import exceptions.DataAccessException;
import handlers.IJsonMarshaller;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.conversions.Bson;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.IDao;
import persistence.IDataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;

/**
 * CAUTION - This class should only be used to perform operations
 * on the entire NVD Mirror. It does not check for existing CVE
 * Documents in the collection and is thus likely to create duplicate entries.
 * Please use the MongoCveDao class for inserting into / updating
 * a subset of cves in the NVD.
 */
public final class MongoBulkCveDao implements IDao<Cve> {
    private final MongoCollection<Document> vulnerabilities;
    private final IJsonMarshaller cveMarshaller;
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoBulkCveDao.class);

    public MongoBulkCveDao(IDataSource<MongoClient> dataSource, IJsonMarshaller cveMarshaller) {
        MongoClient client = dataSource.getConnection();
        this.vulnerabilities = client.getDatabase("nvdMirror").getCollection("vulnerabilities");
        this.cveMarshaller = cveMarshaller;
    }

    @Override
    public void insert(List<Cve> cves) {
        List<WriteModel<Document>> bulkOperations = new ArrayList<>();

        try {
            for (Cve cve : cves) {
                bulkOperations.add(new InsertOneModel<>(Document.parse(cveMarshaller.marshalJson(cve))));
            }
            vulnerabilities.bulkWrite(bulkOperations);
        } catch (MongoBulkWriteException e) {
            LOGGER.error("Bulk Write to MongoDB failed.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(List<Cve> cves) throws DataAccessException {
        throw new NotImplementedException("Not implemented");
    }

    @Override
    public void delete(List<String> cveIds) throws DataAccessException {
        throw new NotImplementedException("Not implemented");
    }

    public List<Cve> fetch(List<String> cveIds) {
        Bson filter = Filters.in("id", cveIds);
        MongoIterable<Document> documents = vulnerabilities.find(filter);
        List<Cve> cves = new ArrayList<>();
        for(Document document : documents) {
            String json = document.toJson();
            cves.add((Cve) cveMarshaller.unmarshalJson(json));
        }
        return cves;
    }
}
