/*
 * Copyright 2009- 2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoCountry;
import org.yes.cart.domain.vo.VoState;
import org.yes.cart.service.endpoint.LocationEndpointController;
import org.yes.cart.service.vo.VoLocationService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 15:41
 */
@Component
public class LocationEndpointControllerImpl implements LocationEndpointController {

    private final VoLocationService voLocationService;

    @Autowired
    public LocationEndpointControllerImpl(final VoLocationService voLocationService) {
        this.voLocationService = voLocationService;
    }

    @Override
    public @ResponseBody List<VoCountry> getAllCountries() throws Exception {
        return voLocationService.getAllCountries();
    }

    @Override
    public @ResponseBody VoCountry getCountryById(@PathVariable("id") final long id) throws Exception {
        return voLocationService.getCountryById(id);
    }

    @Override
    public @ResponseBody VoCountry createCountry(@RequestBody final VoCountry voCategory) throws Exception {
        return voLocationService.createCountry(voCategory);
    }

    @Override
    public @ResponseBody VoCountry updateCountry(@RequestBody final VoCountry voCategory) throws Exception {
        return voLocationService.updateCountry(voCategory);
    }

    @Override
    public @ResponseBody void removeCountry(@PathVariable("id") final long id) throws Exception {
        voLocationService.removeCountry(id);
    }

    @Override
    public @ResponseBody List<VoState> getAllStates(@PathVariable("id") final long id) throws Exception {
        return voLocationService.getAllStates(id);
    }

    @Override
    public @ResponseBody VoState getStateById(@PathVariable("id") final long id) throws Exception {
        return voLocationService.getStateById(id);
    }

    @Override
    public @ResponseBody VoState createState(@RequestBody final VoState voState) throws Exception {
        return voLocationService.createState(voState);
    }

    @Override
    public @ResponseBody VoState updateState(@RequestBody final VoState voState) throws Exception {
        return voLocationService.updateState(voState);
    }

    @Override
    public @ResponseBody void removeState(@PathVariable("id") final long id) throws Exception {
        voLocationService.removeState(id);
    }
}
