package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.DtoToEntityMatcher;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.entity.AttrValueCustomer;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AttrValueCustomerMatcher implements DtoToEntityMatcher<AttrValueCustomerDTO, AttrValueCustomer> {


    /** {@inheritDoc} */
    public boolean match(final AttrValueCustomerDTO attrValueCustomerDTO, final AttrValueCustomer attrValueCustomer) {
        return attrValueCustomerDTO != null
                && attrValueCustomer != null
                && (attrValueCustomer.getAttrvalueId() == attrValueCustomerDTO.getAttrvalueId());
    }
}
