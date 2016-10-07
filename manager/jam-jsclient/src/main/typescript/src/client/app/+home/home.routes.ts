import { RouterConfig } from '@angular/router';

import { HomeComponent, ReportsComponent } from './index';

export const HomeRoutes: RouterConfig = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'dashboard',
    component: HomeComponent
  },
  {
    path: 'reports',
    component: ReportsComponent
  }
];
