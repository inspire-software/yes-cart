package org.yes.cart.service.async.utils;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * User: denispavlov
 * Date: 22/10/2015
 * Time: 18:29
 */
public class RunAsUserAuthentication extends AbstractAuthenticationToken {

    private final String username;
    private final String password;

    public RunAsUserAuthentication(final String username, final String password, final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getCredentials() {
        return password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPrincipal() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticated() {
        return true;
    }

}
