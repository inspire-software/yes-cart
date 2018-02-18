REF: YC-855 Review price rendering to ensure it honours tax settings for current user

This is a tidy up task as we have some discrepancies in certain cases when tax options are selected. The calculation
of all prices (including totals and sub totals calculations) should honour the NET/GROSS options and the totals should
align. This task is tidy up for rounding errors and oversights when dealing with multiple tax options available on
storefront.

The scope of this task increased after initial review and some refactoring was done to ensure that we have consistency
in all areas of price and total calculation.

Affected areas:

- MoneyUtils was updated with new API isPositive() (as a replacement for isFirstBiggerThanSecond(x, 0))

- Facades in websupport were fully reworked and covered with unit tests. Note that this does not gurantee that
  there are no rounding errors (especially with corner case when pricelists have GROSS prices but customer wishes
  to see NET prices on front end gives worst possible scenario). So if there is a requirement to show NET prices
  on front end the pricelists should have NET prices initially to minimise the rounding errors.

- ProductPriceModel is now renamed to PriceModel to better reflect the API

- ProductQuantityModel is now renamed to QuantityModel to better reflect the API

- ProductPromotionModel is now renamed to PromotionModel to better reflect the API