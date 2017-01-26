/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.ro.*;
import org.yes.cart.service.domain.BrandService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.service.rest.impl.BookmarkMixin;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.service.rest.impl.RoMappingMixin;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.service.CurrencySymbolService;
import org.yes.cart.web.support.service.ProductServiceFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/03/2015
 * Time: 10:43
 */
@Controller
public class ProductController {

    @Autowired
    private CentralViewResolver centralViewResolver;
    @Autowired
    private BrandService brandService;
    @Autowired
    private ProductServiceFacade productServiceFacade;
    @Autowired
    private CurrencySymbolService currencySymbolService;

    @Autowired
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


    @Autowired
    private CartMixin cartMixin;
    @Autowired
    @Qualifier("restRoMappingMixin")
    private RoMappingMixin mappingMixin;
    @Autowired
    private BookmarkMixin bookmarkMixin;



    private ProductRO viewProductInternal(final String product, final boolean recordViewed) {

        final long productId = bookmarkMixin.resolveProductId(product);

        final Product productEntity = productServiceFacade.getProductById(productId);

        if (productEntity != null) {

            final ShoppingCart cart = cartMixin.getCurrentCart();
            final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(cart.getCurrencyCode());


            final ProductRO prodRO = mappingMixin.map(productEntity, ProductRO.class, Product.class);

            final Pair<String, String> templates = resolveTemplate(prodRO);
            if (templates != null) {
                prodRO.setUitemplate(templates.getFirst());
                prodRO.setUitemplateFallback(templates.getSecond());
            }

            // Brand is lazy so need to retrieve name manually
            final Brand brand = brandService.findById(prodRO.getBrandId());
            prodRO.setBrandName(brand.getName());

            final ProductAvailabilityModel skuPam = productServiceFacade.getProductAvailability(productEntity, cart.getShoppingContext().getCustomerShopId());

            final ProductAvailabilityModelRO amRo = mappingMixin.map(skuPam, ProductAvailabilityModelRO.class, ProductAvailabilityModel.class);
            prodRO.setProductAvailabilityModel(amRo);

            final ProductPriceModel price = productServiceFacade.getSkuPrice(
                    cart,
                    productEntity.getProductId(),
                    null,
                    BigDecimal.ONE
            );

            final SkuPriceRO priceRo = mappingMixin.map(price, SkuPriceRO.class, ProductPriceModel.class);
            priceRo.setSymbol(symbol.getFirst());
            priceRo.setSymbolPosition(symbol.getSecond() != null && symbol.getSecond() ? "after" : "before");

            prodRO.setPrice(priceRo);

            final List<ProductSkuRO> skuRo = new ArrayList<ProductSkuRO>();
            if (CollectionUtils.isNotEmpty(productEntity.getSku())) {
                for (final ProductSku sku : productEntity.getSku()) {
                    final ProductSkuRO skuRoItem = viewSkuInternal(sku, cart, symbol);
                    if (skuRoItem != null) {
                        skuRo.add(skuRoItem);
                    }
                }
            }
            prodRO.setSkus(skuRo);

            if (recordViewed) {
                executeViewProductCommand(productEntity);
            }

            return prodRO;

        }

        return null;
    }


    private Pair<String, String> resolveTemplate(final ProductRO prodRO) {
        final Map params = new HashMap();
        params.put(WebParametersKeys.PRODUCT_ID, String.valueOf(prodRO.getProductId()));
        return centralViewResolver.resolveMainPanelRendererLabel(params);
    }



