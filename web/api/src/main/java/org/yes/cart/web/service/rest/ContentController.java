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
import org.springframework.web.bind.annotation.*;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.ro.AttrValueCategoryRO;
import org.yes.cart.domain.ro.BreadcrumbRO;
import org.yes.cart.domain.ro.ContentListRO;
import org.yes.cart.domain.ro.ContentRO;
import org.yes.cart.domain.ro.xml.XMLParamsRO;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.service.rest.impl.BookmarkMixin;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.service.rest.impl.RoMappingMixin;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.seo.BookmarkService;
import org.yes.cart.web.support.service.CentralViewResolver;
import org.yes.cart.web.support.service.ContentServiceFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * User: denispavlov
 * Date: 19/08/2014
 * Time: 23:42
 */
@Controller
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private CentralViewResolver centralViewResolver;
    @Autowired
    private ContentServiceFacade contentServiceFacade;
    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private AttributeService attributeService;

    @Autowired
    private CartMixin cartMixin;
    @Autowired
    @Qualifier("restRoMappingMixin")
    private RoMappingMixin mappingMixin;
    @Autowired
    private BookmarkMixin bookmarkMixin;

    private ContentRO viewContentInternal(final String content,
                                          final Map<String, Object> contentParams) {

        final long contentId = bookmarkMixin.resolveContentId(content);
        final long contentShopId = cartMixin.getCurrentShopId();

        final Category contentEntity = contentServiceFacade.getContent(contentId, contentShopId);

        if (contentEntity != null && !CentralViewLabel.INCLUDE.equals(contentEntity.getUitemplate())) {

            final ContentRO cntRO = mappingMixin.map(contentEntity, ContentRO.class, Category.class);
            cntRO.setBreadcrumbs(generateBreadcrumbs(cntRO.getCategoryId(), contentShopId));
            removeContentBodyAttributes(cntRO);
            cntRO.setContentBody(generateContentBody(cntRO.getCategoryId(), contentShopId, contentParams));
            final Pair<String, String> templates = resolveTemplate(cntRO);
            if (templates != null) {
                cntRO.setUitemplate(templates.getFirst());
                cntRO.setUitemplateFallback(templates.getSecond());
            }
            return cntRO;

        }

        return null;

    }

    /**
     * Interface: GET /yes-api/rest/content/view/{id}
     * <p>
     * <p>
     * Display content. uitemplate is is correctly resolved using central view resolver.
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
     *     <tr><td>JSON object ContentRO</td><td>
     * <pre><code>
     * {
     *         "availablefrom" : null,
     *         "categoryId" : 10002,
     *         "description" : "Dynamic Content Site Map Page",
     *         "metakeywords" : null,
     *         "uri" : "sitemap",
     *         "metadescription" : null,
     *         "contentBody" : "\n<p>This page demonstrates dynamic content featur ... \n\n\n",
     *         "availableto" : null,
     *         "breadcrumbs" : [
     *             {
     *                 "displayNames" : null,
     *                 "categoryId" : 10002,
     *                 "uri" : "sitemap",
     *                 "name" : "Sitemap"
     *             }
     *         ],
     *         "title" : null,
     *         "rank" : 0,
     *         "uitemplate" : "dynocontent",
     *         "attributes" : [],
     *         "displayTitles" : null,
     *         "displayNames" : null,
     *         "displayMetadescriptions" : null,
     *         "displayMetakeywords" : null,
     *         "name" : "Sitemap",
     *         "parentId" : 10000
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object ContentRO</td><td>
     * <pre><code>
     *         &lt;content category-id="10002" parent-id="10000"&gt;
     *             &lt;breadcrumbs&gt;
     *                 &lt;breadcrumb category-id="10002"&gt;
     *                     &lt;name&gt;Sitemap&lt;/name&gt;
     *                     &lt;uri&gt;sitemap&lt;/uri&gt;
     *                 &lt;/breadcrumb&gt;
     *             &lt;/breadcrumbs&gt;
     *             &lt;content-body&gt;
     *                &lt;!-- Escaped content body --&gt;
     *
     *                 &lt;p&gt;This page demonstrates dynamic content features&lt;/p&gt;
     *
     *                 &lt;p&gt;Links:
     *                 &lt;ul&gt;
     *                 &lt;li&gt;&lt;a href="${contentURL('license')}"&gt;License page (content link)&lt;/a&gt;&lt;/li&gt;
     *                 &lt;li&gt;&lt;a href="${categoryURL('netbooks')}"&gt;Notebooks (category link)&lt;/a&gt;&lt;/li&gt;
     *                 &lt;li&gt;&lt;a href="${URL('')}"&gt;Home (plain link)&lt;/a&gt;&lt;/li&gt;
     *                 &lt;/ul&gt;
     *                 &lt;/p&gt;
     *
     *                 &lt;p&gt;Dynamic variable: ${datetime}&lt;/p&gt;
     *
     *                 &lt;p&gt;Dynamic include:&lt;/p&gt;
     *
     *                 ${include('license')}
     *
     *
     *             &lt;/content-body&gt;
     *             &lt;description&gt;Dynamic Content Site Map Page&lt;/description&gt;
     *             &lt;name&gt;Sitemap&lt;/name&gt;
     *             &lt;rank&gt;0&lt;/rank&gt;
     *             &lt;uitemplate&gt;dynocontent&lt;/uitemplate&gt;
     *             &lt;uri&gt;sitemap&lt;/uri&gt;
     *         &lt;/content&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     *
     * @param content SEO URI or categoryId
     * @param request request
     * @param response response
     *
     * @return content object
     */
    @RequestMapping(
            value = "/view/{id}",
            method = RequestMethod.GET,
            produces =  { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ContentRO viewContent(@PathVariable(value = "id") final String content,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return viewContentInternal(content, null);
    }


    /**
     * Interface: PUT /yes-api/rest/content/view/{id}
     * <p>
     * <p>
     * Display dynamic content. uitemplate is is correctly resolved using central view resolver.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>Content-Type</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>id</td><td>SEO URI or categoryId</td></tr>
     *     <tr><td>JSON variables to dynamic content</td><td>
     * <pre><code>
     *         {
     *             "datetime": "2015-01-01"
     *         }
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object ContentRO</td><td>
     * <pre><code>
     * {
     *         "availablefrom" : null,
     *         "categoryId" : 10002,
     *         "description" : "Dynamic Content Site Map Page",
     *         "metakeywords" : null,
     *         "uri" : "sitemap",
     *         "metadescription" : null,
     *         "contentBody" : "\n<p>This page demonstrates dynamic content featur ... \n\n\n",
     *         "availableto" : null,
     *         "breadcrumbs" : [
     *             {
     *                 "displayNames" : null,
     *                 "categoryId" : 10002,
     *                 "uri" : "sitemap",
     *                 "name" : "Sitemap"
     *             }
     *         ],
     *         "title" : null,
     *         "rank" : 0,
     *         "uitemplate" : "dynocontent",
     *         "attributes" : [],
     *         "displayTitles" : null,
     *         "displayNames" : null,
     *         "displayMetadescriptions" : null,
     *         "displayMetakeywords" : null,
     *         "name" : "Sitemap",
     *         "parentId" : 10000
     * }
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     *
     * @param content SEO URI or categoryId
     * @param request request
     * @param response response
     *
     * @return content object
     */
    @RequestMapping(
            value = "/view/{id}",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody ContentRO viewContent(@PathVariable(value = "id") final String content,
                                               @RequestBody final Map<String, Object> params,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return viewContentInternal(content, params);
    }

    /**
     * Interface: PUT /yes-api/rest/content/view/{id}
     * <p>
     * <p>
     * Display dynamic content. uitemplate is is correctly resolved using central view resolver.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>Content-Type</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid (optional)</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>id</td><td>SEO URI or categoryId</td></tr>
     *     <tr><td>XML variables to dynamic content</td><td>
     * <pre><code>
     *         &lt;parameters&gt;
     *             &lt;parameter key="datetime"&gt;2015-01-01&lt;/entry&gt;
     *         &lt;/parameters&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML object ContentRO</td><td>
     * <pre><code>
     *         &lt;content category-id="10002" parent-id="10000"&gt;
     *             &lt;breadcrumbs&gt;
     *                 &lt;breadcrumb category-id="10002"&gt;
     *                     &lt;name&gt;Sitemap&lt;/name&gt;
     *                     &lt;uri&gt;sitemap&lt;/uri&gt;
     *                 &lt;/breadcrumb&gt;
     *             &lt;/breadcrumbs&gt;
     *             &lt;content-body&gt;
     *                &lt;!-- Escaped content body --&gt;
     *
     *                     &lt;p&gt;This page demonstrates dynamic content features&lt;/p&gt;
     *
     *                     &lt;p&gt;Links:
     *                     &lt;ul&gt;
     *                     &lt;li&gt;&lt;a href="http://testdevshop.yes-cart.org/yes-shop/content/license"&gt;License page (content link)&lt;/a&gt;&lt;/li&gt;
     *                     &lt;li&gt;&lt;a href="http://testdevshop.yes-cart.org/yes-shop/category/netbooks"&gt;Notebooks (category link)&lt;/a&gt;&lt;/li&gt;
     *                     &lt;li&gt;&lt;a href="http://testdevshop.yes-cart.org/yes-shop/"&gt;Home (plain link)&lt;/a&gt;&lt;/li&gt;
     *                     &lt;/ul&gt;
     *                     &lt;/p&gt;
     *
     *                     &lt;p&gt;Dynamic variable: 2015-01-01&lt;/p&gt;
     *
     *                     &lt;p&gt;Dynamic include:&lt;/p&gt;
     *
     *
     *             &lt;/content-body&gt;
     *             &lt;description&gt;Dynamic Content Site Map Page&lt;/description&gt;
     *             &lt;name&gt;Sitemap&lt;/name&gt;
     *             &lt;rank&gt;0&lt;/rank&gt;
     *             &lt;uitemplate&gt;dynocontent&lt;/uitemplate&gt;
     *             &lt;uri&gt;sitemap&lt;/uri&gt;
     *         &lt;/content&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     *
     * @param content SEO URI or categoryId
     * @param request request
     * @param response response
     *
     * @return content object
     */
    @RequestMapping(
            value = "/view/{id}",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes = MediaType.APPLICATION_XML_VALUE
    )
    public @ResponseBody ContentRO viewContentXML(@PathVariable(value = "id") final String content,
                                                  @RequestBody final XMLParamsRO params,
                                                  final HttpServletRequest request,
                                                  final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return viewContentInternal(content, (Map) params.getParameters());
    }

    private List<ContentRO> listContentInternal(final String content) {

        final long contentId = bookmarkMixin.resolveContentId(content);
        final long contentShopId = cartMixin.getCurrentShopId();

        final String lang = cartMixin.getCurrentCart().getCurrentLocale();

        final List<Category> menu = contentServiceFacade.getCurrentContentMenu(contentId, contentShopId, lang);

        if (!menu.isEmpty()) {

            final List<ContentRO> cnts = mappingMixin.map(menu, ContentRO.class, Category.class);
            if (cnts.size() > 0) {

                for (final ContentRO cnt : cnts) {
                    final List<BreadcrumbRO> crumbs = generateBreadcrumbs(cnt.getCategoryId(), contentShopId);
                    cnt.setBreadcrumbs(crumbs);
                    removeContentBodyAttributes(cnt);
                }

            }

            return cnts;

        }

        return new ArrayList<ContentRO>();

    }

    /**
     * Interface: GET /yes-api/rest/content/menu/{id}
     * <p>
     * <p>
     * Display content menu. uitemplate is taken from the object without failover.
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
     *     <tr><td>JSON array of object ContentRO</td><td>
     * <pre><code>
     * [{
     *         "availablefrom" : null,
     *         "categoryId" : 10002,
     *         "description" : "Dynamic Content Site Map Page",
     *         "metakeywords" : null,
     *         "uri" : "sitemap",
     *         "metadescription" : null,
     *         "contentBody" : "\n<p>This page demonstrates dynamic content featur ... \n\n\n",
     *         "availableto" : null,
     *         "breadcrumbs" : [
     *             {
     *                 "displayNames" : null,
     *                 "categoryId" : 10002,
     *                 "uri" : "sitemap",
     *                 "name" : "Sitemap"
     *             }
     *         ],
     *         "title" : null,
     *         "rank" : 0,
     *         "uitemplate" : "dynocontent",
     *         "attributes" : [],
     *         "displayTitles" : null,
     *         "displayNames" : null,
     *         "displayMetadescriptions" : null,
     *         "displayMetakeywords" : null,
     *         "name" : "Sitemap",
     *         "parentId" : 10000
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param content SEO URI or categoryId
     * @param request request
     * @param response response
     *
     * @return content object
     */
    @RequestMapping(
            value = "/menu/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody List<ContentRO> listContent(@PathVariable(value = "id") final String content,
                                                     final HttpServletRequest request,
                                                     final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return listContentInternal(content);

    }

    /**
     * Interface: GET /yes-api/rest/content/menu/{id}
     * <p>
     * <p>
     * Display content menu. uitemplate is taken from the object without failover.
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
     *     <tr><td>XML of objects ContentRO</td><td>
     * <pre><code>
     *   &lt;site&gt;
     *         &lt;content category-id="10002" parent-id="10000"&gt;
     *             &lt;breadcrumbs&gt;
     *                 &lt;breadcrumb category-id="10002"&gt;
     *                     &lt;name&gt;Sitemap&lt;/name&gt;
     *                     &lt;uri&gt;sitemap&lt;/uri&gt;
     *                 &lt;/breadcrumb&gt;
     *             &lt;/breadcrumbs&gt;
     *             &lt;content-body&gt;
     *                &lt;!-- Escaped content body --&gt;
     *
     *                 &lt;p&gt;This page demonstrates dynamic content features&lt;/p&gt;
     *
     *                 &lt;p&gt;Links:
     *                 &lt;ul&gt;
     *                 &lt;li&gt;&lt;a href="${contentURL('license')}"&gt;License page (content link)&lt;/a&gt;&lt;/li&gt;
     *                 &lt;li&gt;&lt;a href="${categoryURL('netbooks')}"&gt;Notebooks (category link)&lt;/a&gt;&lt;/li&gt;
     *                 &lt;li&gt;&lt;a href="${URL('')}"&gt;Home (plain link)&lt;/a&gt;&lt;/li&gt;
     *                 &lt;/ul&gt;
     *                 &lt;/p&gt;
     *
     *                 &lt;p&gt;Dynamic variable: ${datetime}&lt;/p&gt;
     *
     *                 &lt;p&gt;Dynamic include:&lt;/p&gt;
     *
     *                 ${include('license')}
     *
     *
     *             &lt;/content-body&gt;
     *             &lt;description&gt;Dynamic Content Site Map Page&lt;/description&gt;
     *             &lt;name&gt;Sitemap&lt;/name&gt;
     *             &lt;rank&gt;0&lt;/rank&gt;
     *             &lt;uitemplate&gt;dynocontent&lt;/uitemplate&gt;
     *             &lt;uri&gt;sitemap&lt;/uri&gt;
     *         &lt;/content&gt;
     *   &lt;/site&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param content SEO URI or categoryId
     * @param request request
     * @param response response
     *
     * @return content object
     */
    @RequestMapping(
            value = "/menu/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public @ResponseBody ContentListRO listContentXML(@PathVariable(value = "id") final String content,
                                                      final HttpServletRequest request,
                                                      final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        return new ContentListRO(listContentInternal(content));

    }


    private Pair<String, String> resolveTemplate(final ContentRO catRO) {
        final Map params = new HashMap();
        params.put(WebParametersKeys.CONTENT_ID, String.valueOf(catRO.getCategoryId()));
        return centralViewResolver.resolveMainPanelRendererLabel(params);
    }


    private List<BreadcrumbRO> generateBreadcrumbs(final long contentId, final long shopId) {


        final List<BreadcrumbRO> crumbs = new ArrayList<BreadcrumbRO>();

        long current = contentId;

        while(true) {

            Category cat = contentServiceFacade.getContent(current, shopId);

            if (cat == null || CentralViewLabel.INCLUDE.equals(cat.getUitemplate()) || cat.isRoot()) {
                break;
            }

            final BreadcrumbRO crumb = mappingMixin.map(cat, BreadcrumbRO.class, Category.class);
            crumbs.add(crumb);

            current = cat.getParentId();

        }

        Collections.reverse(crumbs);

        return crumbs;

    }

    private void removeContentBodyAttributes(final ContentRO content) {

        final Set<AttrValueCategoryRO> attrs = content.getAttributes();
        if (attrs != null) {

            final List<Attribute> cntAttrs = attributeService.getAvailableAttributesByGroupCodeStartsWith(
                    AttributeGroupNames.CATEGORY, "CONTENT_BODY_");
            if (!cntAttrs.isEmpty()) {
                final List<Long> cntAttrsIds = new ArrayList<Long>();
                for (final Attribute cntAttr : cntAttrs) {
                    cntAttrsIds.add(cntAttr.getId());
                }

                final Iterator<AttrValueCategoryRO> it = attrs.iterator();
                while (it.hasNext()) {

                    final AttrValueCategoryRO val = it.next();
                    if (cntAttrsIds.contains(val.getAttributeId())) {
                        it.remove();
                    }
                }
            }
        }

    }

    private String generateContentBody(final long contentId, final long shopId, final Map<String, Object> parameters) {

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final String locale = cart.getCurrentLocale();

        if (parameters != null && !parameters.isEmpty()) {

            final Map<String, Object> params = new HashMap<String, Object>(parameters);

            params.put("shop", cartMixin.getCurrentShop());
            params.put("shoppingCart", cart);

            return contentServiceFacade.getDynamicContentBody(contentId, shopId, locale, params);

        }

        return contentServiceFacade.getContentBody(contentId, shopId, locale);

    }

}
