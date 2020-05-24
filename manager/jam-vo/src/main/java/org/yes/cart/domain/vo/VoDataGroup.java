/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.misc.MutablePair;

import java.util.List;

/**
 * User: denispavlov
 * Date: 30/03/2019
 * Time: 12:12
 */
@Dto
public class VoDataGroup {

    @DtoField(value = "datagroupId", readOnly = true)
    private long datagroupId;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "displayName", converter = "DisplayValuesI18n")
    private List<MutablePair<String, String>> displayNames;

    @DtoField(value = "qualifier")
    private String qualifier;

    @DtoField(value = "type")
    private String type;

    @DtoField(value = "descriptors")
    private String descriptors;

    public long getDatagroupId() {
        return datagroupId;
    }

    public void setDatagroupId(final long datagroupId) {
        this.datagroupId = datagroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<MutablePair<String, String>> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final List<MutablePair<String, String>> displayNames) {
        this.displayNames = displayNames;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(final String descriptors) {
        this.descriptors = descriptors;
    }
}