    /**
     * Interface: GET /product/{id}
     * <p>
     * <p>
     * Display full product details.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>id</td><td>SEO URI or product PK</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * 	{
     * 	  "displayNames" : {
     * 	    "uk" : "Mini Mouse M187",
     * 	    "ru" : "Mini Mouse M187",
     * 	    "en" : "Mini Mouse M187"
     * 	  },
     * 	  "maxOrderQuantity" : null,
     * 	  "productTypeId" : 5,
     * 	  "minOrderQuantity" : null,
     * 	  "availability" : 1,
     * 	  "code" : "910-002",
     * 	  "title" : null,
     * 	  "attributes" : [
     * 	    {
     * 	      "attrvalueId" : 891,
     * 	      "productId" : 297,
     * 	      "val" : "Logitech Wireless Mini Mouse M187...",
     * 	      "displayVals" : null,
     * 	      "attributeName" : "Опис Продукту (uk)",
     * 	      "attributeId" : 11022,
     * 	      "attributeDisplayNames" : null
     * 	    },
     * 	    {
     * 	      "attrvalueId" : 19865,
     * 	      "productId" : 297,
     * 	      "val" : "Y",
     * 	      "displayVals" : {
     * 	        "uk" : "Да",
     * 	        "ru" : "Да",
     * 	        "en" : "Y"
     * 	      },
     * 	      "attributeName" : "Receiver included",
     * 	      "attributeId" : 1423,
     * 	      "attributeDisplayNames" : {
     * 	        "uk" : "Ресівер, що поставляється",
     * 	        "ru" : "Поставляемый ресивер",
     * 	        "en" : "Receiver included"
     * 	      }
     * 	    },
     * 	...
     * 	  ],
     * 	  "displayMetadescriptions" : {},
     * 	  "displayTitles" : {},
     * 	  "productId" : 297,
     * 	  "name" : "Mini Mouse M187",
     * 	  "stepOrderQuantity" : null,
     * 	  "tag" : "multisku",
     * 	  "brandName" : "Logitech",
     * 	  "brandId" : 5,
     * 	  "availablefrom" : null,
     * 	  "uri" : null,
     * 	  "availableto" : null,
     * 	  "productTypeName" : "Mice",
     * 	  "metakeywords" : null,
     * 	  "manufacturerCode" : "M001",
     * 	  "productAvailabilityModel" : {
     * 	    "firstAvailableSkuCode" : "910-002-741",
     * 	    "available" : true,
     * 	    "defaultSkuCode" : "910-002-741",
     * 	    "inStock" : true,
     * 	    "skuCodes" : [
     * 	      "910-002-741",
     * 	      "910-002-742",
     * 	      "910-002-743",
     * 	      "910-002-744"
     * 	    ],
     * 	    "perpetual" : false,
     * 	    "availableToSellQuantity" : {
     * 	      "910-002-742" : 300,
     * 	      "910-002-744" : 300,
     * 	      "910-002-741" : 300,
     * 	      "910-002-743" : 300
     * 	    }
     * 	  },
     * 	  "skus" : [
     * 	    {
     * 	      "metakeywords" : null,
     * 	      "productId" : 297,
     * 	      "description" : "",
     * 	      "metadescription" : null,
     * 	      "uri" : null,
     * 	      "price" : {
     * 	        "symbol" : "€",
     * 	        "quantity" : 1,
     * 	        "regularPrice" : 387.56,
     * 	        "salePrice" : null,
     * 	        "discount" : null,
     * 	        "currency" : "EUR",
     * 	        "symbolPosition" : "before"
     * 	      },
     * 	      "attributes" : [
     * 	        {
     * 	          "attrvalueId" : 948,
     * 	          "val" : "White",
     * 	          "displayVals" : {
     * 	            "uk" : "Білий",
     * 	            "ru" : "Белый",
     * 	            "en" : "White"
     * 	          },
     * 	          "attributeName" : "Color of product",
     * 	          "attributeId" : 532,
     * 	          "attributeDisplayNames" : {
     * 	            "uk" : "Колір продукту",
     * 	            "ru" : "Цвет товара",
     * 	            "en" : "Color of product"
     * 	          },
     * 	          "skuId" : 308
     * 	        },
     * 			...
     * 	      ],
     * 	      "title" : null,
     * 	      "manufacturerCode" : null,
     * 	      "code" : "910-002-744",
     * 	      "skuId" : 308,
     * 	      "rank" : 0,
     * 	      "displayTitles" : null,
     * 	      "displayNames" : {
     * 	        "uk" : "M187 Білий",
     * 	        "ru" : "M187 Белый",
     * 	        "en" : "M187 White"
     * 	      },
     * 	      "productAvailabilityModel" : {
     * 	        "firstAvailableSkuCode" : "910-002-744",
     * 	        "available" : true,
     * 	        "defaultSkuCode" : "910-002-744",
     * 	        "inStock" : true,
     * 	        "skuCodes" : [
     * 	          "910-002-744"
     * 	        ],
     * 	        "perpetual" : false,
     * 	        "availableToSellQuantity" : {
     * 	          "910-002-744" : 300
     * 	        }
     * 	      },
     * 	      "barCode" : "",
     * 	      "displayMetakeywords" : null,
     * 	      "name" : "M187 White",
     * 	      "displayMetadescriptions" : null
     * 	    },
     * 		...
     * 	  ],
     * 	  "price" : {
     * 	    "symbol" : "€",
     * 	    "quantity" : 1,
     * 	    "regularPrice" : 387.56,
     * 	    "salePrice" : null,
     * 	    "discount" : null,
     * 	    "currency" : "EUR",
     * 	    "symbolPosition" : "before"
     * 	  },
     * 	  "featured" : false,
     * 	  "displayMetakeywords" : {},
     * 	  "metadescription" : null,
     * 	  "description" : "Logitech Wireless Mini ..."
     * 	}
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * 	&lt;product&gt;
     * 	    &lt;attribute-values&gt;
     * 	        &lt;attribute-value attribute-id="11022" attrvalue-id="891" product-id="297"&gt;
     * 	            &lt;attribute-name&gt;Опис Продукту (uk)&lt;/attribute-name&gt;
     * 	            &lt;val&gt;Logitech Wireless Mini Mouse M187...&lt;/val&gt;
     * 	        &lt;/attribute-value&gt;
     * 	        &lt;attribute-value attribute-id="1423" attrvalue-id="19865" product-id="297"&gt;
     * 	            &lt;attribute-display-names&gt;
     * 	                &lt;entry lang="uk"&gt;Ресівер, що поставляється&lt;/entry&gt;
     * 	                &lt;entry lang="en"&gt;Receiver included&lt;/entry&gt;
     * 	                &lt;entry lang="ru"&gt;Поставляемый ресивер&lt;/entry&gt;
     * 	            &lt;/attribute-display-names&gt;
     * 	            &lt;attribute-name&gt;Receiver included&lt;/attribute-name&gt;
     * 	            &lt;display-vals&gt;
     * 	                &lt;entry lang="uk"&gt;Да&lt;/entry&gt;
     * 	                &lt;entry lang="en"&gt;Y&lt;/entry&gt;
     * 	                &lt;entry lang="ru"&gt;Да&lt;/entry&gt;
     * 	            &lt;/display-vals&gt;
     * 	            &lt;val&gt;Y&lt;/val&gt;
     * 	        &lt;/attribute-value&gt;
     * 	        &lt;attribute-value attribute-id="11008" attrvalue-id="20509" product-id="297"&gt;
     * 	            &lt;attribute-name&gt;Product default image&lt;/attribute-name&gt;
     * 	            &lt;val&gt;Logitech-M187_910-002-744_a.jpg&lt;/val&gt;
     * 	        &lt;/attribute-value&gt;
     * 	        ...
     * 	    &lt;/attribute-values&gt;
     * 	    &lt;availability&gt;1&lt;/availability&gt;
     * 	    &lt;brand-id&gt;5&lt;/brand-id&gt;
     * 	    &lt;brand-name&gt;Logitech&lt;/brand-name&gt;
     * 	    &lt;code&gt;910-002&lt;/code&gt;
     * 	    &lt;description&gt;Logitech Wireless Mini Mouse M187...&lt;/description&gt;
     * 	    &lt;display-metadescription/&gt;
     * 	    &lt;display-metakeywords/&gt;
     * 	    &lt;display-names&gt;
     * 	        &lt;entry lang="uk"&gt;Mini Mouse M187&lt;/entry&gt;
     * 	        &lt;entry lang="en"&gt;Mini Mouse M187&lt;/entry&gt;
     * 	        &lt;entry lang="ru"&gt;Mini Mouse M187&lt;/entry&gt;
     * 	    &lt;/display-names&gt;
     * 	    &lt;display-titles/&gt;
     * 	    &lt;featured&gt;false&lt;/featured&gt;
     * 	    &lt;manufacturer-code&gt;M001&lt;/manufacturer-code&gt;
     * 	    &lt;name&gt;Mini Mouse M187&lt;/name&gt;
     * 	    &lt;price&gt;
     * 	        &lt;currency&gt;EUR&lt;/currency&gt;
     * 	        &lt;quantity&gt;1.00&lt;/quantity&gt;
     * 	        &lt;regular-price&gt;387.56&lt;/regular-price&gt;
     * 	        &lt;symbol&gt;€&lt;/symbol&gt;
     * 	        &lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 	    &lt;/price&gt;
     * 	    &lt;product-availability&gt;
     * 	        &lt;available&gt;true&lt;/available&gt;
     * 	        &lt;ats-quantity&gt;
     * 	            &lt;entry sku="910-002-742"&gt;300.00&lt;/entry&gt;
     * 	            &lt;entry sku="910-002-741"&gt;300.00&lt;/entry&gt;
     * 	            &lt;entry sku="910-002-744"&gt;300.00&lt;/entry&gt;
     * 	            &lt;entry sku="910-002-743"&gt;300.00&lt;/entry&gt;
     * 	        &lt;/ats-quantity&gt;
     * 	        &lt;default-sku&gt;910-002-741&lt;/default-sku&gt;
     * 	        &lt;first-available-sku&gt;910-002-741&lt;/first-available-sku&gt;
     * 	        &lt;in-stock&gt;true&lt;/in-stock&gt;
     * 	        &lt;perpetual&gt;false&lt;/perpetual&gt;
     * 	        &lt;sku-codes&gt;910-002-741&lt;/sku-codes&gt;
     * 	        &lt;sku-codes&gt;910-002-742&lt;/sku-codes&gt;
     * 	        &lt;sku-codes&gt;910-002-743&lt;/sku-codes&gt;
     * 	        &lt;sku-codes&gt;910-002-744&lt;/sku-codes&gt;
     * 	    &lt;/product-availability&gt;
     * 	    &lt;product-id&gt;297&lt;/product-id&gt;
     * 	    &lt;product-type-id&gt;5&lt;/product-type-id&gt;
     * 	    &lt;product-type-name&gt;Mice&lt;/product-type-name&gt;
     * 	    &lt;skus&gt;
     * 	        &lt;sku&gt;
     * 	            &lt;attribute-values&gt;
     * 	                &lt;attribute-value attribute-id="532" attrvalue-id="948" sku-id="308"&gt;
     * 	                    &lt;attribute-display-names&gt;
     * 	                        &lt;entry lang="uk"&gt;Колір продукту&lt;/entry&gt;
     * 	                        &lt;entry lang="en"&gt;Color of product&lt;/entry&gt;
     * 	                        &lt;entry lang="ru"&gt;Цвет товара&lt;/entry&gt;
     * 	                    &lt;/attribute-display-names&gt;
     * 	                    &lt;attribute-name&gt;Color of product&lt;/attribute-name&gt;
     * 	                    &lt;display-vals&gt;
     * 	                        &lt;entry lang="uk"&gt;Білий&lt;/entry&gt;
     * 	                        &lt;entry lang="en"&gt;White&lt;/entry&gt;
     * 	                        &lt;entry lang="ru"&gt;Белый&lt;/entry&gt;
     * 	                    &lt;/display-vals&gt;
     * 	                    &lt;val&gt;White&lt;/val&gt;
     * 	                &lt;/attribute-value&gt;
     * 	                ...
     * 	            &lt;/attribute-values&gt;
     * 	            &lt;barcode&gt;&lt;/barcode&gt;
     * 	            &lt;code&gt;910-002-744&lt;/code&gt;
     * 	            &lt;description&gt;&lt;/description&gt;
     * 	            &lt;display-names&gt;
     * 	                &lt;entry lang="uk"&gt;M187 Білий&lt;/entry&gt;
     * 	                &lt;entry lang="en"&gt;M187 White&lt;/entry&gt;
     * 	                &lt;entry lang="ru"&gt;M187 Белый&lt;/entry&gt;
     * 	            &lt;/display-names&gt;
     * 	            &lt;name&gt;M187 White&lt;/name&gt;
     * 	            &lt;price&gt;
     * 	                &lt;currency&gt;EUR&lt;/currency&gt;
     * 	                &lt;quantity&gt;1.00&lt;/quantity&gt;
     * 	                &lt;regular-price&gt;387.56&lt;/regular-price&gt;
     * 	                &lt;symbol&gt;€&lt;/symbol&gt;
     * 	                &lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 	            &lt;/price&gt;
     * 	            &lt;product-availability&gt;
     * 	                &lt;available&gt;true&lt;/available&gt;
     * 	                &lt;ats-quantity&gt;
     * 	                    &lt;entry sku="910-002-744"&gt;300.00&lt;/entry&gt;
     * 	                &lt;/ats-quantity&gt;
     * 	                &lt;default-sku&gt;910-002-744&lt;/default-sku&gt;
     * 	                &lt;first-available-sku&gt;910-002-744&lt;/first-available-sku&gt;
     * 	                &lt;in-stock&gt;true&lt;/in-stock&gt;
     * 	                &lt;perpetual&gt;false&lt;/perpetual&gt;
     * 	                &lt;sku-codes&gt;910-002-744&lt;/sku-codes&gt;
     * 	            &lt;/product-availability&gt;
     * 	            &lt;product-id&gt;297&lt;/product-id&gt;
     * 	            &lt;rank&gt;0&lt;/rank&gt;
     * 	            &lt;sku-id&gt;308&lt;/sku-id&gt;
     * 	        &lt;/sku&gt;
     * 	        ...
     * 	    &lt;/skus&gt;
     * 	    &lt;tag&gt;multisku&lt;/tag&gt;
     * 	&lt;/product&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param product PK or SEO URI
     * @param request request
     * @param response response
     *
     * @return product
     */
    @RequestMapping(
            value = "/product/{id}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductRO viewProduct(@PathVariable(value = "id") final String product,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        final ProductRO ro = viewProductInternal(product, true);
        cartMixin.persistShoppingCart(request, response);
        return ro;

    }


