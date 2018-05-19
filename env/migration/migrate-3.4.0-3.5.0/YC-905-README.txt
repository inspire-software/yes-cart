REF: YC-905 Improved registration and validation process

OVERVIEW:
=========

This is a general review of registration process and some refactoring around the form on frontend in general.

Main features are:
- customer validation for emails
- all fields in registration form to be sortable (including email)
- allow entering passwords during registration.
- updated approach to reset password emails (taking into account customer chosen passwords)
- preserve existing auto generated passwords

CHANGES:
========

Now ALL fields in the registration form are rendered according to registration form attributes including "email", "password" and
"confirmPassword" fields. Note that these three are special fields and therefore CUSTOMER attribute must specify VAL which is
either "email", "password" and "confirmPassword" respectively.

This gives flexibility to have multiple configurations. E.g. create one attribute with [CODE=email, VAL=email] with one regex
and then [CODE=another_email, VAL=email] with different regex. Thus you can define different set of the attributes per shop or
even per customer type. In addition to this because form renders fields according to the order attributes are specified in
SHOP_CREGATTRS_XXX you can reposition all fields as required (e.g. have email after firstname/lastname)

In order to provide flexible configutation for the email regex (which is also present on newsletter sign up, contact us and
login forms) a new special SHOP_CREGATTRS_EMAIL shop attribute was added, so that regex configurations can be defined by
providing a single attribute code that has the email configurations.

Legacy implementation of auto generated passwords is still supported, as well as $password variable in the emails. Thus
decision whether to print passwords in email or not is totally dependent on the content of the email template.

Now the following flows are supported:

1. Customer chosen passwords
For this password/confirmPassword attributes needs tobe  defined in SHOP_CREGATTRS_XXX. In case that customer uses
"Reset password" button from their account they will be send a password reset link via email (as before). This step was
preserved as a special precaution to prevent someone reseting passwords if customer forgot to log out. Once the customer
clicks the password reset link they will be send to password change form (ResetPag) where they can type the password of
their choice. The password is validated using same configurations as registration password/confirmPassword attributes.

2. Customer chosen passwords (reset from Admin)
JAM has password reset functionality in CallCenter section. Depending on the desired behaviour shop owner can set
SHOP_CUSTOMER_PASSWORD_RESET_CC shop attribute. If it is filled in (with secure token of shop owner choice) the behaviour
would be to auto generate the password. If it is not filled in the the customer will be sent password reset link via email.
In case password reset is triggered from Admin email template will have $callCentrePasswordReset variable set to 'true',
otherwise this variable is 'false'.

3. Auto generated passwords (old behaviour)
If password/confirmPassword are not defined in list of SHOP_CREGATTRS_XXX then password auto generating will be enabled.
Note that email template must contain $password variable or there will be no way for customer to receive the password.

Thus changes to registration attributes and contents of email templates (customer-registered, customer-change-password)
must provide consistent behaviour.

As part of this task a new  MAILDUMP logger has been added in the MailComposerImpl to print full HTML version of the email
in logs. This is also a usefull feature for other environments if devops need resilient copy of all generated messages.
For dev-idea and dev this is enabled by default and prints to CONSOLE as well. MAILDUMP logs only at DEBUG level

PRODUCTION:
===========

New "email" attribute (or its equivalent as described above) must be added to all SHOP_CREGATTRS_XXX.

To enable customer chosen passwords password/confirmPassword attributes must be added to SHOP_CREGATTRS_XXX, email
templates customer-registered/customer-change-password modified accordingly and your theme (if it is custom) has
to include new page ResetPage