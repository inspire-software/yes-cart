package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Carrier;

import java.util.List;

/**
 *
 * Carrier service to work with carriers and his SLAs.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CarrierService extends GenericService<Carrier> {

    /**
     * Find carriers, that can prepare delivery to given country and state.
     *
     * At this moment only one supply point is supported.  Supply location
     * must be moved from shop to assigned warehouses to have multiple supply point.
     * TODO such sofisticated logistic shall be in separate module 
     *
     * @param countryCode given country code.
     * @param stateCode state code - optional.
     * @param city city - optional.
     * @param currency currency in shopping cart
     * @return list of carries with filtered SLA, that satisfy to given search criteria.
     */
    List<Carrier> findCarriers(String countryCode, String stateCode, String city, String currency);

}
