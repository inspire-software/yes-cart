// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false
};

export const Config = {
  ENV: 'DEV',
  API: 'https://admin-edemo.yes-cart.org/cp/',
  DEBUG_ON: true,
  SUPPORTED_LANGS: '(uk|ru|en|de)',
  DEFAULT_LANG: 'en',
  CONTEXT_PATH: '/cp',
  AUTH_JWT_BUFFER: 15000,
  AUTH_USERCHECK_BUFFER: 10000,
  UI_INPUT_DELAY: 300,
  UI_ALERTCHECK_DELAY: 120000,
  UI_BULKSERVICE_DELAY: 15000,
  UI_FILTER_CAP: 50,
  UI_FILTER_NO_CAP: 100,
  UI_TABLE_PAGE_SIZE: 10,
  UI_TABLE_PAGE_NUMS: 5,
  UI_ORDER_TOTALS: 'gross',
  UI_DOC_LINK: 'https://docs.inspire-software.com/docs/display/YD',
  UI_COPY_NOTE: '<a href=\\"https://www.yes-cart.org\\" target=\\"_blank\\">&copy; YC - pure e-Commerce <i class=\\"fa fa-globe pull-right\\" title=\\"YesCart.org\\"></i></a>',
  UI_LABEL: ''
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
import 'zone.js/dist/zone-error';  // Included with Angular CLI.
