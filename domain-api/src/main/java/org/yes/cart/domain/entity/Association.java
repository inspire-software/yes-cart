package org.yes.cart.domain.entity;


/**
 * Associations between products. It can be:
 * Cross sell - other related products from other categories.
 * up sell - the simular products with higher prices.
 * accesories - accessories to product, like charger for mobile phone.
 * who buy also buy - dynamic association, base on different customers buy history.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Association extends Auditable {


    String CROSS_SELL = "cross";
    String ACCESSORIES = "accessories";
    String UP_SELL = "up";
    String BUY_WITH_THIS = "buywiththis";

    /**
     * @return pkimary key.
     */
    public long getAssociationId();

    /**
     * @param associationId primary key to set
     */
    public void setAssociationId(long associationId);

    /**
     * Unique human readable association code.
     *
     * @return unique human readable association code
     */
    public String getCode();

    /**
     * @param code unique human readable association code to use.
     */
    public void setCode(String code);

    /**
     * @return Association name.
     */
    public String getName();

    /**
     * @param name association name.
     */
    public void setName(String name);

    /**
     * @return description
     */
    public String getDescription();

    /**
     * @param description description to use.
     */
    public void setDescription(String description);


}


