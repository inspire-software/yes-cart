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

package org.yes.cart.domain.ro;

import org.yes.cart.domain.ro.xml.impl.StringMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 02/04/2015
 * Time: 14:14
 */
@XmlRootElement(name = "order-placed")
public class OrderPlacedRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private boolean success;
    private Map<String, String> problems;

    private OrderRO order;

    @XmlAttribute(name = "success")
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    @XmlJavaTypeAdapter(StringMapAdapter.class)
    @XmlElement(name = "problems")
    public Map<String, String> getProblems() {
        return problems;
    }

    public void setProblems(final Map<String, String> problems) {
        this.problems = problems;
    }

    public OrderRO getOrder() {
        return order;
    }

    public void setOrder(final OrderRO order) {
        this.order = order;
    }
}
