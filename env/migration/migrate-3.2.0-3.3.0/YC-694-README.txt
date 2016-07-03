REF: YC-694	Change SLA pricing to use price lists
REF: YC-688	Improve carrier SLA pricing API

During implementation of the above features the pricing for delivery cost has been fully
reworked.

MAJOR CHANGES
- previously (prioir 3.3.0) SKU code on shipping lines was CarrierSla.CARRIERSLA_ID now it is CarrierSla.GUID.
- there is no longer domain properties on CarrierSla to hold pricing information all of it is now held in SkuPrice
- we now support E (External) type out of the box through invocation by bean name
- we now support regional availability for CarrierSla. Where previously the filtering was on currency only, now the
  filtering is on SKU code in SkuPrice. see YC-688

The following types of pricing are supported:

F - Fixed pricing
This type is serviced by PriceListDeliveryCostCalculationStrategy and uses CarrierSla.GUID as SKU code when querying
PriceService.getMinimalPrice(). This behaves in the same way as regular product pricing subject to availability, tier
and pricing policy for current user.

R - Free
This type is serviced by FreeDeliveryCostCalculationStrategy and simply sets 0.00 cost.
However when displaying the available CarrierSla ShippingServiceFacade.findCarriers(cart) will still use
PriceService.getMinimalPrice() to see if there is price marker (1.00x price available) which is used to mark that this
carrier SLA is available for specific region.

E - External calculation
This type is serviced by ExternalDeliveryCostCalculationStrategy which simply invokes bean with name CarrierSla.SCRIPT
in current Spring context. This bean must implement DeliveryCostCalculationStrategy.
However when displaying the available CarrierSla ShippingServiceFacade.findCarriers(cart) will still use
PriceService.getMinimalPrice() to see if there is price marker (1.00x price available) which is used to mark that this
carrier SLA is available for specific region.

Note that the main entry point is still DefaultDeliveryCostCalculationStrategy which acts as mediator on the
CarrierSla.SLA_TYPE and routes the calculation to specific DeliveryCostCalculationStrategy.

Regional pricing (YC-688)

Availability of CarrierSla is now fully dependent on the availability of price in SkuPrice, which provides a flexible
mechanim to configure prices by:
- specific currency
- specicic country
- specific state/county

ShippingServiceFacade.findCarriers(cart) will attempt the following PriceService.getMinimalPrice() to try to resolve
price for CarrierSla and if one exists then CarrierSla is considered as available for selection:
- Currency+State specific lookup: [CarrierSla.GUID]_[cart.shoppingContext.countryCode]_[cart.shoppingContext.stateCode]
  e.g. SKU code: CRSL001_GB_CAMB
- Currency+Country specific lookup: [CarrierSla.GUID]_[cart.shoppingContext.countryCode]
  e.g. SKU code: CRSL001_GB
- Currency specific lookup: [CarrierSla.GUID]
  e.g. SKU code: CRSL001


