/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.CountryType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CountryService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class CountryXmlEntityHandler extends AbstractXmlEntityHandler<CountryType, Country> implements XmlEntityImportHandler<CountryType, Country> {

    private CountryService countryService;

    public CountryXmlEntityHandler() {
        super("country");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Country country) {
        this.countryService.delete(country);
        this.countryService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Country domain, final CountryType xmlType, final EntityImportModeType mode) {
        domain.setIsoCode(xmlType.getIso31661Numeric());
        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        if (domain.getCountryId() == 0L) {
            this.countryService.create(domain);
        } else {
            this.countryService.update(domain);
        }
        this.countryService.getGenericDao().flush();
        this.countryService.getGenericDao().evict(domain);
    }

    @Override
    protected Country getOrCreate(final JobStatusListener statusListener, final CountryType xmlType) {
        Country country = this.countryService.findSingleByCriteria(" where e.countryCode = ?1", xmlType.getIso31661Alpha2());
        if (country != null) {
            return country;
        }
        country = this.countryService.getGenericDao().getEntityFactory().getByIface(Country.class);
        country.setCreatedBy(xmlType.getCreatedBy());
        country.setCreatedTimestamp(processInstant(xmlType.getCreatedTimestamp()));
        country.setGuid(xmlType.getIso31661Alpha2());
        country.setCountryCode(xmlType.getIso31661Alpha2());
        return country;
    }

    @Override
    protected EntityImportModeType determineImportMode(final CountryType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Country domain) {
        return domain.getCountryId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param countryService country service
     */
    public void setCountryService(final CountryService countryService) {
        this.countryService = countryService;
    }
}
