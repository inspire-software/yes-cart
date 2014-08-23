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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.ro.CategoryListRO;
import org.yes.cart.domain.ro.CategoryRO;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.support.seo.BookmarkService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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


    @RequestMapping("/top")
    public @ResponseBody List<CategoryRO> listRoot(final HttpServletRequest request,
                                                   final HttpServletResponse response) {

        persistShoppingCart(request, response);
        final List<Category> categories = categoryService.getTopLevelCategories(ShopCodeContext.getShopId());
        return map(categories, CategoryRO.class, Category.class);

    }

    @RequestMapping(value = "/top", headers={ "Accept=application/xml" })
    public @ResponseBody CategoryListRO listRootXML(final HttpServletRequest request,
                                                    final HttpServletResponse response) {

        return new CategoryListRO(listRoot(request, response));

    }

    @RequestMapping("/view/{id}")
    public @ResponseBody CategoryRO viewCategory(@PathVariable(value = "id") final String category,
                                                 final HttpServletRequest request,
                                                 final HttpServletResponse response) {

        persistShoppingCart(request, response);

        final Set<Long> catIds = shopService.getShopCategoriesIds(ShopCodeContext.getShopId());
        final long categoryId = resolveId(category);

        if (categoryId > 0L && catIds.contains(categoryId)) {

            return map(categoryService.getById(categoryId), CategoryRO.class, Category.class);

        }
        return null;

    }

    @RequestMapping("/list/{id}")
    public @ResponseBody List<CategoryRO> listCategory(@PathVariable(value = "id") final String category,
                                                       final HttpServletRequest request,
                                                       final HttpServletResponse response) {

        persistShoppingCart(request, response);

        final Set<Long> catIds = shopService.getShopCategoriesIds(ShopCodeContext.getShopId());
        final long categoryId = resolveId(category);

        if (categoryId > 0L && catIds.contains(categoryId)) {

            return map(categoryService.getChildCategories(categoryId), CategoryRO.class, Category.class);

        }
        return new ArrayList<CategoryRO>();

    }

    @RequestMapping(value = "/list/{id}", headers={ "Accept=application/xml" })
    public @ResponseBody CategoryListRO listCategoryXML(@PathVariable(value = "id") final String category,
                                                        final HttpServletRequest request,
                                                        final HttpServletResponse response) {

        return new CategoryListRO(listCategory(category, request, response));

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

}
