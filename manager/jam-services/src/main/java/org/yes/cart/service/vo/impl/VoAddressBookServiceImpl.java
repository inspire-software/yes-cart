package org.yes.cart.service.vo.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.AddressDTO;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.entity.State;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoAddress;
import org.yes.cart.domain.vo.VoAddressBook;
import org.yes.cart.domain.vo.VoAttrValue;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.CountryService;
import org.yes.cart.service.domain.StateService;
import org.yes.cart.service.dto.DtoAddressService;
import org.yes.cart.service.dto.DtoShopService;
import org.yes.cart.service.federation.FederationFacade;
import org.yes.cart.service.vo.VoAddressBookService;
import org.yes.cart.service.vo.VoAssemblySupport;

import java.util.*;

/**
 * User: denispavlov
 * Date: 31/01/2017
 * Time: 14:49
 */
public class VoAddressBookServiceImpl implements VoAddressBookService {

    private final DtoAddressService dtoAddressService;
    private final DtoShopService dtoShopService;
    private final CountryService countryService;
    private final StateService stateService;

    private final FederationFacade federationFacade;
    private final VoAssemblySupport voAssemblySupport;

    public VoAddressBookServiceImpl(final DtoAddressService dtoAddressService,
                                    final DtoShopService dtoShopService,
                                    final CountryService countryService,
                                    final StateService stateService,
                                    final FederationFacade federationFacade,
                                    final VoAssemblySupport voAssemblySupport) {
        this.dtoAddressService = dtoAddressService;
        this.dtoShopService = dtoShopService;
        this.countryService = countryService;
        this.stateService = stateService;
        this.federationFacade = federationFacade;
        this.voAssemblySupport = voAssemblySupport;
    }


