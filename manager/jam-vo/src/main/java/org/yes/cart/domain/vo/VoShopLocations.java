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

import java.util.List;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 17:50
 */
public class VoShopLocations {

    private long shopId;

    private List<String> supportedBilling;
    private List<String> supportedShipping;

    private List<VoLocation>  all;


    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public List<String> getSupportedBilling() {
        return supportedBilling;
    }

    public void setSupportedBilling(List<String> supportedBilling) {
        this.supportedBilling = supportedBilling;
    }

    public List<String> getSupportedShipping() {
        return supportedShipping;
    }

    public void setSupportedShipping(final List<String> supportedShipping) {
        this.supportedShipping = supportedShipping;
    }

    public List<VoLocation> getAll() {
        return all;
    }

    public void setAll(List<VoLocation> all) {
        this.all = all;
    }
}
