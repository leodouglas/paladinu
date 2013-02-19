package system.controller.scope;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import system.controller.scope.utils.MongoSession;
import system.server.Config;

/**
 *
 * @author leodouglas
 */
public class Session {

    private HashMap<String, Object> values;
    private final String sessionId;
    private static String cookieSessionName;
    private MongoSession mongoSession;

    public Session(Request request) {
        if (request.cookies.get(cookieSessionName) == null) {
            request.cookies.add(cookieSessionName, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        }
        this.sessionId = request.cookies.get("SESSIONID").getValue();


        if (Config.getBoolProperty("databaseSession.enable")) {
            switch (Config.getStrProperty("databaseSession.type")) {
                case "mongo": {
                    try {
                        mongoSession = MongoSession.getIntance();
                    } catch (UnknownHostException ex) {
                    }
                }
            }
        }


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
                        DBObject findOne = mongoSession.getSession().findOne(new BasicDBObject("sessionid", this.sessionId));
                        if (findOne != null) {

                            ByteArrayInputStream bis = new ByteArrayInputStream((byte[]) findOne.get("values"));
                            try (ObjectInputStream oInputStream = new ObjectInputStream(bis)) {
                                values = (HashMap<String, Object>) oInputStream.readObject();
                            }
                        } else {
                            values = new HashMap<>();
                        }
                    } catch (IOException | ClassNotFoundException ex) {
                    }
                    break;
                }
            }
        }else{
            values = new HashMap<>();
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

                        DBCollection session = mongoSession.getSession();
                        DBObject findReg = session.findOne(new BasicDBObject("sessionid", this.sessionId));

                        if (findReg != null) {
                            findReg.put("values", serSession);
                            findReg.put("timestamp", new Date());
                            session.save(findReg);
                        } else {
                            Map<String, Object> reg = new HashMap<>();
                            reg.put("sessionid", this.sessionId);
                            reg.put("values", serSession);
                            reg.put("timestamp", new Date());
                            session.save(new BasicDBObject(reg));
                        }
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
                        DBCollection session = MongoSession.getIntance().getSession();

                        if (force) {
                            session.drop();
                        } else {
                            int maxlifetime = Config.getIntProperty("session.maxlifetime") > 0 ? Config.getIntProperty("session.maxlifetime") : 1440;
                            BasicDBObject query = new BasicDBObject();
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.SECOND, -(maxlifetime));
                            query.put("timestamp", new BasicDBObject("$lte", calendar.getTime()));
                            session.remove(query);
                        }

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
