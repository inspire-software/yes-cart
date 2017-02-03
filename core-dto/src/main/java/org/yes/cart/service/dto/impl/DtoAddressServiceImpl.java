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
import org.yes.cart.domain.dto.AddressDTO;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.AddressDTOImpl;
import org.yes.cart.domain.dto.impl.AttrValueCustomerDTOImpl;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.dto.DtoAddressService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoAddressServiceImpl extends AbstractDtoServiceImpl<AddressDTO, AddressDTOImpl, Address> implements DtoAddressService {


    private final CustomerService customerService;
    private final ShopService shopService;
    private final AddressCustomisationSupport addressCustomisationSupport;

    /**
     * Construct base dto service.
     * @param dtoFactory               {@link DtoFactory}
     * @param addressGenericService    {@link GenericService}
     * @param adaptersRepository {@link AdaptersRepository}
     * @param customerService service
     * @param shopService service
     * @param addressCustomisationSupport support
     */
    public DtoAddressServiceImpl(final DtoFactory dtoFactory,
                                 final GenericService<Address> addressGenericService,
                                 final AdaptersRepository adaptersRepository,
                                 final CustomerService customerService,
                                 final ShopService shopService,
                                 final AddressCustomisationSupport addressCustomisationSupport) {
        super(dtoFactory, addressGenericService, adaptersRepository);
        this.customerService = customerService;
        this.shopService = shopService;
        this.addressCustomisationSupport = addressCustomisationSupport;
    }


    /** {@inheritDoc} */
    public List<AddressDTO> getAddressesByCustomerId(final long customerId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {        
        return getDTOs(((AddressService)service).getAddressesByCustomerId(customerId));

    }

    /** {@inheritDoc} */
    public List<Pair<Long, String>> getFormattedAddressesByCustomerId(final long customerId, final long formattingShopId, final String lang)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<Pair<Long, String>> formatted = new ArrayList<Pair<Long, String>>();
        final Customer customer = this.customerService.findById(customerId);
        if (customer != null) {

            Shop shop = this.shopService.getById(formattingShopId);

            if (shop != null) {

                if (shop.getMaster() != null) {
                    shop = shop.getMaster();
                }
            }

            final List<Address> addresses = ((AddressService) service).getAddressesByCustomerId(customerId);

            for (final Address address : addresses) {

                formatted.add(
                        new Pair<Long, String>(
                                address.getAddressId(),
                                this.addressCustomisationSupport.formatAddressFor(address, shop, customer, lang)
                        )
                );
            }
        }

        return formatted;
    }

    /** {@inheritDoc} */
    public List<AttrValueCustomerDTO> getAddressForm(final long customerId, final long formattingShopId, final String addressType) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final List<AttrValueCustomerDTO> dtos = new ArrayList<AttrValueCustomerDTO>();

        final List<Pair<Long, String>> formatted = new ArrayList<Pair<Long, String>>();
        final Customer customer = this.customerService.findById(customerId);
        if (customer != null) {

            Shop shop = this.shopService.getById(formattingShopId);

            if (shop != null) {

                if (shop.getMaster() != null) {
                    shop = shop.getMaster();
                }
            }

            final List<AttrValueCustomer> fields = (List) this.addressCustomisationSupport.getShopCustomerAddressAttributes(customer, shop, addressType);

            final Assembler asm = DTOAssembler.newAssembler(AttrValueCustomerDTOImpl.class, AttrValueCustomer.class);

            for (final AttrValueCustomer field : fields) {

                final AttrValueCustomerDTO dto = new AttrValueCustomerDTOImpl();

                asm.assembleDto(dto, field, getAdaptersRepository(), getAssemblerDtoFactory());

                dtos.add(dto);

            }

        }

        return dtos;

    }

    /** {@inheritDoc} */
    protected void createPostProcess(final AddressDTO dto, final Address entity) {
        if (dto.getCustomerId() > 0L) {
            entity.setCustomer(customerService.findById(dto.getCustomerId()));
        }
    }

    /** {@inheritDoc} */
    public AddressDTO update(final AddressDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        super.update(instance);
        if (instance.isDefaultAddress()) {
            final Address address = this.service.findById(instance.getAddressId());
            if (!address.isDefaultAddress()) {
                ((AddressService) this.service).updateSetDefault(address);
            }
        }
        return getById(instance.getAddressId());
    }

    /** {@inheritDoc} */
    public Class<AddressDTO> getDtoIFace() {
        return AddressDTO.class;
    }

    /** {@inheritDoc} */
    public Class<AddressDTOImpl> getDtoImpl() {
        return AddressDTOImpl.class;
    }

    /** {@inheritDoc} */
    public Class<Address> getEntityIFace() {
        return Address.class;
    }
}
