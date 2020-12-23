import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { OrganisationRoutingModule } from './organisation-routing.module';
import { ManagerComponent, ManagerCategoryMinComponent, ManagersComponent, RoleComponent, RolesComponent } from './components/index';
import { OrganisationManagerComponent, OrganisationRoleComponent } from './index';

@NgModule({
    imports: [OrganisationRoutingModule, CommonModule, SharedModule, ServicesModule],
    declarations: [
      ManagerComponent, ManagerCategoryMinComponent, ManagersComponent,
      OrganisationManagerComponent,
      RoleComponent, RolesComponent,
      OrganisationRoleComponent
    ],
    exports: [
      ManagerComponent, ManagerCategoryMinComponent, ManagersComponent,
      OrganisationManagerComponent,
      RoleComponent, RolesComponent,
      OrganisationRoleComponent
    ]
})

export class OrganisationPagesModule { }