    private List<ProductRO> viewProductsInternal(final String products) {

        final String[] productsIds = StringUtils.split(products, '|');

        final List<ProductRO> ros = new ArrayList<ProductRO>(productsIds.length);

        for (final String productId : productsIds) {

            final ProductRO ro = viewProductInternal(productId, false);
            if (ro != null) {
                ros.add(ro);
            }
        }

        return ros;
    }

    /**
     * Interface: GET /products/{id}
     * <p>
     * <p>
     * Display full product details.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>ids</td><td>SEO URI or product PK separated by pipe character ('|')</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * 	[{
     * 	  "displayNames" : {
     * 	    "uk" : "Mini Mouse M187",
     * 	    "ru" : "Mini Mouse M187",
     * 	    "en" : "Mini Mouse M187"
     * 	  },
     * 	  "maxOrderQuantity" : null,
     * 	  "productTypeId" : 5,
     * 	  "minOrderQuantity" : null,
     * 	  "availability" : 1,
     * 	  "code" : "910-002",
     * 	  "title" : null,
     * 	  "attributes" : [
     * 	    {
     * 	      "attrvalueId" : 891,
     * 	      "productId" : 297,
     * 	      "val" : "Logitech Wireless Mini Mouse M187...",
     * 	      "displayVals" : null,
     * 	      "attributeName" : "Опис Продукту (uk)",
     * 	      "attributeId" : 11022,
     * 	      "attributeDisplayNames" : null
     * 	    },
     * 	    {
     * 	      "attrvalueId" : 19865,
     * 	      "productId" : 297,
     * 	      "val" : "Y",
     * 	      "displayVals" : {
     * 	        "uk" : "Да",
     * 	        "ru" : "Да",
     * 	        "en" : "Y"
     * 	      },
     * 	      "attributeName" : "Receiver included",
     * 	      "attributeId" : 1423,
     * 	      "attributeDisplayNames" : {
     * 	        "uk" : "Ресівер, що поставляється",
     * 	        "ru" : "Поставляемый ресивер",
     * 	        "en" : "Receiver included"
     * 	      }
     * 	    },
     * 	...
     * 	  ],
     * 	  "displayMetadescriptions" : {},
     * 	  "displayTitles" : {},
     * 	  "productId" : 297,
     * 	  "name" : "Mini Mouse M187",
     * 	  "stepOrderQuantity" : null,
     * 	  "tag" : "multisku",
     * 	  "brandName" : "Logitech",
     * 	  "brandId" : 5,
     * 	  "availablefrom" : null,
     * 	  "uri" : null,
     * 	  "availableto" : null,
     * 	  "productTypeName" : "Mice",
     * 	  "metakeywords" : null,
     * 	  "manufacturerCode" : "M001",
     * 	  "productAvailabilityModel" : {
     * 	    "firstAvailableSkuCode" : "910-002-741",
     * 	    "available" : true,
     * 	    "defaultSkuCode" : "910-002-741",
     * 	    "inStock" : true,
     * 	    "skuCodes" : [
     * 	      "910-002-741",
     * 	      "910-002-742",
     * 	      "910-002-743",
     * 	      "910-002-744"
     * 	    ],
     * 	    "perpetual" : false,
     * 	    "availableToSellQuantity" : {
     * 	      "910-002-742" : 300,
     * 	      "910-002-744" : 300,
     * 	      "910-002-741" : 300,
     * 	      "910-002-743" : 300
     * 	    }
     * 	  },
     * 	  "skus" : [
     * 	    {
     * 	      "metakeywords" : null,
     * 	      "productId" : 297,
     * 	      "description" : "",
     * 	      "metadescription" : null,
     * 	      "uri" : null,
     * 	      "price" : {
     * 	        "symbol" : "€",
     * 	        "quantity" : 1,
     * 	        "regularPrice" : 387.56,
     * 	        "salePrice" : null,
     * 	        "discount" : null,
     * 	        "currency" : "EUR",
     * 	        "symbolPosition" : "before"
     * 	      },
     * 	      "attributes" : [
     * 	        {
     * 	          "attrvalueId" : 948,
     * 	          "val" : "White",
     * 	          "displayVals" : {
     * 	            "uk" : "Білий",
     * 	            "ru" : "Белый",
     * 	            "en" : "White"
     * 	          },
     * 	          "attributeName" : "Color of product",
     * 	          "attributeId" : 532,
     * 	          "attributeDisplayNames" : {
     * 	            "uk" : "Колір продукту",
     * 	            "ru" : "Цвет товара",
     * 	            "en" : "Color of product"
     * 	          },
     * 	          "skuId" : 308
     * 	        },
     * 			...
     * 	      ],
     * 	      "title" : null,
     * 	      "manufacturerCode" : null,
     * 	      "code" : "910-002-744",
     * 	      "skuId" : 308,
     * 	      "rank" : 0,
     * 	      "displayTitles" : null,
     * 	      "displayNames" : {
     * 	        "uk" : "M187 Білий",
     * 	        "ru" : "M187 Белый",
     * 	        "en" : "M187 White"
     * 	      },
     * 	      "productAvailabilityModel" : {
     * 	        "firstAvailableSkuCode" : "910-002-744",
     * 	        "available" : true,
     * 	        "defaultSkuCode" : "910-002-744",
     * 	        "inStock" : true,
     * 	        "skuCodes" : [
     * 	          "910-002-744"
     * 	        ],
     * 	        "perpetual" : false,
     * 	        "availableToSellQuantity" : {
     * 	          "910-002-744" : 300
     * 	        }
     * 	      },
     * 	      "barCode" : "",
     * 	      "displayMetakeywords" : null,
     * 	      "name" : "M187 White",
     * 	      "displayMetadescriptions" : null
     * 	    },
     * 		...
     * 	  ],
     * 	  "price" : {
     * 	    "symbol" : "€",
     * 	    "quantity" : 1,
     * 	    "regularPrice" : 387.56,
     * 	    "salePrice" : null,
     * 	    "discount" : null,
     * 	    "currency" : "EUR",
     * 	    "symbolPosition" : "before"
     * 	  },
     * 	  "featured" : false,
     * 	  "displayMetakeywords" : {},
     * 	  "metadescription" : null,
     * 	  "description" : "Logitech Wireless Mini ..."
     * 	}]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param products PK or SEO URI
     * @param request request
     * @param response response
     *
     * @return product
     */
    @RequestMapping(
            value = "/products/{ids}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<ProductRO> viewProducts(@PathVariable(value = "ids") final String products,
                                                      final HttpServletRequest request,
                                                      final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewProductsInternal(products);

    }

