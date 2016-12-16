import { Route } from '@angular/router';

import { ShopComponent } from './index';

export const ShopRoutes: Route[] = [
  {
    path: 'shop/:shopId',
    component: ShopComponent
  },
];
