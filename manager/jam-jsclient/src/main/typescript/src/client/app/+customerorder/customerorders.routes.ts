import { RouterConfig } from '@angular/router';

import { AllCustomerOrdersComponent } from './index';

export const CustomerOrdersRoutes: RouterConfig = [
  {
    path: 'customerorder/allorders',
    component: AllCustomerOrdersComponent
  }
];
