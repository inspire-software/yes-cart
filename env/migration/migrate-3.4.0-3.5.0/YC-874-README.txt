REF: YC-874 Promotion engine review

This task aims to improve (and tidy up) some of the promotion engine features.

Fixes:
- registered variable now correctly resolves if customer is a guest
- customerType variable is now resolved from cart thus giving a better support for B2G and other defaults covered by
  cart calculations
- pricingPolicy is now correctly resolved using the PricingPolicyProvider

New variables:
- SKU, short form of "shoppingCartItem?.productSkuCode"
- shopId, short form of "shoppingCart?.shoppingContext.shopId"
- customerShopId, short form of "shoppingCart?.shoppingContext.customerShopId"

New functions:
- product, allows to retrieve Product entity by SKU code, example usage: "product(SKU) != null"
- productSku, allows to retrieve ProductSku entity by SKU code, example usage: "productSku(SKU) != null"
- brand, allows to retrieve Brand entity by SKU code, example usage: "brand(SKU) != null"
- hasProductAttribute, allows to check if SKU (or product) has attribute value, example usage:
  "hasProductAttribute(SKU, 'color')"
- productAttributeValue, allows to get SKU (or product) attribute value, example usage:
  "productAttributeValue(SKU, 'color') == 'blue'"
- isSKUofBrand, allows to check if SKU is of particular brand (case insensitive name, OR'ed), example
  usage: "isSKUofBrand(SKU, 'toshiba', 'sony')"
- isSKUinCategory, allows to check if SKU is in particular category (case insensitive code, with fallback to parent, OR'ed),
  example sage: "isSKUinCategory(SKU, '313', '310')"