    /**
     * Interface: GET /products/{id}
     * <p>
     * <p>
     * Display full product details.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>ids</td><td>SEO URI or product PK separated by pipe character ('|')</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * 	&lt;products&gt;
     * 	&lt;product&gt;
     * 	    &lt;attribute-values&gt;
     * 	        &lt;attribute-value attribute-id="11022" attrvalue-id="891" product-id="297"&gt;
     * 	            &lt;attribute-name&gt;Опис Продукту (uk)&lt;/attribute-name&gt;
     * 	            &lt;val&gt;Logitech Wireless Mini Mouse M187...&lt;/val&gt;
     * 	        &lt;/attribute-value&gt;
     * 	        &lt;attribute-value attribute-id="1423" attrvalue-id="19865" product-id="297"&gt;
     * 	            &lt;attribute-display-names&gt;
     * 	                &lt;entry lang="uk"&gt;Ресівер, що поставляється&lt;/entry&gt;
     * 	                &lt;entry lang="en"&gt;Receiver included&lt;/entry&gt;
     * 	                &lt;entry lang="ru"&gt;Поставляемый ресивер&lt;/entry&gt;
     * 	            &lt;/attribute-display-names&gt;
     * 	            &lt;attribute-name&gt;Receiver included&lt;/attribute-name&gt;
     * 	            &lt;display-vals&gt;
     * 	                &lt;entry lang="uk"&gt;Да&lt;/entry&gt;
     * 	                &lt;entry lang="en"&gt;Y&lt;/entry&gt;
     * 	                &lt;entry lang="ru"&gt;Да&lt;/entry&gt;
     * 	            &lt;/display-vals&gt;
     * 	            &lt;val&gt;Y&lt;/val&gt;
     * 	        &lt;/attribute-value&gt;
     * 	        &lt;attribute-value attribute-id="11008" attrvalue-id="20509" product-id="297"&gt;
     * 	            &lt;attribute-name&gt;Product default image&lt;/attribute-name&gt;
     * 	            &lt;val&gt;Logitech-M187_910-002-744_a.jpg&lt;/val&gt;
     * 	        &lt;/attribute-value&gt;
     * 	        ...
     * 	    &lt;/attribute-values&gt;
     * 	    &lt;availability&gt;1&lt;/availability&gt;
     * 	    &lt;brand-id&gt;5&lt;/brand-id&gt;
     * 	    &lt;brand-name&gt;Logitech&lt;/brand-name&gt;
     * 	    &lt;code&gt;910-002&lt;/code&gt;
     * 	    &lt;description&gt;Logitech Wireless Mini Mouse M187...&lt;/description&gt;
     * 	    &lt;display-metadescription/&gt;
     * 	    &lt;display-metakeywords/&gt;
     * 	    &lt;display-names&gt;
     * 	        &lt;entry lang="uk"&gt;Mini Mouse M187&lt;/entry&gt;
     * 	        &lt;entry lang="en"&gt;Mini Mouse M187&lt;/entry&gt;
     * 	        &lt;entry lang="ru"&gt;Mini Mouse M187&lt;/entry&gt;
     * 	    &lt;/display-names&gt;
     * 	    &lt;display-titles/&gt;
     * 	    &lt;featured&gt;false&lt;/featured&gt;
     * 	    &lt;manufacturer-code&gt;M001&lt;/manufacturer-code&gt;
     * 	    &lt;name&gt;Mini Mouse M187&lt;/name&gt;
     * 	    &lt;price&gt;
     * 	        &lt;currency&gt;EUR&lt;/currency&gt;
     * 	        &lt;quantity&gt;1.00&lt;/quantity&gt;
     * 	        &lt;regular-price&gt;387.56&lt;/regular-price&gt;
     * 	        &lt;symbol&gt;€&lt;/symbol&gt;
     * 	        &lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 	    &lt;/price&gt;
     * 	    &lt;product-availability&gt;
     * 	        &lt;available&gt;true&lt;/available&gt;
     * 	        &lt;ats-quantity&gt;
     * 	            &lt;entry sku="910-002-742"&gt;300.00&lt;/entry&gt;
     * 	            &lt;entry sku="910-002-741"&gt;300.00&lt;/entry&gt;
     * 	            &lt;entry sku="910-002-744"&gt;300.00&lt;/entry&gt;
     * 	            &lt;entry sku="910-002-743"&gt;300.00&lt;/entry&gt;
     * 	        &lt;/ats-quantity&gt;
     * 	        &lt;default-sku&gt;910-002-741&lt;/default-sku&gt;
     * 	        &lt;first-available-sku&gt;910-002-741&lt;/first-available-sku&gt;
     * 	        &lt;in-stock&gt;true&lt;/in-stock&gt;
     * 	        &lt;perpetual&gt;false&lt;/perpetual&gt;
     * 	        &lt;sku-codes&gt;910-002-741&lt;/sku-codes&gt;
     * 	        &lt;sku-codes&gt;910-002-742&lt;/sku-codes&gt;
     * 	        &lt;sku-codes&gt;910-002-743&lt;/sku-codes&gt;
     * 	        &lt;sku-codes&gt;910-002-744&lt;/sku-codes&gt;
     * 	    &lt;/product-availability&gt;
     * 	    &lt;product-id&gt;297&lt;/product-id&gt;
     * 	    &lt;product-type-id&gt;5&lt;/product-type-id&gt;
     * 	    &lt;product-type-name&gt;Mice&lt;/product-type-name&gt;
     * 	    &lt;skus&gt;
     * 	        &lt;sku&gt;
     * 	            &lt;attribute-values&gt;
     * 	                &lt;attribute-value attribute-id="532" attrvalue-id="948" sku-id="308"&gt;
     * 	                    &lt;attribute-display-names&gt;
     * 	                        &lt;entry lang="uk"&gt;Колір продукту&lt;/entry&gt;
     * 	                        &lt;entry lang="en"&gt;Color of product&lt;/entry&gt;
     * 	                        &lt;entry lang="ru"&gt;Цвет товара&lt;/entry&gt;
     * 	                    &lt;/attribute-display-names&gt;
     * 	                    &lt;attribute-name&gt;Color of product&lt;/attribute-name&gt;
     * 	                    &lt;display-vals&gt;
     * 	                        &lt;entry lang="uk"&gt;Білий&lt;/entry&gt;
     * 	                        &lt;entry lang="en"&gt;White&lt;/entry&gt;
     * 	                        &lt;entry lang="ru"&gt;Белый&lt;/entry&gt;
     * 	                    &lt;/display-vals&gt;
     * 	                    &lt;val&gt;White&lt;/val&gt;
     * 	                &lt;/attribute-value&gt;
     * 	                ...
     * 	            &lt;/attribute-values&gt;
     * 	            &lt;barcode&gt;&lt;/barcode&gt;
     * 	            &lt;code&gt;910-002-744&lt;/code&gt;
     * 	            &lt;description&gt;&lt;/description&gt;
     * 	            &lt;display-names&gt;
     * 	                &lt;entry lang="uk"&gt;M187 Білий&lt;/entry&gt;
     * 	                &lt;entry lang="en"&gt;M187 White&lt;/entry&gt;
     * 	                &lt;entry lang="ru"&gt;M187 Белый&lt;/entry&gt;
     * 	            &lt;/display-names&gt;
     * 	            &lt;name&gt;M187 White&lt;/name&gt;
     * 	            &lt;price&gt;
     * 	                &lt;currency&gt;EUR&lt;/currency&gt;
     * 	                &lt;quantity&gt;1.00&lt;/quantity&gt;
     * 	                &lt;regular-price&gt;387.56&lt;/regular-price&gt;
     * 	                &lt;symbol&gt;€&lt;/symbol&gt;
     * 	                &lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 	            &lt;/price&gt;
     * 	            &lt;product-availability&gt;
     * 	                &lt;available&gt;true&lt;/available&gt;
     * 	                &lt;ats-quantity&gt;
     * 	                    &lt;entry sku="910-002-744"&gt;300.00&lt;/entry&gt;
     * 	                &lt;/ats-quantity&gt;
     * 	                &lt;default-sku&gt;910-002-744&lt;/default-sku&gt;
     * 	                &lt;first-available-sku&gt;910-002-744&lt;/first-available-sku&gt;
     * 	                &lt;in-stock&gt;true&lt;/in-stock&gt;
     * 	                &lt;perpetual&gt;false&lt;/perpetual&gt;
     * 	                &lt;sku-codes&gt;910-002-744&lt;/sku-codes&gt;
     * 	            &lt;/product-availability&gt;
     * 	            &lt;product-id&gt;297&lt;/product-id&gt;
     * 	            &lt;rank&gt;0&lt;/rank&gt;
     * 	            &lt;sku-id&gt;308&lt;/sku-id&gt;
     * 	        &lt;/sku&gt;
     * 	        ...
     * 	    &lt;/skus&gt;
     * 	    &lt;tag&gt;multisku&lt;/tag&gt;
     * 	&lt;/product&gt;
     * 	&lt;/products&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param products PK or SEO URI
     * @param request request
     * @param response response
     *
     * @return product
     */
    @RequestMapping(
            value = "/products/{ids}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductListRO viewProductsXML(@PathVariable(value = "ids") final String products,
                                                       final HttpServletRequest request,
                                                       final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new ProductListRO(viewProductsInternal(products));

    }

