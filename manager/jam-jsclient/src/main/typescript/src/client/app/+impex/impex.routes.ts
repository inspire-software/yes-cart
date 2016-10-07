import { RouterConfig } from '@angular/router';

import { ImportManagerComponent, ExportManagerComponent } from './index';

export const ImpexRoutes: RouterConfig = [
  {
    path: 'impex/import',
    component: ImportManagerComponent
  },
  {
    path: 'impex/export',
    component: ExportManagerComponent
  }
];
