package org.gtungi.reactiweb;

import io.vertx.ext.web.RoutingContext;

public class HomeHandler extends ActionHandler {

    @Override
    public void handle(RoutingContext ctx) {
        // TODO Auto-generated method stub
        log.info("Handling request in home.");
        ctx.end("From home I came!");
    }
    
}
