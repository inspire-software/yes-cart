package org.yes.cart.web.application.view;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.web.util.ShopUtil;

import javax.faces.view.facelets.ResourceResolver;
import java.net.URL;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/17/11
 * Time: 10:17 PM
 */
public class FaceletsResourceResolver extends ResourceResolver {


    private ResourceResolver parent;

    public FaceletsResourceResolver(final ResourceResolver parent) {
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL resolveUrl(final String path) {
        if (!"/".equals(path)) {
            final Shop shop = ShopUtil.getShop();
            if (!path.startsWith(shop.getMarkupFolder())) {
                final URL url = parent.resolveUrl(shop.getMarkupFolder() + path);
                if (url != null) {
                    return url;
                }
            }
        }

        return parent.resolveUrl(path); //default location for web app resources, without any triks.
    }


}
