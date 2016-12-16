import { Route } from '@angular/router';

import { OrganisationRoleComponent, OrganisationManagerComponent } from './index';

export const OrganisationRoutes: Route[] = [
  {
    path: 'organisation/roles',
    component: OrganisationRoleComponent
  },
  {
    path: 'organisation/management',
    component: OrganisationManagerComponent
  }
];
