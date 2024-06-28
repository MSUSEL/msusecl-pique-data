package database.mongo;

import businessObjects.cveData.Cve;
import handlers.CveDetailsMarshaller;
import handlers.IJsonMarshaller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import database.IDao;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MongoCveDao implements IDao<Cve> {
    private final MongoClient client = MongoConnection.getInstance();
    private final MongoDatabase db = client.getDatabase("nvdMirror");
    private final MongoCollection<Document> vulnerabilities = db.getCollection("vulnerabilities");
    private final IJsonMarshaller<Cve> cveDetailsMarshaller = new CveDetailsMarshaller();
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoCveDao.class);

    @Override
    public Cve fetchById(String id) {
        Cve cve = new Cve();
        Document retrievedDoc = vulnerabilities.find(Filters.eq("id", id)).first();
        if (retrievedDoc != null) {
            cve = cveDetailsMarshaller.unmarshalJson(retrievedDoc.toJson());
        } else {
            LOGGER.info("Requested data is not in the collection");
        }

        return cve;
    }

    @Override
    public void insert(Cve cve) {
        String cveDetails = cveDetailsMarshaller.marshalJson(cve);
        Document filter = new Document("id", cve.getId());
        long documentCount = vulnerabilities.countDocuments(filter);
        System.out.println(documentCount);

        if (documentCount == 0) {
            vulnerabilities.insertOne(Document.parse(cveDetails));
        } else {
            // TODO apply update operation? or error out?
            LOGGER.info("Document already exists");
            System.out.println("Document already exists");
        }
    }

    @Override
    public void update(Cve cveDetails) {

    }

    @Override
    public void delete(Cve cveDetails) {

    }
}
