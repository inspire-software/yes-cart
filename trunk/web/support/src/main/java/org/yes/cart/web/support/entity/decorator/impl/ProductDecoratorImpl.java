package org.yes.cart.web.support.entity.decorator.impl;

import org.springframework.beans.BeanUtils;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.impl.ProductEntity;
import org.yes.cart.web.support.entity.decorator.ProductDecorator;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:39 PM
 */
public class ProductDecoratorImpl extends ProductEntity implements ProductDecorator {

    /**
     * Construct decorated for web product.
     *
     * @param productEntity original product.
     */
    public ProductDecoratorImpl(final Product productEntity) {

        BeanUtils.copyProperties(productEntity, this);
    }
}
