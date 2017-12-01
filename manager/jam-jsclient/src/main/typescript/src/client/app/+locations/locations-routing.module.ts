import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { LocationsComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'locations',
        component: LocationsComponent
      }
    ])
  ],
  exports: [RouterModule]
})
export class LocationsRoutingModule { }
