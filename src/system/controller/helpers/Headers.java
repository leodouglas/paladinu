package system.controller.helpers;

import java.util.Map;

/**
 *
 * @author leodouglas
 */
public class Headers {
    
    private final Map<String, String> headers;

    public Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public String get(String key){
        return headers.get(key);
    }
    
    public boolean clear(){
        headers.clear();
        return true;
    }

}
