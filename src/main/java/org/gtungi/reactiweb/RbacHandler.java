package org.gtungi.reactiweb;

import java.util.Map;
import java.util.logging.Logger;

import org.gtungi.Const;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * A handler which performs the role based access control (RBAC) checks
 * for requests received on various routes.
 * @see {@link WebApp#start(io.vertx.core.Promise, int)}
 * 
 * @author Balwinder Sodhi
 */
public class RbacHandler implements Handler<RoutingContext> {

    private Map<String, RouteConfig> routesConfig;
    private static Logger log = Logger.getLogger(RbacHandler.class.getName());
    
    /**
     * 
     * @param routesConfig
     */
    public RbacHandler(Map<String, RouteConfig> routesConfig) {
        this.routesConfig = routesConfig;
    }

    @Override
    public void handle(RoutingContext ctx) {
        String path = ctx.request().path();
        String loginId = ctx.session().get(Const.SK_LOGIN_ID);
        log.fine("Checking RBAC for path ("+path
            +"). Logged in user: "+loginId);
        RouteConfig rc = routesConfig.get(path);
        if (rc != null) {
            ctx.put(Const.CK_ALLOWED_ROLES, rc.roles);
            ctx.put(Const.CK_REQUIRES_LOGIN, rc.requireLogin);

            // Check the user and roles in session
            if (loginId == null && rc.requireLogin) {
                ctx.end("Login required for accessing "+path);
            } else if (rc.roles != null) {
                if (rc.roles.trim().equals("*")) {
                    ctx.next();
                } else {
                    String currRoles = ctx.session().get(Const.SK_USER_ROLES);
                    if (currRoles == null || currRoles.isEmpty()) {
                        log.severe("User "+loginId+" attempted access to "+path);
                        ctx.end("User does not have a role assigned!");
                    } else {
                        boolean found = false;
                        for (String role : rc.roles.split(",")) {
                            if (currRoles.toLowerCase().contains(role.trim().toLowerCase())) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            ctx.next();
                        } else {
                            log.severe("User "+loginId+" attempted access to "+path);
                            ctx.end("You do not have required role to access "+path);
                        }
                    }
                }
            }
        } else {
            ctx.next();
        }
        
    }

}
