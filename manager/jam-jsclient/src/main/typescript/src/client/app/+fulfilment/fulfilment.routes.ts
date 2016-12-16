import { Route } from '@angular/router';

import { FulfilmentComponent, CentreInventoryComponent } from './index';

export const FulfilmentRoutes: Route[] = [
  {
    path: 'fulfilment/centres',
    component: FulfilmentComponent
  },
  {
    path: 'fulfilment/inventory',
    component: CentreInventoryComponent
  }
];
