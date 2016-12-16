import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { ManagerComponent, ManagersComponent, RoleComponent, RolesComponent } from './components/index';
import { OrganisationManagerComponent, OrganisationRoleComponent } from './index';

@NgModule({
    imports: [CommonModule, SharedModule, ServicesModule],
    declarations: [
      ManagerComponent, ManagersComponent,
      OrganisationManagerComponent,
      RoleComponent, RolesComponent,
      OrganisationRoleComponent
    ],
    exports: [
      ManagerComponent, ManagersComponent,
      OrganisationManagerComponent,
      RoleComponent, RolesComponent,
      OrganisationRoleComponent
    ]
})

export class OrganisationPagesModule { }
