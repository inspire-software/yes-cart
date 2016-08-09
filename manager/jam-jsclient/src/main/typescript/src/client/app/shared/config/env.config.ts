// Feel free to extend this interface
// depending on your app specific config.
export interface IConfig {
  API: string;
  UI_INPUT_DELAY: number;
}

export const Config: IConfig = JSON.parse('<%= ENV_CONFIG %>');
