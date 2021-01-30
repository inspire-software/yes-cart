/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.bulkjob.images;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkjob.cron.CronJobProcessor;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.SystemService;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 28/01/2021
 * Time: 15:13
 */
public class LocalFileShareImageVaultProcessorImplTest extends BaseCoreDBTestCase {

    @Test
    public void testRun() throws Exception {

        final SystemService systemService = ctx().getBean("systemService", SystemService.class);
        final ProductService productService = ctx().getBean("productService", ProductService.class);
        final CronJobProcessor imageVaultProcessor = this.ctx().getBean("imageVaultProcessor", CronJobProcessor.class);

        final Map<String, Object> ctx = configureJobContext("imageVaultProcessor",
                "config.reindex=false\n" +
                        "config.user=admin@yes-cart.com\n" +
                        "config.pass=1234567\n");

        final File imgVault = new File("target/test-classes/impex/imagevault/");
        imgVault.mkdirs();

        systemService.createOrGetAttributeValue(AttributeNamesKeys.System.SYSTEM_IMAGE_VAULT, "String");
        systemService.updateAttributeValue(AttributeNamesKeys.System.SYSTEM_IMAGE_VAULT, imgVault.getAbsolutePath());

        final String root = systemService.getImageRepositoryDirectory();

        new File(root + "/product/B/BENDER").mkdirs();
        FileUtils.copyFile(
                new File("target/test-classes/impex/imagescan/im-image-file_BENDER_c.jpeg"),
                new File(root + "/product/B/BENDER/im-image-file_BENDER_c.jpeg")
        );

        assertHasImage(productService, "BENDER", "IMAGE2", null);

        imageVaultProcessor.process(ctx);

        final JobStatus status = ((JobStatusAware) imageVaultProcessor).getStatus(null);
        assertNotNull(status);
        assertTrue(status.getReport(),
                status.getReport().contains("file im-image-file_BENDER_c.jpeg attached as IMAGE2 to product BENDER"));
        assertTrue(status.getReport(),
                status.getReport().contains("file im-image-file_BENDER_c.jpeg attached as IMAGE2 to product sku BENDER"));

        assertHasImage(productService, "BENDER", "IMAGE2", "im-image-file_BENDER_c.jpeg");

    }

    private void assertHasImage(final ProductService productService, final String code, final String attrKey, final String expected) {
        getTxReadOnly().executeWithoutResult(status -> {
            final Long productId = productService.findProductIdByCode(code);
            assertNotNull(productId);
            final Product product = productService.findById(productId);
            assertNotNull(product);
            assertEquals(expected, product.getAttributeValueByCode(attrKey));
        });
    }
}