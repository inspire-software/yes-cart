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

     /**
     * Construct.
     *
     * @param request
     *            The current request object
     */
    public StorefrontWebSession(Request request)
    {
        super(request);
    }

   /** {@inheritDoc} */
    public boolean authenticate(final String username, final String password)
    {
        // todo Check username and password
        return username.equals("wicket") && password.equals("wicket");
    }

    /** {@inheritDoc} */
    public Roles getRoles()
    {
        if (isSignedIn())
        {
            // If the user is signed in, they have these roles
            return new Roles(Roles.ADMIN);
        }
        return null;
    }

}
