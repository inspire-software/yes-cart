import { EnvConfig } from './env-config.interface';

const BaseConfig: EnvConfig = {
  // Sample API url
  API: 'https://demo.com',
  DEBUG_ON: false,
  UI_INPUT_DELAY: 300,
  UI_BULKSERVICE_DELAY: 15000,
  UI_FILTER_CAP: 50,
  UI_FILTER_NO_CAP: 100,
  UI_TABLE_PAGE_SIZE: 10,
  UI_TABLE_PAGE_NUMS: 5,
  UI_DOC_LINK: 'http://www.inspire-software.com/confluence/display/YC3EN/YC+3.x.x+Wiki',
  UI_COPY_NOTE: '<a href=http://www.yes-cart.org target=_blank>YesCart.org</a>',
  UI_LABEL: ''
};

export = BaseConfig;

