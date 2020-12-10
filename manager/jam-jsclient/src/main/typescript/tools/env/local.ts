import { EnvConfig } from './env-config.interface';

const DevConfig: EnvConfig = {
  ENV: 'DEV',
  API: 'http://localhost:8082/cp/',
  DEBUG_ON: true,
};

export = DevConfig;

