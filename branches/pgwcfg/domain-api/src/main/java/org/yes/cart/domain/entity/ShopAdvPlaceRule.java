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

import java.util.Date;


/**
 * Rule , that responsible to show particular content in named adv. place.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */

public interface ShopAdvPlaceRule extends Auditable {

    /**
     */
    long getShopadvrulesId();

    void setShopadvrulesId(long shopadvrulesId);

    /**
     */
    int getRank();

    void setRank(int rank);

    /**
     */
    String getName();

    void setName(String name);

    /**
     */
    String getDescription();

    void setDescription(String description);

    /**
     */
    Date getAvailablefrom();

    void setAvailablefrom(Date availablefrom);

    /**
     */
    Date getAvailabletill();

    void setAvailabletill(Date availabletill);

    /**
     */
    String getRule();

    void setRule(String rule);

    /**
     */
    ShopAdvPlace getShopAdvPlace();

    void setShopAdvPlace(ShopAdvPlace shopAdvPlace);

}


