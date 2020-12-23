import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ShopPriceListComponent, ShopTaxesComponent, ShopTaxConfigsComponent, ShopPromotionsComponent } from './index';

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
        path: 'taxconfigs/shop',
        component: ShopTaxConfigsComponent
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
