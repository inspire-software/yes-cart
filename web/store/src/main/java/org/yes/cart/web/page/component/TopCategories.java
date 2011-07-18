package org.yes.cart.web.page.component;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.page.component.data.SortableCategoryDataProvider;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;
import org.yes.cart.web.support.util.HttpUtil;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 8:43 PM
 */
public class TopCategories extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CATEGORY_LIST = "categoryList";
    private final static String CATEGORY_NAME_LINK = "categoryLinkName";
    private final static String CATEGORY_NAME = "categoryName";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    /**
     * Construct top categories view.
     *
     * @param id component id
     */
    public TopCategories(final String id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final List<Category> categories = categoryService.getTopLevelCategories(ApplicationDirector.getCurrentShop());

        final long categoryId = NumberUtils.toLong(getPage().getPageParameters().get(WebParametersKeys.CATEGORY_ID).toString());

        add(
                new ListView<Category>(CATEGORY_LIST, categories) {

                    @Override
                    protected void populateItem(final ListItem<Category> categoryListItem) {

                        final Category category = categoryListItem.getModelObject();

                        final PageParameters pageParameters = new PageParameters().add(WebParametersKeys.CATEGORY_ID, category.getCategoryId());

                        categoryListItem.add(
                                new BookmarkablePageLink<HomePage>(CATEGORY_NAME_LINK, HomePage.class, pageParameters).add(
                                        new Label(CATEGORY_NAME, category.getName()).setEscapeModelStrings(false)
                                )
                        ).add(
                                new AttributeModifier(
                                        "class",
                                        categoryService.isCategoryHasSubcategory(category.getCategoryId(), categoryId) ?
                                                "active-category":""
                                        )
                        );

                    }
                }

        );

        super.onBeforeRender();


    }


}
