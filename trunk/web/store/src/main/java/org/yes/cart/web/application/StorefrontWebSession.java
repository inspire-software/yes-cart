package org.yes.cart.web.application;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/16/11
 * Time: 9:49 PM
 */
public class StorefrontWebSession  extends AuthenticatedWebSession {

    private  String username;

     /**
     * Construct.
     *
     * @param request
     *            The current request object
     */
    public StorefrontWebSession(Request request) {
        super(request);
        ((StorefrontApplication)getApplication()).getSpringComponentInjector().inject(this);   // allow to use @SpringBean
    }

   /** {@inheritDoc} */
    public boolean authenticate(final String username, final String password) {
        // todo Check username and password
        if (username.equals("wicket") && password.equals("wicket")) {
            this.username = username;
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    public Roles getRoles() {
        if (isSignedIn()) {
            // If the user is signed in, they have these roles
            return new Roles(Roles.USER);
        }
        return null;
    }

}
