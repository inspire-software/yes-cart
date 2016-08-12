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
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoAttribute;
import org.yes.cart.domain.vo.VoAttributeGroup;
import org.yes.cart.domain.vo.VoEtype;
import org.yes.cart.service.vo.AttributeEndpointController;
import org.yes.cart.service.vo.VoAttributeService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 09/08/2016
 * Time: 18:43
 */
@Component
public class AttributeEndpointControllerImpl implements AttributeEndpointController {

    private final VoAttributeService voAttributeService;

    @Autowired
    public AttributeEndpointControllerImpl(final VoAttributeService voAttributeService) {
        this.voAttributeService = voAttributeService;
    }

    public @ResponseBody
    List<VoEtype> getAllEtypes() throws Exception {
        return voAttributeService.getAllEtypes();
    }

    public @ResponseBody
    List<VoAttributeGroup> getAllGroups() throws Exception {
        return voAttributeService.getAllGroups();
    }

    public @ResponseBody
    List<VoAttribute> getAllAttributes(@PathVariable("group") final String group) throws Exception {
        return voAttributeService.getAllAttributes(group);
    }

    public @ResponseBody
    List<VoAttribute> getFilteredAttributes(@PathVariable("group") final String group, @RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
        return voAttributeService.getFilteredAttributes(group, filter, max);
    }

    public @ResponseBody
    List<MutablePair<Long, String>> getProductTypesByAttributeCode(@PathVariable("code") final String code) throws Exception {
        return voAttributeService.getProductTypesByAttributeCode(code);
    }

    public @ResponseBody
    VoAttribute getAttributeById(@PathVariable("id") final long id) throws Exception {
        return voAttributeService.getAttributeById(id);
    }

    public @ResponseBody
    VoAttribute createAttribute(@RequestBody final VoAttribute vo) throws Exception {
        return voAttributeService.createAttribute(vo);
    }

    public @ResponseBody
    VoAttribute updateAttribute(@RequestBody final VoAttribute vo) throws Exception {
        return voAttributeService.updateAttribute(vo);
    }

    public @ResponseBody
    void removeAttribute(@PathVariable("id") final long id) throws Exception {
        voAttributeService.removeAttribute(id);
    }
}
