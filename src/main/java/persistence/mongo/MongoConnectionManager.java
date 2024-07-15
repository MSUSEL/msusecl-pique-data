package persistence.mongo;

import com.mongodb.client.MongoClient;
import persistence.IDataSource;

public class MongoConnectionManager implements IDataSource<MongoClient> {

    @Override
    public MongoClient getConnection() {
        return MongoConnection.getInstance();
    }
}
