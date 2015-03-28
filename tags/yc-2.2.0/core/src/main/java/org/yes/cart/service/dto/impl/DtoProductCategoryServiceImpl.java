/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
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
                                         final DtoFactory dtoFactory,
                                         final AdaptersRepository adaptersRepository
    ) {
        super(dtoFactory, productCategoryGenericService, adaptersRepository);
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * {@inheritDoc}
     */
    public ProductCategoryDTO create(final ProductCategoryDTO instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductCategory productCategory = getPersistenceEntityFactory().getByIface(ProductCategory.class);
        assembler.assembleEntity(instance, productCategory, getAdaptersRepository(), dtoFactory);
        productCategory.setCategory(categoryService.findById(instance.getCategoryId()));
        productCategory.setProduct(productService.findById(instance.getProductId()));
        productCategory = service.create(productCategory);
        return getById(productCategory.getProductCategoryId());
    }

    /**
     * {@inheritDoc}
     */
    public ProductCategoryDTO update(final ProductCategoryDTO instance)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductCategory productCategory = service.findById(instance.getProductCategoryId());
        assembler.assembleEntity(instance, productCategory, getAdaptersRepository(), dtoFactory);
        productCategory.setCategory(categoryService.findById(instance.getCategoryId()));
        productCategory.setProduct(productService.findById(instance.getProductId()));
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

    /**
     * Check is product already assigned to category
     *
     * @param categoryId given category id
     * @param productId  given product id
     */
    public boolean isAssignedCategoryProductIds(final long categoryId, final long productId) {
        return  (((ProductCategoryService)service).findByCategoryIdProductId(categoryId, productId) != null)  ;
    }

    /** {@inheritDoc} */
    public void removeByProductIds(final long productId) {
        ((ProductCategoryService)service).removeByProductIds(productId);

    }

    /** {@inheritDoc}*/
    public int getNextRank(final long categoryId) {
        return ((ProductCategoryService)service).getNextRank(categoryId);

    }

}
