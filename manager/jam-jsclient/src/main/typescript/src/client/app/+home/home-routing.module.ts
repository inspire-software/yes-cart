import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HomeComponent, ReportsComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: HomeComponent
      },
      {
        path: 'index.html',
        component: HomeComponent
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
