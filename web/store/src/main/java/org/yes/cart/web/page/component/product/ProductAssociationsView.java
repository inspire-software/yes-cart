package org.yes.cart.web.page.component.product;

import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.HomePage;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.service.domain.ProductAssociationService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.domain.entity.Product;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 *
 * Product associationa panel, that can show configured from markup
 * associations like cross/up/accessories/sell/etc
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 16-Sep-2011
 * Time: 15:36:10
 */
public class ProductAssociationsView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String PRODUCT_LIST = "products";
    private final static String PRODUCT_NAME_LINK = "productLinkName";
    // ------------------------------------- MARKUP IDs END   ---------------------------------- //

    @SpringBean(name = ServiceSpringKeys.PRODUCT_ASSOCIATIONS_SERVICE)
    private ProductAssociationService productAssociationService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SKU_SERVICE)
    protected ProductSkuService productSkuService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;


    private final String assotiationType;
    private List<ProductAssociation> associatedProductList = null;


    /**
     * Construct product association view.
     * @param id component id
     * @param assotiationType type of association
     */
    public ProductAssociationsView(final String id, final String assotiationType) {
        super(id);
        this.assotiationType = assotiationType;
    }


    @Override
    protected void onBeforeRender() {
        if (isVisible()) {

            final ListView<ProductAssociation> productListView = new ListView<ProductAssociation>(PRODUCT_LIST, getAssociatedProducts()) {
                protected void populateItem(ListItem<ProductAssociation> listItem) {
                    final ProductAssociation prodAssociation = listItem.getModelObject();
                    final Product prod = productService.getProductById(prodAssociation.getProductAssociated().getProductId());
                    /*final ContextImage defaultimage = getImage(
                            AbstractProductPanel.DEFAULT_IMAGE,
                            Constants.PRODUCT_DEFAULT_IMAGE_ATTR_NAME,
                            prod,
                            getProductThumbnailImageWidth(),
                            getProductThumbnailImageHeight());*/
                   /* final PageParameters pageParameters = new PageParameters(getValueMapParameters(prod));

                    final Link link = new BookmarkablePageLink<HomePage>(ProductAssociationsView.PRODUCT_LINK, HomePage.class, pageParameters);
                    link.add(defaultimage);

                    final Label productLabel = new Label(AbstractProductPanel.NAME, prod.getName());
                    productLabel.setEscapeModelStrings(false);

                    final Link linkFromName = new BookmarkablePageLink<HomePage>(ProductAssociationsView.PRODUCT_NAME_LINK, HomePage.class, pageParameters);
                    linkFromName.add( productLabel  );

                    listItem.add( link );
                    listItem.add( linkFromName );                 */
                }

            };

            add(productListView);
        }
        super.onBeforeRender();
    }

    private List<ProductAssociation> getAssociatedProducts() {
        if (associatedProductList == null) {
            associatedProductList = productAssociationService.getProductAssociations(
                    getProductId(),
                    assotiationType
            );
        }
        return associatedProductList;
    }

    /**
     * Get product id from paga parameters or from sku
     * @return product id
     */
    private long getProductId() {
        final long productId;
        String productIdStr = getPage().getPageParameters().get(WebParametersKeys.PRODUCT_ID).toString();
        if (productIdStr == null) {
            long skuId = getPage().getPageParameters().get(WebParametersKeys.SKU_ID).toLong();
            productId = productSkuService.getById(skuId).getProduct().getProductId();
        } else {
            productId = Long.valueOf(productIdStr);
        }
        return productId;
    }

    /** {@inheritDoc}*/
    @Override
    public boolean isVisible() {
        return super.isVisible() && !getAssociatedProducts().isEmpty();
    }


}
