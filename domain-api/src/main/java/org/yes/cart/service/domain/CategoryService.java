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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Category;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CategoryService extends GenericService<Category> {

    /**
     * Get the root category.
     *
     * @return root category.
     */
    Category getRootCategory();

    /**
     * Resolve links to given category (i.e. virtual parents from other category branches).
     *
     * @param categoryId given category PK
     *
     * @return link of links to this category
     */
    List<Long> getCategoryLinks(long categoryId);

    /**
     * Get the "template variation" template (No fail over).
     *
     * @param categoryId given category PK
     * @return Template variation
     */
    String getCategoryTemplate(long categoryId);

    /**
     * Get the "template variation" template (No fail over).
     *
     * @param categoryId given category PK
     * @return Template variation
     */
    String getCategorySearchTemplate(long categoryId);

    /**
     * Get category type with failover mechanism. Uses category product type if set, otherwise
     * looks up parent category product type, until one is found
     *
     * @param categoryId category id
     *
     * @return product type for this category
     */
    Long getCategoryProductTypeId(long categoryId);

    /**
     * Does given this category have at least one sub category.
     *
     * @param categoryId   given categoryId
     *
     * @return true if at least one sub category exists
     */
    boolean isCategoryHasChildren(long categoryId);

    /**
     * Get the child categories without recursion, only one level down.
     *
     * @param categoryId given categoryId
     * @return list of child categories
     */
    List<Category> getChildCategories(long categoryId);

    /**
     * Get the child categories without recursion, only one level down.
     *
     * @param categoryId       given categoryId
     * @param withAvailability with availability date range filtering or not
     * @return list of child categories
     */
    List<Category> findChildCategoriesWithAvailability(long categoryId, boolean withAvailability);

    /**
     * Get the child categories with recursion.
     * Category from parameter will be included also.
     *
     * @param categoryId given categoryId
     * @return list of child categories
     */
    Set<Category> getChildCategoriesRecursive(long categoryId);

    /**
     * Get all ids for all categories represented by given GUIDs.
     *
     * @param guids GUIDs of category branches
     *
     * @return set of ids
     */
    Set<Long> getAllCategoryIds(Collection<String> guids);

    /**
     * Get the child categories with recursion.
     * Category from parameter will be included also.
     *
     * @param categoryId given categoryId
     * @return list of child categories
     */
    List<Long> getChildCategoriesRecursiveIds(long categoryId);

    /**
     * Get the child categories with recursion (including linkToId).
     * Category from parameter will be included also.
     *
     * @param categoryId given categoryId
     * @return list of child categories
     */
    List<Long> getChildCategoriesRecursiveIdsAndLinkIds(long categoryId);

    /**
     * Get category id including linkToId.
     *
     * @param categoryId given categoryId
     * @return list of categories
     */
    List<Long> getCategoryIdAndLinkId(long categoryId);

    /**
     * Get category by id.
     *
     * @param categoryId given category id
     * @return category
     */
    Category getById(long categoryId);

    /**
     * Get category id by given seo uri
     *
     * @param seoUri given seo uri
     * @return category id if found otherwise null
     */
    Long findCategoryIdBySeoUri(String seoUri);

    /**
     * Get category id by given GUID
     *
     * @param guid given GUID
     * @return category id if found otherwise null
     */
    Long findCategoryIdByGUID(String guid);

    /**
     * Get category SEO uri by given id
     *
     * @param categoryId given category id
     * @return seo uri if found otherwise null
     */
    String findSeoUriByCategoryId(Long categoryId);

    /**
     * Get category id by given seo uri
     *
     * @param seoUriOrGuid given seo urior guid
     * @return category id if found otherwise null
     */
    Category findCategoryIdBySeoUriOrGuid(String seoUriOrGuid);

    /**
     * Get all categories, that contains product with given id.
     *
     * @param productId given product id.
     *
     * @return list of categories, that contains product.
     */
    List<Category> findByProductId(long productId);


    /**
     * Does given sub category belong to tree with given parent <code>topCategory</code>.
     *
     * @param topCategoryId given root for category tree.
     * @param subCategoryId candidate to check.
     *
     * @return true in case if category belongs to tree that starts from <code>topCategory</code>
     */
    boolean isCategoryHasSubcategory(long topCategoryId, long subCategoryId);


    /**
     * Find categories by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return list of categories.
     */
    List<Category> findCategories(int start,
                                  int offset,
                                  String sort,
                                  boolean sortDescending,
                                  Map<String, List> filter);

    /**
     * Find categories by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. name, guid)
     *
     * @return count
     */
    int findCategoryCount(Map<String, List> filter);



}
