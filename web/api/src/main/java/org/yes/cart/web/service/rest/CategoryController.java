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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.ro.BreadcrumbRO;
import org.yes.cart.domain.ro.CategoryListRO;
import org.yes.cart.domain.ro.CategoryRO;
import org.yes.cart.web.service.rest.impl.BookmarkMixin;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.service.rest.impl.RoMappingMixin;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CategoryServiceFacade;
import org.yes.cart.web.support.service.CentralViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * User: denispavlov
 * Date: 19/08/2014
 * Time: 23:42
 */
@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CentralViewResolver centralViewResolver;
    @Autowired
    private CategoryServiceFacade categoryServiceFacade;

    @Autowired
    private CartMixin cartMixin;
    @Autowired
    @Qualifier("restRoMappingMixin")
    private RoMappingMixin mappingMixin;
    @Autowired
    private BookmarkMixin bookmarkMixin;

    private List<CategoryRO> listRootInternal() {

        final long browsingShopId = cartMixin.getCurrentCustomerShopId();
        final String lang = cartMixin.getCurrentCart().getCurrentLocale();
        final List<Category> categories = categoryServiceFacade.getCurrentCategoryMenu(0l, browsingShopId, lang);

        final List<CategoryRO> cats = mappingMixin.map(categories, CategoryRO.class, Category.class);
        if (cats.size() > 0) {

            for (final CategoryRO cat : cats) {
                final List<BreadcrumbRO> crumbs = generateBreadcrumbs(cat.getCategoryId(), browsingShopId);
                cat.setBreadcrumbs(crumbs);
                final Long parentId = categoryServiceFacade.getCategoryParentId(cat.getCategoryId(), browsingShopId);
                if (parentId != null) {
                    cat.setParentId(parentId); // This may be parent from linkTo
                }
            }

        }

        return cats;

    }

    /**
     * Interface: GET /yes-api/rest/category/menu
     * <p>
     * <p>
     * Display top category menu. uitemplate is taken from the object without failover.
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
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array of object CategoryRO</td><td>
     * <pre><code>
     * [{
     *      "metadescription" : null,
     *      "rank" : 500,
     *      "productTypeId" : 1,
     *      "displayNames" : {
     *          "uk" : "мобільні робочі станції",
     *          "ru" : "мобильные рабочие станции",
     *          "en" : "Laptops"
     *      },
     *      "title" : null,
     *      "navigationByPrice" : true,
     *      "displayMetadescriptions" : null,
     *      "navigationByAttributes" : true,
     *      "navigationByBrand" : true,
     *      "uitemplate" : null,
     *      "displayTitles" : null,
     *      "navigationByPriceTiers" : "\n                \n\n<price-navigation>\n    <currencies>\n ... ",
     *      "name" : "Laptops",
     *      "uri" : "notebooks",
     *      "metakeywords" : null,
     *      "availablefrom" : null,
     *      "availableto" : null,
     *      "productTypeName" : "Laptops",
     *      "parentId" : 100,
     *      "breadcrumbs" : [],
     *      "displayMetakeywords" : null,
     *      "attributes" : [
     *      {
     *          "attrvalueId" : 2,
     *          "val" : "A notebook, also known ...",
     *          "displayVals" : null,
     *          "attributeName" : "Описание Категории (ru)",
     *          "attributeId" : 11007,
     *          "attributeDisplayNames" : null,
     *          "categoryId" : 1
     *      }],
     *      "categoryId" : 1,
     *      "description" : "A notebook, also know..."
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return category object
     */
    @RequestMapping(
            value = "/menu",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody List<CategoryRO> listRoot(final HttpServletRequest request,
                                                   final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return listRootInternal();

    }

    /**
     * Interface: GET /yes-api/rest/category/menu
     * <p>
     * <p>
     * Display top category menu. uitemplate is taken from the object without failover.
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
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     *
     * <table border="1">
     *     <tr><td>XML of objects CategoryRO</td><td>
     * <pre><code>
     *   &lt;categories&gt;
     *   &lt;category category-id="1" navigation-by-attributes="true" navigation-by-brand="true" navigation-by-price="true" parent-id="100" product-type-id="1"&gt;
     *       &lt;attribute-values&gt;
     *           &lt;attribute-value attribute-id="11014" attrvalue-id="3" category-id="1"&gt;
     *               &lt;attribute-name&gt;Опис Категорії (uk)&lt;/attribute-name&gt;
     *               &lt;val&gt;A notebook, also known as laptop, is a...&lt;/val&gt;
     *           &lt;/attribute-value&gt;
     *           &lt;attribute-value attribute-id="11006" attrvalue-id="1" category-id="1"&gt;
     *               &lt;attribute-name&gt;Category Description (en)&lt;/attribute-name&gt;
     *               &lt;val&gt;A notebook, also known as laptop, is a po...&lt;/val&gt;
     *           &lt;/attribute-value&gt;
     *           &lt;attribute-value attribute-id="11007" attrvalue-id="2" category-id="1"&gt;
     *              &lt;attribute-name&gt;Описание Категории (ru)&lt;/attribute-name&gt;
     *              &lt;val&gt;A notebook, also known as laptop, is a port...&lt;/val&gt;
     *           &lt;/attribute-value&gt;
     *       &lt;/attribute-values&gt;
     *       &lt;breadcrumbs&gt;
     *           &lt;breadcrumb category-id="1"&gt;
     *               &lt;display-names&gt;
     *               &lt;entry lang="uk"&gt;мобільні робочі станції&lt;/entry&gt;
     *               &lt;entry lang="en"&gt;Laptops&lt;/entry&gt;
     *               &lt;entry lang="ru"&gt;мобильные рабочие станции&lt;/entry&gt;
     *               &lt;/display-names&gt;
     *               &lt;name&gt;Laptops&lt;/name&gt;
     *               &lt;uri&gt;notebooks&lt;/uri&gt;
     *           &lt;/breadcrumb&gt;
     *       &lt;/breadcrumbs&gt;
     *       &lt;description&gt;A notebook, also known as laptop, is a por...&lt;/description&gt;
     *       &lt;display-names&gt;
     *           &lt;entry lang="uk"&gt;мобільні робочі станції&lt;/entry&gt;
     *           &lt;entry lang="en"&gt;Laptops&lt;/entry&gt;
     *           &lt;entry lang="ru"&gt;мобильные рабочие станции&lt;/entry&gt;
     *       &lt;/display-names&gt;
     *       &lt;name&gt;Laptops&lt;/name&gt;
     *       &lt;navigation-by-price-tiers&gt;
     *                            ...
     *              &lt;!-- Escaped --&gt;
     *                  &lt;price-navigation&gt;
     *                       &lt;currencies&gt;
     *
     *       &lt;/navigation-by-price-tiers&gt;
     *       &lt;product-type-name&gt;Laptops&lt;/product-type-name&gt;
     *       &lt;rank&gt;500&lt;/rank&gt;
     *       &lt;uitemplate&gt;products&lt;/uitemplate&gt;
     *       &lt;uri&gt;notebooks&lt;/uri&gt;
     *   &lt;/category&gt;
     *   &lt;/categories&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     *
     * @param request request
     * @param response response
     *
     * @return category object
     */

    @RequestMapping(
            value = "/menu",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public @ResponseBody CategoryListRO listRootXML(final HttpServletRequest request,
                                                    final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return new CategoryListRO(listRootInternal());

    }



    /**
     * Interface: GET /yes-api/rest/category/view/{id}
     * <p>
     * <p>
     * Display category. uitemplate is is correctly resolved using central view resolver.
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
     *     <tr><td>id</td><td>SEO URI or categoryId</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object CategoryRO</td><td>
     * <pre><code>
     * {
     *      "metadescription" : null,
     *      "rank" : 500,
     *      "productTypeId" : 1,
     *      "displayNames" : {
     *          "uk" : "мобільні робочі станції",
     *          "ru" : "мобильные рабочие станции",
     *          "en" : "Laptops"
     *      },
     *      "title" : null,
     *      "navigationByPrice" : true,
     *      "displayMetadescriptions" : null,
     *      "navigationByAttributes" : true,
     *      "navigationByBrand" : true,
     *      "uitemplate" : null,
     *      "displayTitles" : null,
     *      "navigationByPriceTiers" : "\n                \n\n<price-navigation>\n    <currencies>\n ... ",
     *      "name" : "Laptops",
     *      "uri" : "notebooks",
     *      "metakeywords" : null,
     *      "availablefrom" : null,
     *      "availableto" : null,
     *      "productTypeName" : "Laptops",
     *      "parentId" : 100,
     *      "breadcrumbs" : [],
     *      "displayMetakeywords" : null,
     *      "attributes" : [
     *      {
     *          "attrvalueId" : 2,
     *          "val" : "A notebook, also known ...",
     *          "displayVals" : null,
     *          "attributeName" : "Описание Категории (ru)",
     *          "attributeId" : 11007,
     *          "attributeDisplayNames" : null,
     *          "categoryId" : 1
     *      }],
     *      "categoryId" : 1,
     *      "description" : "A notebook, also know..."
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CategoryRO</td><td>
     * <pre><code>
     *   &lt;category category-id="1" navigation-by-attributes="true" navigation-by-brand="true" navigation-by-price="true" parent-id="100" product-type-id="1"&gt;
     *       &lt;attribute-values&gt;
     *           &lt;attribute-value attribute-id="11014" attrvalue-id="3" category-id="1"&gt;
     *               &lt;attribute-name&gt;Опис Категорії (uk)&lt;/attribute-name&gt;
     *               &lt;val&gt;A notebook, also known as laptop, is a...&lt;/val&gt;
     *           &lt;/attribute-value&gt;
     *           &lt;attribute-value attribute-id="11006" attrvalue-id="1" category-id="1"&gt;
     *               &lt;attribute-name&gt;Category Description (en)&lt;/attribute-name&gt;
     *               &lt;val&gt;A notebook, also known as laptop, is a po...&lt;/val&gt;
     *           &lt;/attribute-value&gt;
     *           &lt;attribute-value attribute-id="11007" attrvalue-id="2" category-id="1"&gt;
     *              &lt;attribute-name&gt;Описание Категории (ru)&lt;/attribute-name&gt;
     *              &lt;val&gt;A notebook, also known as laptop, is a port...&lt;/val&gt;
     *           &lt;/attribute-value&gt;
     *       &lt;/attribute-values&gt;
     *       &lt;breadcrumbs&gt;
     *           &lt;breadcrumb category-id="1"&gt;
     *               &lt;display-names&gt;
     *               &lt;entry lang="uk"&gt;мобільні робочі станції&lt;/entry&gt;
     *               &lt;entry lang="en"&gt;Laptops&lt;/entry&gt;
     *               &lt;entry lang="ru"&gt;мобильные рабочие станции&lt;/entry&gt;
     *               &lt;/display-names&gt;
     *               &lt;name&gt;Laptops&lt;/name&gt;
     *               &lt;uri&gt;notebooks&lt;/uri&gt;
     *           &lt;/breadcrumb&gt;
     *       &lt;/breadcrumbs&gt;
     *       &lt;description&gt;A notebook, also known as laptop, is a por...&lt;/description&gt;
     *       &lt;display-names&gt;
     *           &lt;entry lang="uk"&gt;мобільні робочі станції&lt;/entry&gt;
     *           &lt;entry lang="en"&gt;Laptops&lt;/entry&gt;
     *           &lt;entry lang="ru"&gt;мобильные рабочие станции&lt;/entry&gt;
     *       &lt;/display-names&gt;
     *       &lt;name&gt;Laptops&lt;/name&gt;
     *       &lt;navigation-by-price-tiers&gt;
     *                            ...
     *              &lt;!-- Escaped --&gt;
     *                  &lt;price-navigation&gt;
     *                       &lt;currencies&gt;
     *
     *       &lt;/navigation-by-price-tiers&gt;
     *       &lt;product-type-name&gt;Laptops&lt;/product-type-name&gt;
     *       &lt;rank&gt;500&lt;/rank&gt;
     *       &lt;uitemplate&gt;products&lt;/uitemplate&gt;
     *       &lt;uri&gt;notebooks&lt;/uri&gt;
     *   &lt;/category&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     *
     * @param category SEO URI or categoryId
     * @param request request
     * @param response response
     *
     * @return category object
     */
    @RequestMapping(
            value = "/view/{id}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CategoryRO viewCategory(@PathVariable(value = "id") final String category,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        final long categoryId = bookmarkMixin.resolveCategoryId(category);
        final long browsingShopId = cartMixin.getCurrentCustomerShopId();

        final Category categoryEntity = categoryServiceFacade.getCategory(categoryId, browsingShopId);

        if (categoryEntity != null && !CentralViewLabel.INCLUDE.equals(categoryEntity.getUitemplate())) {

            final CategoryRO catRO = mappingMixin.map(categoryEntity, CategoryRO.class, Category.class);
            catRO.setBreadcrumbs(generateBreadcrumbs(catRO.getCategoryId(), browsingShopId));
            final Pair<String, String> templates = resolveTemplate(catRO);
            if (templates != null) {
                catRO.setUitemplate(templates.getFirst());
                catRO.setUitemplateFallback(templates.getSecond());
            }
            final Long parentId = categoryServiceFacade.getCategoryParentId(categoryId, browsingShopId);
            if (parentId != null) {
                catRO.setParentId(parentId); // This may be parent from linkTo
            }

            return catRO;

        }

        return null;

    }

    private List<CategoryRO> listCategoryInternal(final String category) {

        final long categoryId = bookmarkMixin.resolveCategoryId(category);
        final long browsingShopId = cartMixin.getCurrentCustomerShopId();
        final String lang = cartMixin.getCurrentCart().getCurrentLocale();

        final List<Category> menu = categoryServiceFacade.getCurrentCategoryMenu(categoryId, browsingShopId, lang);

        if (!menu.isEmpty()) {

            final List<CategoryRO> cats = mappingMixin.map(menu, CategoryRO.class, Category.class);
            if (cats.size() > 0) {

                for (final CategoryRO cat : cats) {
                    final List<BreadcrumbRO> crumbs = generateBreadcrumbs(cat.getCategoryId(), browsingShopId);
                    cat.setBreadcrumbs(crumbs);
                    final Long parentId = categoryServiceFacade.getCategoryParentId(cat.getCategoryId(), browsingShopId);
                    if (parentId != null) {
                        cat.setParentId(parentId); // This may be parent from linkTo
                    }
                }

            }

            return cats;

        }
        return new ArrayList<CategoryRO>();
    }


    /**
     * Interface: GET /yes-api/rest/category/menu/{id}
     * <p>
     * <p>
     * Display category menu. uitemplate is taken from the object without failover.
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
     *     <tr><td>id</td><td>SEO URI or categoryId</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     *
     * <table border="1">
     *     <tr><td>JSON array of object CategoryRO</td><td>
     * <pre><code>
     * [{
     *      "metadescription" : null,
     *      "rank" : 500,
     *      "productTypeId" : 1,
     *      "displayNames" : {
     *          "uk" : "мобільні робочі станції",
     *          "ru" : "мобильные рабочие станции",
     *          "en" : "Laptops"
     *      },
     *      "title" : null,
     *      "navigationByPrice" : true,
     *      "displayMetadescriptions" : null,
     *      "navigationByAttributes" : true,
     *      "navigationByBrand" : true,
     *      "uitemplate" : null,
     *      "displayTitles" : null,
     *      "navigationByPriceTiers" : "\n                \n\n<price-navigation>\n    <currencies>\n ... ",
     *      "name" : "Laptops",
     *      "uri" : "notebooks",
     *      "metakeywords" : null,
     *      "availablefrom" : null,
     *      "availableto" : null,
     *      "productTypeName" : "Laptops",
     *      "parentId" : 100,
     *      "breadcrumbs" : [],
     *      "displayMetakeywords" : null,
     *      "attributes" : [
     *      {
     *          "attrvalueId" : 2,
     *          "val" : "A notebook, also known ...",
     *          "displayVals" : null,
     *          "attributeName" : "Описание Категории (ru)",
     *          "attributeId" : 11007,
     *          "attributeDisplayNames" : null,
     *          "categoryId" : 1
     *      }],
     *      "categoryId" : 1,
     *      "description" : "A notebook, also know..."
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param category SEO URI or categoryId
     * @param request request
     * @param response response
     *
     * @return category object
     */
    @RequestMapping(
            value = "/menu/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody List<CategoryRO> listCategory(@PathVariable(value = "id") final String category,
                                                       final HttpServletRequest request,
                                                       final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return listCategoryInternal(category);

    }

    /**
     * Interface: GET /yes-api/rest/category/menu/{id}
     * <p>
     * <p>
     * Display category menu. uitemplate is taken from the object without failover.
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
     *     <tr><td>id</td><td>SEO URI or categoryId</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML of objects CategoryRO</td><td>
     * <pre><code>
     *   &lt;categories&gt;
     *   &lt;category category-id="1" navigation-by-attributes="true" navigation-by-brand="true" navigation-by-price="true" parent-id="100" product-type-id="1"&gt;
     *       &lt;attribute-values&gt;
     *           &lt;attribute-value attribute-id="11014" attrvalue-id="3" category-id="1"&gt;
     *               &lt;attribute-name&gt;Опис Категорії (uk)&lt;/attribute-name&gt;
     *               &lt;val&gt;A notebook, also known as laptop, is a...&lt;/val&gt;
     *           &lt;/attribute-value&gt;
     *           &lt;attribute-value attribute-id="11006" attrvalue-id="1" category-id="1"&gt;
     *               &lt;attribute-name&gt;Category Description (en)&lt;/attribute-name&gt;
     *               &lt;val&gt;A notebook, also known as laptop, is a po...&lt;/val&gt;
     *           &lt;/attribute-value&gt;
     *           &lt;attribute-value attribute-id="11007" attrvalue-id="2" category-id="1"&gt;
     *              &lt;attribute-name&gt;Описание Категории (ru)&lt;/attribute-name&gt;
     *              &lt;val&gt;A notebook, also known as laptop, is a port...&lt;/val&gt;
     *           &lt;/attribute-value&gt;
     *       &lt;/attribute-values&gt;
     *       &lt;breadcrumbs&gt;
     *           &lt;breadcrumb category-id="1"&gt;
     *               &lt;display-names&gt;
     *               &lt;entry lang="uk"&gt;мобільні робочі станції&lt;/entry&gt;
     *               &lt;entry lang="en"&gt;Laptops&lt;/entry&gt;
     *               &lt;entry lang="ru"&gt;мобильные рабочие станции&lt;/entry&gt;
     *               &lt;/display-names&gt;
     *               &lt;name&gt;Laptops&lt;/name&gt;
     *               &lt;uri&gt;notebooks&lt;/uri&gt;
     *           &lt;/breadcrumb&gt;
     *       &lt;/breadcrumbs&gt;
     *       &lt;description&gt;A notebook, also known as laptop, is a por...&lt;/description&gt;
     *       &lt;display-names&gt;
     *           &lt;entry lang="uk"&gt;мобільні робочі станції&lt;/entry&gt;
     *           &lt;entry lang="en"&gt;Laptops&lt;/entry&gt;
     *           &lt;entry lang="ru"&gt;мобильные рабочие станции&lt;/entry&gt;
     *       &lt;/display-names&gt;
     *       &lt;name&gt;Laptops&lt;/name&gt;
     *       &lt;navigation-by-price-tiers&gt;
     *                            ...
     *              &lt;!-- Escaped --&gt;
     *                  &lt;price-navigation&gt;
     *                       &lt;currencies&gt;
     *
     *       &lt;/navigation-by-price-tiers&gt;
     *       &lt;product-type-name&gt;Laptops&lt;/product-type-name&gt;
     *       &lt;rank&gt;500&lt;/rank&gt;
     *       &lt;uitemplate&gt;products&lt;/uitemplate&gt;
     *       &lt;uri&gt;notebooks&lt;/uri&gt;
     *   &lt;/category&gt;
     *   &lt;/categories&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param category SEO URI or categoryId
     * @param request request
     * @param response response
     *
     * @return category object
     */
    @RequestMapping(
            value = "/menu/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public @ResponseBody CategoryListRO listCategoryXML(@PathVariable(value = "id") final String category,
                                                        final HttpServletRequest request,
                                                        final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return new CategoryListRO(listCategoryInternal(category));

    }

    private Pair<String, String> resolveTemplate(final CategoryRO catRO) {
        final Map params = new HashMap();
        params.put(WebParametersKeys.CATEGORY_ID, String.valueOf(catRO.getCategoryId()));
        return centralViewResolver.resolveMainPanelRendererLabel(params);
    }

    private List<BreadcrumbRO> generateBreadcrumbs(final long categoryId, final long shopId) {

        final List<BreadcrumbRO> crumbs = new ArrayList<BreadcrumbRO>();

        long current = categoryId;

        while(true) {

            Category cat = categoryServiceFacade.getCategory(current, shopId);

            if (cat == null || CentralViewLabel.INCLUDE.equals(cat.getUitemplate())) {
                break;
            }

            final BreadcrumbRO crumb = mappingMixin.map(cat, BreadcrumbRO.class, Category.class);
            crumbs.add(crumb);

            Long parentId = categoryServiceFacade.getCategoryParentId(cat.getCategoryId(), shopId);
            if (parentId == null) {
                break;
            }

            current = parentId;

        }

        Collections.reverse(crumbs);

        return crumbs;

    }

}
