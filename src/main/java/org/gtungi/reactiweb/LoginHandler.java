package org.gtungi.reactiweb;

import java.util.logging.Logger;

import io.vertx.ext.web.RoutingContext;

/**
 * A simple login handler class. It should perform the authentication
 * of a user by retrieving relevant data from the {@link RoutingContext}.
 * 
 * @author Balwinder Sodhi
 */
public class LoginHandler extends ActionHandler {
    private static Logger log = Logger.getLogger(
        LoginHandler.class.getName());
    public LoginHandler() {
        super();
    }

    public void handle(RoutingContext ctx) {
        // TODO
        log.info("Authenticating someone.");
        ctx.response().end("Hi there!");
    }
}
