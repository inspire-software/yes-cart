package org.yes.cart.web.page.payment.callback;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.web.page.AbstractWebPage;

import javax.servlet.http.HttpServletRequest;

/**
 * Authorize.Net SIM method integration  payment call back page.
 * The redirect to this page in successful payment case only.
 * Authorize do not perform call back in case of failed payment
 * <p/>
 * Page responsible to change order status and redirect to home page.
 * Redirection performed javascript and meta tag.  Output of this page
 * aggregated/included into other page on
 * payment gateway side.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/12/11
 * Time: 10:47 AM
 */
public class AuthorizeNetSimPaymentOkPage extends AbstractWebPage {

    private static final long serialVersionUID = 20110323L;

    private static final String ORDER_GUID = "orderGuid";     // correspond to  AuthorizeNetSimPaymentGatewayImpl

    @SpringBean(name = ServiceSpringKeys.CUSTOMER_ORDER_SERVICE)
    private CustomerOrderService customerOrderService;

    @SpringBean(name = ServiceSpringKeys.SHOP_SERVICE)
    private ShopService shopService;

    @SpringBean(name = ServiceSpringKeys.SYSTEM_SERVICE)
    private SystemService systemService;


    private String shopUrl;

    private String orderNum = "orderNotFound";

    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public AuthorizeNetSimPaymentOkPage(final PageParameters params) {

        super(params);

        final String orderGuid = getPageParameters().get(ORDER_GUID).toString();

        final CustomerOrder order = customerOrderService.findByGuid(orderGuid);

        try {

            orderNum = order.getCartGuid();

            shopUrl = shopService.getShopByOrderGuid(orderGuid).getShopUrl().iterator().next().getUrl();

        } catch (Exception e) {

            e.printStackTrace(); //todo

            shopUrl = systemService.getDefaultShopURL();

        }


    }


    @Override
    protected void onBeforeRender() {

        final HttpServletRequest httpServletRequest = (HttpServletRequest)
                ((WebRequest) getRequest()).getContainerRequest();

        final String redirectTo = "http://" + shopUrl + httpServletRequest.getContextPath() + "/paymentresult?orderNum=" + orderNum;

        System.out.println("Redirect from authorize net page to " + redirectTo);


        //"http://shop.domain.com/context/paymentresult?orderNum=" + 123


        add(
                new Label("redirectJavaScript", "<!--\nlocation.replace(\"" + redirectTo + "\"); \n//-->").setEscapeModelStrings(false)
        );

        add(
                new Label("metaRefresh").add(
                        new AttributeAppender("content", new Model<String>("0; url=" + redirectTo), " ")
                )
        );

        add(
                new BookmarkablePageLink<ResultPage>(
                        "redirectLink",
                        ResultPage.class,
                        new PageParameters("orderNum=" + orderNum))
        );

        super.onBeforeRender();
    }
}
