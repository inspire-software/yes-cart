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

import java.util.List;

/**
 * User: inspiresoftware
 * Date: 12/01/2021
 * Time: 17:40
 */
@Dto
public class VoDataGroupImpEx extends VoDataGroup {

    private List<VoDataDescriptorImpEx> impexDescriptors;

    public List<VoDataDescriptorImpEx> getImpexDescriptors() {
        return impexDescriptors;
    }

    public void setImpexDescriptors(final List<VoDataDescriptorImpEx> impexDescriptors) {
        this.impexDescriptors = impexDescriptors;
    }
}
