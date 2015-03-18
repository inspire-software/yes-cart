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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.ro.BreadcrumbRO;
import org.yes.cart.domain.ro.CategoryListRO;
import org.yes.cart.domain.ro.CategoryRO;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.seo.BookmarkService;
import org.yes.cart.web.support.service.CategoryServiceFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 19/08/2014
 * Time: 23:42
 */
@Controller
@RequestMapping("/category")
public class CategoryController extends AbstractApiController {

    @Autowired
    private CategoryServiceFacade categoryServiceFacade;
    @Autowired
    private BookmarkService bookmarkService;


    private List<CategoryRO> listRootInternal() {

        final List<Category> categories = categoryServiceFacade.getCurrentCategoryMenu(0l, ShopCodeContext.getShopId());
        return map(categories, CategoryRO.class, Category.class);

    }


    @RequestMapping(
            value = "/menu",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody List<CategoryRO> listRoot(final HttpServletRequest request,
                                                   final HttpServletResponse response) {

        persistShoppingCart(request, response);

        return listRootInternal();

    }

    @RequestMapping(
            value = "/menu",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public @ResponseBody CategoryListRO listRootXML(final HttpServletRequest request,
                                                    final HttpServletResponse response) {

        persistShoppingCart(request, response);

        return new CategoryListRO(listRootInternal());

    }

    @RequestMapping(
            value = "/view/{id}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CategoryRO viewCategory(@PathVariable(value = "id") final String category,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        persistShoppingCart(request, response);

        final long categoryId = resolveId(category);
        final long shopId = ShopCodeContext.getShopId();

        final Category categoryEntity = categoryServiceFacade.getCategory(categoryId, shopId);

        if (categoryEntity != null && !CentralViewLabel.INCLUDE.equals(categoryEntity.getUitemplate())) {

            final CategoryRO catRO = map(categoryEntity, CategoryRO.class, Category.class);
            catRO.setBreadcrumbs(generateBreadcrumbs(catRO.getCategoryId(), shopId));
            return catRO;

        }

        return null;

    }

    private List<CategoryRO> listCategoryInternal(final String category) {

        final long categoryId = resolveId(category);
        final long shopId = ShopCodeContext.getShopId();

        final List<Category> menu = categoryServiceFacade.getCurrentCategoryMenu(categoryId, shopId);

        if (!menu.isEmpty()) {

            final List<CategoryRO> cats = map(menu, CategoryRO.class, Category.class);
            if (cats.size() > 0) {

                for (final CategoryRO cat : cats) {
                    final List<BreadcrumbRO> crumbs = generateBreadcrumbs(cat.getCategoryId(), shopId);
                    cat.setBreadcrumbs(crumbs);
                }

            }

            return cats;

        }
        return new ArrayList<CategoryRO>();
    }

    @RequestMapping(
            value = "/menu/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody List<CategoryRO> listCategory(@PathVariable(value = "id") final String category,
                                                       final HttpServletRequest request,
                                                       final HttpServletResponse response) {

        persistShoppingCart(request, response);

        return listCategoryInternal(category);

    }

    @RequestMapping(
            value = "/menu/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public @ResponseBody CategoryListRO listCategoryXML(@PathVariable(value = "id") final String category,
                                                        final HttpServletRequest request,
                                                        final HttpServletResponse response) {

        persistShoppingCart(request, response);

        return new CategoryListRO(listCategoryInternal(category));

    }

    private long resolveId(final String category) {
        final long categoryId = NumberUtils.toLong(category, 0L);
        if (categoryId > 0L) {
            bookmarkService.saveBookmarkForCategory(category);
            return categoryId;
        }
        final String categoryIdStr = bookmarkService.getCategoryForURI(category);
        return NumberUtils.toLong(categoryIdStr, 0L);
    }

    private List<BreadcrumbRO> generateBreadcrumbs(final long categoryId, final long shopId) {

        final List<BreadcrumbRO> crumbs = new ArrayList<BreadcrumbRO>();

        long current = categoryId;

        while(true) {

            Category cat = categoryServiceFacade.getCategory(current, shopId);

            if (cat == null || CentralViewLabel.INCLUDE.equals(cat.getUitemplate())) {
                break;
            }

            final BreadcrumbRO crumb = map(cat, BreadcrumbRO.class, Category.class);
            crumbs.add(crumb);

            current = cat.getParentId();

        }

        Collections.reverse(crumbs);

        return crumbs;

    }

}
