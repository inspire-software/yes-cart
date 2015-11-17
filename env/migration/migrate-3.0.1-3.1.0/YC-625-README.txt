REF: YC-625 Allow use of same email to create multiple accounts on one server instance

Previously to 3.1.0 the email address was unique customer identifier on a server instance.
Thus when hosting multiple shop on single instance the customers were offered a choice to
link their data as their email is already know.

This may create akward situation where customer could think that their details were misused/sold
to other shops. Therefore the automatic link shall be disabled and for purpose of customers
identification customers will now be identified via CustomerShopEntity by two attributes:
EMAIL and SHOPCODE.

The linking will only be enabled via YUM (i.e. Call center or directly in theme).
We recommend to reserve this feature for marketplaces or automatic registrations.

In order to upgrade the SQL schema the unique constrain from TCUSTOMER.EMAIL must be dropped

Due to this change some of the API (i.e. services and facades have been changes to include Shop
as parameter).
