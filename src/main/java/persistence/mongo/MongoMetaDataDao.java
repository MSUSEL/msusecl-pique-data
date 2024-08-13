package persistence.mongo;

import businessObjects.cve.NvdMirrorMetaData;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;

import common.Constants;
import exceptions.DataAccessException;
import org.apache.commons.lang3.NotImplementedException;
import persistence.IDao;
import persistence.IDataSource;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// TODO finish adding functionality. Currently works for udpate
public final class MongoMetaDataDao implements IDao<NvdMirrorMetaData> {
    private final MongoCollection<Document> vulnerabilities;
    private final Document metadataFilter = new Document("_id", "nvd_metadata");
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoMetaDataDao.class);

    public MongoMetaDataDao(IDataSource<MongoClient> dataSource) {
        MongoClient client = dataSource.getConnection();
        MongoDatabase db = client.getDatabase("nvdMirror");
        this.vulnerabilities = db.getCollection("vulnerabilities");
    }

    @Override
    public List<NvdMirrorMetaData> fetch(List<String> ids) {
        throw new NotImplementedException(Constants.METHOD_NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public void insert(List<NvdMirrorMetaData> metadata) throws DataAccessException {
        throw new NotImplementedException(Constants.METHOD_NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public void update(List<NvdMirrorMetaData> rawMetaData) throws DataAccessException {
        String NOT_ACKNOWLEDGED_MESSAGE = "Update was not acknowleged. Check DB connection.";

        Document metadata = generateMetadata(rawMetaData.get(0));
        ReplaceOptions opts = new ReplaceOptions().upsert(true);
        UpdateResult updateResult = vulnerabilities.replaceOne(metadataFilter, metadata, opts);
        if (!updateResult.wasAcknowledged()) {
            LOGGER.error(NOT_ACKNOWLEDGED_MESSAGE);
           throw new DataAccessException(NOT_ACKNOWLEDGED_MESSAGE);
        }
    }

    @Override
    public void delete(List<String> t) throws DataAccessException {
        throw new NotImplementedException(Constants.METHOD_NOT_IMPLEMENTED_MESSAGE);
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