    private List<ProductSearchResultRO> viewProductAssociationsInternal(final String product,
                                                                        final String type) {

        final long productId = bookmarkMixin.resolveProductId(product);
        final ShoppingCart cart = cartMixin.getCurrentCart();

        final List<ProductSearchResultDTO> productAssociations = productServiceFacade.getProductAssociations(productId, cart.getShoppingContext().getCustomerShopId(), type);

        final List<ProductSearchResultRO> ros = new ArrayList<ProductSearchResultRO>();

        if (CollectionUtils.isNotEmpty(productAssociations)) {

            final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(cart.getCurrencyCode());

            for (final ProductSearchResultDTO hit : productAssociations) {

                final ProductAvailabilityModel skuPam = productServiceFacade.getProductAvailability(hit, cart.getShoppingContext().getCustomerShopId());

                final ProductSearchResultRO ro = mappingMixin.map(hit, ProductSearchResultRO.class, ProductSearchResultDTO.class);

                final ProductAvailabilityModelRO amRo = mappingMixin.map(skuPam, ProductAvailabilityModelRO.class, ProductAvailabilityModel.class);
                ro.setProductAvailabilityModel(amRo);

                final ProductPriceModel price = productServiceFacade.getSkuPrice(
                        cart,
                        null,
                        skuPam.getFirstAvailableSkuCode(),
                        BigDecimal.ONE
                );

                final SkuPriceRO priceRo = mappingMixin.map(price, SkuPriceRO.class, ProductPriceModel.class);
                priceRo.setSymbol(symbol.getFirst());
                priceRo.setSymbolPosition(symbol.getSecond() != null && symbol.getSecond() ? "after" : "before");

                ro.setPrice(priceRo);

                ros.add(ro);

            }

            return ros;

        }

        return ros;

    }


