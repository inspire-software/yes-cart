import { Route } from '@angular/router';

import { ShopComponent, SubShopComponent } from './index';

export const ShopRoutes: Route[] = [
  {
    path: 'shop/:shopId',
    component: ShopComponent
  },
  {
    path: 'subshop/:shopId',
    component: SubShopComponent
  },
];
