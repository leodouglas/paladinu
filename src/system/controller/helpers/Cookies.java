package system.controller.helpers;

import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author leodouglas
 */
public class Cookies {
    
    private final HashMap<String, Cookie> cookies = new HashMap<>();

    public Cookie add(String key, String value){
        return add(key, value, "", "/", null, false, false);
    }
    
    public Cookie add(String key, String value, String domain, String path, Date expire, boolean secure, boolean httpOnly){
        Cookie cookie = new Cookie();
        cookie.setKey(key);
        cookie.setValue(value);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setExpires(expire);
        cookie.setSecure(secure);
        cookie.setHttpOnly(httpOnly);
        return cookies.put(key, cookie);
    }
    
    public Cookie get(String key){
        return cookies.get(key);
    }
    
    public boolean clear(){
        cookies.clear();
        return true;
    }

    public HashMap<String, Cookie> getCookies() {
        return cookies;
    }

}
