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
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.SystemService;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 28/01/2021
 * Time: 19:30
 */
public class LocalFileShareProductImageVaultCleanupProcessorImplTest extends BaseCoreDBTestCase {

    @Test
    public void testRun() throws Exception {

        final SystemService systemService = ctx().getBean("systemService", SystemService.class);
        final CronJobProcessor productImageVaultCleanupProcessor = this.ctx().getBean("productImageVaultCleanupProcessor", CronJobProcessor.class);

        final Map<String, Object> ctxScan = configureJobContext("productImageVaultCleanupProcessor",
                "clean-mode=scan");

        final File imgVault = new File("target/test-classes/impex/imagevault/");
        imgVault.mkdirs();

        systemService.createOrGetAttributeValue(AttributeNamesKeys.System.SYSTEM_IMAGE_VAULT, "String");
        systemService.updateAttributeValue(AttributeNamesKeys.System.SYSTEM_IMAGE_VAULT, imgVault.getAbsolutePath());

        final String root = systemService.getImageRepositoryDirectory();

        new File(root + "/product/X/XXXXX").mkdirs();
        FileUtils.copyFile(
                new File("target/test-classes/impex/imagescan/im-image-file_XXXXX_c.jpeg"),
                new File(root + "/product/X/XXXXX/im-image-file_XXXXX_c.jpeg")
        );

        productImageVaultCleanupProcessor.process(ctxScan);

        final JobStatus statusScan = ((JobStatusAware) productImageVaultCleanupProcessor).getStatus(null);
        assertNotNull(statusScan);
        assertTrue(statusScan.getReport(),
                statusScan.getReport().contains("XXXXX'}' is eligible for removal"));
        assertTrue(statusScan.getReport(),
                statusScan.getReport().contains("Counters [Eligible image codes: 1]"));

        assertTrue(new File(root + "/product/X/XXXXX/im-image-file_XXXXX_c.jpeg").exists());

        final Map<String, Object> ctxDelete = configureJobContext("productImageVaultCleanupProcessor",
                "clean-mode=delete");

        productImageVaultCleanupProcessor.process(ctxDelete);

        final JobStatus statusDelete = ((JobStatusAware) productImageVaultCleanupProcessor).getStatus(null);
        assertNotNull(statusDelete);
        assertTrue(statusDelete.getReport(),
                statusDelete.getReport().contains("XXXXX'}' is eligible for removal"));
        assertTrue(statusDelete.getReport(),
                statusDelete.getReport().contains("Counters [Eligible image codes: 1, Removed image codes: 1]"));

        assertFalse(new File(root + "/product/X/XXXXX/im-image-file_XXXXX_c.jpeg").exists());


    }

}