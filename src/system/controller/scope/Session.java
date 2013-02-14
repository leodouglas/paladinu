package system.controller.scope;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import system.server.Config;

/**
 *
 * @author leodouglas
 */
public class Session {

    private HashMap<String, Object> values;
    private final String sessionId;
    private static String cookieSessionName;

    public Session(Request request) {
        if (request.cookies.get(cookieSessionName) == null) {
            request.cookies.add(cookieSessionName, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        }
        this.sessionId = request.cookies.get("SESSIONID").getValue();
        loadSessionValues();
    }

    public Object add(String key, Object value) {
        Object added = values.put(key, value);

        saveSessionValues();
        return added;
    }

    public Object get(String key) {
        return values.get(key);
    }

    public boolean destroy() {
        values.clear();
        return true;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return "Session{" + values + '}';
    }

    private void loadSessionValues() {
        if (Config.getBoolProperty("databaseSession.enable")) {
            switch (Config.getStrProperty("databaseSession.type")) {
                case "mongo": {
                    try {
                        MongoClient mdb = new MongoClient(Config.getStrProperty("databaseSession.host"), Config.getIntProperty("databaseSession.port"));
                        DB db = mdb.getDB(Config.getStrProperty("databaseSession.db"));
                        DBCollection collection = db.getCollection("sessions");
                        DBObject findOne = collection.findOne(new BasicDBObject("sessionid", this.sessionId));
                        if (findOne != null) {

                            ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) findOne.get("values"));
                            try (ObjectInputStream oInputStream = new ObjectInputStream(bis)) {
                                values = (HashMap<String, Object>) oInputStream.readObject();
                            }
                        } else {
                            values = new HashMap<>();
                        }
                        mdb.close();
                    } catch (IOException | ClassNotFoundException ex) {
                    }
                    break;
                }
            }
        }
    }

    private void saveSessionValues() {
        if (Config.getBoolProperty("databaseSession.enable")) {
            switch (Config.getStrProperty("databaseSession.type")) {
                case "mongo": {
                    try {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] serSession;
                        try (ObjectOutputStream os = new ObjectOutputStream(bos)) {
                            os.writeObject(values);
                            serSession = bos.toByteArray();
                        }

                        MongoClient mdb = new MongoClient(Config.getStrProperty("databaseSession.host"), Config.getIntProperty("databaseSession.port"));
                        DB db = mdb.getDB(Config.getStrProperty("databaseSession.db"));
                        DBCollection collection = db.getCollection("sessions");
                        DBObject findReg = collection.findOne(new BasicDBObject("sessionid", this.sessionId));

                        if (findReg != null) {
                            findReg.put("values", serSession);
                            findReg.put("timestamp", new Date());
                            collection.save(findReg);
                        } else {
                            Map<String, Object> reg = new HashMap<>();
                            reg.put("sessionid", this.sessionId);
                            reg.put("values", serSession);
                            reg.put("timestamp", new Date());
                            collection.save(new BasicDBObject(reg));
                        }
                        mdb.close();
                    } catch (Exception ex) {
                    }
                }
                break;
            }
        }
    }

    public static void cleanUpExpiredSessions(boolean force) {
        if (Config.getBoolProperty("databaseSession.enable")) {
            switch (Config.getStrProperty("databaseSession.type")) {
                case "mongo": {
                    try {
                        MongoClient mdb = new MongoClient(Config.getStrProperty("databaseSession.host"), Config.getIntProperty("databaseSession.port"));
                        DB db = mdb.getDB(Config.getStrProperty("databaseSession.db"));
                        DBCollection collection = db.getCollection("sessions");

                        if (force) {
                            collection.drop();
                        } else {
                            int maxlifetime = Config.getIntProperty("session.maxlifetime") > 0 ? Config.getIntProperty("session.maxlifetime") : 1440;
                            BasicDBObject query = new BasicDBObject();
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.SECOND, -(maxlifetime)); 
                            query.put("timestamp", new BasicDBObject("$lte", calendar.getTime()));
                            collection.remove(query);
                        }

                        mdb.close();
                    } catch (Exception ex) {
                    }
                }
                break;
            }
        }
    }

    public static void init() {
        cookieSessionName = Config.getStrProperty("session.name").isEmpty() ? "SESSIONID" : Config.getStrProperty("session.name");

        Session.cleanUpExpiredSessions(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Session.cleanUpExpiredSessions(false);
            }
        }, 60000, 60000);
    }
}
