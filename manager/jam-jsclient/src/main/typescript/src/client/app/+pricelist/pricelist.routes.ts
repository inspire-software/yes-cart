import { Route } from '@angular/router';

import { ShopPriceListComponent, ShopTaxesComponent, ShopPromotionsComponent } from './index';

export const PriceListRoutes: Route[] = [
  {
    path: 'pricelist/shop',
    component: ShopPriceListComponent
  },
  {
    path: 'taxes/shop',
    component: ShopTaxesComponent
  },
  {
    path: 'promotions/shop',
    component: ShopPromotionsComponent
  }
];
