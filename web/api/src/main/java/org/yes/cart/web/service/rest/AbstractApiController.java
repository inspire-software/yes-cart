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

package org.yes.cart.web.service.rest;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.ro.TokenRO;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.shoppingcart.ShoppingCartPersister;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 20/08/2014
 * Time: 09:11
 */
public class AbstractApiController {


    @Autowired
    @Qualifier("roInterfaceToClassFactory")
    private DtoFactory dtoFactory;
    @Autowired
    @Qualifier("roAssemblerAdaptersRepository")
    private AdaptersRepository adaptersRepository;
    @Autowired
    @Qualifier("shoppingCartPersister")
    private ShoppingCartPersister shoppingCartPersister;

    /**
     * Generic mapping method.
     *
     * @param objects object to map
     * @param ro RO class required
     * @param entity entity class provided
     * @param <RO> type
     * @param <Entity> type
     *
     * @return list of RO
     */
    protected <RO, Entity> List<RO> map(final List<Entity> objects, final Class<RO> ro, final Class<Entity> entity) {
        final List<RO> ros = new ArrayList<RO>();
        if (objects != null) {
            DTOAssembler.newAssembler(ro, entity).assembleDtos(ros, objects, adaptersRepository.getAll(), dtoFactory);
        }
        return ros;
    }

    /**
     * Generic mapping method.
     *
     * @param object object to map
     * @param ro RO class required
     * @param entity entity class provided
     * @param <RO> type
     * @param <Entity> type
     *
     * @return list of RO
     */
    protected <RO, Entity> RO map(final Entity object, final Class<RO> ro, final Class<Entity> entity) {
        if (entity != null) {
            final RO dto = dtoFactory.getByIface(ro);
            DTOAssembler.newAssembler(ro, entity).assembleDto(dto, object, adaptersRepository.getAll(), dtoFactory);
            return dto;
        }
        return null;
    }

    /**
     * Retrieve current cart.
     *
     * @return cart object
     */
    protected ShoppingCart getCurrentCart() {
        return ApplicationDirector.getShoppingCart();
    }

    /**
     * Generate token ro for current state of the cart.
     *
     * @return token
     */
    protected TokenRO persistShoppingCart(HttpServletRequest request, HttpServletResponse response) {

        final ShoppingCart cart = getCurrentCart();

        if (cart.isModified()) {

            shoppingCartPersister.persistShoppingCart(request, response, cart);

        }

        return new TokenRO(cart.getGuid());

    }


}
