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

package org.yes.cart.utils.impl;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Enumeration;



/**
 * User: denispavlov
 * Date: 24/10/2019
 * Time: 09:38
 */
public class ZipUtils {


    private final String encoding;

    /**
     * Create Zip util.
     * @param encoding  archive encoding
     */
    public ZipUtils(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Unzip archive to given folder.
     *
     * @param archive   given archive
     * @param outputDir given folder
     *
     * @throws IOException in case of error
     */
    public void unzipArchive(final String archive, final String outputDir) throws IOException {
        unzipArchive(
                new File(archive),
                new File(outputDir)
        );
    }
    /**
     * Unzip archive to given folder.
     *
     * @param archive given archive
     * @param outputDir  given folder
     *
     * @throws IOException in case of error
     */

    public void unzipArchive(final File archive, final File outputDir) throws IOException {
        ZipFile zipfile = new ZipFile(archive, encoding);
        for (Enumeration e = zipfile.getEntries(); e.hasMoreElements(); ) {
            final ZipArchiveEntry entry = (ZipArchiveEntry) e.nextElement();
            unzipEntry(zipfile, entry, outputDir);
        }
        zipfile.close();
    }

    private void unzipEntry(final ZipFile zipfile, final ZipArchiveEntry entry, final File outputDir) throws IOException {

        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            createDir(outputFile.getParentFile());
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    private void createDir(final File dir) throws IOException {
        if (!dir.mkdirs()) throw new IOException("Unable to create dir:" + dir);
    }


}
