import { RouterConfig } from '@angular/router';

import { OrganisationRoleComponent, OrganisationManagerComponent } from './index';

export const OrganisationRoutes: RouterConfig = [
  {
    path: 'organisation/roles',
    component: OrganisationRoleComponent
  },
  {
    path: 'organisation/management',
    component: OrganisationManagerComponent
  }
];
