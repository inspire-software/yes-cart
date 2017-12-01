import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ShopContentComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'content/:shopId',
        component: ShopContentComponent
      }
    ])
  ],
  exports: [RouterModule]
})
export class ContentRoutingModule { }
