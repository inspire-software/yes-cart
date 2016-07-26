import { RouterConfig } from '@angular/router';

import { LocationsComponent } from './index';

export const LocationRoutes: RouterConfig = [
  {
    path: 'locations',
    component: LocationsComponent
  },
  {
    path: 'location/:locationId',
    component: LocationsComponent
  },
];
