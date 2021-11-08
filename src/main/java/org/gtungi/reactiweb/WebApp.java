package org.gtungi.reactiweb;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.gtungi.Const;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
/**
 * This class contains the logic for setting up the web application.
 * It reads the supplied settings file to load configuration values
 * for various items such as max. allowed size of a request's body,
 * HTTP session timeout duration, and routes definition, etc.
 * 
 * Then, based on the settings supplied it initializes the necessary
 * routes and handlers.
 * 
 * The {@link #start(Promise, int)} method should be called on an
 * instance of this class to start the web application.
 * 
 * @author Balwinder Sodhi
 */
public class WebApp {
    private Vertx vertx;
    private Router router;
    private HashMap<String, Object> config;
    private HashMap<String, RouteConfig> routesConfig;
    private static Logger log = Logger.getLogger(WebApp.class.getName());

    /**
     * Creates an instance of the web application. It reads the settings file
     * and initializes the various settings.
     * 
     * @param vtx Vertx object to use for the web application.
     * @param configFilePath Path of settings JSON file.
     * @throws IOException
     */
    public WebApp(Vertx vtx, String configFilePath) throws IOException {
        this.vertx = vtx;
        this.router = Router.router(vertx);
        JsonReader jr = new JsonReader(new FileReader(configFilePath));
        String cwd = System.getProperty("user.dir");
        log.info("Current directory: " + cwd);
        Gson gs = new Gson();
        HashMap<String, Object> cfgType = new HashMap<>();
        this.config = gs.fromJson(jr, cfgType.getClass());

        jr = new JsonReader(new FileReader((String) this.config.get(Const.ROUTES)));
        Type rcType = new TypeToken<HashMap<String, RouteConfig>>() {
        }.getType();
        this.routesConfig = gs.fromJson(jr, rcType);
    }

    /**
     * Starts the web application in a Vertx HTTP server instance.
     * @param startPromise This promize object will complete when
     * the web application starts successfully, else the promise
     * will fail.
     * @param port Port number to listen at.
     */
    public void start(Promise<Void> startPromise, int port) {
        HttpServer server = vertx.createHttpServer();
        
        // Setup the body handler first
        int maxReqSzMb = Integer.parseInt("" + config.get(Const.MAX_REQ_SIZE_MB));
        long maxReqSz = 1000000 * maxReqSzMb;
        router.route().handler(BodyHandler.create().setBodyLimit(maxReqSz));
        log.info("Max. request body size set to: " + maxReqSzMb + " MB");
        
        // Setup the session handler
        setupSessionHandler();

        // Setup RBAC handler for all requests
        router.route().handler(new RbacHandler(routesConfig));

        // Static files go under directory named "webroot"
        String staticFilesPath = (String) config.get(Const.STATIC_FILES_PATH);
        log.info("Static content URI path: " + staticFilesPath);
        if (staticFilesPath != null) {
            String staticWebDir = (String) config.get(Const.STATIC_WEB_DIR);
            if (staticWebDir == null)
                staticWebDir = "webroot";
            log.info("Static content directory: " + staticWebDir);
            router.route(staticFilesPath).handler(StaticHandler.create().setWebRoot(staticWebDir));
        }

        // Setup all remaining handlers
        setupRouteHanlders();

        // Start the server
        server.requestHandler(router).listen(port, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port: " + port);
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    /**
     * We read the routes JSON file from the location mentioned in the
     * settings JSON file under 'routesConfigFile' key. For each route
     * path specified in the routes JSON file, we will then create a
     * suitable route objects.
     */
    private void setupRouteHanlders() {

        String baseUri = (String) config.get(Const.BASE_URI_PATH);
        log.info("Base URI path: " + baseUri);

        for (String path : routesConfig.keySet()) {
            RouteConfig rc = routesConfig.get(path);
            if (baseUri != null) {
                path = baseUri + path;
            }
            
            if (rc.isMultipart) {
                router.route(path).handler(ctx -> {
                    ctx.request().setExpectMultipart(true);
                    ctx.next();
                });
            }
            try {
                Class cls = Class.forName(rc.handlerClass);
                Constructor<ActionHandler> ctor = cls.getDeclaredConstructor();
                ActionHandler ah = ctor.newInstance();
                router.route(path).blockingHandler(ah);
            } catch (Throwable th) {
                throw new RuntimeException(th);
            }
        }

        // This is the application's default landing page
        String defRootPg = (String) config.get(Const.DEFAULT_ROOT_PAGE);
        log.info("Default page for root path is: "+defRootPg);
        if (defRootPg != null && !defRootPg.isEmpty()) {
            router.route("/").blockingHandler(ctx -> {
                ctx.reroute(HttpMethod.GET, defRootPg);
            });
        }

    }

    /** 
     * Setup the session handler.
    */
    private void setupSessionHandler() {
        // Reaper interval for expired sessions is 15min
        int sto = Integer.parseInt("" + config.get(Const.SESS_TIMEOUT));
        SessionStore ss = LocalSessionStore.create(vertx, "hpp.ams.sessionmap", 1000 * 60 * sto);
        // All requests will have a session
        router.route().handler(SessionHandler.create(ss));
        log.log(Level.FINE, "Initialized session handler. Session timeout " + sto + " min.");
    }

}
