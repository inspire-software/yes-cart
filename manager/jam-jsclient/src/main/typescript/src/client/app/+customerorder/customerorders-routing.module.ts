import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AllCustomerOrdersComponent, AllPaymentsComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'customerorder/allorders',
        component: AllCustomerOrdersComponent
      },
      {
        path: 'customerorder/allpayments',
        component: AllPaymentsComponent
      }
    ])
  ],
  exports: [RouterModule]
})
export class CustomerOrdersRoutingModule { }
