package persistence.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and manages a Mongo Connection
 */
public final class MongoConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoConnection.class);
    private static volatile MongoClient mongoClient = null;

    private MongoConnection() {}

    /**
     * Builds a MongoClient instance if one does not already exist.
     * This uses the MongoClient class to manage the connection pool.
     *
     * @return existing MongoClient or a new one if one does not already exist
     */
    public static MongoClient getInstance() {
        if (mongoClient == null) {
            synchronized (MongoClient.class) {
                if (mongoClient == null) {
                    mongoClient = MongoClients.create("mongodb://localhost:27017");
                }
            }
        }
        return mongoClient;
    }
}
