/*
 * Copyright 2013 Igor Azarnyi, Denys Pavlov
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
package org.yes.cart.web.support.entity.decorator.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.impl.AttrValueEntityProductSku;
import org.yes.cart.domain.entity.impl.AttributeEntity;
import org.yes.cart.domain.entity.impl.ProductEntity;
import org.yes.cart.domain.entity.impl.ProductSkuEntity;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.support.i18n.impl.I18NWebSupportImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import org.yes.cart.service.domain.ProductService;

/**
 * User: igor
 * Date: 03.01.14
 * Time: 11:50
 */
@RunWith(org.jmock.integration.junit4.JMock.class)
public class ProductSkuDecoratorImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGetDescriptionFailoverToSkuDescription() throws Exception {
        //"EN#~#Some text#~#RU#~#Текст#~#UK#~#"
        I18NWebSupport i18NWebSupport = new I18NWebSupportImpl();

        ProductSkuEntity productSkuEntity = new ProductSkuEntity();
        productSkuEntity.setDescription("Description");

        ProductSkuDecoratorImpl productSkuDecorator = new ProductSkuDecoratorImpl(
                null, null, null,
                productSkuEntity,
                null, null,
                i18NWebSupport

        );

        assertEquals("Description", productSkuDecorator.getDescription("en"));


    }

    @Test
    public void testGetDescriptionFailoverToProductDescription() throws Exception {

        final ProductService productService = mockery.mock(ProductService.class, "systemService0");
        mockery.checking(new Expectations() {{
            allowing(productService).getProductAttribute("en", 123, 0, "PRODUCT_DESCRIPTION_en" );
            //allowing(productService).
            will(returnValue(null));
        }});

        I18NWebSupport i18NWebSupport = new I18NWebSupportImpl();

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(123);
        productEntity.setDescription("Prod Description");

        ProductSkuEntity productSkuEntity = new ProductSkuEntity();
        // productSkuEntity.setDescription("Description");
        productSkuEntity.setProduct(productEntity);

        ProductSkuDecoratorImpl productSkuDecorator = new ProductSkuDecoratorImpl(
                null, null, null,
                productSkuEntity,
                null, productService,
                i18NWebSupport

        );

        assertEquals("Prod Description", productSkuDecorator.getDescription("en"));


    }

    @Test
    public void testGetDescriptionSkuDisplayValue() throws Exception {
        I18NWebSupport i18NWebSupport = new I18NWebSupportImpl();

        ProductSkuEntity productSkuEntity = new ProductSkuEntity();
        productSkuEntity.setDescription("Description");


        Collection<AttrValueProductSku> skuAVCollection = new ArrayList<AttrValueProductSku>();
        skuAVCollection.add(
                getAttrValueSku(
                        productSkuEntity,
                        "PRODUCT_DESCRIPTION_en",
                        "En specific description"
                )
        );
        skuAVCollection.add(
                getAttrValueSku(
                        productSkuEntity,
                        "PRODUCT_DESCRIPTION_ua",
                        "Бе́ндер, він же Бе́ндер Згинач Родрі́ґес"
                )
        );

        productSkuEntity.setAttributes(skuAVCollection);

        ProductSkuDecoratorImpl productSkuDecorator = new ProductSkuDecoratorImpl(
                null, null, null,
                productSkuEntity,
                null, null,
                i18NWebSupport

        );

        assertEquals("Description", productSkuDecorator.getDescription("anyLocale"));
        assertEquals("En specific description", productSkuDecorator.getDescription("en"));
        assertEquals("Бе́ндер, він же Бе́ндер Згинач Родрі́ґес", productSkuDecorator.getDescription("ua"));


    }


    @Test
    public void testGetDescriptionFailoverToProductDisplayValue() throws Exception {

        final ProductService productService = mockery.mock(ProductService.class, "systemService0");
        mockery.checking(new Expectations() {{
            allowing(productService).getProductAttribute("en", 123, 0, "PRODUCT_DESCRIPTION_en" );
            //allowing(productService).
            will(returnValue(new Pair<String, String>("en", "Localized product description")));
        }});
        mockery.checking(new Expectations() {{
            allowing(productService).getProductAttribute("ru", 123, 0, "PRODUCT_DESCRIPTION_ru" );
            //allowing(productService).
            will(returnValue(null));
        }});

        I18NWebSupport i18NWebSupport = new I18NWebSupportImpl();

        ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId(123);
        productEntity.setDescription("Prod Description");

        ProductSkuEntity productSkuEntity = new ProductSkuEntity();
        // productSkuEntity.setDescription("Description");
        productSkuEntity.setProduct(productEntity);

        ProductSkuDecoratorImpl productSkuDecorator = new ProductSkuDecoratorImpl(
                null, null, null,
                productSkuEntity,
                null, productService,
                i18NWebSupport

        );

        assertEquals("Localized product description", productSkuDecorator.getDescription("en"));
        assertEquals("Prod Description", productSkuDecorator.getDescription("ru"));


    }

    private AttrValueProductSku getAttrValueSku(
            final ProductSkuEntity productSkuEntity,
            final String code,
            final String val) {
        Attribute attribute = new AttributeEntity();
        attribute.setCode(code);
        AttrValueProductSku enDesc = new AttrValueEntityProductSku();
        enDesc.setProductSku(productSkuEntity);
        enDesc.setAttribute(attribute);
        enDesc.setVal(val);
        return enDesc;
    }
}
