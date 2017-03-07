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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.dto.DtoShoppingCartService;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.service.order.OrderDisassembler;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 16/03/2016
 * Time: 17:31
 */
public class DtoShoppingCartServiceImpl implements DtoShoppingCartService {

    private static final Logger LOG = LoggerFactory.getLogger(DtoShoppingCartServiceImpl.class);

    private final DtoFactory dtoFactory;
    private final AdaptersRepository adaptersRepository;
    private final CustomerOrderService customerOrderService;
    private final CartRepository cartRepository;
    private final OrderDisassembler orderDisassembler;
    private final ShoppingCartCommand shoppingCartCommandFactory;

    protected final Assembler assembler;

    public DtoShoppingCartServiceImpl(final DtoFactory dtoFactory,
                                      final CustomerOrderService customerOrderService,
                                      final CartRepository cartRepository,
                                      final OrderDisassembler orderDisassembler,
                                      final ShoppingCartCommand shoppingCartCommandFactory,
                                      final AdaptersRepository adaptersRepository) {
        this.dtoFactory = dtoFactory;
        this.adaptersRepository = adaptersRepository;
        this.customerOrderService = customerOrderService;
        this.cartRepository = cartRepository;
        this.orderDisassembler = orderDisassembler;
        this.shoppingCartCommandFactory = shoppingCartCommandFactory;
        this.assembler = DTOAssembler.newAssembler(dtoFactory.getImplClass(ShoppingCart.class), ShoppingCart.class);

    }

    /** {@inheritDoc} */
    public ShoppingCart create(final String ref, final boolean recalculate) {

        final CustomerOrder order = customerOrderService.findByReference(ref);
        if (order != null) {
            try {
                final ShoppingCart cart = orderDisassembler.assembleShoppingCart(order, !recalculate);
                cartRepository.storeShoppingCart(cart);
                return fillDTO(cart);
            } catch (OrderAssemblyException e) {
                LOG.error("Unable to create shopping cart for: " + ref + ", cause: " + e.getMessage(), e);
            }
        }
        return null;
    }

    private ShoppingCart fillDTO(final ShoppingCart cart) {
        final ShoppingCart cartDTO = dtoFactory.getByIface(ShoppingCart.class);
        this.assembler.assembleDto(cartDTO, cart, this.adaptersRepository.getAll(), dtoFactory);
        return cartDTO;
    }

    /** {@inheritDoc} */
    public ShoppingCart getById(final String guid) {

        final ShoppingCart cart = cartRepository.getShoppingCart(guid);
        if (cart != null) {
            return fillDTO(cart);
        }
        return null;
    }

    /** {@inheritDoc} */
    public void removeLine(final String guid, final String sku) {

        final ShoppingCart cart = cartRepository.getShoppingCart(guid);
        if (cart != null) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put(ShoppingCartCommand.CMD_REMOVEALLSKU, sku);
            shoppingCartCommandFactory.execute(cart, (Map) params);
            cartRepository.storeShoppingCart(cart);
        }

    }

    /** {@inheritDoc} */
    public void updateLineQuantity(final String guid, final String sku, final BigDecimal quantity) {

        final ShoppingCart cart = cartRepository.getShoppingCart(guid);
        if (cart != null) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put(ShoppingCartCommand.CMD_SETQTYSKU, sku);
            params.put(ShoppingCartCommand.CMD_SETQTYSKU_P_QTY, quantity.toPlainString());
            shoppingCartCommandFactory.execute(cart, (Map) params);
            cartRepository.storeShoppingCart(cart);
        }

    }

    /** {@inheritDoc} */
    public void updateLinePrice(final String guid, final String sku, final BigDecimal offer, final String auth) {

        final ShoppingCart cart = cartRepository.getShoppingCart(guid);
        if (cart != null) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put(ShoppingCartCommand.CMD_SETPRICE, sku);
            params.put(ShoppingCartCommand.CMD_SETPRICE_P_PRICE, offer.toPlainString());
            params.put(ShoppingCartCommand.CMD_SETPRICE_P_AUTH, auth);
            shoppingCartCommandFactory.execute(cart, (Map) params);
            cartRepository.storeShoppingCart(cart);
        }

    }

    /** {@inheritDoc} */
    public void remove(final String guid) {

        final ShoppingCart cart = cartRepository.getShoppingCart(guid);
        if (cart != null) {
            cartRepository.evictShoppingCart(cart);
        }
    }
}
