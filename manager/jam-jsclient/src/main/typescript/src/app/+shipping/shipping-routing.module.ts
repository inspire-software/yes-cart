import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ShippingComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'shipping',
        component: ShippingComponent
      }
    ])
  ],
  exports: [RouterModule]
})
export class ShippingRoutingModule { }
