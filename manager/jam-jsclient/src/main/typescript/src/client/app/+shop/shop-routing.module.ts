import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ShopComponent, SubShopComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'shop/:shopId',
        component: ShopComponent
      },
      {
        path: 'subshop/:shopId',
        component: SubShopComponent
      },
    ])
  ],
  exports: [RouterModule]
})
export class ShopRoutingModule { }
