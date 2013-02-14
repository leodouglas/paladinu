package system.controller.helpers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author leodouglas
 */
public class ViewData {

    private HashMap<String, Object> values = new HashMap<>();

    public Object get(String key) {
        return values.get(key);
    }

    public boolean clear() {
        values.clear();
        return true;
    }

    public ViewData add(String key, Object value) {
        values.put(key, value);
        return this;
    }
    
    public Set<Entry<String, Object>> list(){
        return values.entrySet();
    }

    public HashMap<String, Object> values() {
        return values;
    }

    
}
