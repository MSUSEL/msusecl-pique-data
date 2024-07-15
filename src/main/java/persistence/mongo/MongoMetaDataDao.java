package persistence.mongo;

// import com.mongodb.MongoCredential;
import businessObjects.cve.NvdMirrorMetaData;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;

import handlers.IJsonMarshaller;
import persistence.IDataSource;
import persistence.IMetaDataDao;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MongoMetaDataDao implements IMetaDataDao<NvdMirrorMetaData> {
    private final MongoCollection<Document> vulnerabilities;
    private final Document metadataFilter = new Document("_id", "nvd_metadata");
    private final IJsonMarshaller<NvdMirrorMetaData> marshaller;
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoMetaDataDao.class);

    public MongoMetaDataDao(IDataSource<MongoClient> dataSource, IJsonMarshaller<NvdMirrorMetaData> marshaller) {
        MongoClient client = dataSource.getConnection();
        MongoDatabase db = client.getDatabase("nvdMirror");
        this.vulnerabilities = db.getCollection("vulnerabilities");
        this.marshaller = marshaller;
    }

    @Override
    public NvdMirrorMetaData fetchMetaData() {
        return null;
    }

    @Override
    public void updateMetaData(NvdMirrorMetaData rawMetaData) {
        Document metadata = generateMetadata(rawMetaData);
        ReplaceOptions opts = new ReplaceOptions().upsert(true);
        UpdateResult updateResult = vulnerabilities.replaceOne(metadataFilter, metadata, opts);
        if (!updateResult.wasAcknowledged()) {
           LOGGER.error("Update was not acknowleged. Check DB connection. ");
        }
    }

    // TODO fix retrieve method here
    public NvdMirrorMetaData get(Document criteria) {
        NvdMirrorMetaData nvdMirrorMetadata = new NvdMirrorMetaData();
        Document result = vulnerabilities.find(Filters.eq(criteria)).first();
        assert result != null;

        nvdMirrorMetadata.setId(result.get("id").toString());
        nvdMirrorMetadata.setTotalResults(result.get("totalResults").toString());
        nvdMirrorMetadata.setFormat(result.get("format").toString());
        nvdMirrorMetadata.setVersion(result.get("version").toString());
        nvdMirrorMetadata.setTimestamp(result.get("timestamp").toString());

        return nvdMirrorMetadata;
    }

    private Document generateMetadata(NvdMirrorMetaData metaData) {
        return new Document("_id", "nvd_metadata")
                .append("totalResults", metaData.getTotalResults())
                .append("format", metaData.getFormat())
                .append("version", metaData.getVersion())
                .append("timestamp", metaData.getTimestamp());
    }

}
