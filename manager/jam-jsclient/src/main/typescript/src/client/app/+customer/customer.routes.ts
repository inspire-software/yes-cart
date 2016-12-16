import { Route } from '@angular/router';

import { AllCustomersComponent } from './index';

export const CustomerRoutes: Route[] = [
  {
    path: 'customer/allcustomers',
    component: AllCustomersComponent
  }
];
