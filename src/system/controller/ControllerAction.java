package system.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import system.controller.helpers.MethodParam;

/**
 *
 * @author leodouglas
 */
public class ControllerAction {

    private String className;
    private String method;
    private LinkedHashMap<String, MethodParam> params = new LinkedHashMap<>();

    public ControllerAction(String action) {
        String[] st = action.split("\\.");
        this.className = st[0];
        String[] strMethod = st[1].split("\\(");
        this.method = strMethod[0];
        String strParams = strMethod[1].substring(0, strMethod[1].length() - 1);
        if (strParams.length() > 0) {
            for (String param : strParams.split("\\,")) {
                String[] splitParam = param.split("=");
                String[] declaration = splitParam[0].trim().split(":");
                String defaultValue = (splitParam.length > 1 ? splitParam[1].trim().replaceAll("\"", "") : null);
                if (declaration.length > 1) {
                    switch (declaration[1].toLowerCase()) {
                        case ("integer"):
                        case ("int"):
                            params.put(declaration[0], new MethodParam(declaration[0], int.class, defaultValue));
                            break;
                        default:
                            params.put(declaration[0], new MethodParam(declaration[0], String.class, defaultValue));
                    }
                } else {
                    params.put(declaration[0], new MethodParam(declaration[0], String.class, defaultValue));
                }
            }
        }
    }

    public ControllerAction(String className, String method) {
        this.className = className;
        this.method = method;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public MethodParam getParam(String name){
        return params.get(name);
    }

    public HashMap<String, MethodParam> getParams() {
        return params;
    }

}
