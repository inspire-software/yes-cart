import { RouterConfig } from '@angular/router';

import { AllCustomersComponent } from './index';

export const CustomerRoutes: RouterConfig = [
  {
    path: 'customer/allcustomers',
    component: AllCustomersComponent
  }
];
