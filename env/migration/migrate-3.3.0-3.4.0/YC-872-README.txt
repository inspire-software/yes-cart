REF: YC-872 Secure string attribute type

This task is aimed at strengthening the security.

New property has been added onto attribute and payment gateway parameter "secure", which denotes sensitive
data, such as passwords of signatures (but can be applied to any attribute).

JAM VO services recognise this property and offer now two distinct methods to manage either only "unsecure"
attributes (default mode) or use secure mode to display all attributes. JAM has strickter rules for the "secure"
mode. For example read only user "ROLE_SMSHOPUSER" would not be able to access secure mode and thus will not have
any access to this data.

This mechanism provides yet another layer to protect sensitive data.

Another feature is introduction new EType "SecureString" which behaves just as normal string but in view mode
has its value masked. Therefore to see the actual value user must open it in editor. Such type is especially
useful for storing things like hashes and API passwords. This prevent exposing the data unnessesarity on the
screen.

Impact:

- There is a schema update to set "secure" flag and "SecureString" etype. However this should not be limited
  to this list. This is a bare minimum. Production systems can apply more thorough settings to prevent exposure
  of sensitive data.

- This update does not alter the way in which attributes are used and these settings do not have any effect on the
  core code. These are just tools for making JAM application more secure.