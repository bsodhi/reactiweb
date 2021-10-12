package org.gtungi.reactiweb;

import java.util.logging.Logger;

import org.gtungi.Const;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Contains the functionality which is common for most of the route handlers
 * that the web application will create. All your route handlers should
 * inherit from this class. However, it is not a requirement to do so.
 * 
 * For example, if you are implementing a typical MVC style web application
 * then your "controller" may become the "handler" here that should inherit 
 * from this class.
 * 
 * @author Balwinder Sodhi
 */
public abstract class ActionHandler implements Handler<RoutingContext> {

    protected Logger log = Logger.getLogger(getClass().getName());
    public ActionHandler() {
        super();
    }

    /**
     * Tests if the currently logged in user has the given role.
     * @param ctx
     * @param role
     * @return
     */
    public boolean isUserInRole(RoutingContext ctx, String role) {
        
        String roles = ctx.session().get(Const.SK_USER_ROLES);
        
        if (roles == null) {
            return false;
        }
        return roles.toLowerCase().contains(role.toLowerCase());
    }

    public String getCurrentLoginId(RoutingContext ctx) {
        return ctx.session().get(Const.SK_LOGIN_ID);
    }
    
}
