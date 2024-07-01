package persistence.mongo;

import businessObjects.cve.Cve;
import handlers.CveMarshaller;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

import persistence.IBulkDao;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * CAUTION - This class should only be used to perform operations
 * on the entire NVD Mirror. It does not check for existing CVE
 * Documents in the collection and is thus likely to create duplicate entries.
 * Please use the MongoCveDao class for inserting into / updating
 * a subset of cves in the NVD.
 */
public class MongoBulkCveDao implements IBulkDao<Cve>{
    private final MongoClient client = MongoConnection.getInstance();
    private final MongoDatabase db = client.getDatabase("nvdMirror");
    private final MongoCollection<Document> vulnerabilities = db.getCollection("vulnerabilities");
    private final CveMarshaller cveMarshaller = new CveMarshaller();
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoBulkCveDao.class);

    @Override
    public void insertMany(List<Cve> cves) {
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
    public Cve[] fetchMany(String[] entities) {
        return new Cve[0];
    }

    @Override
    public Cve[] fetchAll() {
        return new Cve[0];
    }
}
