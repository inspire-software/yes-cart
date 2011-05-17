package org.yes.cart.breadcrumbs;

import org.yes.cart.constants.WebParametersKeys;
import org.yes.cart.util.NavigationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.domain.entity.Category;

import java.util.*;

/**
 * Bread crumbs builder produce category and
 * attributive filtered navigation breadcrumbs based on
 * web query string and context.
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 9:50:51 AM
 */
public class BreadCrumbsBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(BreadCrumbsBuilder.class);

    private final CrumbNamePrefixProvider namePrefixProvider;
    private final List<Long> shopCategoryIds;
    private final long categoryId;
    private final Map pageParameters;
    private final List<Object> allowedAttributeNames;
    private final CategoryService categoryService;

    /**
     *
     * Bread crumbs builder constructor.
     *
     * @param categoryId current category id
     * @param pageParameters current query string
     * @param allowedAttributeNames allowed attribute names for filtering including price, brand, search...
     * @param shopCategoryIds all categoryIds, that belong to shop
     * @param namePrefixProvider name prifix provider for price, brand, search..
     * @param categoryService category service
     */
    public BreadCrumbsBuilder(
            final long categoryId,
            final Map pageParameters,
            final List<Object> allowedAttributeNames,
            final List<Long> shopCategoryIds,
            final CrumbNamePrefixProvider namePrefixProvider,
            final CategoryService categoryService) {

        this.namePrefixProvider = namePrefixProvider;
        this.shopCategoryIds = shopCategoryIds;
        this.categoryId = categoryId;
        this.pageParameters = pageParameters;
        this.allowedAttributeNames = allowedAttributeNames;
        this.categoryService = categoryService;
    }


    /**
     * We have 2 kinds of breadcrumbs:
     * 1. category path, for example electronics -> phones -> ip phones
     * 2. attributive filters, for example ip phones [price range, brands, weight, ect]
     */
    public List<Crumb> getBreadCrumbs() {
        List<Crumb> crumbs = new ArrayList<Crumb>();
        crumbs.addAll(getCategoriesCrumbs(categoryId));
        crumbs.addAll(getFilteredNavigationCrumbs(allowedAttributeNames));
        return crumbs;
    }

    private List<Crumb> getFilteredNavigationCrumbs(final List<Object> allowedAttributeNames) {
        final List<Crumb> navigationCrumbs = new ArrayList<Crumb>();
        fillAttributes(navigationCrumbs, allowedAttributeNames);
        return navigationCrumbs;
    }

    private List<Crumb> getCategoriesCrumbs(final long categoryId) {
        final List<Crumb> categoriesCrumbs = new ArrayList<Crumb>();
        if (categoryId > 0) {
            fillCategories(categoriesCrumbs, categoryId);
            Collections.reverse(categoriesCrumbs);
        }
        return categoriesCrumbs;
    }


    /**
     * Recursive function to reverse build the breadcrumbs by categories, starting from currently selected one.
     *
     * @param categoriesCrumbs the crumbs list
     * @param categoryId the current category id
     */
    private void fillCategories(final List<Crumb> categoriesCrumbs, final long categoryId) {
        final Category category = categoryService.getById(categoryId);
        if (categoryId != category.getParentId()) {
            categoriesCrumbs.add(new Crumb(category.getName(),
                    getCategoryLinkParameters(categoryId),
                    getRemoveCategoryLinkParameters(category))
            );

            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding breadcrumb for category: [" + categoryId + "] " + category.getName());
            }
            fillCategories(categoriesCrumbs, category.getParentId());
        }
    }

    public LinkedHashMap getCategoryLinkParameters(final long categoryId) {
        final LinkedHashMap parameters = new LinkedHashMap();
        parameters.put(WebParametersKeys.CATEGORY_ID, categoryId);
        return parameters;
    }

    private LinkedHashMap getRemoveCategoryLinkParameters(final Category category) {
        if (shopCategoryIds.contains(category.getParentId())) {
            return getCategoryLinkParameters(category.getParentId());
        }
        return new LinkedHashMap();
    }

    private void fillAttributes(
            final List<Crumb> navigationCrumbs,
            final List<Object> allowedAttributeNames) {

        //This is attributive only filtered navigation from request
        final LinkedHashMap<String, ?> attributesOnly = NavigationUtil.getRetainedRequestParameters(
                pageParameters,
                allowedAttributeNames);

        //Base hold category path from begining and accomulate all attributive navigation
        final LinkedHashMap<String, ?> base = NavigationUtil.getFilteredRequestParameters(
                pageParameters,
                allowedAttributeNames);

        //If we are on display product page, we have to remove for filtering
        base.remove(WebParametersKeys.PRODUCT_ID);

        for (Map.Entry<String, ?> entry : attributesOnly.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue() instanceof Object[] ) {
                for (Object obj : (Object[])entry.getValue()) {
                    navigationCrumbs.add(createFilteredNavigationCrumb(base, key, obj));
                }
            } else {
                navigationCrumbs.add(createFilteredNavigationCrumb(base, key, entry.getValue()));
            }
        }
    }

    /**
     * Create filtered navigation crubm with two links:
     *
     * First - curreent postion, that include the whole path before current.
     *     example category/17/subcategory/156/proce/100-200/currentkey/currentvalue
     *
     * Second - the whole current path without current
     *     example category/17/subcategory/156/proce/100-200/currentkey/currentvalue/nextkey/nextvalue
     *                                                       ^^^^^^^^^^^^^^^^^^^^^^^ this will be removed,
     * so uri will be
     *     example category/17/subcategory/156/proce/100-200/nextkey/nextvalue
     *
     *
     * @param base initial parameter map, usually category and sub category navigation
     * @param key current key
     * @param value current value
     * @return {@link Crumb}
     */
    private Crumb createFilteredNavigationCrumb(
            final LinkedHashMap base,
            final String key,
            final Object value) {
        final String stringValue = String.valueOf(value);

        final LinkedHashMap<String, ?> withoutCurrent = getAllPathWithoutMe(key, value);

        final String linkName = namePrefixProvider.getLinkNamePrefix(key)
                + "::" 
                + namePrefixProvider.getLinkName(key, stringValue);
        base.put(key, value);
        return new Crumb(linkName, new LinkedHashMap(base), withoutCurrent);
    }

    private LinkedHashMap<String, ?> getAllPathWithoutMe(final String key, final Object value) {
        final LinkedHashMap<String,?> withoutCurrent =
                NavigationUtil.getFilteredRequestParameters(pageParameters);

        //withoutCurrent.remove(key); this can not be used, because of multiple parameters for one key
        //example search/search_term1/search/search_term2
        for (Map.Entry entry : withoutCurrent.entrySet() ) {
            if (entry.getKey().equals(key)) {
                if ( entry.getValue() instanceof Object [] ) {
                    List newValues =  new ArrayList(Arrays.asList((Object []) entry.getValue()));
                    newValues.remove(value);
                    String [] valuesToSet = new String[newValues.size()];
                    entry.setValue(newValues.toArray(valuesToSet));
                    break;
                }
                withoutCurrent.entrySet().remove(entry);
                break;
            }
        }
        return withoutCurrent;
    }

}
