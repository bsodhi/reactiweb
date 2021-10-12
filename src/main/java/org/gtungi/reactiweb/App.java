package org.gtungi.reactiweb;

import java.io.IOException;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Main entry point into the application.
 */
public class App {

    public static void main(String[] args) throws IOException {
        String configFilePath = args.length > 0 ? args[0] : "config/settings.json";
        WebApp app = new WebApp(Vertx.vertx(), configFilePath);
        Promise<Void> startPromise = Promise.promise();
        app.start(startPromise, 8888);
    }
}
