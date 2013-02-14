package system.controller;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import system.controller.helpers.MethodParam;

/**
 *
 * @author leodouglas
 */
public class HandleControllerAction {

    private final Method method;
    private final Controller controller;
    private final LinkedHashMap<String, MethodParam> methodParams = new LinkedHashMap<>();

    public HandleControllerAction(ControllerAction controllerAction, Map<String, String> headers, Map<String, String> paramsget, Map<String, String> paramspost) throws Exception {
        Class controllerRef = Class.forName("app.controller." + controllerAction.getClassName());
        controller = (Controller) controllerRef.newInstance();
        controllerRef.getSuperclass().getDeclaredMethod("init", Map.class, Map.class, Map.class).invoke(controller, headers, paramsget, paramspost);
        for (Entry<String, MethodParam> entry : controllerAction.getParams().entrySet()) {
            MethodParam methodParam = new MethodParam(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getValue());
            if (paramsget.get(methodParam.getName()) != null) {
                methodParam.setValue(paramsget.get(methodParam.getName()));
            }
            methodParams.put(entry.getKey(), methodParam);
        }
        Class[] params = new Class[methodParams.size()];
        int i = 0;
        for (Entry<String, MethodParam> methodParam : methodParams.entrySet()) {
            params[i] = methodParam.getValue().getType();
            i++;
        }
        method = controllerRef.getMethod(controllerAction.getMethod(), params);
    }

    public Result makeResponse() throws Exception {
        Object[] paramsValues = new Object[methodParams.size()];
        int i = 0;
        for (Entry<String, MethodParam> methodParam : methodParams.entrySet()) {
            paramsValues[i] = methodParam.getValue().getValue();
            i++;
        }

        Result response = (Result) method.invoke(controller, paramsValues);
        if (!response.getHeaders().containsKey("Content-Type")) {
            response.getHeaders().put("Content-Type", "text/html; charset=UTF-8");
        }
        response.setContent(response.getContent());
        return response;
    }
}
