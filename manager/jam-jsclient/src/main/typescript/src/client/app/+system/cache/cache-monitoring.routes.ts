import { Route } from '@angular/router';

import { CacheMonitoringComponent } from './index';

export const CacheMonitoringRoutes: Route[] = [
  {
    path: 'system/cache',
    component: CacheMonitoringComponent
  }
];
