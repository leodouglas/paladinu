package system.controller.scope.utils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import system.server.Config;

/**
 *
 * @author ldpadilha
 */
public class MongoSession {

    private static MongoSession mongoSession = null;

    public static MongoSession getIntance() throws UnknownHostException {
        if (mongoSession == null) {
            mongoSession = new MongoSession();
        }
        return mongoSession;
    }
    private MongoClient mongoClient;
    private DB db;
    private DBCollection sessionCollection;

    public MongoSession() throws UnknownHostException {
        mongoClient = new MongoClient(Config.getStrProperty("databaseSession.host"), Config.getIntProperty("databaseSession.port"));
        db = mongoClient.getDB(Config.getStrProperty("databaseSession.db"));
        sessionCollection = db.getCollection("sessions");
    }

    public DBCollection getSession() {
        return sessionCollection;
    }

    public void close() {
        mongoClient.close();
    }
}
