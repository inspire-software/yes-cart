package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.AttributeGroupDTO;
import org.yes.cart.domain.entity.AttributeGroup;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.dto.GenericDTOService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoAttributeGroupService extends GenericDTOService<AttributeGroupDTO> {
    

    /**
     * Get single attribute by given code.
     * @param code given code
     * @return {@link AttributeGroup} if found, otherwise null.
     * @throws org.yes.cart.exception.UnableToCreateInstanceException in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException in case of configuration problem
     */
    AttributeGroupDTO getAttributeGroupByCode(String code)  throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Persist {@link AttributeGroup}
     * @param code code
     * @param name name
     * @param description description
     * @return created dto
     * @throws org.yes.cart.exception.UnableToCreateInstanceException in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException in case of configuration problem
     */
    AttributeGroupDTO create(String code, String name, String description) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Update  {@link AttributeGroup} entity.
     * @param code code
     * @param name name
     * @param description description
     * @return updated entity
     * @throws org.yes.cart.exception.UnableToCreateInstanceException in case of reflection problem
     * @throws org.yes.cart.exception.UnmappedInterfaceException in case of configuration problem
     */
    AttributeGroupDTO update(String code, String name, String description) throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Delete  {@link AttributeGroup} by given code.
     * @param code code of {@link AttributeGroup} to delete
     */
    void remove(String code);


}
