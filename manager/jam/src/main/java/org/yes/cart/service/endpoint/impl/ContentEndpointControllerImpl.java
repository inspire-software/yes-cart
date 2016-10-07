/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
import org.yes.cart.domain.vo.VoAttrValueContent;
import org.yes.cart.domain.vo.VoContent;
import org.yes.cart.domain.vo.VoContentBody;
import org.yes.cart.domain.vo.VoContentWithBody;
import org.yes.cart.service.endpoint.ContentEndpointController;
import org.yes.cart.service.vo.VoContentService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 09/09/2016
 * Time: 08:55
 */
@Component
public class ContentEndpointControllerImpl implements ContentEndpointController {

    private final VoContentService voContentService;

    @Autowired
    public ContentEndpointControllerImpl(final VoContentService voContentService) {
        this.voContentService = voContentService;
    }

    public @ResponseBody
    List<VoContent> getShopContent(@PathVariable("shopId") final int shopId) throws Exception {
        return voContentService.getAll(shopId);
    }

    public @ResponseBody
    List<VoContent> getFilteredContent(@PathVariable("shopId") final int shopId, @RequestBody final String filter, @PathVariable("max") final int max) throws Exception {
        return voContentService.getFiltered(shopId, filter, max);
    }

    public @ResponseBody
    VoContentWithBody getContentById(@PathVariable("id") final long id) throws Exception {
        return voContentService.getById(id);
    }

    public @ResponseBody
    VoContentWithBody createContent(@RequestBody final VoContent voContent) throws Exception {
        return voContentService.create(voContent);
    }

    public @ResponseBody
    VoContentWithBody updateContent(@RequestBody final VoContentWithBody voContent) throws Exception {
        return voContentService.update(voContent);
    }

    public @ResponseBody
    void removeContent(@PathVariable("id") final long id) throws Exception {
        voContentService.remove(id);
    }

    public @ResponseBody
    List<VoAttrValueContent> getContentAttributes(@PathVariable("contentId") final long contentId) throws Exception {
        return voContentService.getContentAttributes(contentId);
    }

    public @ResponseBody
    List<VoAttrValueContent> updateContent(@RequestBody final List<MutablePair<VoAttrValueContent, Boolean>> vo) throws Exception {
        return voContentService.update(vo);
    }

    public @ResponseBody
    List<VoContentBody> getContentBody(@PathVariable("contentId") final long contentId) throws Exception {
        return voContentService.getContentBody(contentId);
    }
}
