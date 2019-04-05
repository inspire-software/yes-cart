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

package org.yes.cart.bulkcommon.service.support.xml;

import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategy;
import org.yes.cart.bulkcommon.xml.XmlImpExDescriptor;
import org.yes.cart.bulkcommon.xml.XmlImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;

/**
 * Strategy to find the parameter values for given queryTemplate
 * and produce LookUpQuery object for ORM framework.
 *
 * User: denispavlov
 * Date: 12-08-08
 * Time: 8:43 AM
 */
public interface XmlLookUpQueryParameterStrategy extends
        LookUpQueryParameterStrategy<XmlImpExDescriptor, XmlImpExTuple, XmlValueAdapter> {

}
