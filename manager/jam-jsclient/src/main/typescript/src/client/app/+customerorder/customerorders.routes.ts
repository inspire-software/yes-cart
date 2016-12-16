import { Route } from '@angular/router';

import { AllCustomerOrdersComponent, AllPaymentsComponent } from './index';

export const CustomerOrdersRoutes: Route[] = [
  {
    path: 'customerorder/allorders',
    component: AllCustomerOrdersComponent
  },
  {
    path: 'customerorder/allpayments',
    component: AllPaymentsComponent
  }
];
