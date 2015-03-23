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

package org.yes.cart.stream.io.impl;

import org.apache.commons.io.FileUtils;
import org.yes.cart.stream.io.IOProvider;
import org.yes.cart.util.ShopCodeContext;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 30/08/2014
 * Time: 19:50
 */
public abstract class AbstractFileSystemIOProviderImpl implements IOProvider {

    /**
     * Resolve file system path by given uri.
     *
     * @param uri uri of the read target
     * @param context any applicable context for given provider
     *
     * @return file object
     */
    protected abstract File resolveFileFromUri(final String uri, final Map<String, Object> context);

    /**
     * Utility method to set the file separators correctly.
     *
     * @param path raw path
     *
     * @return OS specific path
     */
    protected String getOsAwarePath(String path) {
        if (File.separatorChar == '/') {
            return path.replace('\\', '/');
        }
        return path.replace('/', '\\');
    }


    /** {@inheritDoc} */
    @Override
    public boolean exists(final String uri, final Map<String, Object> context) {

        return resolveFileFromUri(uri, context).exists();

    }

    /** {@inheritDoc} */
    @Override
    public boolean isNewerThan(final String uriToCheck, final String uriToCheckAgainst, final Map<String, Object> context) {

        final File file1 = resolveFileFromUri(uriToCheck, context);
        final File file2 = resolveFileFromUri(uriToCheckAgainst, context);

        return file1.exists() && file2.exists() && file2.lastModified() < file1.lastModified();

    }

    /** {@inheritDoc} */
    @Override
    public byte[] read(final String uri, final Map<String, Object> context) throws IOException {

        return FileUtils.readFileToByteArray(resolveFileFromUri(uri, context));

    }

    /** {@inheritDoc} */
    @Override
    public void write(final String uri, final byte[] content, final Map<String, Object> context) throws IOException {

        final File file = resolveFileFromUri(uri, context);

        // ensure we create all dirs necessary
        if (!file.getParentFile().mkdirs()) {
            ShopCodeContext.getLog(this).error("Unable to create directory {}", file.getParent());
        }

        FileUtils.writeByteArrayToFile(resolveFileFromUri(uri, context), content);

    }

    /** {@inheritDoc} */
    @Override
    public void delete(final String uri, final Map<String, Object> context) throws IOException {

        final File file = resolveFileFromUri(uri, context);

        if (file.exists()) {
            if (!file.delete()) {
                ShopCodeContext.getLog(this).error("Unable to delete file {}", file.getAbsolutePath());
            }
        }

    }
}
