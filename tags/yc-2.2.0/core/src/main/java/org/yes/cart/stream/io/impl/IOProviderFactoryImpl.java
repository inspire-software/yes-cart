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

import org.yes.cart.stream.io.IOProvider;

import java.io.IOException;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 30/08/2014
 * Time: 15:32
 */
public class IOProviderFactoryImpl implements IOProvider {

    private final IOProvider[] ioProviders;

    public IOProviderFactoryImpl(final IOProvider[] providers) {

        this.ioProviders = providers;

    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String uri) {
        for (final IOProvider ioProvider : ioProviders) {
            if (ioProvider.supports(uri)) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(final String uri, final Map<String, Object> context) {
        for (final IOProvider ioProvider : ioProviders) {
            if (ioProvider.supports(uri)) {
                return ioProvider.exists(uri, context);
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNewerThan(final String uriToCheck, final String uriToCheckAgainst, final Map<String, Object> context) {
        for (final IOProvider ioProvider : ioProviders) {
            if (ioProvider.supports(uriToCheck)) {
                return ioProvider.isNewerThan(uriToCheck, uriToCheckAgainst, context);
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] read(final String uri, final Map<String, Object> context) throws IOException {
        for (final IOProvider ioProvider : ioProviders) {
            if (ioProvider.supports(uri)) {
                return ioProvider.read(uri, context);
            }
        }
        throw new IOException("Unsupported uri " + uri);
    }

    /** {@inheritDoc} */
    @Override
    public void write(final String uri, final byte[] content, final Map<String, Object> context) throws IOException {
        for (final IOProvider ioProvider : ioProviders) {
            if (ioProvider.supports(uri)) {
                ioProvider.write(uri, content, context);
                return;
            }
        }
        throw new IOException("Unsupported uri " + uri);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final String uri, final Map<String, Object> context) throws IOException {
        for (final IOProvider ioProvider : ioProviders) {
            if (ioProvider.supports(uri)) {
                ioProvider.delete(uri, context);
                return;
            }
        }
        throw new IOException("Unsupported uri " + uri);
    }

}
