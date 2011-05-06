
package org.yes.cart.domain.entity;

import java.util.Date;


/**
 * Rule , that responsible to show particular content in named adv. place.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54

 */

public interface ShopAdvPlaceRule extends Auditable {

    /**
     */
    public long getShopadvrulesId();

    public void setShopadvrulesId(long shopadvrulesId);

    /**
     */
    public int getRank();

    public void setRank(int rank);

    /**
     */
    public String getName();

    public void setName(String name);

    /**
     */
    public String getDescription();

    public void setDescription(String description);

    /**
     */
    public Date getAvailablefrom();

    public void setAvailablefrom(Date availablefrom);

    /**
     */
    public Date getAvailabletill();

    public void setAvailabletill(Date availabletill);

    /**
     */
    public String getRule();

    public void setRule(String rule);

    /**
     */
    public ShopAdvPlace getShopAdvPlace();

    public void setShopAdvPlace(ShopAdvPlace shopAdvPlace);

}


