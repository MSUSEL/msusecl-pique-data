package database.mongo;

import api.cveData.Cve;
import api.handlers.CveDetailsMarshaller;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

import database.IDao;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CAUTION - This class should only be used to perform operations
 * on the entire NVD Mirror. It does not check for existing CVE
 * Documents in the collection and is thus likely to create duplicate entries.
 * Please use the CveDetailsDao class for inserting into / updating
 * a subset of cves in the NVD.
 */
public class NvdBulkOperationsDao implements IDao<List<Cve>>{
    private final MongoClient client = MongoConnection.getInstance();
    private final MongoDatabase db = client.getDatabase("nvdMirror");
    private final MongoCollection<Document> vulnerabilities = db.getCollection("vulnerabilities");
    private final CveDetailsMarshaller cveDetailsMarshaller = new CveDetailsMarshaller();
    private static final Logger LOGGER = LoggerFactory.getLogger(NvdBulkOperationsDao.class);

    @Override
    public List<Cve> getById(String id) {
        return Collections.emptyList();
    }

    @Override
    public void insert(List<Cve> cves) {
        List<WriteModel<Document>> bulkOperations = new ArrayList<>();

        try {
            for (Cve cve : cves) {
                bulkOperations.add(new InsertOneModel<>(Document.parse(cveDetailsMarshaller.marshalJson(cve))));
            }
            vulnerabilities.bulkWrite(bulkOperations);
        } catch (MongoBulkWriteException e) {
            LOGGER.error("Bulk Write to MongoDB failed.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(List<Cve> cveDetails) {

    }

    @Override
    public void delete(List<Cve> cveDetails) {

    }
}
