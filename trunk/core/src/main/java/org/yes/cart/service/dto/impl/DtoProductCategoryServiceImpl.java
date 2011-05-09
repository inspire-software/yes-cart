package org.yes.cart.service.dto.impl;

import org.yes.cart.domain.dto.ProductCategoryDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ProductCategoryDTOImpl;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.ProductCategoryService;
import org.yes.cart.service.dto.DtoProductCategoryService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductCategoryServiceImpl
    extends AbstractDtoServiceImpl<ProductCategoryDTO, ProductCategoryDTOImpl, ProductCategory>
        implements DtoProductCategoryService {

    private final GenericService<Product> productService;
    private final GenericService<Category> categoryService;


    /**
     * Construct base remote service.
     *
     * @param dtoFactory    {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param productCategoryGenericService       {@link org.yes.cart.service.domain.GenericService}
     */
    public DtoProductCategoryServiceImpl(
                                         final GenericService<ProductCategory> productCategoryGenericService,
                                         final GenericService<Product> productService,
                                         final GenericService<Category> categoryService,
                                         final DtoFactory dtoFactory
    ) {
        super(dtoFactory, productCategoryGenericService, null);
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * {@inheritDoc}
     */
    public ProductCategoryDTO create(final ProductCategoryDTO instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductCategory productCategory = getEntityFactory().getByIface(ProductCategory.class);
        assembler.assembleEntity(instance, productCategory, null, dtoFactory);
        productCategory.setCategory(categoryService.getById(instance.getCategoryId()));
        productCategory.setProduct(productService.getById(instance.getProductId()));
        productCategory = service.create(productCategory);
        return getById(productCategory.getProductCategoryId());
    }

    /**
     * {@inheritDoc}
     */
    public ProductCategoryDTO update(final ProductCategoryDTO instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductCategory productCategory = service.getById(instance.getProductCategoryId());
        assembler.assembleEntity(instance, productCategory, null, dtoFactory);
        productCategory.setCategory(categoryService.getById(instance.getCategoryId()));
        productCategory.setProduct(productService.getById(instance.getProductId()));
        productCategory = service.update(productCategory);
        return getById(productCategory.getProductCategoryId());

    }

    /**
     * Get the dto interface.
     *
     * @return dto interface.
     */
    public Class<ProductCategoryDTO> getDtoIFace() {
        return ProductCategoryDTO.class;
    }

    /**
     * Get the dto implementation class.
     *
     * @return dto implementation class.
     */
    public Class<ProductCategoryDTOImpl> getDtoImpl() {
        return ProductCategoryDTOImpl.class;
    }

    /**
     * Get the entity interface.
     *
     * @return entity interface.
     */
    public Class<ProductCategory> getEntityIFace() {
        return ProductCategory.class;
    }

    /** {@inheritDoc} */
    public void removeByCategoryProductIds(final long categoryId, final long productId) {
        ((ProductCategoryService)service).removeByCategoryProductIds(categoryId, productId);
    }

    /** {@inheritDoc}*/
    public int getNextRank(final long categoryId) {
        return ((ProductCategoryService)service).getNextRank(categoryId);

    }

}
