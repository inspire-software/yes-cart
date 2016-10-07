import { RouterConfig } from '@angular/router';

import { AllCustomerOrdersComponent, AllPaymentsComponent } from './index';

export const CustomerOrdersRoutes: RouterConfig = [
  {
    path: 'customerorder/allorders',
    component: AllCustomerOrdersComponent
  },
  {
    path: 'customerorder/allpayments',
    component: AllPaymentsComponent
  }
];
