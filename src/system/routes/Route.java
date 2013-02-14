package system.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import system.controller.ControllerAction;

/**
 *
 * @author leodouglas
 */
public class Route {

    private final List<String> methods;
    private final String path;
    private final ControllerAction controller;

    public Route(String methods, String path, String controller) {
        this.methods = new ArrayList<>();
        this.methods.add(methods);
        this.path = path;
        this.controller = new ControllerAction(controller);
    }

    public Route(String path, String controller) {
        this.methods = new ArrayList<>();
        this.methods.add("GET");
        this.methods.add("POST");
        this.path = path;
        this.controller = new ControllerAction(controller);
    }

    public Route(List<String> methods, String path, String controller) {
        this.methods = methods;
        this.path = path;
        this.controller = new ControllerAction(controller);
    }

    public String getPath() {
        return path;
    }

    public List<String> getMethods() {
        return methods;
    }

    public ControllerAction getController() {
        return controller;
    }

    @Override
    public String toString() {
        return "Route{" + "methods=" + methods + ", path=" + path + ", controller=" + controller + '}';
    }

    public boolean processPath(String path, Map<String, String> getparams) {
        HashMap<String, String> params = new HashMap<>();
        if (this.path.contains(":")) {
            if (path.startsWith(this.path.substring(0, this.path.indexOf(":")))) {
                String[] splitPathRoute = this.path.split("/");
                String[] splitPathTest = path.split("/");
                if (splitPathTest.length > 0) {
                    String checkPathRoute = new String();
                    String checkPathTest = new String();
                    for (int i = 0; i < (Math.min(splitPathRoute.length, splitPathTest.length)); i++) {
                        if (splitPathRoute[i].startsWith(":")) {
                            params.put(splitPathRoute[i].substring(1), splitPathTest[i]);
                            splitPathRoute[i] = "*";
                            splitPathTest[i] = "*";
                        }
                        checkPathRoute += splitPathRoute[i];
                        checkPathTest += splitPathTest[i];
                    }
                    if (checkPathRoute.equals(checkPathTest)) {
                        for (Entry<String, String> entry : params.entrySet()) {
                            getparams.put(entry.getKey(), entry.getValue());
                        }
                        return true;
                    }
                }
            }
        } else {
            if (path.equals(this.path)) {
                return true;
            }
        }
        return false;
    }
}
