import { RouterConfig } from '@angular/router';

import { ShopPriceListComponent, ShopTaxesComponent, ShopPromotionsComponent } from './index';

export const PriceListRoutes: RouterConfig = [
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
