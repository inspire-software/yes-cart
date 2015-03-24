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

package org.yes.cart.bulkimport.service.support;

/**
 * Query object ready to be passed to ORM framework.
 *
 * User: denispavlov
 * Date: 12-08-08
 * Time: 8:49 AM
 */
public interface LookUpQuery {

    String NAITIVE = "NAITIVE";
    String HSQL = "HSQL";
    String LUCENE = "LUCENE";

    /**
     * @return type of this query as defined by constants of this interface
     */
    String getQueryType();

    /**
     * @return query string with parameters as ?1, ?2, ... ?n
     */
    String getQueryString();

    /**
     * @return corresponding value of objects
     */
    Object[] getParameters();

}
