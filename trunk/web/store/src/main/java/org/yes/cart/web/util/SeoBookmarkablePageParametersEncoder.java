package org.yes.cart.web.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.constants.WebParametersKeys;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6 aug 11
 * Time: 5:06 PM
 */
public class SeoBookmarkablePageParametersEncoder implements IPageParametersEncoder {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final boolean seoEnabled;

    /**
     * Construct page parameter encoder.
     *
     * @param categoryService to get category seo info
     * @param productService  used to get product and sku seo info.
     * @param seoEnabled      is seo url rewriting enabled.
     */
    public SeoBookmarkablePageParametersEncoder(final CategoryService categoryService,
                                                final ProductService productService,
                                                final boolean seoEnabled) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.seoEnabled = seoEnabled;
    }

    /**
     * {@inheritDoc}
     */
    public Url encodePageParameters(final PageParameters pageParameters) {
        final Url url = new Url();
        for (PageParameters.NamedPair pair : pageParameters.getAllNamed()) {
            url.getSegments().add(pair.getKey());
            url.getSegments().add(encodeId(pair.getKey(), pair.getValue()));
        }
        return url;
    }

    private String encodeId(final String idName, final String idValueToEncode) {
        if (seoEnabled) {
            if (WebParametersKeys.CATEGORY_ID.equals(idName)) {
                final Category category = categoryService.getById(NumberUtils.toLong(idValueToEncode));
                if (category != null) {
                    return encodeId(
                            idValueToEncode,
                            category.getSeo()
                    );
                }
            } else if (WebParametersKeys.PRODUCT_ID.equals(idName)) {
                final Product product = productService.getById(NumberUtils.toLong(idValueToEncode));
                if (product != null) {
                    return encodeId(
                            idValueToEncode,
                            product.getSeo()
                    );
                }

            } else if (WebParametersKeys.SKU_ID.equals(idName)) {
                final ProductSku productSku = productService.getSkuById(NumberUtils.toLong(idValueToEncode));
                if (productSku != null) {
                    return encodeId(
                            idValueToEncode,
                            productSku.getSeo()
                    );
                }

            }
        }
        return idValueToEncode;
    }

    private String encodeId(final String idValueToEncode, final Seo seo) {
        if (seo != null && StringUtils.isNotBlank(seo.getUri())) {
            return seo.getUri();
        } else {
            return idValueToEncode;
        }
    }

    /**
     * {@inheritDoc}
     */
    public PageParameters decodePageParameters(final Request request) {
        final PageParameters parameters = new PageParameters();
        String name = null;
        for (String segment : request.getUrl().getSegments()) {
            if (name == null) {
                name = segment;
            } else {
                parameters.set(name, decodeId(name, segment));
                name = null;
            }
        }
        return parameters;
    }


    private String decodeId(final String idName, final String idValueToDecode) {
        if (seoEnabled && !NumberUtils.isDigits(idValueToDecode)) {
            if (WebParametersKeys.CATEGORY_ID.equals(idName)) {
                final Long id = categoryService.getCategoryIdBySeoUri(idValueToDecode);
                if (id != null) {
                    return id.toString();
                }
            } else if (WebParametersKeys.PRODUCT_ID.equals(idName)) {
                final Long id = productService.getProductIdBySeoUri(idValueToDecode);
                if (id != null) {
                    return id.toString();
                }
            } else if (WebParametersKeys.SKU_ID.equals(idName)) {
                final Long id = productService.getProductSkuIdBySeoUri(idValueToDecode);
                if (id != null) {
                    return id.toString();
                }
            }

        }
        return idValueToDecode;
    }


}
