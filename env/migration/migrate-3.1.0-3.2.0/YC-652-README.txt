Ref: YC-652 Introduce a separate field for customer and customer address to hold 'salutation'

In order to make creation of profile and registration forms more flexible all core properties
(firstname, middlename, lastname and salutation) have been removed from the forms.

Corresponding custom CUSTOMER attributes have been added with the same name that cannot hold a
value but act as a configuration 'relay' for those properties.

This way the positioning of the inputs can be fully customized. However 'firstname, middlename,
lastname and salutation' attributes must now be added explicitly to the
SHOP_CUSTOMER_REGISTRATION_ATTRIBUTES and SHOP_CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE shop attributes.

Recommended Salutation attribute type is CommaSeparatedValue to allow generation of select box style
input where choices are presented. The configuration should be following the same convention as any
onther custom attribute of this type. E.g. en=Mr-Mr,Mrs-Mrs,Miss-Miss. It is recommended to use human
readable values for value and display value as this will simplify use of this field in email
templates.