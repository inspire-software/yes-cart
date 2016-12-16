import { Route } from '@angular/router';

import { HomeComponent, ReportsComponent } from './index';

export const HomeRoutes: Route[] = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'index.html',
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
