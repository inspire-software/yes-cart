import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { EmptyComponent, HomeComponent, ReportsComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: EmptyComponent
      },
      {
        path: 'index.html',
        component: EmptyComponent
      },
      {
        path: 'dashboard',
        component: HomeComponent
      },
      {
        path: 'reports',
        component: ReportsComponent
      }
    ])
  ],
  exports: [RouterModule]
})
export class HomeRoutingModule { }
