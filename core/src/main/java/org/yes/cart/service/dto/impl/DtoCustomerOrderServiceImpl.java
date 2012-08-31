/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.dto.CustomerOrderDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.CustomerOrderDTOImpl;
import org.yes.cart.domain.dto.impl.CustomerOrderDeliveryDTOImpl;
import org.yes.cart.domain.dto.impl.CustomerOrderDeliveryDetailDTOImpl;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.dto.DtoCustomerOrderService;
import org.yes.cart.util.ShopCodeContext;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCustomerOrderServiceImpl
        extends AbstractDtoServiceImpl<CustomerOrderDTO, CustomerOrderDTOImpl, CustomerOrder>
        implements DtoCustomerOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ShopCodeContext.getShopCode());

    protected final Assembler orderDeliveryDetailAssembler;
    protected final Assembler orderDeliveryAssembler;

    /**
     * Construct service.
     *
     * @param dtoFactory                  {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param customerOrderGenericService generic serivce
     * @param adaptersRepository    value converter
     */
    public DtoCustomerOrderServiceImpl(
            final DtoFactory dtoFactory,
            final GenericService<CustomerOrder> customerOrderGenericService,
            final AdaptersRepository adaptersRepository) {
        super(dtoFactory, customerOrderGenericService, adaptersRepository);
        orderDeliveryDetailAssembler = DTOAssembler.newAssembler(CustomerOrderDeliveryDetailDTOImpl.class, CustomerOrderDeliveryDet.class);
        orderDeliveryAssembler = DTOAssembler.newAssembler(CustomerOrderDeliveryDTOImpl.class, CustomerOrderDelivery.class);
    }

    /**
     * {@inheritDoc}
     */
    public CustomerOrderDTO create(final CustomerOrderDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnableToCreateInstanceException("Customer order cannot be created via back end", null);
    }


    /**
     * {@inheritDoc}
     */
    public Class<CustomerOrderDTO> getDtoIFace() {
        return CustomerOrderDTO.class;
    }

    public Class<CustomerOrderDTOImpl> getDtoImpl() {
        return CustomerOrderDTOImpl.class;
    }

    public Class<CustomerOrder> getEntityIFace() {
        return CustomerOrder.class;
    }

    /** {@inheritDoc} */
    public List<CustomerOrderDeliveryDTO> findDeliveryByOrderNumber(final String orderNum)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CustomerOrder> orderList = ((CustomerOrderService) service).findCustomerOrdersByCriteria(
                0, null, null, null, null, null, null, orderNum);

        if (CollectionUtils.isNotEmpty(orderList)) {
            final CustomerOrder customerOrder = orderList.get(0);
            final List<CustomerOrderDeliveryDTO> rez = new ArrayList<CustomerOrderDeliveryDTO>(customerOrder.getDelivery().size());
            for(CustomerOrderDelivery delivery : customerOrder.getDelivery()) {
                final CustomerOrderDeliveryDTO dto = dtoFactory.getByIface(CustomerOrderDeliveryDTO.class);
                orderDeliveryAssembler.assembleDto(dto, delivery, getAdaptersRepository(), dtoFactory);
                rez.add(dto);
            }
            return rez;

        }   else {
            LOG.warn("Customer order not found. Order number is " + orderNum);
        }
        return Collections.emptyList();

    }

    /** {@inheritDoc} */
    public List<CustomerOrderDeliveryDetailDTO> findDeliveryDetailsByOrderNumber(final String orderNum) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<CustomerOrder> orderList = ((CustomerOrderService) service).findCustomerOrdersByCriteria(
                0, null, null, null, null, null, null, orderNum);

        if (CollectionUtils.isNotEmpty(orderList)) {
            final CustomerOrder customerOrder = orderList.get(0);
            final List<CustomerOrderDeliveryDet> allDeliveryDet = new ArrayList<CustomerOrderDeliveryDet>();
            for (CustomerOrderDelivery orderDelivery : customerOrder.getDelivery())  {
                allDeliveryDet.addAll(orderDelivery.getDetail());
            }
            final List<CustomerOrderDeliveryDetailDTO> rez = new ArrayList<CustomerOrderDeliveryDetailDTO>(allDeliveryDet.size());

            for (CustomerOrderDeliveryDet entity : allDeliveryDet) {
                CustomerOrderDeliveryDetailDTO dto =  dtoFactory.getByIface(CustomerOrderDeliveryDetailDTO.class);
                orderDeliveryDetailAssembler.assembleDto(dto, entity, getAdaptersRepository(), dtoFactory);
                rez.add(dto);
            }

            return rez;
        }   else {
            LOG.warn("Customer order not found. Order num is " + orderNum);
        }
        return Collections.emptyList();

    }


    /**
     * {@inheritDoc}
     */
    public List<CustomerOrderDTO> findCustomerOrdersByCriteria(
            final long customerId,
            final String firstName,
            final String lastName,
            final String email,
            final String orderStatus,
            final Date fromDate,
            final Date toDate,
            final String orderNum
    ) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<CustomerOrder> orders = ((CustomerOrderService) service).findCustomerOrdersByCriteria(
                customerId,
                firstName,
                lastName,
                email,
                orderStatus,
                fromDate,
                toDate,
                orderNum
        );
        final List<CustomerOrderDTO> ordersDtos = new ArrayList<CustomerOrderDTO>(orders.size());
        fillDTOs(orders, ordersDtos);
        return ordersDtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillDTOs(final Collection<CustomerOrder> entities, final Collection<CustomerOrderDTO> dtos)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        for (CustomerOrder entity : entities) {
            CustomerOrderDTO dto = (CustomerOrderDTO) dtoFactory.getByIface(getDtoIFace());
            assembler.assembleDto(dto, entity, getAdaptersRepository(), dtoFactory);
            dto.setAmount(((CustomerOrderService) service).getOrderAmount(entity.getOrdernum()));
            dtos.add(dto);
        }
    }
}
