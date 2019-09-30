REF: YC-986 Enhance domain model to use I18n component

OVERVIEW:
=========

Localizable values on domain level were historically "packed" to provide unlimited language specific values.
As a result these values were mostly exposed at the domain level as String properties thus making it ambiguous as
to the contents of the property and making it harder to use in code as it would have to be decomed into I18nModel
prior to its use.

Goal of this task is to enrich domain model to encapsulate the tranformation of packed value to I18nModel at the
persistence level thus making service layer code cleared and more usable.

DEV:
====

Core code is fully upgraded to utilise new properties throughout the applications layers vertical.

Affected classes:
AttributeEntity - validationFailedMessage, choiceData, displayName
AttrValueEntityXXX - displayVal
CarrierEntity - displayName, displayDescription
CarrierSlaEntity - displayName, displayDescription
CategoryEntity - displayName
ContentEntity - displayName
CountryEntity - displayName (upgraded to use i18n, previously was a single value in country's language)
CustomerOrderDeliveryDetEntity - storedAttributes (display value packed in stored attributes)
CustomerOrderDetEntity - storedAttributes (display value packed in stored attributes)
CustomerOrderEntity - storedAttributes (display value packed in stored attributes)
DataGroupEntity - displayName
ProdTypeAttributeViewGroupEntity - displayName
ProductEntity - displayName
ProductSkuEntity - displayName
ProductTypeEntity - displayName
PromotionEntity - displayName, displayDescription
SeoEntity - displayTitle, displayMetakeywords, displayMetadescription
SeoImageEntity - displayTitle
StateEntity - displayName (upgraded to use i18n, previously was a single value in country's language)
WarehouseEntity - displayName

GUIDELINE FOR UPGRADE:
======================

Any language specific values previously used as single value, must now be migrated to use "lang=xx" i18n
style. xx is a special language code for "failover" language.


SEARCH WORDS:
=============

DisplayName
DisplayVal
DisplayDescription
DisplayTitle
DisplayMetakeywords
DisplayMetadescription
DescriptionAsIs

