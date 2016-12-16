// Feel free to extend this interface
// depending on your app specific config.
export interface EnvConfig {
  API?: string;
  ENV?: string;
  DEBUG_ON?: boolean;

  UI_INPUT_DELAY?: number;
  UI_BULKSERVICE_DELAY?: number;
  UI_FILTER_CAP?: number;
  UI_FILTER_NO_CAP?: number;
  UI_TABLE_PAGE_SIZE?: number;
  UI_TABLE_PAGE_NUMS?: number;
}

export const Config: EnvConfig = JSON.parse('<%= ENV_CONFIG %>');

