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

package org.yes.cart.service.vo.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCustomerService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoCustomerService;
import org.yes.cart.service.vo.VoIOSupport;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 21/08/2016
 * Time: 14:29
 */
public class VoCustomerServiceImpl implements VoCustomerService {

    private final DtoCustomerService dtoCustomerService;
    private final DtoAttributeService dtoAttributeService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;
    private final VoIOSupport voIOSupport;

    private Set<String> skipAttributesInView = Collections.emptySet();

    private final VoAttributesCRUDTemplate<VoAttrValueCustomer, AttrValueCustomerDTO> voAttributesCRUDTemplate;

    public VoCustomerServiceImpl(final DtoCustomerService dtoCustomerService,
                                 final DtoAttributeService dtoAttributeService,
                                 final FederationFacade federationFacade,
                                 final VoAssemblySupport voAssemblySupport,
                                 final VoIOSupport voIOSupport) {
        this.dtoCustomerService = dtoCustomerService;
        this.dtoAttributeService = dtoAttributeService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
        this.voIOSupport = voIOSupport;

        this.voAttributesCRUDTemplate =
                new VoAttributesCRUDTemplate<VoAttrValueCustomer, AttrValueCustomerDTO>(
                        VoAttrValueCustomer.class,
                        AttrValueCustomerDTO.class,
                        this.dtoCustomerService,
                        this.dtoAttributeService,
                        this.voAssemblySupport,
                        this.voIOSupport
                )
        {
            @Override
            protected boolean skipAttributesInView(final String code, final boolean includeSecure) {
                return skipAttributesInView.contains(code);
            }

            @Override
            protected long determineObjectId(final VoAttrValueCustomer vo) {
                return vo.getCustomerId();
            }

            @Override
            protected Pair<Boolean, String> verifyAccessAndDetermineObjectCode(final long objectId, final boolean includeSecure) throws Exception {

                boolean accessible = federationFacade.isManageable(objectId, CustomerDTO.class);
                if (!accessible) {
                    return new Pair<>(false, null);
                }
                final CustomerDTO customer = dtoCustomerService.getById(objectId);
                return new Pair<>(true, String.valueOf(customer.getCustomerId()));
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoSearchResult<VoCustomerInfo> getFilteredCustomers(final VoSearchContext filter) throws Exception {

        final VoSearchResult<VoCustomerInfo> result = new VoSearchResult<>();
        final List<VoCustomerInfo> results = new ArrayList<>();
        result.setSearchContext(filter);
        result.setItems(results);

        Set<Long> shopIds = null;
        if (!federationFacade.isCurrentUserSystemAdmin()) {
            shopIds = federationFacade.getAccessibleShopIdsByCurrentManager();
            if (CollectionUtils.isEmpty(shopIds)) {
                return result;
            }
        }

        final SearchContext searchContext = new SearchContext(
                filter.getParameters(),
                filter.getStart(),
                filter.getSize(),
                filter.getSortBy(),
                filter.isSortDesc(),
                "filter"
        );

        final SearchResult<CustomerDTO> batch = dtoCustomerService.findCustomers(shopIds, searchContext);
        results.addAll(voAssemblySupport.assembleVos(VoCustomerInfo.class, CustomerDTO.class, batch.getItems()));
        result.setTotal(batch.getTotal());

        return result;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VoCustomer getCustomerById(final long id) throws Exception {
        if (federationFacade.isManageable(id, CustomerDTO.class)) {
            final CustomerDTO customerDTO = dtoCustomerService.getById(id);
            final VoCustomer vo = voAssemblySupport.assembleVo(VoCustomer.class, CustomerDTO.class, new VoCustomer(), customerDTO);

            vo.setAttributes(getCustomerAttributes(customerDTO.getCustomerId()));

            final Map<String, VoAttrValueCustomer> attrsMap = getStringVoAttrValueCustomerMap(vo.getAttributes());

            vo.setCheckoutBocked(Boolean.valueOf(getStringVoAttrValueCustomer(attrsMap, AttributeNamesKeys.Customer.BLOCK_CHECKOUT)));
            final String blockLimit = vo.isCheckoutBocked() ? "0" : getStringVoAttrValueCustomer(attrsMap, AttributeNamesKeys.Customer.BLOCK_CHECKOUT_X);
            vo.setCheckoutBockedForOrdersOver(StringUtils.isNotBlank(blockLimit) ? new BigDecimal(NumberUtils.toInt(blockLimit)) : null);

            vo.setOrdersRequireApproval(Boolean.valueOf(getStringVoAttrValueCustomer(attrsMap, AttributeNamesKeys.Customer.B2B_REQUIRE_APPROVE)));
            final String approveLimit = vo.isOrdersRequireApproval() ? "0" : getStringVoAttrValueCustomer(attrsMap, AttributeNamesKeys.Customer.B2B_REQUIRE_APPROVE_X);
            vo.setOrdersRequireApprovalForOrdersOver(StringUtils.isNotBlank(approveLimit) ? new BigDecimal(NumberUtils.toInt(approveLimit)) : null);

            return vo;
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    protected Map<String, VoAttrValueCustomer> getStringVoAttrValueCustomerMap(final List<VoAttrValueCustomer> attrs) throws Exception {
        final Map<String, VoAttrValueCustomer> attrsMap = new HashMap<>(attrs.size() * 2);
        for (final VoAttrValueCustomer attr : attrs) {
            attrsMap.put(attr.getAttribute().getCode(), attr);
        }
        return attrsMap;
    }

    protected String getStringVoAttrValueCustomer(final Map<String, VoAttrValueCustomer> map, String key) {
        final VoAttrValueCustomer val = map.get(key);
        return val != null ? val.getVal() : null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public VoCustomer updateCustomer(final VoCustomer vo) throws Exception {

        if (federationFacade.isManageable(vo.getCustomerId(), CustomerDTO.class)) {
            final CustomerDTO customerDTO = dtoCustomerService.getById(vo.getCustomerId());
            dtoCustomerService.update(
                    voAssemblySupport.assembleDto(CustomerDTO.class, VoCustomerInfo.class, customerDTO, vo)
            );

            final Map<ShopDTO, Boolean> links = dtoCustomerService.getAssignedShop(vo.getCustomerId());
            for (final VoCustomerShopLink link : vo.getCustomerShops()) {
                if (federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    if (link.isDisabled()) {
                        // revoke only ones that were there previously active
                        for (final Map.Entry<ShopDTO, Boolean> assign : links.entrySet()) {
                            if (link.getShopId() == assign.getKey().getShopId()) {
                                if (!assign.getValue()) {
                                    dtoCustomerService.revokeShop(vo.getCustomerId(), link.getShopId(), true);
                                }
                                break;
                            }
                        }
                    } else {
                        // activate existing only if they were revoked
                        boolean found = false;
                        for (final Map.Entry<ShopDTO, Boolean> assign : links.entrySet()) {
                            if (link.getShopId() == assign.getKey().getShopId()) {
                                found = true;
                                if (assign.getValue()) {
                                    dtoCustomerService.grantShop(vo.getCustomerId(), link.getShopId(), false);
                                }
                                break;
                            }
                        }
                        // or this is new assignment
                        if (!found) {
                            dtoCustomerService.grantShop(vo.getCustomerId(), link.getShopId(), false);
                        }
                    }
                }
            }

        } else {
            throw new AccessDeniedException("Access is denied");
        }
        return getCustomerById(vo.getCustomerId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoCustomer createCustomer(final VoCustomer vo) throws Exception {

        final List<CustomerDTO> existing = dtoCustomerService.findCustomers(vo.getEmail());
        final Map<String, Set<Long>> registered = new HashMap<>();
        if (!existing.isEmpty()) {
            for (final CustomerDTO existingDto : existing) {
                final Map<ShopDTO, Boolean> shops = dtoCustomerService.getAssignedShop(existingDto.getCustomerId());
                final Set<Long> shopIds = new HashSet<>();
                for (final ShopDTO shopDTO : shops.keySet()) {
                    shopIds.add(shopDTO.getShopId());
                }
                if (registered.containsKey(existingDto.getEmail())) {
                    registered.get(existingDto.getEmail()).addAll(shopIds);
                } else {
                    registered.put(existingDto.getEmail(), shopIds);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(vo.getCustomerShops())) {

            final Set<Long> alreadyRegistered = registered.get(vo.getEmail());

            for (final VoCustomerShopLink link : vo.getCustomerShops()) {
                if (!federationFacade.isShopAccessibleByCurrentManager(link.getShopId())) {
                    throw new AccessDeniedException("Access is denied");
                }
                if (alreadyRegistered != null && alreadyRegistered.contains(link.getShopId())) {
                    throw new DuplicateKeyException(vo.getEmail() + " is already registered in " + link.getShopId());
                }
            }
        } else {
            throw new AccessDeniedException("Access is denied");
        }


        CustomerDTO customerDTO =
            voAssemblySupport.assembleDto(CustomerDTO.class, VoCustomerInfo.class, dtoCustomerService.getNew(), vo);
        customerDTO.setEmail(vo.getEmail());
        customerDTO = dtoCustomerService.createForShop(
                customerDTO,
                vo.getCustomerShops().get(0).getShopId()
        );

        for (final VoCustomerShopLink link : vo.getCustomerShops()) {
            dtoCustomerService.grantShop(customerDTO.getCustomerId(), link.getShopId(), false);
        }

        return getCustomerById(customerDTO.getCustomerId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeCustomer(final long id) throws Exception {
        if (federationFacade.isCurrentUserSystemAdmin()) {
            dtoCustomerService.remove(id);
        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoAttrValueCustomer> getCustomerAttributes(final long customerId) throws Exception {

        return voAttributesCRUDTemplate.verifyAccessAndGetAttributes(customerId, true);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<VoAttrValueCustomer> updateCustomerAttributes(final List<MutablePair<VoAttrValueCustomer, Boolean>> vo) throws Exception {

        final long customerId = voAttributesCRUDTemplate.verifyAccessAndUpdateAttributes(vo, true);

        return getCustomerAttributes(customerId);
    }

    @Override
    public void resetPassword(final long customerId, final long shopId) throws Exception {
        if (federationFacade.isManageable(customerId, CustomerDTO.class) && federationFacade.isShopAccessibleByCurrentManager(shopId)) {

            dtoCustomerService.resetPassword(dtoCustomerService.getById(customerId), shopId);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /**
     * Spring IoC
     *
     * @param attributes attributes to skip
     */
    public void setSkipAttributesInView(List<String> attributes) {
        this.skipAttributesInView = new HashSet<>(attributes);
    }
}
