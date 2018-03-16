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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoAttrValueContent;
import org.yes.cart.domain.vo.VoContent;
import org.yes.cart.domain.vo.VoContentBody;
import org.yes.cart.domain.vo.VoContentWithBody;
import org.yes.cart.service.endpoint.ContentEndpointController;
import org.yes.cart.service.vo.VoContentService;
import org.yes.cart.service.vo.VoMailService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 09/09/2016
 * Time: 08:55
 */
@Component
public class ContentEndpointControllerImpl implements ContentEndpointController {

    private final VoContentService voContentService;
    private final VoMailService voMailService;

    @Autowired
    public ContentEndpointControllerImpl(final VoContentService voContentService,
                                         final VoMailService voMailService) {
        this.voContentService = voContentService;
        this.voMailService = voMailService;
    }

    @Override
    public @ResponseBody
    List<VoContent> getShopContent(@PathVariable("shopId") final long shopId) throws Exception {
        return voContentService.getAll(shopId);
    }

    @Override
    public @ResponseBody
    List<VoContent> getShopBranchContent(@PathVariable("shopId") final long shopId, @PathVariable("branch") final long branch, @RequestParam(value = "expand", required = false) final String expand) throws Exception {
        return voContentService.getBranch(shopId, branch, determineBranchIds(expand));
    }

    @Override
    public @ResponseBody
    List<Long> getShopBranchesContentPaths(@PathVariable("shopId") final long shopId, @RequestParam(value = "expand", required = false) final String expand) throws Exception {
        return voContentService.getBranchesPaths(shopId, determineBranchIds(expand));
    }

    private List<Long> determineBranchIds(final @RequestParam(value = "expand", required = false) String expand) {
        List<Long> expandIds = new ArrayList<>(50);
        if (StringUtils.isNotBlank(expand)) {
            for (final String expandItem : StringUtils.split(expand, '|')) {
                final long id = NumberUtils.toLong(expandItem);
                if (id > 0L) {
                    expandIds.add(id);
                }
            }
        }
        return expandIds;
    }

    @Override
    public @ResponseBody
    List<VoContent> getFilteredContent(@PathVariable("shopId") final long shopId, @RequestBody(required = false) final String filter, @PathVariable("max") final int max) throws Exception {
        return voContentService.getFiltered(shopId, filter, max);
    }

    @Override
    public @ResponseBody
    VoContentWithBody getContentById(@PathVariable("id") final long id) throws Exception {
        return voContentService.getById(id);
    }

    @Override
    public @ResponseBody
    VoContentWithBody createContent(@RequestBody final VoContent voContent) throws Exception {
        return voContentService.create(voContent);
    }

    @Override
    public @ResponseBody
    VoContentWithBody updateContent(@RequestBody final VoContentWithBody voContent) throws Exception {
        return voContentService.update(voContent);
    }

    @Override
    public @ResponseBody
    void removeContent(@PathVariable("id") final long id) throws Exception {
        voContentService.remove(id);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueContent> getContentAttributes(@PathVariable("contentId") final long contentId) throws Exception {
        return voContentService.getContentAttributes(contentId);
    }

    @Override
    public @ResponseBody
    List<VoAttrValueContent> updateContent(@RequestBody final List<MutablePair<VoAttrValueContent, Boolean>> vo) throws Exception {
        return voContentService.update(vo);
    }

    @Override
    public @ResponseBody
    List<VoContentBody> getContentBody(@PathVariable("contentId") final long contentId) throws Exception {
        return voContentService.getContentBody(contentId);
    }

    @Override
    public @ResponseBody
    String getShopMail(@PathVariable("shopId") final long shopId, @PathVariable("template") final String template, @RequestParam(value = "order", required = false) final String order, @RequestParam(value = "delivery", required = false) final String delivery, @RequestParam(value = "customer", required = false) final String customer) throws Exception {
        return voMailService.getShopMail(shopId, template, order, delivery, customer);
    }
}
