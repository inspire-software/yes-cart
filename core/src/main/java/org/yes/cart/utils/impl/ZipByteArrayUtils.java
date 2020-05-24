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

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: denispavlov
 * Date: 30/05/2018
 * Time: 22:00
 */
public final class ZipByteArrayUtils {

    private ZipByteArrayUtils() {
        // no instance
    }

    /**
     * Create zip byte array from given file.
     *
     * @param file file
     *
     * @return zip file as bytes
     *
     * @throws IOException io exception
     */
    public static byte[] fileToZipBytes(final File file) throws IOException {

        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File must not be null");
        }

        if (file.isFile() && file.getName().endsWith(".zip")) {

            // This is zip already
            return FileUtils.readFileToByteArray(file);

        } else {

            // Non zip file, needs to be zipped
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ZipOutputStream zos = new ZipOutputStream(baos);
            appendFile(zos, null, file);
            zos.close();

            return baos.toByteArray();

        }
            
    }

    private static void appendFile(final ZipOutputStream zos, final String path, final File file) throws IOException {

        if (file.isDirectory()) {
            final File[] sub = file.listFiles();
            if (sub != null) {
                for (final File item : sub) {
                    appendFile(zos, (path != null ? path + File.separator : "") + file.getName(), item);
                }
            }
        } else {

            final InputStream is = new BufferedInputStream(new FileInputStream(file));

            byte[] buff = new byte[1024];
            final ZipEntry entry = new ZipEntry((path != null ? path + File.separator : "") + file.getName());
            zos.putNextEntry(entry);

            int len;
            while ((len = is.read(buff)) > 0) {
                zos.write(buff, 0, len);
            }

            is.close();
            zos.closeEntry();

        }

    }


}
