import { RouterConfig } from '@angular/router';

import { CatalogComponent, CatalogBrandComponent } from './index';

export const CatalogRoutes: RouterConfig = [
  {
    path: 'catalog/categories',
    component: CatalogComponent
  },
  {
    path: 'catalog/brands',
    component: CatalogBrandComponent
  }
];
