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

package org.yes.cart.web.page.component.filterednavigation;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dto.FilteredNavigationRecord;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CategoryServiceFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * Responsible to compose widget for filtering navigation.
 * The base widget look following:
 * <pre>
 * -------------
 * |   Color   |
 * -------------
 * |Green     4|
 * |Red       2|
 * |Black     7|
 * -------------
 * </pre>
 * Where Color - atribute name
 * and Green, Red, Black available attribute values and available quantity of products.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/18/11
 * Time: 10:29 AM
 */
public abstract class AbstractProductFilter extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    protected static final String FILTERED_NAVIGATION_LIST = "filteredNavigationList";
    protected static final String FILTER = "filteredNavigation";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CATEGORY_SERVICE_FACADE)
    protected CategoryServiceFacade categoryServiceFacade;

    private final long categoryId;
    private Category category;
    private Category defaultCategory;

    private List<FilteredNavigationRecord> navigationRecords = null;

    private final List<Long> categories;

    private final NavigationContext navigationContext;

    private final int recordLimit;

    /**
     * Construct panel.
     *
     * @param id    panel id
     * @param categoryId current category id
     * @param navigationContext navigation context.
     */
    public AbstractProductFilter(final String id, final long categoryId, final NavigationContext navigationContext) {
        super(id);
        this.navigationContext = navigationContext;
        this.categoryId  = categoryId;

        final long configShopId = getCurrentShopId();
        final long browsingShopId = getCurrentCustomerShopId();
        this.categories = categoryServiceFacade.getSearchCategoriesIds(categoryId, browsingShopId).getFirst();
        this.recordLimit = getCategoryFilterLimitConfig(categoryId, configShopId);
    }

    /**
     * Determine size of the filter nav records. Allows to overwrite the default setting.
     *
     * @param categoryId category
     * @param shopId shop
     *
     * @return max records to render
     */
    protected int getCategoryFilterLimitConfig(final long categoryId, final long shopId) {
        return categoryServiceFacade.getCategoryFilterLimitConfig(categoryId, shopId);
    }

    /**
     * Determine size of the filter nav records. Allows to overwrite the default setting for specific code.
     *
     * @param code filter group code
     * @param limit max limit for category
     *
     * @return max records to render
     */
    protected int getCategoryFilterLimitConfig(final String code, final int limit) {
        return limit;
    }

    /**
     * Get current category.
     *
     * @return {@link Category}
     */
    public Category getCategory() {
        if (category == null) {
            category = categoryServiceFacade.getCategory(categoryId, getCurrentCustomerShopId());
        }
        return category;
    }

    /**
     * Get default category.
     *
     * @return {@link Category}
     */
    public Category getDefaultCategory() {
        if (defaultCategory == null) {
            defaultCategory = categoryServiceFacade.getDefaultNavigationCategory(getCurrentCustomerShopId());
        }
        return defaultCategory;
    }

    /**
     * Get the current subcategories sub tree as list.
     *
     * @return current subcategories sub tree as list.
     */
    protected List<Long> getCategories() {
        return categories;
    }

    /**
     * Adapt list of  {@link FilteredNavigationRecord} into Blocks.
     *
     * @param records navigation records
     * @return list of pairs
     */
    protected List<Block> adaptNavigationRecords(final List<FilteredNavigationRecord> records) {

        final List<Block> navBlocks = new ArrayList<Block>();

        String head = StringUtils.EMPTY;
        Block currentBlock = null;

        for (FilteredNavigationRecord navigationRecord : records) {
            if (!navigationRecord.getName().equalsIgnoreCase(head)) {
                currentBlock = new Block(
                        navigationRecord.getCode(),
                        navigationRecord.getTemplate(),
                        (String) ObjectUtils.defaultIfNull(navigationRecord.getDisplayName(), navigationRecord.getName())
                );

                navBlocks.add(currentBlock);
                head = navigationRecord.getName();
            }
            currentBlock.getValues().add(new Value(
                        adaptValueForLinkLabel(navigationRecord.getValue(), navigationRecord.getDisplayValue()),
                        navigationRecord.getCount(),
                        getNavigationParameter(navigationRecord.getCode(), navigationRecord.getValue())
            ));
        }
        return navBlocks;
    }

    /**
     * Adapt parameter value to be represented as filtered navigation label.
     *
     * @param valueToAdapt value to adapt
     * @param displayValue display value
     * @return  adapted value
     */
    protected String adaptValueForLinkLabel(final String valueToAdapt, final String displayValue) {
        if (displayValue == null) {
            return valueToAdapt;
        }
        return displayValue;
    }

    /**
     * Get page parameters for navigate to products, that will be filtering by given attribute value.
     * @param attributeCode  given attribute code.
     * @param attributeValue  given attribute value.
     * @return composed page parameters.
     */
    protected PageParameters getNavigationParameter(final String attributeCode, final String attributeValue) {
        return getWicketUtil().getFilteredRequestParameters(getPage().getPageParameters())
                .remove(WebParametersKeys.PRODUCT_ID)
                .set(WebParametersKeys.CATEGORY_ID, categoryId)
                .set(attributeCode, attributeValue);

    }

    /** {@inheritDoc} */
    protected void onBeforeRender() {
        add(
                new ListView<Block>(
                        FILTERED_NAVIGATION_LIST,
                        adaptNavigationRecords(navigationRecords)) {
                    protected void populateItem(ListItem<Block> pairListItem) {
                        final Block block = pairListItem.getModelObject();
                        if ("i18n".equalsIgnoreCase(block.getTemplate())) {
                            pairListItem.add(
                                    new I18nFilterView(
                                            FILTER,
                                            block.code,
                                            block.label,
                                            block.getValues(),
                                            getCategoryFilterLimitConfig(block.getCode(), recordLimit)
                                    )
                            );
                        } else {
                            pairListItem.add(
                                    new BaseFilterView(
                                            FILTER,
                                            block.code,
                                            block.label,
                                            block.getValues(),
                                            getCategoryFilterLimitConfig(block.getCode(), recordLimit)
                                    )
                            );
                        }
                    }
                }
        );

        super.onBeforeRender();
    }

    @Override
    public boolean isVisible() {
        return  super.isVisible() && !isShowProduct();
    }


    /**
     *
     * @return true if product or his sku is shown;
     */
    protected boolean isShowProduct() {
        final Set<String> paramNames = getPage().getPageParameters().getNamedKeys();
        return paramNames.contains(WebParametersKeys.PRODUCT_ID)
                || paramNames.contains(WebParametersKeys.SKU_ID);
    }

    /**
     * Get current category id.
     * @return current category id.
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Set particular navigation record to use.
     * @param navigationRecords navigation records.
     */
    public void setNavigationRecords(final List<FilteredNavigationRecord> navigationRecords) {
        this.navigationRecords = navigationRecords;
    }

    /**
     * Get navigation records.
     * @return   list of navigation records.
     */
    public List<FilteredNavigationRecord> getNavigationRecords() {
        return navigationRecords;
    }

    /**
     * Get current lucene product  query.
     *
     * @return current lucene product  query.
     */
    public NavigationContext getNavigationContext() {
        return navigationContext;
    }

    static class Value {

        private final String label;
        private final Integer count;
        private final PageParameters parameters;

        Value(final String label, final Integer count, final PageParameters parameters) {
            this.label = label;
            this.count = count;
            this.parameters = parameters;
        }

        public String getLabel() {
            return label;
        }

        public Integer getCount() {
            return count;
        }

        public PageParameters getParameters() {
            return parameters;
        }
    }

    static class Block {

        private final String label;
        private final String code;
        private final String template;

        private final List<Value> values = new ArrayList<Value>();

        Block(final String code, final String template, final String label) {
            this.code = code;
            this.template = template;
            this.label = label;
        }

        public String getCode() {
            return code;
        }

        public String getTemplate() {
            return template;
        }

        public String getLabel() {
            return label;
        }

        public List<Value> getValues() {
            return values;
        }
    }

}
