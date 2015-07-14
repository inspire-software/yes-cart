/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.web.support.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.ProductType;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.web.support.constants.CentralViewLabel;
import org.yes.cart.web.support.constants.WebParametersKeys;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 10/04/2015
 * Time: 11:58
 */
public class CentralViewResolverProductImplTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testResolveMainPanelRendererLabelNA() throws Exception {

        final ProductService productService = context.mock(ProductService.class, "productService");

        CentralViewResolverProductImpl resolver = new CentralViewResolverProductImpl(productService);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put("someparam", "1");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();

    }

    @Test
    public void testResolveMainPanelRendererLabelSkuNaN() throws Exception {

        final ProductService productService = context.mock(ProductService.class, "productService");

        CentralViewResolverProductImpl resolver = new CentralViewResolverProductImpl(productService);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.SKU_ID, "abc");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();
    }

    @Test
    public void testResolveMainPanelRendererLabelSkuNoSku() throws Exception {

        final ProductService productService = context.mock(ProductService.class, "productService");

        CentralViewResolverProductImpl resolver = new CentralViewResolverProductImpl(productService);

        context.checking(new Expectations() {{
            one(productService).getSkuById(1L, true); will(returnValue(null));
        }});

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.SKU_ID, "1");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();
    }

    @Test
    public void testResolveMainPanelRendererLabelSkuTemplate() throws Exception {

        final ProductService productService = context.mock(ProductService.class, "productService");
        final ProductSku sku = context.mock(ProductSku.class, "sku");
        final Product product = context.mock(Product.class, "product");
        final ProductType type = context.mock(ProductType.class, "type");

        CentralViewResolverProductImpl resolver = new CentralViewResolverProductImpl(productService);

        context.checking(new Expectations() {{
            one(productService).getSkuById(1L, true); will(returnValue(sku));
            one(sku).getProduct(); will(returnValue(product));
            one(product).getProducttype(); will(returnValue(type));
            one(type).getUitemplate(); will(returnValue("skutemplate1"));
        }});

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.SKU_ID, "1");
        }});

        assertNotNull(resolved);
        assertEquals("skutemplate1", resolved.getFirst());
        assertEquals(CentralViewLabel.SKU, resolved.getSecond());
        context.assertIsSatisfied();
    }

    @Test
    public void testResolveMainPanelRendererLabelSkuNoTemplate() throws Exception {

        final ProductService productService = context.mock(ProductService.class, "productService");
        final ProductSku sku = context.mock(ProductSku.class, "sku");
        final Product product = context.mock(Product.class, "product");
        final ProductType type = context.mock(ProductType.class, "type");

        CentralViewResolverProductImpl resolver = new CentralViewResolverProductImpl(productService);

        context.checking(new Expectations() {{
            one(productService).getSkuById(1L, true); will(returnValue(sku));
            one(sku).getProduct(); will(returnValue(product));
            one(product).getProducttype(); will(returnValue(type));
            one(type).getUitemplate(); will(returnValue(""));
        }});

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.SKU_ID, "1");
        }});

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.SKU, resolved.getFirst());
        assertEquals(CentralViewLabel.SKU, resolved.getSecond());
        context.assertIsSatisfied();
    }

    @Test
    public void testResolveMainPanelRendererLabelProductNaN() throws Exception {

        final ProductService productService = context.mock(ProductService.class, "productService");

        CentralViewResolverProductImpl resolver = new CentralViewResolverProductImpl(productService);

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.PRODUCT_ID, "abc");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();
    }

    @Test
    public void testResolveMainPanelRendererLabelProductNoProduct() throws Exception {

        final ProductService productService = context.mock(ProductService.class, "productService");

        CentralViewResolverProductImpl resolver = new CentralViewResolverProductImpl(productService);

        context.checking(new Expectations() {{
            one(productService).getProductById(1L, true); will(returnValue(null));
        }});

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.PRODUCT_ID, "1");
        }});

        assertNull(resolved);
        context.assertIsSatisfied();
    }

    @Test
    public void testResolveMainPanelRendererLabelProductTemplate() throws Exception {

        final ProductService productService = context.mock(ProductService.class, "productService");
        final Product product = context.mock(Product.class, "product");
        final ProductType type = context.mock(ProductType.class, "type");

        CentralViewResolverProductImpl resolver = new CentralViewResolverProductImpl(productService);

        context.checking(new Expectations() {{
            one(productService).getProductById(1L, true); will(returnValue(product));
            one(product).getProducttype(); will(returnValue(type));
            one(type).getUitemplate(); will(returnValue("producttemplate1"));
        }});

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.PRODUCT_ID, "1");
        }});

        assertNotNull(resolved);
        assertEquals("producttemplate1", resolved.getFirst());
        assertEquals(CentralViewLabel.PRODUCT, resolved.getSecond());
        context.assertIsSatisfied();
    }

    @Test
    public void testResolveMainPanelRendererLabelProductNoTemplate() throws Exception {

        final ProductService productService = context.mock(ProductService.class, "productService");
        final Product product = context.mock(Product.class, "product");
        final ProductType type = context.mock(ProductType.class, "type");

        CentralViewResolverProductImpl resolver = new CentralViewResolverProductImpl(productService);

        context.checking(new Expectations() {{
            one(productService).getProductById(1L, true); will(returnValue(product));
            one(product).getProducttype(); will(returnValue(type));
            one(type).getUitemplate(); will(returnValue(""));
        }});

        final Pair<String, String> resolved = resolver.resolveMainPanelRendererLabel(new HashMap<String, String>() {{
            put(WebParametersKeys.PRODUCT_ID, "1");
        }});

        assertNotNull(resolved);
        assertEquals(CentralViewLabel.PRODUCT, resolved.getFirst());
        assertEquals(CentralViewLabel.PRODUCT, resolved.getSecond());
        context.assertIsSatisfied();
    }


}
