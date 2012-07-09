package org.yes.cart.web.page.component;

import org.apache.lucene.search.BooleanQuery;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/12/11
 * Time: 10:11 PM
 */
public abstract class AbstractCentralView extends BaseComponent {

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    private final long categoryId;

    private final BooleanQuery booleanQuery;

    private Category category;



    /**
      * Construct panel.
     *
     * @param id panel id
     * @param categoryId  current category id.
     * @param booleanQuery     boolean query.
     */
    public AbstractCentralView(final String id, final long categoryId, final BooleanQuery booleanQuery) {
        super(id);
        this.categoryId = categoryId;
        this.booleanQuery = booleanQuery;
    }

    /**
     * Get category id.
     * @return current category id.
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Get current query.
     * @return  query.
     */
    public BooleanQuery getBooleanQuery() {
        return booleanQuery;
    }

    /**
     * Get category.
     * @return {@link Category}
     */
    public Category getCategory() {
        if (category == null) {
            category = categoryService.getById(categoryId);
        }
        return category;
    }

    /**
     * Get category service
     * @return    category id.
     */
    public CategoryService getCategoryService() {
        return categoryService;
    }
}
