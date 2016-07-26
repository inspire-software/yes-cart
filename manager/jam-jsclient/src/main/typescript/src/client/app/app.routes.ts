import { provideRouter, RouterConfig } from '@angular/router';

import { AboutRoutes } from './+about/index';
import { HomeRoutes } from './+home/index';
import { ShopRoutes } from './+shop/index';
import { LocationRoutes } from './+locations/index';

const routes: RouterConfig = [
  ...HomeRoutes,
  ...ShopRoutes,
  ...LocationRoutes,
  ...AboutRoutes,
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes),
];
