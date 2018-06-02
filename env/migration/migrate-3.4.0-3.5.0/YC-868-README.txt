REF: YC-868 Customer registration improvements

OVERVIEW
========

This feature is similar in approach to YC-905 and is concerned with customisation of the registration forms.

In essence new CUSTOMER attribute is added regAddressForm, which acts as a marker. If this attribute is placed
in the SHOP_CREGATTRS_XXX it effectively embeds a shipping address type form inside the registration form.

Resulting effect of this is that customers register and have shipping address added to their address book.

Because address form repeats some fields such as salutation, firstname, middlename, lastname and phone there is
a fallback mechanism from address form onto the Customer object. Thus you need only to add these attributes to
the shipping address form.

For example if original registration form was defined like so:
SHOP_CREGATTRS_B2C=email,salutation,firstname,middlename,lastname,CUSTOMER_PHONE,MARKETING_OPT_IN,password,confirmPassword

And your address form already includes salutation,firstname,middlename,lastname and phone1 then new configuration
with address form would be:
SHOP_CREGATTRS_B2C=email,regAddressForm,MARKETING_OPT_IN,password,confirmPassword

As can be seen from this configuration there is a slight limitation that all address form attributes are "inserted"
together using regAddressForm, so you cannot say put MARKETING_OPT_IN in between address form attributes but only
before of after the whole address section.