package org.yes.cart.service.domain;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface AttributeManageGenericService<T> extends GenericService<T> {

    /**
     * Set attribute value. New attribute value will be created,
     * if attribute has not value for given shop.
     *
     * @param entityId       entity pk value
     * @param attributeKey   attribute key
     * @param attributeValue attribute value.
     */
    void updateAttributeValue(long entityId, String attributeKey, String attributeValue);


}
