package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoAttributeService extends GenericDTOService<AttributeDTO> {


    /**
     * Get list of {@link AttributeDTO} by given group code.
     *
     * @param attributeGroupCode given attribute group code
     * @return list of {@link AttributeDTO}
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<AttributeDTO> findByAttributeGroupCode(String attributeGroupCode)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Get list of available attributes within given <code>attributeGroupCode</code>, that can be assigned to business entity.
     *
     * @param attributeGroupCode     see AttributeGroup#code
     * @param assignedAttributeCodes list of codes, that already assinged to entity
     * @return list of available attributes to fill with values.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<AttributeDTO> findAvailableAttributes(String attributeGroupCode, List<String> assignedAttributeCodes)
            throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Find all attribute DTOs in given group , that allow to have several attributes
     *
     * @param attributeGroupCode group of attributes
     * @return list of attribute DTOs with allowed multiple values or null if no such attributes found
     * @throws org.yes.cart.exception.UnableToCreateInstanceException
     *          in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException
     *          in case of configuration problem
     */
    List<AttributeDTO> findAttributesWithMultipleValues(
            String attributeGroupCode) throws UnmappedInterfaceException, UnableToCreateInstanceException;

}
