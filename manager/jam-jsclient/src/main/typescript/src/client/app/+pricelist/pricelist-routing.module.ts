import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ShopPriceListComponent, ShopTaxesComponent, ShopPromotionsComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
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
    ])
  ],
  exports: [RouterModule]
})
export class PriceListRoutingModule { }
