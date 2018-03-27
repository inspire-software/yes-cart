REF: YC-890 JAM services section to see customised (plugged in) services

OVERVIEW:
=========

This task aims to improve visibility of the system configuration for the devops teams.

Namely the following areas were considered:

- All implementors of AbstractConfigurationImpl configuration, which work with the following interfaces:
  CartContentsValidator, InventoryResolver, PriceResolver, PricingPolicyProvider, ProductAvailabilityStrategy,
  TaxProvider

- OrderExporter implementors

- Iterator<OrderDeliveryStatusUpdate> implementors

The following mechanism was proposed:

- org.yes.cart.config.Configuration interface must be implemented by all custom implementations of the above stated
  configuration points. This interface allows to expose basic information about the customised service in the form
  of ConfigurationContext object

- org.yes.cart.config.ActiveConfigurationDetector interface must be implemented by all extension containers that have
  sufficient information about activation of the extension.
  For all extension points derived from AbstractConfigurationImpl this should be automatically working if custom
  beans configured implement org.yes.cart.config.Configuration
  OrderExporter and Iterator<OrderDeliveryStatusUpdate> implementors must implement both
  org.yes.cart.config.ActiveConfigurationDetector and org.yes.cart.config.Configuration as these are self contained
  extension points.

PRODUCTION:
===========

This change should not affect production systems, however existing extension points should implement the described
above interfaces to benefit from automatic configuration resolution feature.

If correctly implemented all configurations will be clearly displayed in new System > Configuration section of JAM.