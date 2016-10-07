import { RouterConfig } from '@angular/router';

import { ShopComponent } from './index';

export const ShopRoutes: RouterConfig = [
  {
    path: 'shop/:shopId',
    component: ShopComponent
  },
];
