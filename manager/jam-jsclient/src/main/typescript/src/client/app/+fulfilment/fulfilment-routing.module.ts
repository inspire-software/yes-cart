import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FulfilmentComponent, CentreInventoryComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'fulfilment/centres',
        component: FulfilmentComponent
      },
      {
        path: 'fulfilment/inventory',
        component: CentreInventoryComponent
      }
    ])
  ],
  exports: [RouterModule]
})
export class FulfilmentRoutingModule { }
