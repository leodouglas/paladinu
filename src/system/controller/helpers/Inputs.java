package system.controller.helpers;

import java.util.Map;

/**
 *
 * @author leodouglas
 */
public class Inputs {
    
    private final Map<String, String> gets;
    private final Map<String, String> posts;

    public Inputs(Map<String, String> gets, Map<String, String> posts) {
        this.gets = gets;
        this.posts = posts;
    }

    public String get(String key){
        return gets.get(key);
    }
    
    public String post(String key){
        return posts.get(key);
    }

    @Override
    public String toString() {
        return "Inputs{" + "gets=" + gets + ", posts=" + posts + '}';
    }
}
