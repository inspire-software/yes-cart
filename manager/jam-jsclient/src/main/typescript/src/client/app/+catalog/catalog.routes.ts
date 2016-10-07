import { RouterConfig } from '@angular/router';

import { CatalogCategoryComponent, CatalogBrandComponent, CatalogTypeComponent, CatalogProductsComponent } from './index';

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
  },
  {
    path: 'catalog/products',
    component: CatalogProductsComponent
  }
];
