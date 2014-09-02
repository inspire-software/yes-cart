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

package org.yes.cart.remote.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.bulkimport.service.ImportDirectorService;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 10/12/11
 * Time: 18:33
 */
public class RemoteUploadServiceImplTest {

    private Mockery mockery = new JUnit4Mockery();

    @Test
    public void testUpload() throws Exception {

        final ImportDirectorService importDirectorService = mockery.mock(ImportDirectorService.class);

        mockery.checking(new Expectations() {{
            one(importDirectorService).getImportDirectory(); will(returnValue(new File("target/uploadtest").getAbsolutePath()));
        }});

        RemoteUploadServiceImpl remoteUploadService = new RemoteUploadServiceImpl(importDirectorService);
        String fullFileName = remoteUploadService.upload("some bytes".getBytes(), "filename" + UUID.randomUUID().toString() + ".txt");
        File file = new File(fullFileName);
        assertTrue(file.exists());

    }

}
