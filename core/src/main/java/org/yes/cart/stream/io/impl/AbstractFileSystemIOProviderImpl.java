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

package org.yes.cart.stream.io.impl;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.stream.io.IOItem;
import org.yes.cart.stream.io.IOProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 30/08/2014
 * Time: 19:50
 */
public abstract class AbstractFileSystemIOProviderImpl implements IOProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFileSystemIOProviderImpl.class);

    /**
     * Resolve file system path by given uri.
     *
     * @param uri uri of the read target
     * @param context any applicable context for given provider
     *
     * @return file object
     */
    public abstract File resolveFileFromUri(final String uri, final Map<String, Object> context);

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
    public List<IOItem> list(final String uri, final Map<String, Object> context) {

        final List<IOItem> items = new ArrayList<>();

        final File dir = resolveFileFromUri(uri, context);
        if (dir.exists() && dir.isDirectory()) {

            final File[] list = dir.listFiles();

            if (list != null) {

                for (final File file : list) {

                    items.add(new IOItemImpl(
                            uri,
                            file.getName(),
                            Instant.ofEpochMilli(file.lastModified()),
                            file.isDirectory(),
                            file.getAbsolutePath()));

                }

            }

        }

        return items;
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(final String uri, final Map<String, Object> context) {

        final File file = resolveFileFromUri(uri, context);
        return file != null && file.exists();

    }

    /** {@inheritDoc} */
    @Override
    public String path(final String uri, final String subPath, final Map<String, Object> context) {
        if (uri.endsWith("/")) {
            return uri + (subPath.startsWith("/") ? subPath.substring(1) : subPath).replace('\\', '/');
        } else {
            return uri + (subPath.startsWith("/") ? subPath : "/".concat(subPath)).replace('\\', '/');
        }
    }

    /** {@inheritDoc} */
    @Override
    public String nativePath(final String uri, final Map<String, Object> context) {

        final File nat = resolveFileFromUri(uri, context);
        return nat.getAbsolutePath();

    }

    /** {@inheritDoc} */
    @Override
    public boolean isNewerThan(final String uriToCheck, final String uriToCheckAgainst, final Map<String, Object> context) {

        final File file1 = resolveFileFromUri(uriToCheck, context);
        final File file2 = resolveFileFromUri(uriToCheckAgainst, context);

        return file1 == null || file2 == null || file1.exists() && file2.exists() && file2.lastModified() < file1.lastModified();

    }

    /** {@inheritDoc} */
    @Override
    public byte[] read(final String uri, final Map<String, Object> context) throws IOException {

        final File file = resolveFileFromUri(uri, context);
        if (file == null) {
            throw new FileNotFoundException("Unable to resolve file path: " + uri);
        }
        return FileUtils.readFileToByteArray(file);

    }

    /** {@inheritDoc} */
    @Override
    public void write(final String uri, final byte[] content, final Map<String, Object> context) throws IOException {

        final File file = resolveFileFromUri(uri, context);
        if (file == null) {
            throw new IOException("Unable to resolve path: " + uri);
        }

        if (!file.getParentFile().exists()) {
            // ensure we create all dirs necessary
            if (!file.getParentFile().mkdirs()) {
                LOG.error("Unable to create directory {}", file.getParent());
            }
        }

        FileUtils.writeByteArrayToFile(file, content);

    }

    /** {@inheritDoc} */
    @Override
    public void delete(final String uri, final Map<String, Object> context) throws IOException {

        final File file = resolveFileFromUri(uri, context);

        if (file != null && file.exists()) {
            delete(file);
        }

    }

    private void delete(final File file) {

        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                final File[] files = file.listFiles();
                if (files != null) {
                    for (final File subFile : files) {
                        delete(subFile);
                    }
                }
            }
            if (!file.delete()) {
                LOG.error("Unable to delete file {}", file.getAbsolutePath());
            }
        }

    }
}
