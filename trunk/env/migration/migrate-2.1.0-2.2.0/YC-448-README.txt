REF: YC-448 Allow localizable images for category, product, sku, brand and shop

Category and Brand images attributes now changed to follow Product image attributes naming convention
whereby the name of attribute end with a number. So default category attribute is CATEGORY_IMAGE0 and
default brand image is BRAND_IMAGE0. This allows adding new image attributes easily and also import them
via YUM using simple a-z suffix naming.

For every image attribute to have localized version thereof you need to create image attribute with the same
name and append locale suffix, which must be supported by the languages service. E.g. if BRAND_IMAGE0 is default
then to make English specific version for this image you need to have BRAND_IMAGE0_en attribute. If attribute value
for Brand is filled then a different image will be shown when the shop is viewed in en language.