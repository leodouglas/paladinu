package system.routes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.vertx.java.core.http.HttpServerRequest;
import system.controller.ControllerAction;
import system.server.Log;

/**
 *
 * @author leodouglas
 */
public class Routing {

    private final List<Route> routes = new ArrayList<>();

    public Routing(File file) throws FileNotFoundException {
        try {
            for (String line : FileUtils.readLines(file)) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                ArrayList<String> items = new ArrayList<>();
                for (String s : line.split("\\s+(?![^\\(]*\\))")) {
                    if (!s.trim().isEmpty()) {
                        items.add(s.trim());
                    }
                }

                if (items.size() > 2) {
                    routes.add(new Route(items.get(0), items.get(1), items.get(2)));
                } else {
                    routes.add(new Route(items.get(0), items.get(1)));
                }
            }

        } catch (IOException ex) {
            Log.logger.fatal("File 'routes' not found!");
            throw new FileNotFoundException();
        }
    }

    public ControllerAction process(HttpServerRequest req, Map<String, String> getparams) {
        for (Route route : routes) {
            if (route.processPath(req.path, getparams)) {
                return route.getController();
            }

        }
        return null;
    }

}
