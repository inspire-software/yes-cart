package org.yes.cart.service.vo.impl;

import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.AddressDTO;
import org.yes.cart.domain.dto.AttrValueCustomerDTO;
import org.yes.cart.domain.dto.CustomerDTO;
import org.yes.cart.domain.dto.ShopDTO;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Country;
import org.yes.cart.domain.entity.State;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoAddress;
import org.yes.cart.domain.vo.VoAddressBook;
import org.yes.cart.domain.vo.VoAttrValue;
import org.yes.cart.domain.vo.VoLocation;
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
    @Override
    public VoAddressBook getAddressBook(final long customerId, final long formattingShopId, final String lang) throws Exception {


        if (federationFacade.isManageable(customerId, CustomerDTO.class)) {

            final VoAddressBook addressBook = new VoAddressBook();

            addressBook.setFormattingShopId(formattingShopId);

            setShopLocations(addressBook, formattingShopId, lang);

            setAddressBookAddresses(addressBook, customerId);

            setFormattedAddresses(addressBook, customerId, formattingShopId, lang);

            setAddressFormFieldsConfiguration(addressBook, customerId, formattingShopId);

            return addressBook;

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }


    protected void setShopLocations(final VoAddressBook addressBook, long shopId, final String lang) throws Exception {

        final ShopDTO shop = this.dtoShopService.getById(shopId);
        String billing = dtoShopService.getSupportedBillingCountries(shop.getMasterId() != null ? shop.getMasterId() : shop.getShopId());
        String shipping = dtoShopService.getSupportedShippingCountries(shop.getMasterId() != null ? shop.getMasterId() : shop.getShopId());
        List<String> billingCodes = billing == null ? Collections.emptyList() : Arrays.asList(billing.split(","));
        List<String> shippingCodes = shipping == null ? Collections.emptyList() : Arrays.asList(shipping.split(","));

        final List<Country> countries = countryService.findAll();

        final List<VoLocation> billingCountries = new ArrayList<>();
        final List<VoLocation> shippingCountries = new ArrayList<>();

        for (final Country country : countries) {

            final boolean billingCountry = billingCodes.contains(country.getCountryCode());
            final boolean shippingCountry = shippingCodes.contains(country.getCountryCode());

            if (billingCountry || shippingCountry) {

                final VoLocation countryInfo = convertToLocation(
                        country.getCountryCode(),
                        country.getName(),
                        country.getDisplayName()
                );

                if (billingCountry) {
                    billingCountries.add(countryInfo);
                }

                if (shippingCountry) {
                    shippingCountries.add(countryInfo);
                }

                final List<State> states = stateService.findByCountry(country.getCountryCode());
                final List<VoLocation> countryStates = new ArrayList<>();
                for (final State state : states) {

                    final VoLocation stateInfo = convertToLocation(
                            state.getStateCode(),
                            state.getName(),
                            state.getDisplayName()
                    );

                    countryStates.add(stateInfo);

                }

                if (!countryStates.isEmpty()) {
                    countryInfo.setSubLocations(countryStates);
                }

            }
        }

        addressBook.setBillingCountries(billingCountries);
        addressBook.setShippingCountries(shippingCountries);

    }

    private VoLocation convertToLocation(final String code, final String name, final I18NModel displayName) {

        final VoLocation loc = new VoLocation();
        loc.setCode(code);
        loc.setName(name);
        final List<MutablePair<String, String>> names = new ArrayList<>();
        if (displayName != null) {
            for (final Map.Entry<String, String> val : displayName.getAllValues().entrySet()) {
                names.add(MutablePair.of(val.getKey(), val.getValue()));
            }
        }
        loc.setDisplayNames(names);

        return loc;
    }


    protected void setAddressBookAddresses(final VoAddressBook addressBook, final long customerId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<AddressDTO> addresses = this.dtoAddressService.getAddressesByCustomerId(customerId);

        addressBook.setAddresses(
                voAssemblySupport.assembleVos(VoAddress.class, AddressDTO.class, addresses)
        );
    }

    protected void setFormattedAddresses(final VoAddressBook addressBook, final long customerId, final long formattingShopId, final String lang) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<Pair<Long, String>> formattedOriginal = this.dtoAddressService.getFormattedAddressesByCustomerId(customerId, formattingShopId, lang);
        final List<MutablePair<Long, String>> formatted = new ArrayList<>();
        for (final Pair<Long, String> formattedAddress : formattedOriginal) {
            formatted.add(MutablePair.of(formattedAddress.getFirst(), formattedAddress.getSecond()));
        }
        addressBook.setFormattedAddresses(formatted);
    }

    protected void setAddressFormFieldsConfiguration(final VoAddressBook addressBook, final long customerId, final long formattingShopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final List<MutablePair<String, List<VoAttrValue>>> addressForm = new ArrayList<>();
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
    @Override
    public VoAddress updateAddress(final VoAddress vo) throws Exception {

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
    @Override
    public VoAddress createAddress(final VoAddress vo) throws Exception {

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
    @Override
    public void removeAddress(final long id) throws Exception {

        final AddressDTO address = this.dtoAddressService.getById(id);

        if (address != null && federationFacade.isManageable(address.getCustomerId(), CustomerDTO.class)) {

            dtoAddressService.remove(id);

        } else {
            throw new AccessDeniedException("Access is denied");
        }
    }
}
