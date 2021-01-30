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

package org.yes.cart.bulkjob.bulkimport;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.bulkjob.cron.CronJobProcessor;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.domain.entity.DataGroup;
import org.yes.cart.service.async.JobStatusAware;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.DataDescriptorService;
import org.yes.cart.service.domain.DataGroupService;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: inspiresoftware
 * Date: 27/01/2021
 * Time: 09:48
 */
public class LocalFileShareImportListenerImplTest extends BaseCoreDBTestCase {

    @Test
    public void testRun() throws Exception {

        final AttributeService attributeService = ctx().getBean("attributeService", AttributeService.class);
        final DataDescriptorService dataDescriptorService = ctx().getBean("dataDescriptorService", DataDescriptorService.class);
        final DataGroupService dataGroupService = ctx().getBean("dataGroupService", DataGroupService.class);
        final CronJobProcessor autoImportListener = this.ctx().getBean("autoImportListener", CronJobProcessor.class);

        final DataDescriptor attrsIn = dataDescriptorService.getGenericDao().getEntityFactory().getByIface(DataDescriptor.class);
        attrsIn.setName(getTestName());
        attrsIn.setType(DataDescriptor.TYPE_RAW_XML_CSV);
        attrsIn.setValue(FileUtils.readFileToString(new File("src/test/resources/impex/descriptors/attributenames.xml"), "UTF-8"));
        dataDescriptorService.create(attrsIn);

        final DataGroup dataGroup = dataGroupService.getGenericDao().getEntityFactory().getByIface(DataGroup.class);
        dataGroup.setName(getTestName());
        dataGroup.setDescriptors(getTestName());
        dataGroup.setType(DataGroup.TYPE_IMPORT);
        dataGroupService.create(dataGroup);

        final Map<String, Object> ctx = configureJobContext("autoImportListener",
                "file-import-root=target/test-classes/impex/autoimport\n" +
                        "SHOIP1.config.0.group=" + getTestName() + "\n" +
                        "SHOIP1.config.0.regex=attributenames.csv\n" +
                        "SHOIP1.config.0.reindex=false\n" +
                        "SHOIP1.config.0.user=admin@yes-cart.com\n" +
                        "SHOIP1.config.0.pass=1234567\n");

        assertNull(attributeService.findByAttributeCode("LocalFileShareImportListenerImplTest"));

        new File("target/test-classes/impex/autoimport/SHOIP1/processed").mkdirs();
        FileUtils.copyFile(
                new File("target/test-classes/impex/autoimport/SHOIP1/incoming/attributenames.csv"),
                new File("target/test-classes/impex/autoimport/SHOIP1/processed/attributenames.csv")
        );

        autoImportListener.process(ctx);

        final JobStatus status = ((JobStatusAware) autoImportListener).getStatus(null);
        assertNotNull(status);
        assertTrue(status.getReport(),
                status.getReport().contains("SHOIP1/processed/attributenames.csv' for shop SHOIP1 using group LocalFileShareImportListenerImplTest.testRun ... completed [OK]"));

        assertNotNull(attributeService.findByAttributeCode("LocalFileShareImportListenerImplTest"));

    }
}