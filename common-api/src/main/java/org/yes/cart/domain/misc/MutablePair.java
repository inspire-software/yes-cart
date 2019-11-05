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

package org.yes.cart.domain.misc;

import java.io.Serializable;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 *
 * @param <FIRST>  the type of the first object
 * @param <SECOND> the type of the second object
 */
public class MutablePair<FIRST, SECOND> implements Serializable {

    private static final long serialVersionUID = 20100711L;

    private FIRST first;
    private SECOND second;

    public static MutablePair of(Object first, Object second) {
        return new MutablePair(first, second);
    }

    public MutablePair(final FIRST first, final SECOND second) {
        this.first = first;
        this.second = second;
    }

    public MutablePair() {
    }

    public FIRST getFirst() {
        return first;
    }

    public SECOND getSecond() {
        return second;
    }

    public void setFirst(FIRST first) {
        this.first = first;
    }

    public void setSecond(SECOND second) {
        this.second = second;
    }


    @Override
    public String toString() {
        return "MutablePair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

}
