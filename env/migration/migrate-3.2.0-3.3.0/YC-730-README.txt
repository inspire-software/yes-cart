REF: YC-730 Shipping cost strategy based on weight and/or volume

This feature introduces additional Weight & Volume based shipping cost calculation strategy.

MAJOR CHANGES (on top of YC-688, YC-694)
Now the shipping availability is defined by price availability (same as in YC-688, YC-694), however the
actual delivery cost calculation strategy is used to get the shipping cost and not just the regional cost resolver.
This means that for each shipping method a shopping cart wrapper is created with alternative carrier SLA and calculation
strategy evaluates the cart thus giving precise cost (but as cost of performance).

W - Weight & Volume strategy
Fullfilled by WeightBasedPriceListDeliveryCostCalculationStrategy. The method of evaluation is following:
- Each product is expected to have either (or both) attributes PRODUCT_WEIGHT_KG and PRODUCT_VOLUME_M3.
  Note that volume should include any additional logistics percentage add ons (e.g. if you use 10% for
  space then this volume should include this value)
- The cart items are evaluated to produce the total weight in KG and/or total volume in m3
- Two prices are calculated [CARRIER GUID]_KG and [CARRIER GUID]_M3. These prices are subject to regional settings
  as decribed in README for YC-688, YC-694. The most expensive price here is chosen because shipping companies
  charge largest of the two usually.
- Further two more prices are expected [CARRIER GUID]_KGMAX and [CARRIER GUID]_M3MAX. These are used to get the
  maximum quantity tier for this delivery. The price is ignored and only quantity tier is used to disallow
  too large weight or volume. If either value is exceeded the shipping method is considered unavailable.