    /** {@inheritDoc} */
    public VoAddressBook getAddressBook(final long customerId, final long formattingShopId, final String lang) throws Exception {


        if (federationFacade.isManageable(customerId, CustomerDTO.class)) {

            final VoAddressBook addressBook = new VoAddressBook();

            addressBook.setFormattingShopId(formattingShopId);

            setShopLocations(addressBook, formattingShopId);

            setAddressBookAddresses(addressBook, customerId);

            setFormattedAddresses(addressBook, customerId, formattingShopId, lang);

            setAddressFormFieldsConfiguration(addressBook, customerId, formattingShopId);

            return addressBook;

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    protected void setShopLocations(final VoAddressBook addressBook, long shopId) throws Exception {

        final ShopDTO shop = this.dtoShopService.getById(shopId);
        String billing = dtoShopService.getSupportedBillingCountries(shop.getMasterId() != null ? shop.getMasterId() : shop.getShopId());
        String shipping = dtoShopService.getSupportedShippingCountries(shop.getMasterId() != null ? shop.getMasterId() : shop.getShopId());
        List<String> billingCodes = billing == null ? Collections.<String>emptyList() : Arrays.asList(billing.split(","));
        List<String> shippingCodes = shipping == null ? Collections.<String>emptyList() : Arrays.asList(shipping.split(","));

        final List<Country> countries = countryService.findAll();
        final Map<String, List<MutablePair<String, String>>> allCountries = new HashMap<String, List<MutablePair<String, String>>>();
        final Map<String, List<MutablePair<String, String>>> allStates = new HashMap<String, List<MutablePair<String, String>>>();
        for (final Country country : countries) {

            final boolean billingCountry = billingCodes.contains(country.getCountryCode());
            final boolean shippingCountry = shippingCodes.contains(country.getCountryCode());

            if (billingCountry || shippingCountry) {

                final MutablePair<String, String> countryInfo = MutablePair.of(
                        country.getCountryCode(),
                        country.getName() + (StringUtils.isNotBlank(country.getDisplayName()) ? " (" + country.getDisplayName() + ")" : ""));

                if (billingCountry) {
                    List<MutablePair<String, String>> allBilling = allCountries.get(Address.ADDR_TYPE_BILLING);
                    if (allBilling == null) {
                        allBilling = new ArrayList<MutablePair<String, String>>();
                        allCountries.put(Address.ADDR_TYPE_BILLING, allBilling);
                    }
                    allBilling.add(countryInfo);
                }

                if (shippingCountry) {
                    List<MutablePair<String, String>> allShipping = allCountries.get(Address.ADDR_TYPE_SHIPPING);
                    if (allShipping == null) {
                        allShipping = new ArrayList<MutablePair<String, String>>();
                        allCountries.put(Address.ADDR_TYPE_SHIPPING, allShipping);
                    }
                    allShipping.add(countryInfo);
                }

                final List<State> states = stateService.findByCountry(country.getCountryCode());
                final List<MutablePair<String, String>> countryStates = new ArrayList<MutablePair<String, String>>();
                for (final State state : states) {
                    countryStates.add(MutablePair.of(
                            state.getStateCode(),
                            state.getName() + (StringUtils.isNotBlank(state.getDisplayName()) ? " (" + state.getDisplayName() + ")" : "")));
                }
                allStates.put(country.getCountryCode(), countryStates);

            }
        }

        final List<MutablePair<String, List<MutablePair<String, String>>>> voCountries = new ArrayList<MutablePair<String, List<MutablePair<String, String>>>>();
        for (final Map.Entry<String, List<MutablePair<String, String>>> countryEntry : allCountries.entrySet()) {
            voCountries.add(MutablePair.of(countryEntry.getKey(), countryEntry.getValue()));
        }

        addressBook.setCountries(voCountries);

        final List<MutablePair<String, List<MutablePair<String, String>>>> voStates = new ArrayList<MutablePair<String, List<MutablePair<String, String>>>>();
        for (final Map.Entry<String, List<MutablePair<String, String>>> stateEntry : allStates.entrySet()) {
            voStates.add(MutablePair.of(stateEntry.getKey(), stateEntry.getValue()));
        }

        addressBook.setStates(voStates);

    }


    protected void setAddressBookAddresses(final VoAddressBook addressBook, final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<AddressDTO> addresses = this.dtoAddressService.getAddressesByCustomerId(customerId);

        addressBook.setAddresses(
                voAssemblySupport.assembleVos(VoAddress.class, AddressDTO.class, addresses)
        );
    }

    protected void setFormattedAddresses(final VoAddressBook addressBook, final long customerId, final long formattingShopId, final String lang) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Pair<Long, String>> formattedOriginal = this.dtoAddressService.getFormattedAddressesByCustomerId(customerId, formattingShopId, lang);
        final List<MutablePair<Long, String>> formatted = new ArrayList<MutablePair<Long, String>>();
        for (final Pair<Long, String> formattedAddress : formattedOriginal) {
            formatted.add(MutablePair.of(formattedAddress.getFirst(), formattedAddress.getSecond()));
        }
        addressBook.setFormattedAddresses(formatted);
    }

    protected void setAddressFormFieldsConfiguration(final VoAddressBook addressBook, final long customerId, final long formattingShopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<MutablePair<String, List<VoAttrValue>>> addressForm = new ArrayList<MutablePair<String, List<VoAttrValue>>>();
        addressForm.add(
                MutablePair.of(
                        Address.ADDR_TYPE_SHIPPING,
                        voAssemblySupport.assembleVos(VoAttrValue.class, AttrValueCustomerDTO.class,
                            this.dtoAddressService.getAddressForm(customerId, formattingShopId, Address.ADDR_TYPE_SHIPPING)
                        )
                )
        );
        addressForm.add(
                MutablePair.of(
                        Address.ADDR_TYPE_BILLING,
                        voAssemblySupport.assembleVos(VoAttrValue.class, AttrValueCustomerDTO.class,
                            this.dtoAddressService.getAddressForm(customerId, formattingShopId, Address.ADDR_TYPE_BILLING)
                        )
                )
        );
        addressBook.setAddressForm(addressForm);
    }

    /** {@inheritDoc} */
    public VoAddress update(final VoAddress vo) throws Exception {

        AddressDTO address = vo != null ? this.dtoAddressService.getById(vo.getAddressId()) : null;

        if (address != null && federationFacade.isManageable(address.getCustomerId(), CustomerDTO.class)) {

            address = voAssemblySupport.assembleDto(AddressDTO.class, VoAddress.class, address, vo);
            address = this.dtoAddressService.update(address);

            return voAssemblySupport.assembleVo(VoAddress.class, AddressDTO.class, new VoAddress(), address);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    public VoAddress create(final VoAddress vo) throws Exception {

        if (vo != null && federationFacade.isManageable(vo.getCustomerId(), CustomerDTO.class)) {

            AddressDTO address = this.dtoAddressService.getNew();

            address =
                    voAssemblySupport.assembleDto(AddressDTO.class, VoAddress.class, address, vo);
            address.setCustomerId(vo.getCustomerId());
            address = this.dtoAddressService.create(address);

            return voAssemblySupport.assembleVo(VoAddress.class, AddressDTO.class, new VoAddress(), address);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }

    /** {@inheritDoc} */
    public void remove(final long id) throws Exception {

        final AddressDTO address = this.dtoAddressService.getById(id);

        if (address != null && federationFacade.isManageable(address.getCustomerId(), CustomerDTO.class)) {

            dtoAddressService.remove(id);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }
}
