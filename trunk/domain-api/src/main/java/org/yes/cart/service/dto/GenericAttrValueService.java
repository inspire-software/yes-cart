package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.exception.UnableToCreateInstanceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface GenericAttrValueService {

    /**
     * Get the business entity attributes values, that contains two set of attribute values: with values
     * and with empty values for available attributes. Example:
     * category type has attr1, attr2 and attr3, partucular category
     * has attr1 and attr3 with values, so service return 3 records:
     * attr1 - value
     * attr2 - empty value
     * attr3 value.
     *
     * This service mostly for UI.
     *
     * @param entityPk entity pk value
     * @return list of attribute values
     * @throws org.yes.cart.exception.UnableToCreateInstanceException in case of configuration erros
     * @throws org.yes.cart.exception.UnmappedInterfaceException in case of configuration erros
     */
    List<? extends AttrValueDTO> getEntityAttributes(long entityPk) 
            throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Update attribute value.
     * @param attrValueDTO value to update
     * @return updated value
     */
    AttrValueDTO updateEntityAttributeValue(AttrValueDTO attrValueDTO);

    /**
     * Create attribute value
     * @param attrValueDTO value to persist
     * @return created value
     */
    AttrValueDTO createEntityAttributeValue(AttrValueDTO attrValueDTO);


    /**
     * Delete attribute value by given pk value.
     * @param attributeValuePk given pk value.
     */
    void deleteAttributeValue(long attributeValuePk);



}
