package system.controller.scope;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import system.controller.helpers.Cookie;
import system.controller.helpers.Cookies;
import system.controller.helpers.Headers;
import system.controller.helpers.Inputs;

/**
 *
 * @author leodouglas
 */
public class Request {

    public Cookies cookies = new Cookies();
    public Inputs inputs;
    public Headers headers;
    private HashMap<String, String> newHeaders = new HashMap<>();

    public void make(Map<String, String> headers, Map<String, String> paramsget, Map<String, String> paramspost) {
        this.headers = new Headers(headers);
        for (Entry<String, String> header : headers.entrySet()) {
            switch (header.getKey()) {
                case "cookie":
                    String[] srtCookies = header.getValue().split(";");
                    for (String cookie : srtCookies) {
                        String[] cookieItems = cookie.split("=");
                        if (cookieItems.length > 1) {
                            cookies.add(cookieItems[0].trim(), cookieItems[1].trim());
                        } else {
                            cookies.add(cookieItems[0].trim(), null);
                        }

                    }
                    break;
            }
        }
        inputs = new Inputs(paramsget, paramspost);
    }

    public void addHeader(String key, String value) {
        newHeaders.put(key, value);
    }

    public Map<String, String> getNewHeaders() {
        HashMap<String, String> sendHeaders = (HashMap<String, String>) newHeaders.clone();
        for (Entry<String, Cookie> cookie : cookies.getCookies().entrySet()) {
            sendHeaders.put("Set-cookie", cookie.getValue().toString());
        }

        return sendHeaders;
    }
}
