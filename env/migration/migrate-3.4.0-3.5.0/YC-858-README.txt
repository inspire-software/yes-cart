REF: YC-858 Module structure refactoring

The following modules were moved

- All payment modules now in "payment-modules". All payment modules now are named "payment-module-XXXXX".
  Files that contained "core-modules-payment" were all renamed to have "payment-module" instead.

PRODUCTION changes:

- Ensure that env/xxxxx/cofig-module-yyyyy for all payments has correct settings with renamed values