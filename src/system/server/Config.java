package system.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author leodouglas
 */
public class Config {

    private static Properties prop = new Properties();

    static void init(File fileConfigs) throws IOException {
        prop.load(new FileInputStream(fileConfigs));
    }

    public static String getStrProperty(String key) {
        return prop.getProperty(key);
    }
    
    public static int getIntProperty(String key) {
        return Integer.parseInt(prop.getProperty(key, "0"));
    }
    
    public static  boolean getBoolProperty(String key) {
        return (prop.getProperty(key, "").isEmpty() ? false : Boolean.valueOf(prop.getProperty(key, "")));
    }
    
    public static boolean existsProperty(String key){
        return !prop.getProperty(key, "").isEmpty();
    }
}
