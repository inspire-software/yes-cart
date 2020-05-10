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

package org.yes.cart.stream.io.impl;

import org.yes.cart.stream.io.IOItem;

import java.time.Instant;

/**
 * User: denispavlov
 * Date: 10/05/2020
 * Time: 12:10
 */
public class IOItemImpl implements IOItem {

    private final String path;
    private final String name;
    private final Instant timestamp;
    private final boolean group;

    private final String nativePath;

    public IOItemImpl(final String path,
                      final String name,
                      final Instant timestamp,
                      final boolean group,
                      final String nativePath) {
        this.path = path;
        this.name = name;
        this.timestamp = timestamp;
        this.group = group;
        this.nativePath = nativePath;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean isGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "IOItemImpl{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", timestamp=" + timestamp +
                ", group=" + group +
                ", nativePath='" + nativePath + '\'' +
                '}';
    }
}
