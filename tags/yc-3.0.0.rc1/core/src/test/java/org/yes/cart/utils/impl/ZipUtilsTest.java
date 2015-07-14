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

package org.yes.cart.utils.impl;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 * User: igora Igor Azarny
 * Date: 1/30/13
 * Time: 10:14 AM
 */
public class ZipUtilsTest {
    /**
     * TEst non ascii arc file entry
     * @throws Exception
     */
    @Test
    public void testUnzipArchive() throws Exception {


        (new ZipUtils("Cp866")).unzipArchive(
                "src/test/resources/import/archive/testfile.zip",
                "target/test-classes/import/archive"
        );

        File [] files = (new File("target/test-classes/import/archive")).listFiles();

        boolean found = false;
        
        for (File file : files) {
            if (file.getAbsolutePath().contains("Привет, я файло.txt")) {
                found = true;
                break;
            }
        }
        assertTrue(found);


    }

}
