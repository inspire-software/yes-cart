import { EnvConfig } from './env-config.interface';

const BaseConfig: EnvConfig = {
  // Sample API url
  API: 'https://demo.com',
  DEBUG_ON: false,
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
  UI_DOC_LINK: 'https://documentation.inspire-software.com/documentation/wiki/docyescart/',
  UI_COPY_NOTE: '<a href=\\"http://www.yes-cart.org\\" target=\\"_blank\\">&copy; YC - pure e-Commerce <i class=\\"fa fa-globe pull-right\\" title=\\"YesCart.org\\"></i></a>',
  UI_LABEL: ''
};

export = BaseConfig;

