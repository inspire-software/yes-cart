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

package org.yes.cart.domain.query.impl;

import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

import java.io.Reader;

/**
 * User: iazarny@yahoo.com Igor Azarny
 * Date: 2/3/12
 * Time: 12:53 PM
 */
public class AsIsTokenizer extends CharTokenizer {


    public AsIsTokenizer(Version matchVersion, Reader input) {
        super(matchVersion, input);
    }

    public AsIsTokenizer(Version matchVersion, AttributeSource source, Reader input) {
        super(matchVersion, source, input);
    }

    public AsIsTokenizer(Version matchVersion, AttributeFactory factory, Reader input) {
        super(matchVersion, factory, input);
    }

    /** {@inheritDoc}*/
    protected int normalize(int c) {
        return c;
    }

    /** {@inheritDoc}*/
    protected boolean isTokenChar(int c) {
        return true;
    }
}
