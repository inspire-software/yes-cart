import { RouterConfig } from '@angular/router';

import { CacheMonitoringComponent } from './index';

export const CacheMonitoringRoutes: RouterConfig = [
  {
    path: 'system/cache',
    component: CacheMonitoringComponent
  }
];
