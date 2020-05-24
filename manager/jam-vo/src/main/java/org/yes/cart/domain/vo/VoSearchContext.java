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

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 02/11/2019
 * Time: 20:00
 */
@Dto
public class VoSearchContext {

    @DtoField
    private Map<String, List> parameters;
    @DtoField
    private int start;
    @DtoField
    private int size;
    @DtoField
    private String sortBy;
    @DtoField
    private boolean sortDesc;

    public Map<String, List> getParameters() {
        return parameters;
    }

    public void setParameters(final Map<String, List> parameters) {
        this.parameters = parameters;
    }

    public int getStart() {
        return start;
    }

    public void setStart(final int start) {
        this.start = start;
    }

    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(final String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isSortDesc() {
        return sortDesc;
    }

    public void setSortDesc(final boolean sortDesc) {
        this.sortDesc = sortDesc;
    }
}
