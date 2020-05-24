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

/**
 * User: denispavlov
 * Date: 11/12/2017
 * Time: 11:01
 */
@Dto
public class VoShopCarrierSla extends VoCarrierSlaInfo {

    private boolean disabled;
    private int rank;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }
}