    /**
     * Interface: GET /product/{id}/associations/{type}
     * <p>
     * <p>
     * Display list of associated products.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>id</td><td>SEO URI or product PK</td></tr>
     *     <tr><td>type</td><td>association type see {@link Association}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array of object ProductSearchResultRO</td><td>
     * <pre><code>
     * 	[
     * 	  {
     * 	    "maxOrderQuantity" : null,
     * 	    "minOrderQuantity" : null,
     * 	    "displayDescription" : {
     * 	      "uk" : "Trust Isotto Wired Mini Mouse...",
     * 	      "ru" : "Trust Isotto Wired Mini Mouse...",
     * 	      "en" : "Trust Isotto Wired Mini Mouse..."
     * 	    },
     * 	    "code" : "19733",
     * 	    "availability" : 1,
     * 	    "displayName" : {
     * 	      "uk" : "Isotto Wired Mini Mouse",
     * 	      "ru" : "Isotto Wired Mini Mouse",
     * 	      "en" : "Isotto Wired Mini Mouse"
     * 	    },
     * 	    "defaultSkuCode" : "19733",
     * 	    "name" : "Isotto Wired Mini Mouse",
     * 	    "stepOrderQuantity" : null,
     * 	    "availablefrom" : null,
     * 	    "id" : 184,
     * 	    "availableto" : null,
     * 	    "manufacturerCode" : null,
     * 	    "skus" : null,
     * 	    "productAvailabilityModel" : {
     * 	      "firstAvailableSkuCode" : "19733",
     * 	      "available" : true,
     * 	      "defaultSkuCode" : "19733",
     * 	      "inStock" : true,
     * 	      "skuCodes" : [
     * 	        "19733"
     * 	      ],
     * 	      "perpetual" : false,
     * 	      "availableToSellQuantity" : {
     * 	        "19733" : 99
     * 	      }
     * 	    },
     * 	    "price" : {
     * 	      "symbol" : "€",
     * 	      "quantity" : 1,
     * 	      "regularPrice" : 643.2,
     * 	      "salePrice" : null,
     * 	      "discount" : null,
     * 	      "currency" : "EUR",
     * 	      "symbolPosition" : "before"
     * 	    },
     * 	    "featured" : false,
     * 	    "defaultImage" : "Trust-Isotto-Wired-Mini-Mouse_19733_a.jpg",
     * 	    "multisku" : false,
     * 	    "description" : "Trust Isotto Wired Mini Mouse..."
     * 	  },
     * 	...
     * 	]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param product PK or SEO URI
     * @param type association type see {@link Association}
     * @param request request
     * @param response response
     *
     * @return list of products
     */
    @RequestMapping(
            value = "/product/{id}/associations/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<ProductSearchResultRO> viewProductAssociations(@PathVariable(value = "id") final String product,
                                                                             @PathVariable(value = "type") final String type,
                                                                             final HttpServletRequest request,
                                                                             final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return viewProductAssociationsInternal(product, type);

    }

    /**
     * Interface: GET /product/{id}/associations/{type}
     * <p>
     * <p>
     * Display list of associated products.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>id</td><td>SEO URI or product PK</td></tr>
     *     <tr><td>type</td><td>association type see {@link Association}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array of object ProductSearchResultRO</td><td>
     * <pre><code>
     * 	&lt;products&gt;
     * 	    &lt;product&gt;
     * 	        &lt;availability&gt;1&lt;/availability&gt;
     * 	        &lt;code&gt;19733&lt;/code&gt;
     * 	        &lt;default-image&gt;Trust-Isotto-Wired-Mini-Mouse_19733_a.jpg&lt;/default-image&gt;
     * 	        &lt;default-sku-code&gt;19733&lt;/default-sku-code&gt;
     * 	        &lt;description&gt;Trust Isotto Wired Mini Mouse...&lt;/description&gt;
     * 	        &lt;display-descriptions&gt;
     * 	            &lt;entry lang="uk"&gt;Trust Isotto Wired Mini Mouse...&lt;/entry&gt;
     * 	            &lt;entry lang="en"&gt;Trust Isotto Wired Mini Mouse...&lt;/entry&gt;
     * 	            &lt;entry lang="ru"&gt;Trust Isotto Wired Mini Mouse...&lt;/entry&gt;
     * 	        &lt;/display-descriptions&gt;
     * 	        &lt;display-names&gt;
     * 	            &lt;entry lang="uk"&gt;Isotto Wired Mini Mouse&lt;/entry&gt;
     * 	            &lt;entry lang="en"&gt;Isotto Wired Mini Mouse&lt;/entry&gt;
     * 	            &lt;entry lang="ru"&gt;Isotto Wired Mini Mouse&lt;/entry&gt;
     * 	        &lt;/display-names&gt;
     * 	        &lt;featured&gt;false&lt;/featured&gt;
     * 	        &lt;id&gt;184&lt;/id&gt;
     * 	        &lt;multisku&gt;false&lt;/multisku&gt;
     * 	        &lt;name&gt;Isotto Wired Mini Mouse&lt;/name&gt;
     * 	        &lt;price&gt;
     * 	            &lt;currency&gt;EUR&lt;/currency&gt;
     * 	            &lt;quantity&gt;1.00&lt;/quantity&gt;
     * 	            &lt;regular-price&gt;643.20&lt;/regular-price&gt;
     * 	            &lt;symbol&gt;€&lt;/symbol&gt;
     * 	            &lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 	        &lt;/price&gt;
     * 	        &lt;product-availability&gt;
     * 	            &lt;available&gt;true&lt;/available&gt;
     * 	            &lt;ats-quantity&gt;
     * 	                &lt;entry sku="19733"&gt;99.000&lt;/entry&gt;
     * 	            &lt;/ats-quantity&gt;
     * 	            &lt;default-sku&gt;19733&lt;/default-sku&gt;
     * 	            &lt;first-available-sku&gt;19733&lt;/first-available-sku&gt;
     * 	            &lt;in-stock&gt;true&lt;/in-stock&gt;
     * 	            &lt;perpetual&gt;false&lt;/perpetual&gt;
     * 	            &lt;sku-codes&gt;19733&lt;/sku-codes&gt;
     * 	        &lt;/product-availability&gt;
     * 	    &lt;/product&gt;
     * 	...
     * 	&lt;/products&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param product PK or SEO URI
     * @param type association type see {@link Association}
     * @param request request
     * @param response response
     *
     * @return list of products
     */
    @RequestMapping(
            value = "/product/{id}/associations/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductSearchResultListRO viewProductAssociationsXML(@PathVariable(value = "id") final String product,
                                                                              @PathVariable(value = "type") final String type,
                                                                              final HttpServletRequest request,
                                                                              final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return new ProductSearchResultListRO(viewProductAssociationsInternal(product, type));

    }

    private ProductSkuRO viewSkuInternal(final ProductSku productSku, final ShoppingCart cart, final Pair<String, Boolean> symbol) {

        final ProductSkuRO skuRO = mappingMixin.map(productSku, ProductSkuRO.class, ProductSku.class);

        final Pair<String, String> templates = resolveTemplate(skuRO);
        if (templates != null) {
            skuRO.setUitemplate(templates.getFirst());
            skuRO.setUitemplateFallback(templates.getSecond());
        }

        final ProductAvailabilityModel skuPam = productServiceFacade.getProductAvailability(productSku, cart.getShoppingContext().getCustomerShopId());

        final ProductAvailabilityModelRO amRo = mappingMixin.map(skuPam, ProductAvailabilityModelRO.class, ProductAvailabilityModel.class);
        skuRO.setProductAvailabilityModel(amRo);

        final ProductPriceModel price = productServiceFacade.getSkuPrice(
                cart,
                null,
                skuPam.getFirstAvailableSkuCode(),
                BigDecimal.ONE
        );

        final SkuPriceRO priceRo = mappingMixin.map(price, SkuPriceRO.class, ProductPriceModel.class);
        priceRo.setSymbol(symbol.getFirst());
        priceRo.setSymbolPosition(symbol.getSecond() != null && symbol.getSecond() ? "after" : "before");

        skuRO.setPrice(priceRo);

        return skuRO;

    }


