package persistence.mongo;

import businessObjects.cve.Cve;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import handlers.CveMarshaller;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

import handlers.IJsonMarshaller;
import org.bson.conversions.Bson;
import persistence.IBulkDao;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public final class MongoBulkCveDao implements IBulkDao<Cve> {
    private final MongoClient client;
    private final MongoCollection<Document> vulnerabilities;
    private final IJsonMarshaller<Cve> cveMarshaller;
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoBulkCveDao.class);

    public MongoBulkCveDao(IDataSource<MongoClient> dataSource, IJsonMarshaller<Cve> cveMarshaller) {
        this.client = dataSource.getConnection();
        this.vulnerabilities = client.getDatabase("nvdMirror").getCollection("vulnerabilities");
        this.cveMarshaller = cveMarshaller;
    }

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
    public Cve[] fetchMany(String[] cveIds) {
        assertNotNull(client);
        List<String> idList = Arrays.asList(cveIds);
        Bson filter = Filters.in("id", idList);
        MongoIterable<Document> documents = vulnerabilities.find(filter);
        List<Cve> cves = new ArrayList<>();
        for(Document document : documents) {
            String json = document.toJson();
            cves.add(cveMarshaller.unmarshalJson(json));
        }
        return cves.toArray(new Cve[0]);
    }

    @Override
    public Cve[] fetchAll() {
        return new Cve[0];
    }
}
