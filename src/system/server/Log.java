package system.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author leodouglas
 */
public class Log {

    public static final Logger logger = Logger.getLogger("log");

    public static void init() throws IOException {
        BasicConfigurator.configure();
        Appender fileAppender;
        String strdate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        fileAppender = new FileAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), "src/app/logs/" + strdate +".log");
        logger.addAppender(fileAppender);
    }
}