    private Pair<String, String> resolveTemplate(final ProductSkuRO skuRO) {
        final Map params = new HashMap();
        params.put(WebParametersKeys.SKU_ID, String.valueOf(skuRO.getSkuId()));
        return centralViewResolver.resolveMainPanelRendererLabel(params);
    }


    private ProductSkuRO viewSkuInternal(final String sku, final boolean recordViewed) {
        final long productId = bookmarkMixin.resolveSkuId(sku);

        final ProductSku skuEntity;
        if (productId > 0L) {
            skuEntity = productServiceFacade.getSkuById(productId);
        } else {
            skuEntity = productServiceFacade.getProductSkuBySkuCode(sku);
        }

        if (skuEntity != null) {

            final ShoppingCart cart = cartMixin.getCurrentCart();
            final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(cart.getCurrencyCode());

            if (recordViewed) {
                executeViewProductCommand(skuEntity.getProduct());
            }

            return viewSkuInternal(skuEntity, cart, symbol);

        }

        return null;
    }


    /**
     * Interface: GET /sku/{id}
     * <p>
     * <p>
     * Display full product details.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>id</td><td>SEO URI or sku PK or sku code</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * 	    {
     * 	      "metakeywords" : null,
     * 	      "productId" : 297,
     * 	      "description" : "",
     * 	      "metadescription" : null,
     * 	      "uri" : null,
     * 	      "price" : {
     * 	        "symbol" : "€",
     * 	        "quantity" : 1,
     * 	        "regularPrice" : 387.56,
     * 	        "salePrice" : null,
     * 	        "discount" : null,
     * 	        "currency" : "EUR",
     * 	        "symbolPosition" : "before"
     * 	      },
     * 	      "attributes" : [
     * 	        {
     * 	          "attrvalueId" : 948,
     * 	          "val" : "White",
     * 	          "displayVals" : {
     * 	            "uk" : "Білий",
     * 	            "ru" : "Белый",
     * 	            "en" : "White"
     * 	          },
     * 	          "attributeName" : "Color of product",
     * 	          "attributeId" : 532,
     * 	          "attributeDisplayNames" : {
     * 	            "uk" : "Колір продукту",
     * 	            "ru" : "Цвет товара",
     * 	            "en" : "Color of product"
     * 	          },
     * 	          "skuId" : 308
     * 	        },
     * 			...
     * 	      ],
     * 	      "title" : null,
     * 	      "manufacturerCode" : null,
     * 	      "code" : "910-002-744",
     * 	      "skuId" : 308,
     * 	      "rank" : 0,
     * 	      "displayTitles" : null,
     * 	      "displayNames" : {
     * 	        "uk" : "M187 Білий",
     * 	        "ru" : "M187 Белый",
     * 	        "en" : "M187 White"
     * 	      },
     * 	      "productAvailabilityModel" : {
     * 	        "firstAvailableSkuCode" : "910-002-744",
     * 	        "available" : true,
     * 	        "defaultSkuCode" : "910-002-744",
     * 	        "inStock" : true,
     * 	        "skuCodes" : [
     * 	          "910-002-744"
     * 	        ],
     * 	        "perpetual" : false,
     * 	        "availableToSellQuantity" : {
     * 	          "910-002-744" : 300
     * 	        }
     * 	      },
     * 	      "barCode" : "",
     * 	      "displayMetakeywords" : null,
     * 	      "name" : "M187 White",
     * 	      "displayMetadescriptions" : null
     * 	    }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * 	        &lt;sku&gt;
     * 	            &lt;attribute-values&gt;
     * 	                &lt;attribute-value attribute-id="532" attrvalue-id="948" sku-id="308"&gt;
     * 	                    &lt;attribute-display-names&gt;
     * 	                        &lt;entry lang="uk"&gt;Колір продукту&lt;/entry&gt;
     * 	                        &lt;entry lang="en"&gt;Color of product&lt;/entry&gt;
     * 	                        &lt;entry lang="ru"&gt;Цвет товара&lt;/entry&gt;
     * 	                    &lt;/attribute-display-names&gt;
     * 	                    &lt;attribute-name&gt;Color of product&lt;/attribute-name&gt;
     * 	                    &lt;display-vals&gt;
     * 	                        &lt;entry lang="uk"&gt;Білий&lt;/entry&gt;
     * 	                        &lt;entry lang="en"&gt;White&lt;/entry&gt;
     * 	                        &lt;entry lang="ru"&gt;Белый&lt;/entry&gt;
     * 	                    &lt;/display-vals&gt;
     * 	                    &lt;val&gt;White&lt;/val&gt;
     * 	                &lt;/attribute-value&gt;
     * 	                ...
     * 	            &lt;/attribute-values&gt;
     * 	            &lt;barcode&gt;&lt;/barcode&gt;
     * 	            &lt;code&gt;910-002-744&lt;/code&gt;
     * 	            &lt;description&gt;&lt;/description&gt;
     * 	            &lt;display-names&gt;
     * 	                &lt;entry lang="uk"&gt;M187 Білий&lt;/entry&gt;
     * 	                &lt;entry lang="en"&gt;M187 White&lt;/entry&gt;
     * 	                &lt;entry lang="ru"&gt;M187 Белый&lt;/entry&gt;
     * 	            &lt;/display-names&gt;
     * 	            &lt;name&gt;M187 White&lt;/name&gt;
     * 	            &lt;price&gt;
     * 	                &lt;currency&gt;EUR&lt;/currency&gt;
     * 	                &lt;quantity&gt;1.00&lt;/quantity&gt;
     * 	                &lt;regular-price&gt;387.56&lt;/regular-price&gt;
     * 	                &lt;symbol&gt;€&lt;/symbol&gt;
     * 	                &lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 	            &lt;/price&gt;
     * 	            &lt;product-availability&gt;
     * 	                &lt;available&gt;true&lt;/available&gt;
     * 	                &lt;ats-quantity&gt;
     * 	                    &lt;entry sku="910-002-744"&gt;300.00&lt;/entry&gt;
     * 	                &lt;/ats-quantity&gt;
     * 	                &lt;default-sku&gt;910-002-744&lt;/default-sku&gt;
     * 	                &lt;first-available-sku&gt;910-002-744&lt;/first-available-sku&gt;
     * 	                &lt;in-stock&gt;true&lt;/in-stock&gt;
     * 	                &lt;perpetual&gt;false&lt;/perpetual&gt;
     * 	                &lt;sku-codes&gt;910-002-744&lt;/sku-codes&gt;
     * 	            &lt;/product-availability&gt;
     * 	            &lt;product-id&gt;297&lt;/product-id&gt;
     * 	            &lt;rank&gt;0&lt;/rank&gt;
     * 	            &lt;sku-id&gt;308&lt;/sku-id&gt;
     * 	        &lt;/sku&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param sku PK or SEO URI
     * @param request request
     * @param response response
     *
     * @return product sku
     */
    @RequestMapping(
            value = "/sku/{id}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductSkuRO viewSku(@PathVariable(value = "id") final String sku,
                                              final HttpServletRequest request,
                                              final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        final ProductSkuRO ro = viewSkuInternal(sku, true);
        cartMixin.persistShoppingCart(request, response);
        return ro;

    }


