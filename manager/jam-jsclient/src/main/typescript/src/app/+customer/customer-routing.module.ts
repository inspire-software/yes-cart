import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AllCustomersComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'customer/allcustomers',
        component: AllCustomersComponent
      },
      {
        path: 'customer/allcustomers/:customerId',
        component: AllCustomersComponent
      }
    ])
  ],
  exports: [RouterModule]
})
export class CustomerRoutingModule { }
