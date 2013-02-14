package system.controller;

import com.google.gson.Gson;
import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.IImplicitRenderArgProvider;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import system.controller.helpers.ViewData;
import system.controller.scope.Request;
import system.controller.scope.Session;

/**
 *
 * @author leodouglas
 */
public abstract class Controller {

    protected ViewData viewData = new ViewData();
    protected Request request;
    protected Session session;

    public Controller() {
    }

    public void init(Map headers, Map paramsget, Map paramspost) {
        request = new Request();
        request.make(headers, paramsget, paramspost);
        session = new Session(request);
    }

    public String processRenderView(String view) {
        Rythm.engine.implicitRenderArgProvider = new IImplicitRenderArgProvider() {
            @Override
            public Map<String, ?> getRenderArgDescriptions() {
                Map<String, Object> m = new HashMap<>();
                //m.put("flash", "com.paladinu.controller.scope.Flash");
                m.put("session", "system.controller.scope.Session");
                //m.put("application", "com.paladinu.controller.scope.Application");
                m.put("request", "system.controller.scope.Request");
                //m.put("params", "com.paladinu.controller.Params");
                //m.put("errors", "com.paladinu.controller.Error");
                for (Entry<String, Object> entry : viewData.list()) {
                    String type = entry.getValue().getClass().getCanonicalName();
                    String name = entry.getKey();
                    m.put(name, type);
                }

                return m;
            }

            @Override
            public void setRenderArgs(ITemplate template) {
                Map<String, Object> m = new HashMap<>();
                //m.put("flash", value);
                m.put("session", session);
                //m.put("application", value);
                m.put("request", request);
                //m.put("params", value);
                //m.put("errors", value);
                for (Entry<String, Object> entry : viewData.list()) {
                    String name = entry.getKey();
                    Object value = entry.getValue();
                    m.put(name, value);
                }
                template.setRenderArgs(m);
            }

            @Override
            public List<String> getImplicitImportStatements() {
                return Arrays.asList(new String[]{});//Arrays.asList(new String[]{"controllers.*", "models.*", "helpers.*"});
            }
        };
        return Rythm.render(view);
    }

    protected Result renderHtml(String content) {
        Result response = new Result();
        response.setContent(content);
        return response;
    }

    protected Result renderView(String view) {
        if (view.endsWith(".html")) {
            view = view.substring(0, view.lastIndexOf(".html"));
        }

        view = view.replaceAll("\\.", "/").concat(".html");

        String render = processRenderView(view);

        Result response = new Result();
        response.setContent(render);
        response.setHeaders(request.getNewHeaders());

        return response;
    }

    protected Result renderJSON() {
        String render = new Gson().toJson(viewData.values());

        Result response = new Result();
        response.setContent(render);
        request.addHeader("Content-Type", "application/json; charset=UTF-8");
        response.setHeaders(request.getNewHeaders());

        return response;
    }

    protected Result renderXML(String root) {
        if (root == null){
            root = "root";
        }
        XStream magicApi = new XStream(new StaxDriver());
        magicApi.alias(root, Map.class);
        magicApi.registerConverter(new Converter() {
            @Override
            public boolean canConvert(Class clazz) {
                return AbstractMap.class.isAssignableFrom(clazz);
            }

            @Override
            public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

                AbstractMap map = (AbstractMap) value;
                for (Object obj : map.entrySet()) {
                    Entry entry = (Entry) obj;
                    writer.startNode(entry.getKey().toString());
                    context.convertAnother(entry.getValue());
                    writer.endNode();
                }
            }

            @Override
            public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
                return null;
            }
        });

        String render = magicApi.toXML(viewData.values());

        Result response = new Result();
        response.setContent(render);
        request.addHeader("Content-Type", "application/xml; charset=UTF-8");
        response.setHeaders(request.getNewHeaders());

        return response;
    }
}