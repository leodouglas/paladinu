package system.server;

import com.greenlaw110.rythm.Rythm;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import system.controller.ControllerAction;
import system.controller.HandleControllerAction;
import system.controller.Result;
import system.controller.scope.Session;
import system.routes.Routing;

public class Server {

    private Routing routing;

    public Server() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        /* init Configs */
        File fileConfigs = new File(classLoader.getResource("app/config/config").getPath());
        Config.init(fileConfigs);
        /* init Session */
        Session.init();
        /* init Routes */
        File fileRoutes = new File(classLoader.getResource("app/config/routes").getPath());
        routing = new Routing(fileRoutes);
        /* init Logs */
        Log.init();
        /* init View engine */
        final Properties props = new Properties();
        props.put("rythm.root", classLoader.getResource("app/views").getPath());
        props.put("rythm.mode", "dev");
        Rythm.init(props);
    }

    public void start() {
        Vertx vertx = Vertx.newVertx();
        HttpServer httpServer = vertx.createHttpServer();

        httpServer.requestHandler(new Handler<HttpServerRequest>() {
            @Override
            public void handle(final HttpServerRequest req) {

                final Map<String, String> getparams = req.params();
                final Map<String, String> postparams = new HashMap<>();

                req.bodyHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {
                        String[] paramSplits = buffer.toString().split("&");

                        if (paramSplits.length > 1) {
                            for (String param : paramSplits) {
                                String[] valueSplits = param.split("=");
                                if (valueSplits.length > 1) {
                                    postparams.put(valueSplits[0], valueSplits[1]);
                                }
                            }
                        }

                        try {
                            ControllerAction controllerAction = routing.process(req, getparams);
                            if (controllerAction == null) {
                                //TODO: fazer tratamento
                                req.response.statusCode = 404;
                                req.response.end("<html><body><h1>Error</h1><h2>404 - File not found</h2></body></html>");
                            } else {
                                HandleControllerAction action = new HandleControllerAction(controllerAction, req.headers(), getparams, postparams);
                                Result response = action.makeResponse();

                                for (Entry<String, String> header : response.getHeaders().entrySet()) {
                                    req.response.headers().put(header.getKey(), header.getValue());
                                }
                                req.response.end(response.getContent());
                            }


                        } catch (Exception ex) {
                            Log.logger.error(ex.getMessage());
                        }
                    }
                });
            }
        });

        httpServer.listen(8080);
    }

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server();
        try {
            server.start();
            System.out.println("Paladinu server started - at " + new Date());
            synchronized (server) {
                server.wait();
            };
        } catch (Exception e) {
            System.err.println("Error in start Paladinu server - " + e.getMessage());
        }
    }
}
