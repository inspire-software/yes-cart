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

package org.yes.cart.web.service.rest;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.ro.*;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.seo.BookmarkService;
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
public class ContentController extends AbstractApiController {

    @Autowired
    private ContentServiceFacade contentServiceFacade;
    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private AttributeService attributeService;


    private ContentRO viewContentInternal(final String content,
                                          final Map<String, Object> contentParams) {

        final long contentId = resolveId(content);
        final long shopId = ShopCodeContext.getShopId();

        final Category contentEntity = contentServiceFacade.getContent(contentId, shopId);

        if (contentEntity != null && !CentralViewLabel.INCLUDE.equals(contentEntity.getUitemplate())) {

            final ContentRO cntRO = map(contentEntity, ContentRO.class, Category.class);
            cntRO.setBreadcrumbs(generateBreadcrumbs(cntRO.getCategoryId(), shopId));
            removeContentBodyAttributes(cntRO);
            cntRO.setContentBody(generateContentBody(cntRO.getCategoryId(), shopId, contentParams));
            return cntRO;

        }

        return null;

    }

    @RequestMapping(
            value = "/view/{id}",
            method = RequestMethod.GET,
            produces =  { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ContentRO viewContent(@PathVariable(value = "id") final String content,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) {

        persistShoppingCart(request, response);

        return viewContentInternal(content, null);
    }

    @RequestMapping(
            value = "/view/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody ContentRO viewContent(@PathVariable(value = "id") final String content,
                                               @RequestBody final Map<String, Object> params,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) {

        persistShoppingCart(request, response);

        return viewContentInternal(content, params);
    }

    @RequestMapping(
            value = "/view/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_XML_VALUE,
            consumes = MediaType.APPLICATION_XML_VALUE
    )
    public @ResponseBody ContentRO viewContentXML(@PathVariable(value = "id") final String content,
                                                  @RequestBody final XMLParamsRO params,
                                                  final HttpServletRequest request,
                                                  final HttpServletResponse response) {

        persistShoppingCart(request, response);

        return viewContentInternal(content, (Map) params.getParameters());
    }

    private List<ContentRO> listContentInternal(final String content) {

        final long contentId = resolveId(content);
        final long shopId = ShopCodeContext.getShopId();

        final List<Category> menu = contentServiceFacade.getCurrentContentMenu(contentId, shopId);

        if (!menu.isEmpty()) {

            final List<ContentRO> cnts = map(menu, ContentRO.class, Category.class);
            if (cnts.size() > 0) {

                for (final ContentRO cnt : cnts) {
                    final List<BreadcrumbRO> crumbs = generateBreadcrumbs(cnt.getCategoryId(), shopId);
                    cnt.setBreadcrumbs(crumbs);
                    removeContentBodyAttributes(cnt);
                }

            }

            return cnts;

        }

        return new ArrayList<ContentRO>();

    }

    @RequestMapping(
            value = "/menu/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody List<ContentRO> listContent(@PathVariable(value = "id") final String content,
                                                     final HttpServletRequest request,
                                                     final HttpServletResponse response) {

        persistShoppingCart(request, response);

        return listContentInternal(content);

    }

    @RequestMapping(
            value = "/menu/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public @ResponseBody ContentListRO listContentXML(@PathVariable(value = "id") final String category,
                                                      final HttpServletRequest request,
                                                      final HttpServletResponse response) {

        persistShoppingCart(request, response);

        return new ContentListRO(listContentInternal(category));

    }

    private long resolveId(final String content) {
        final long contentId = NumberUtils.toLong(content, 0L);
        if (contentId > 0L) {
            bookmarkService.saveBookmarkForContent(content);
            return contentId;
        }
        final String contentIdStr = bookmarkService.getContentForURI(content);
        return NumberUtils.toLong(contentIdStr, 0L);
    }

    private List<BreadcrumbRO> generateBreadcrumbs(final long contentId, final long shopId) {


        final List<BreadcrumbRO> crumbs = new ArrayList<BreadcrumbRO>();

        long current = contentId;

        while(true) {

            Category cat = contentServiceFacade.getContent(current, shopId);

            if (cat == null || CentralViewLabel.INCLUDE.equals(cat.getUitemplate()) || cat.isRoot()) {
                break;
            }

            final BreadcrumbRO crumb = map(cat, BreadcrumbRO.class, Category.class);
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

        final String locale = getCurrentCart().getCurrentLocale();

        if (parameters != null && !parameters.isEmpty()) {

            return contentServiceFacade.getDynamicContentBody(contentId, shopId, locale, parameters);

        }

        return contentServiceFacade.getContentBody(contentId, shopId, locale);

    }

}
