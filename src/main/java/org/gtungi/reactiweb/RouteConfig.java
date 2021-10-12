package org.gtungi.reactiweb;

import java.io.Serializable;
/**
 * A data holder object for route configuration. Instances of this class
 * are created during the initialization of web application.
 * 
 * @author Balwinder Sodhi
 */
public class RouteConfig implements Serializable {
    public boolean requireLogin;
    public String roles;
    public boolean isMultipart;
    public String handlerClass;
}
