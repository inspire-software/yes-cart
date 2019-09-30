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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import org.yes.cart.domain.misc.MutablePair;

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 17:54
 */
@Dto
public class VoLocation {

    private String code;

    private String name;

    private List<MutablePair<String, String>> displayNames;

    private List<VoLocation> subLocations;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
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

    public List<VoLocation> getSubLocations() {
        return subLocations;
    }

    public void setSubLocations(final List<VoLocation> subLocations) {
        this.subLocations = subLocations;
    }
}
