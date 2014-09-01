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
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.DomainApiUtil;
import org.yes.cart.web.support.seo.BookmarkService;

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
public class CategoryController extends AbstractApiController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BookmarkService bookmarkService;


    private List<CategoryRO> listRootInternal() {

        final List<Category> categories = categoryService.getTopLevelCategories(getCurrentCart().getShoppingContext().getShopId());
        return map(categories, CategoryRO.class, Category.class);

    }


    @RequestMapping(
            value = "/top",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody List<CategoryRO> listRoot(final HttpServletRequest request,
                                                   final HttpServletResponse response) {

        persistShoppingCart(request, response);

        return listRootInternal();

    }

    @RequestMapping(
            value = "/top",
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

        if (categoryIsVisibleInShop(categoryId)) {

            final CategoryRO cat = map(categoryService.getById(categoryId), CategoryRO.class, Category.class);
            cat.setBreadcrumbs(generateBreadcrumbs(cat.getCategoryId()));
            return cat;

        }

        return null;

    }

    private List<CategoryRO> listCategoryInternal(final String category) {

        final long categoryId = resolveId(category);

        if (categoryIsVisibleInShop(categoryId)) {

            final List<CategoryRO> cats = map(categoryService.getChildCategories(categoryId), CategoryRO.class, Category.class);
            if (cats.size() > 0) {

                final List<BreadcrumbRO> crumbs = generateBreadcrumbs(cats.get(0).getCategoryId());
                for (final CategoryRO cat : cats) {
                    cat.setBreadcrumbs(crumbs);
                }

            }

            return cats;

        }
        return new ArrayList<CategoryRO>();
    }

    @RequestMapping(
            value = "/list/{id}",
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
            value = "/list/{id}",
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

    private boolean categoryIsVisibleInShop(long categoryId) {

        if (categoryId > 0L) {

            final Set<Long> catIds = shopService.getShopCategoriesIds(getCurrentCart().getShoppingContext().getShopId());

            Category cat = categoryService.getById(categoryId);

            final Date now = new Date();

            while (cat != null
                    && DomainApiUtil.isObjectAvailableNow(true, cat.getAvailablefrom(), cat.getAvailableto(), now)
                    && cat.getId() != cat.getParentId()) { // while enabled and not reached root

                if (catIds.contains(categoryId)) {

                    return true;

                }
                cat = categoryService.getById(cat.getParentId());

            }

        }
        return false;
    }

    private List<BreadcrumbRO> generateBreadcrumbs(final long categoryId) {

        final List<Category> categories = categoryService.getTopLevelCategories(getCurrentCart().getShoppingContext().getShopId());
        final List<Long> topCatIds = new ArrayList<Long>();
        for (final Category cat : categories) {
            topCatIds.add(cat.getId());
        }

        Category cat = categoryService.getById(categoryId);

        final List<BreadcrumbRO> crumbs = new ArrayList<BreadcrumbRO>();
        while (!topCatIds.contains(cat.getId())) {

            cat = categoryService.getById(cat.getParentId());

            final BreadcrumbRO crumb = map(cat, BreadcrumbRO.class, Category.class);
            crumbs.add(crumb);

        }

        Collections.reverse(crumbs);

        return crumbs;

    }

}
