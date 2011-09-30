package org.yes.cart.web.page.component.product;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.entity.decorator.Depictable;
import org.yes.cart.web.util.WicketUtil;

/**
 *
 * Product or sku images view.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 9/24/11
 * Time: 11:45 AM
 */
public class ImageView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    protected final static String DEFAULT_IMAGE = "defaultImage";
    protected final static String ALT_IMAGE_LIST = "altImageList";
    protected final static String ALT_IMAGE = "altImage";
    // ------------------------------------- MARKUP IDs END   ---------------------------------- //

    protected final static String DEFAULT_IMAGE_ALT = "defaultImageAlt";
    protected final static String DEFAULT_IMAGE_TITLE = "defaultImageTitle";
    protected final static String DEFAULT_IMAGE_LONGDESC = "defaultImageLongDesc";

    protected final static String ALT_IMAGE_ALT = "altImageAlt";
    protected final static String ALT_IMAGE_TITLE = "altImageTitle";
    protected final static String ALT_IMAGE_LONGDESC = "altImageLongDesc";


    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;


    private final Depictable deciptable;
    private final Category category;


    /**
     * Construct product or sku image view.
     * @param id component id.
     * @param deciptable the something that can be shown.
     */
    public ImageView(final String id, final Depictable deciptable) {
        super(id);
        this.deciptable = deciptable;
        long categoryId = WicketUtil.getCategoryId();
        if (categoryId > 0) {
            category = categoryService.getById(categoryId);
        } else {
            category = categoryService.getRootCategory();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final String width = deciptable.getDefaultImageWidth(category);
        final String height = deciptable.getDefaultImageHeight(category);


        final String contextRelativePath = deciptable.getDefaultImage(
                width,
                height);

        add(
                new ContextImage(DEFAULT_IMAGE,contextRelativePath)
                    .add(
                            new AttributeModifier("width", width),
                            new AttributeModifier("height", height)
                    )

        );


        super.onBeforeRender();
    }
}
