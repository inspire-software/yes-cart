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

package org.yes.cart.domain.entity;

import java.util.Map;

/**
 * TODO kill this interface.
 */
public interface System extends Auditable, Codable {

    /**
     * @return code identifier
     */
    String getCode();

    /**
     * @param code code identifier
     */
    void setCode(String code);

    /**
     * @return name of the system
     */
    String getName();

    /**
     * @param name name of the system
     */
    void setName(String name);

    /**
     * @return description
     */
    String getDescription();

    /**
     * @param description description
     */
    void setDescription(String description);

    /**
     * @return system configuration attributes
     */
    Map<String, AttrValueSystem> getAttributes();

    /**
     * @param attribute system configuration attributes
     */
    void setAttributes(Map<String, AttrValueSystem> attribute);

    /**
     * @return PK
     */
    long getSystemId();

    /**
     * @param systemId PK
     */
    void setSystemId(long systemId);
}
