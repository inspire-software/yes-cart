REF: YC-849 Ensure that default context for storefront is ROOT

Storefront application is now deployed as ROOT.war by default.
All email templates and configurations that previously defaulted to "/yes-shop" are now defaulting
to root "/". Which is more suitable for prod deployments and allows to reuse default email templates
with little effort (just changing the logo is enough).

SYSTEM_IMAGE_VAULT, SYSTEM_FILE_VAULT and SYSTEM_SYSFILE_VAULT will no longer have default and it
is mandatory to specify absolute path to these directories.

New properties have been added to config-fs.properties which are then copied to yc-config.properties
via maven filter and then used by SystemService to resolve default root if system preferences are not
set. For dev and dev-idea the defaults are pointing to
file://${basedir}/../../theme/imagevault/src/main/resources/default/XXXvault/ so that during the build
process this value is correctly resolved. For "real" environments the values should be set in
config-fs.properties (fs.config.default.XXXXXX=)

Default imagevault, filevault and sysfilevault directory structures can be found in:
YC_HOME/theme/imagevault/src/main/resources/default/