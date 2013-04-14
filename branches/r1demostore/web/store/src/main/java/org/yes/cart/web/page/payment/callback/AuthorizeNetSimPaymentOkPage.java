/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.page.payment.callback;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.ShopUrl;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.page.AbstractWebPage;
import org.yes.cart.web.support.util.HttpUtil;
import org.yes.cart.web.util.WicketUtil;

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

    private final String redirectTo;

    private final String orderGuid;


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public AuthorizeNetSimPaymentOkPage(final PageParameters params) {

        super(params);

        final HttpServletRequest httpServletRequest = WicketUtil.getHttpServletRequest();

        final Logger log = ShopCodeContext.getLog(this);
        if (log.isDebugEnabled()) {
            log.debug(HttpUtil.dumpRequest(httpServletRequest));
        }

        orderGuid = httpServletRequest.getParameter(ORDER_GUID);

        log.info("#### orderGuid = " + orderGuid);

        final CustomerOrder order = customerOrderService.findByGuid(orderGuid);

        if (order == null) {

            redirectTo = systemService.getDefaultShopURL();

        } else {

            redirectTo = "http://"
                    + getShopUrl(httpServletRequest)
                    + (httpServletRequest.getServerPort() != 80 ? ":" + httpServletRequest.getServerPort() : "")
                    + httpServletRequest.getContextPath()
                    + "/paymentresult?orderNum="
                    + orderGuid;

        }

    }

    /**
     * Get shop url. Shop may be registered for several urls, so need to get one, that related to call back request.
     *
     * @param httpServletRequest httpServletRequest
     * @return return shop url.
     */
    private String getShopUrl(final HttpServletRequest httpServletRequest) {

        for (ShopUrl url : shopService.getShopByOrderGuid(orderGuid).getShopUrl()) {
            final String urlCandidate = url.getUrl();
            if (urlCandidate.startsWith(httpServletRequest.getServerName())) {
                return urlCandidate;
            }
        }

        for (ShopUrl url : shopService.getShopByOrderGuid(orderGuid).getShopUrl()) {
            final String urlCandidate = url.getUrl();
            if (urlCandidate.contains(httpServletRequest.getServerName())) {
                return urlCandidate;
            }
        }

        return shopService.getShopByOrderGuid(orderGuid).getShopUrl().iterator().next().getUrl();
    }


    @Override
    protected void onBeforeRender() {

        ShopCodeContext.getLog(this).info("Redirect from authorize net page to {}", redirectTo);

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
                        new PageParameters("orderNum=" + orderGuid))
        );

        super.onBeforeRender();
    }
}
