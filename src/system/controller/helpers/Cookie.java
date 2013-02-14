package system.controller.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import lombok.Data;

/**
 *
 * @author leodouglas
 */
@Data
public class Cookie {
    //key=value; Domain=.foo.com; Path=/; Expires=Wed, 13-Jan-2021 22:23:01 GMT; Secure; HttpOnly

    private String key;
    private String value;
    private String domain;
    private String path;
    private Date expires;
    private boolean secure;
    private boolean httpOnly;

    @Override
    public String toString() {
        String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
        SimpleDateFormat formatter = new SimpleDateFormat(PATTERN_RFC1123, Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return key + "=" + value
                + (domain != null && !domain.isEmpty() ? "; Domain=" + domain : "")
                + (path != null && !path.isEmpty()  ? "; Path=" + path : "")
                + (expires != null ? "; Expires=" + formatter.format(expires) : "")
                + (secure ? "; Secure" : "")
                + (httpOnly ? "; HttpOnly" : "");
    }
}
