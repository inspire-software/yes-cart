/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.web.service.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.entity.PromotionModel;
import org.yes.cart.domain.ro.PromotionListRO;
import org.yes.cart.domain.ro.PromotionRO;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.service.rest.impl.RoMappingMixin;
import org.yes.cart.web.support.service.ProductServiceFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 06/10/2015
 * Time: 16:14
 */
@Controller
@Api(value = "Promotion", description = "Promotion controller", tags = "promotions")
public class PromotionController {

    @Autowired
    private ProductServiceFacade productServiceFacade;

    @Autowired
    private CartMixin cartMixin;
    @Autowired
    @Qualifier("restRoMappingMixin")
    private RoMappingMixin mappingMixin;

    /**
     * Interface: GET /promotions/{id}
     * <p>
     * <p>
     * Display promotion details.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>X-CW-TOKEN</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>ids</td><td>Promotion codes separated by comma (',')</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * 	[
     * 	     {
     * 	     	     "action" : "P",
     *	      	     "originalCode" : "SHOP10EURITEMQTY20P",
     *	      	     "activeTo" : null,
     *	      	     "code" : "SHOP10EURITEMQTY20P",
     *	      	     "couponCode" : null,
     *	      	     "context" : "20",
     *	      	     "type" : "I",
     *	      	     "description" : {},
     *	      	     "name" : {
     *	     	      	     "uk" : "Знижка 20% при покупці більше 20 одиниц",
     *	     	      	     "ru" : "Скидка 20% при покупке свыше 20 единиц",
     *	     	      	     "en" : "20% off when buying 20 or more items"
     *	      	     },
     *	      	     "activeFrom" : null
     * 	     }
     * ]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param promotions codes comma separated list
     * @param request request
     * @param response response
     *
     * @return promotions
     */
    @ApiOperation(value = "Display promotion details.")
    @RequestMapping(
            value = "/promotions/{codes}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody
    List<PromotionRO> viewProducts(final @ApiParam(value = "CSV of promotion codes") @PathVariable(value = "codes") String promotions,
                                   final HttpServletRequest request,
                                   final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewPromotionsInternal(promotions);

    }


    /**
     * Interface: GET /promotions/{id}
     * <p>
     * <p>
     * Display promotion details.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>X-CW-TOKEN</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>ids</td><td>Promotion codes separated by comma (',')</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * 	&lt;promotions&gt;
     * 	     &lt;promotion&gt;
     * 	     &lt;action&gt;P&lt;/action&gt;
     * 	     &lt;code&gt;SHOP10EURITEMQTY20P&lt;/code&gt;
     * 	     &lt;context&gt;20&lt;/context&gt;
     * 	     &lt;display-descriptions/&gt;
     * 	     &lt;display-names&gt;
     * 	      	     &lt;entry lang="ru"&gt;Скидка 20% при покупке свыше 20 единиц&lt;/entry&gt;
     * 	      	     &lt;entry lang="uk"&gt;Знижка 20% при покупці більше 20 одиниц&lt;/entry&gt;
     * 	      	     &lt;entry lang="en"&gt;20% off when buying 20 or more items&lt;/entry&gt;
     * 	     &lt;/display-names&gt;
     * 	     &lt;original-code&gt;SHOP10EURITEMQTY20P&lt;/original-code&gt;
     * 	     &lt;type&gt;I&lt;/type&gt;
     * 	     &lt;/promotion&gt;
     * 	&lt;/promotions&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param promotions codes comma separated list
     * @param request request
     * @param response response
     *
     * @return promotions
     */
    @ApiOperation(value = "Display promotion details.")
    @RequestMapping(
            value = "/promotions/{codes}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody
    PromotionListRO viewProductsXML(final @ApiParam(value = "CSV of promotion codes") @PathVariable(value = "codes") String promotions,
                                    final HttpServletRequest request,
                                    final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new PromotionListRO(viewPromotionsInternal(promotions));

    }



    private List<PromotionRO> viewPromotionsInternal(final String promotions) {

        final Map<String, PromotionModel> promoData = productServiceFacade.getPromotionModel(promotions);

        final List<PromotionRO> all = new ArrayList<>();

        for (final Map.Entry<String, PromotionModel> promo : promoData.entrySet()) {

            final PromotionRO promoRO = mappingMixin.map(promo.getValue(), PromotionRO.class, PromotionModel.class);
            promoRO.setOriginalCode(promo.getKey());
            all.add(promoRO);

        }

        return all;
    }


}
