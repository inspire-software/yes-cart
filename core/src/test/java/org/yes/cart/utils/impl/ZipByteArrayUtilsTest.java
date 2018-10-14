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
import java.nio.file.Files;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 30/05/2018
 * Time: 22:21
 */
public class ZipByteArrayUtilsTest {

    @Test
    public void fileToZipBytesZip() throws Exception {

        final File zipFile = new File("src/test/resources/archive/testfile.zip");

        final byte[] zipBytes = ZipByteArrayUtils.fileToZipBytes(zipFile);

        assertArrayEquals(zipBytes, Files.readAllBytes(zipFile.toPath()));

    }

    @Test
    public void fileToZipBytesFile() throws Exception {


        final File zipFile = new File("src/test/resources/imgrepo/a/arbuz/speli_arbuz_arbuz_a.jpeg");

        final byte[] zipBytes = ZipByteArrayUtils.fileToZipBytes(zipFile);

        assertFalse(zipBytes.length == zipFile.length());

        final File zipFileTarget = new File("target/test-classes/ziptest/speli_arbuz_arbuz_a.zip");

        Files.write(zipFileTarget.toPath(), zipBytes);

        final File unzipFileTarget = new File("target/test-classes/ziptest/");

        new ZipUtils("UTF-8").unzipArchive(zipFileTarget, unzipFileTarget);

        final File[] files = unzipFileTarget.listFiles();

        boolean found = false;
        for (File file : files) {
            if (file.getName().equals("speli_arbuz_arbuz_a.jpeg")) {
                found = true;
                assertEquals(file.length(), zipFile.length());
                break;
            }
        }
        assertTrue(found);

    }

    @Test
    public void fileToZipBytesDir() throws Exception {

        final File zipFile = new File("src/test/resources/imgrepo/C");

        final byte[] zipBytes = ZipByteArrayUtils.fileToZipBytes(zipFile);

        assertFalse(zipBytes.length == zipFile.length());

        final File zipFileTarget = new File("target/test-classes/ziptest/C.zip");

        Files.write(zipFileTarget.toPath(), zipBytes);

        final File unzipFileTarget = new File("target/test-classes/ziptest/");

        new ZipUtils("UTF-8").unzipArchive(zipFileTarget, unzipFileTarget);

        final File[] files = unzipFileTarget.listFiles();

        boolean found = false;
        for (File file : files) {
            if (file.getName().equals("C")) {
                found = true;
                assertEquals(file.length(), zipFile.length());
                break;
            }
        }
        assertTrue(found);


    }

}