import { RouterConfig } from '@angular/router';

import { ShopPriceListComponent } from './index';

export const PriceListRoutes: RouterConfig = [
  {
    path: 'pricelist/shop',
    component: ShopPriceListComponent
  }
];
