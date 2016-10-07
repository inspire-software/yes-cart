// Feel free to extend this interface
// depending on your app specific config.
export interface IConfig {
  API: string;
  UI_INPUT_DELAY: number;
  UI_BULKSERVICE_DELAY: number;
  UI_FILTER_CAP: number;
  UI_FILTER_NO_CAP: number;
  UI_TABLE_PAGE_SIZE: number;
  UI_TABLE_PAGE_NUMS: number;
}

export const Config: IConfig = JSON.parse('<%= ENV_CONFIG %>');
