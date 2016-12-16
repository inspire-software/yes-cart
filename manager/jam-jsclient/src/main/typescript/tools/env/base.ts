import { EnvConfig } from './env-config.interface';

const BaseConfig: EnvConfig = {
  // Sample API url
  API: 'https://demo.com',
  DEBUG_ON: false,
  UI_INPUT_DELAY: 300,
  UI_BULKSERVICE_DELAY: 15000,
  UI_FILTER_CAP: 50,
  UI_FILTER_NO_CAP: 1000,
  UI_TABLE_PAGE_SIZE: 10,
  UI_TABLE_PAGE_NUMS: 5
};

export = BaseConfig;

