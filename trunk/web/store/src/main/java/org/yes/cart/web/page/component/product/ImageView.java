package org.yes.cart.web.page.component.product;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.entity.decorator.Depictable;
import org.yes.cart.web.util.WicketUtil;

import java.util.List;

/**
 * Product or sku images view.
 * <p/>
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


    private final Depictable depictable;
    private final Category category;


    /**
     * Construct product or sku image view.
     *
     * @param id         component id.
     * @param depictable the something that can be shown.
     */
    public ImageView(final String id, final Depictable depictable) {
        super(id);
        this.depictable = depictable;
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

        final String width = depictable.getDefaultImageWidth(category);
        final String height = depictable.getDefaultImageHeight(category);
        final String tumbWidth = depictable.getThumbnailImageWidth(category);
        final String tumbHeight = depictable.getThumbnailImageHeight(category);
        final List<Pair<String, String>> filledImageAttributes = depictable.getImageAttributeFileNames();


        final String contextRelativePath = depictable.getDefaultImage(
                width, height);


        add(
                createSeoImage(
                        new ContextImage(DEFAULT_IMAGE, contextRelativePath)
                                .add(
                                        new AttributeModifier("width", width),
                                        new AttributeModifier("height", height)
                                ),
                        getSeoImage(filledImageAttributes, depictable.getDefaultImageAttributeName()))

        );


        super.onBeforeRender();
    }

    private Component createSeoImage(final Component component, final SeoImage seoImage) {
        if (seoImage != null) {
            component.add(
                    new AttributeModifier("alt", seoImage.getAlt()),
                    new AttributeModifier("title", seoImage.getTitle())
            ); //todo long descr

        }
        return component;
    }


    private SeoImage getSeoImage(final List<Pair<String, String>> allImages, final String attrName) {
        final String fileName = getFileName(allImages, attrName);
        if (StringUtils.isNotBlank(fileName)) {
            depictable.getSeoImage(fileName);
        }
        return null;
    }


    //TODO not optimal remake
    private String getFileName(final List<Pair<String, String>> allImages, final String attrName) {
        for (Pair<String, String> attrFileName : allImages) {
            if (attrFileName.getFirst().equals(attrName)) {
                return attrFileName.getSecond();
            }
        }
        return null;
    }

}
