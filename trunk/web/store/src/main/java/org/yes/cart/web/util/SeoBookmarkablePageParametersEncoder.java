package org.yes.cart.web.util;

import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6 aug 11
 * Time: 5:06 PM
 */
public class SeoBookmarkablePageParametersEncoder implements IPageParametersEncoder {

    private final CategoryService categoryService;
    private final ProductService productService;

    /**
     * Construct page parameter encoder.
     * @param categoryService to get category seo info
     * @param productService used to get product and sku seo info.
     */
    public SeoBookmarkablePageParametersEncoder(final CategoryService categoryService,
                                                final ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    /** {@inheritDoc} */
    public Url encodePageParameters(final PageParameters pageParameters) {
        final Url url = new Url();
        for (PageParameters.NamedPair pair : pageParameters.getAllNamed()) {
            url.getSegments().add(pair.getKey());
            url.getSegments().add(pair.getValue());
        }
        return url;
    }

    /** {@inheritDoc} */
    public PageParameters decodePageParameters(final Request request) {
        final PageParameters parameters = new PageParameters();
        String name = null;
        for (String segment : request.getUrl().getSegments()) {
            if (name == null) {
                name = segment;
            } else {
                parameters.set(name, segment);
                name = null;
            }
        }
        return parameters;
    }

}
