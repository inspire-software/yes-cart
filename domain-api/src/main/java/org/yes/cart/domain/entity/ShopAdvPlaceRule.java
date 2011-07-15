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


