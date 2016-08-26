import { RouterConfig } from '@angular/router';

import { CatalogCategoryComponent, CatalogBrandComponent, CatalogTypeComponent } from './index';

export const CatalogRoutes: RouterConfig = [
  {
    path: 'catalog/categories',
    component: CatalogCategoryComponent
  },
  {
    path: 'catalog/brands',
    component: CatalogBrandComponent
  },
  {
    path: 'catalog/producttypes',
    component: CatalogTypeComponent
  }
];
