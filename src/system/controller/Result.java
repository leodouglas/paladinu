package system.controller;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author leodouglas
 */
public class Result {

    private String content;
    private Map<String, String> headers = new HashMap<>();

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    
}
