package org.yes.cart.web.page.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 2:01 PM
 */
public class CategoryView extends  BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String CATEGORY_IMAGE_LINK = "categoryLinkImage";
    private final static String CATEGORY_IMAGE = "categoryImage";
    private final static String CATEGORY_NAME_LINK = "categoryLinkName";
    private final static String CATEGORY_NAME = "categoryName";
    private final static String CATEGORY_DESCR_LINK = "categoryLinkDescription";
    private final static String CATEGORY_DESCR = "categoryDescription";
    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //


    /**
     * Construct category view.
     * @param id component id
     */
    public CategoryView(final String id) {
        super(id);
    }

    @Override
    protected void onBeforeRender() {

        final CategoryDecorator category = (CategoryDecorator) this.getParent().getDefaultModel().getObject();

        final PageParameters pageParameters = new PageParameters().add(WebParametersKeys.CATEGORY_ID, category.getCategoryId());

        final String width = category.getImageWidth(category);

        final String height = category.getImageHeight(category);

        add(
            new BookmarkablePageLink<HomePage>(CATEGORY_IMAGE_LINK, HomePage.class, pageParameters).add(
                    new ContextImage(CATEGORY_IMAGE, category.getDefaultImage(width, height))
                            .add(new AttributeModifier("width", width))
                            .add(new AttributeModifier("height", height))
            )
        );

        add(
            new BookmarkablePageLink<HomePage>(CATEGORY_NAME_LINK, HomePage.class, pageParameters).add(
                    new Label(CATEGORY_NAME, category.getName()).setEscapeModelStrings(false)
            )
        );

        add(
            new BookmarkablePageLink<HomePage>(CATEGORY_DESCR_LINK, HomePage.class, pageParameters).add(
                    new Label(CATEGORY_DESCR, category.getName()).setEscapeModelStrings(false)
            )
        );


        super.onBeforeRender();
    }
}
