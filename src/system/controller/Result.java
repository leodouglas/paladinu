package system.controller;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author leodouglas
 */
public class Result {

    private String content = null;
    private InputStream inputStream = null;
    private File file = null;
    private Map<String, String> headers = new HashMap<>();

    public void setContent(String content) {
        this.content = content;
    }
    
    public void setContent(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setContent(File file) {
        this.file = file;
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

    public InputStream getInputStream() {
        return inputStream;
    }

    public File getFile() {
        return file;
    }


}
