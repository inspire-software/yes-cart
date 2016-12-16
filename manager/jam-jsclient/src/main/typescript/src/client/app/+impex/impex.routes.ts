import { Route } from '@angular/router';

import { ImportManagerComponent, ExportManagerComponent } from './index';

export const ImpexRoutes: Route[] = [
  {
    path: 'impex/import',
    component: ImportManagerComponent
  },
  {
    path: 'impex/export',
    component: ExportManagerComponent
  }
];
