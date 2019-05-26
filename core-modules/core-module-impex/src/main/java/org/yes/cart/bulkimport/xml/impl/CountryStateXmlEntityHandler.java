/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.bulkimport.xml.internal.CountryStateType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.State;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.StateService;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class CountryStateXmlEntityHandler extends AbstractXmlEntityHandler<CountryStateType, State> implements XmlEntityImportHandler<CountryStateType, State> {

    private StateService stateService;

    public CountryStateXmlEntityHandler() {
        super("country-state");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final State state, final Map<String, Integer> entityCount) {
        this.stateService.delete(state);
        this.stateService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final State domain, final CountryStateType xmlType, final EntityImportModeType mode, final Map<String, Integer> entityCount) {
        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        if (domain.getStateId() == 0L) {
            this.stateService.create(domain);
        } else {
            this.stateService.update(domain);
        }
        this.stateService.getGenericDao().flush();
        this.stateService.getGenericDao().evict(domain);
    }

    @Override
    protected State getOrCreate(final JobStatusListener statusListener, final CountryStateType xmlType, final Map<String, Integer> entityCount) {
        State state = this.stateService.findSingleByCriteria(" where e.stateCode = ?1", xmlType.getRegionCode());
        if (state != null) {
            return state;
        }
        state = this.stateService.getGenericDao().getEntityFactory().getByIface(State.class);
        state.setGuid(xmlType.getRegionCode());
        state.setStateCode(xmlType.getRegionCode());
        state.setCountryCode(xmlType.getIso31661Alpha2());
        return state;
    }

    @Override
    protected EntityImportModeType determineImportMode(final CountryStateType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final State domain) {
        return domain.getStateId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param stateService country state service
     */
    public void setStateService(final StateService stateService) {
        this.stateService = stateService;
    }
}
