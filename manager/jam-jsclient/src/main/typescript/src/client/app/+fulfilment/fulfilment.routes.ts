import { RouterConfig } from '@angular/router';

import { FulfilmentComponent, CentreInventoryComponent } from './index';

export const FulfilmentRoutes: RouterConfig = [
  {
    path: 'fulfilment/centres',
    component: FulfilmentComponent
  },
  {
    path: 'fulfilment/inventory',
    component: CentreInventoryComponent
  }
];
