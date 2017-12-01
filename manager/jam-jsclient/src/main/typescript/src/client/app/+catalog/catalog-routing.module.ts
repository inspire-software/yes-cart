import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CatalogCategoryComponent, CatalogBrandComponent, CatalogTypeComponent, CatalogProductsComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
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
    ])
  ],
  exports: [RouterModule]
})
export class CatalogRoutingModule { }
