package org.yes.cart.service.domain.impl;

import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.Availability;
import org.yes.cart.service.domain.AvailabilityService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AvailabilityServiceImpl extends BaseGenericServiceImpl<Availability> implements AvailabilityService {

    public AvailabilityServiceImpl(final GenericDAO<Availability, Long> availabilityDao) {
        super(availabilityDao);
    }

}
