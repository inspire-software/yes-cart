package org.yes.cart.web.application.view;

import com.sun.faces.facelets.Facelet;
import com.sun.faces.facelets.FaceletFactory;
import com.sun.faces.facelets.compiler.Compiler;
import com.sun.faces.facelets.impl.DefaultFaceletFactory;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.web.util.ReflUtil;
import org.yes.cart.web.util.ShopUtil;

import javax.faces.view.facelets.FaceletCache;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/19/11
 * Time: 10:43 PM
 */
public class MultiStoreFaceletFactory extends DefaultFaceletFactory {

    private Map<String, URL> msRelativeLocations;
    private URL msBaseUrl;

    /**
     * Construct decorator to {@link DefaultFaceletFactory}
     * @param root  parent to decorate
     * @throws IOException  in case of error
     */
    public MultiStoreFaceletFactory(final FaceletFactory root) throws IOException {
        super(
                (Compiler) ReflUtil.getFieldValue(DefaultFaceletFactory.class, root, "compiler"),  ////((DefaultFaceletFactory) root).compiler;
                ((DefaultFaceletFactory) root).getResourceResolver(),
                -1,
                (FaceletCache) ReflUtil.getFieldValue(DefaultFaceletFactory.class, root, "cache") );
        msRelativeLocations = new ConcurrentHashMap<String, URL>();
        msBaseUrl = root.getResourceResolver().resolveUrl("/");
    }


    /*
    * (non-Javadoc)
    *
    * @see com.sun.facelets.FaceletFactory#getFacelet(java.lang.String)
    */
    public Facelet getFacelet(final String uri) throws IOException {
        return this.getFacelet(resolveURL(uri));
    }


    public Facelet getMetadataFacelet(final String uri) throws IOException {
        return this.getMetadataFacelet(resolveURL(uri));
    }


    private URL resolveURL(String uri) throws IOException {

        final Shop shop = ShopUtil.getShop();

        URL url = this.msRelativeLocations.get(uri + shop.getCode());
        if (url == null) {
            url = this.resolveURL(this.msBaseUrl, uri);
            if (url != null) {
                this.msRelativeLocations.put(uri + shop.getCode(), url);
            } else {
                throw new IOException("'" + uri + "' not found for shop with code '" + shop.getCode() + "'");
            }
        }
        return url;

    }


}
