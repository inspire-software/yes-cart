/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 21:30
 */
@Dto
public class VoCarrier extends VoCarrierLocale {

    private List<VoCarrierShopLink> carrierShops = new ArrayList<VoCarrierShopLink>();

    public List<VoCarrierShopLink> getCarrierShops() {
        return carrierShops;
    }

    public void setCarrierShops(final List<VoCarrierShopLink> carrierShops) {
        this.carrierShops = carrierShops;
    }
}