    private List<ProductSkuRO> viewProductSkusInternal(final String skus) {

        final String[] skuIds = StringUtils.split(skus, '|');

        final List<ProductSkuRO> ros = new ArrayList<ProductSkuRO>(skuIds.length);

        for (final String skuId : skuIds) {

            final ProductSkuRO ro = viewSkuInternal(skuId, false);
            if (ro != null) {
                ros.add(ro);
            }
        }

        return ros;
    }


    /**
     * Interface: GET /skus/{id}
     * <p>
     * <p>
     * Display full product details.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>ids</td><td>SEO URI or sku PK or sku code separated by pipe character ('|')</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON example</td><td>
     * <pre><code>
     * 	   [ {
     * 	      "metakeywords" : null,
     * 	      "productId" : 297,
     * 	      "description" : "",
     * 	      "metadescription" : null,
     * 	      "uri" : null,
     * 	      "price" : {
     * 	        "symbol" : "€",
     * 	        "quantity" : 1,
     * 	        "regularPrice" : 387.56,
     * 	        "salePrice" : null,
     * 	        "discount" : null,
     * 	        "currency" : "EUR",
     * 	        "symbolPosition" : "before"
     * 	      },
     * 	      "attributes" : [
     * 	        {
     * 	          "attrvalueId" : 948,
     * 	          "val" : "White",
     * 	          "displayVals" : {
     * 	            "uk" : "Білий",
     * 	            "ru" : "Белый",
     * 	            "en" : "White"
     * 	          },
     * 	          "attributeName" : "Color of product",
     * 	          "attributeId" : 532,
     * 	          "attributeDisplayNames" : {
     * 	            "uk" : "Колір продукту",
     * 	            "ru" : "Цвет товара",
     * 	            "en" : "Color of product"
     * 	          },
     * 	          "skuId" : 308
     * 	        },
     * 			...
     * 	      ],
     * 	      "title" : null,
     * 	      "manufacturerCode" : null,
     * 	      "code" : "910-002-744",
     * 	      "skuId" : 308,
     * 	      "rank" : 0,
     * 	      "displayTitles" : null,
     * 	      "displayNames" : {
     * 	        "uk" : "M187 Білий",
     * 	        "ru" : "M187 Белый",
     * 	        "en" : "M187 White"
     * 	      },
     * 	      "productAvailabilityModel" : {
     * 	        "firstAvailableSkuCode" : "910-002-744",
     * 	        "available" : true,
     * 	        "defaultSkuCode" : "910-002-744",
     * 	        "inStock" : true,
     * 	        "skuCodes" : [
     * 	          "910-002-744"
     * 	        ],
     * 	        "perpetual" : false,
     * 	        "availableToSellQuantity" : {
     * 	          "910-002-744" : 300
     * 	        }
     * 	      },
     * 	      "barCode" : "",
     * 	      "displayMetakeywords" : null,
     * 	      "name" : "M187 White",
     * 	      "displayMetadescriptions" : null
     * 	    } ]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param skus PK or SEO URI or code
     * @param request request
     * @param response response
     *
     * @return product sku
     */
    @RequestMapping(
            value = "/skus/{ids}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<ProductSkuRO> viewSkus(@PathVariable(value = "ids") final String skus,
                                                     final HttpServletRequest request,
                                                     final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewProductSkusInternal(skus);

    }

    /**
     * Interface: GET /skus/{id}
     * <p>
     * <p>
     * Display full product details.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>ids</td><td>SEO URI or sku PK or sku code separated by pipe character ('|')</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML example</td><td>
     * <pre><code>
     * 	        &lt;skus&gt;
     * 	        &lt;sku&gt;
     * 	            &lt;attribute-values&gt;
     * 	                &lt;attribute-value attribute-id="532" attrvalue-id="948" sku-id="308"&gt;
     * 	                    &lt;attribute-display-names&gt;
     * 	                        &lt;entry lang="uk"&gt;Колір продукту&lt;/entry&gt;
     * 	                        &lt;entry lang="en"&gt;Color of product&lt;/entry&gt;
     * 	                        &lt;entry lang="ru"&gt;Цвет товара&lt;/entry&gt;
     * 	                    &lt;/attribute-display-names&gt;
     * 	                    &lt;attribute-name&gt;Color of product&lt;/attribute-name&gt;
     * 	                    &lt;display-vals&gt;
     * 	                        &lt;entry lang="uk"&gt;Білий&lt;/entry&gt;
     * 	                        &lt;entry lang="en"&gt;White&lt;/entry&gt;
     * 	                        &lt;entry lang="ru"&gt;Белый&lt;/entry&gt;
     * 	                    &lt;/display-vals&gt;
     * 	                    &lt;val&gt;White&lt;/val&gt;
     * 	                &lt;/attribute-value&gt;
     * 	                ...
     * 	            &lt;/attribute-values&gt;
     * 	            &lt;barcode&gt;&lt;/barcode&gt;
     * 	            &lt;code&gt;910-002-744&lt;/code&gt;
     * 	            &lt;description&gt;&lt;/description&gt;
     * 	            &lt;display-names&gt;
     * 	                &lt;entry lang="uk"&gt;M187 Білий&lt;/entry&gt;
     * 	                &lt;entry lang="en"&gt;M187 White&lt;/entry&gt;
     * 	                &lt;entry lang="ru"&gt;M187 Белый&lt;/entry&gt;
     * 	            &lt;/display-names&gt;
     * 	            &lt;name&gt;M187 White&lt;/name&gt;
     * 	            &lt;price&gt;
     * 	                &lt;currency&gt;EUR&lt;/currency&gt;
     * 	                &lt;quantity&gt;1.00&lt;/quantity&gt;
     * 	                &lt;regular-price&gt;387.56&lt;/regular-price&gt;
     * 	                &lt;symbol&gt;€&lt;/symbol&gt;
     * 	                &lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 	            &lt;/price&gt;
     * 	            &lt;product-availability&gt;
     * 	                &lt;available&gt;true&lt;/available&gt;
     * 	                &lt;ats-quantity&gt;
     * 	                    &lt;entry sku="910-002-744"&gt;300.00&lt;/entry&gt;
     * 	                &lt;/ats-quantity&gt;
     * 	                &lt;default-sku&gt;910-002-744&lt;/default-sku&gt;
     * 	                &lt;first-available-sku&gt;910-002-744&lt;/first-available-sku&gt;
     * 	                &lt;in-stock&gt;true&lt;/in-stock&gt;
     * 	                &lt;perpetual&gt;false&lt;/perpetual&gt;
     * 	                &lt;sku-codes&gt;910-002-744&lt;/sku-codes&gt;
     * 	            &lt;/product-availability&gt;
     * 	            &lt;product-id&gt;297&lt;/product-id&gt;
     * 	            &lt;rank&gt;0&lt;/rank&gt;
     * 	            &lt;sku-id&gt;308&lt;/sku-id&gt;
     * 	        &lt;/sku&gt;
     * 	        &lt;/skus&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param skus PK or SEO URI or code
     * @param request request
     * @param response response
     *
     * @return product sku
     */
    @RequestMapping(
            value = "/skus/{ids}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductSkuListRO viewSkusXML(@PathVariable(value = "ids") final String skus,
                                                      final HttpServletRequest request,
                                                      final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new ProductSkuListRO(viewProductSkusInternal(skus));

    }


    /**
     * Execute view product command.
     *
     * @param product product.
     */
    protected void executeViewProductCommand(final Product product) {
        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, cartMixin.getCurrentCart(),
                new HashMap<String, Object>() {{
                    put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, product);
                }}
        );
    }




}
