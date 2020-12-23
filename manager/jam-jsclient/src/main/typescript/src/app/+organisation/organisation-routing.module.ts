import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { OrganisationRoleComponent, OrganisationManagerComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'organisation/roles',
        component: OrganisationRoleComponent
      },
      {
        path: 'organisation/management',
        component: OrganisationManagerComponent
      }
    ])
  ],
  exports: [RouterModule]
})
export class OrganisationRoutingModule { }
