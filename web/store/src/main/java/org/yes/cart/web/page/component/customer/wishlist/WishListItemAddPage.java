package org.yes.cart.web.page.component.customer.wishlist;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.ProductServiceFacade;
import org.yes.cart.web.util.WicketUtil;

/**
 * User: denispavlov
 * Date: 02/06/2014
 * Time: 15:49
 */
@RequireHttps
@AuthorizeInstantiation("USER")
public class WishListItemAddPage extends AbstractWebPage {


    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    protected ProductServiceFacade productServiceFacade;


    public WishListItemAddPage(final PageParameters params) {
        super(params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final PageParameters params = getPageParameters();

        final String skuCode = params.get(ShoppingCartCommand.CMD_ADDTOWISHLIST).toString(null);

        executeHttpPostedCommands();
        super.onBeforeRender();
        persistCartIfNecessary();

        final PageParameters targetParams = WicketUtil.getFilteredRequestParameters(params);

        if (StringUtils.isNotBlank(skuCode)) {

            final ProductSku sku = productServiceFacade.getProductSkuBySkuCode(skuCode);
            targetParams.set(WebParametersKeys.SKU_ID, sku.getSkuId());
            targetParams.set(WebParametersKeys.WISHLIST_ITEM_ADDED, skuCode);

        }
        throw new RestartResponseException(Application.get().getHomePage(), targetParams);

    }

}
