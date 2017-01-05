import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import {
  BrandsComponent, BrandComponent,
  CategoriesComponent, CategoryComponent,
  ProductsComponent, ProductComponent, ProductCategoryComponent, ProductAssociationsComponent, SKUsComponent, SKUComponent,
  ProductTypesComponent, ProductTypeComponent, ProductTypeGroupComponent, ProductTypeAttributeComponent
} from './components/index';
import { CatalogBrandComponent, CatalogCategoryComponent, CatalogProductsComponent, CatalogTypeComponent } from './index';

@NgModule({
    imports: [CommonModule, SharedModule, ServicesModule],
    declarations: [
      BrandsComponent, BrandComponent,
      CategoriesComponent, CategoryComponent,
      ProductsComponent, ProductComponent, ProductCategoryComponent, ProductAssociationsComponent, SKUsComponent, SKUComponent,
      ProductTypesComponent, ProductTypeComponent, ProductTypeGroupComponent, ProductTypeAttributeComponent,
      CatalogBrandComponent, CatalogCategoryComponent, CatalogProductsComponent, CatalogTypeComponent
    ],
    exports: [
      BrandsComponent, BrandComponent,
      CategoriesComponent, CategoryComponent,
      ProductsComponent, ProductComponent, ProductCategoryComponent, ProductAssociationsComponent, SKUsComponent, SKUComponent,
      ProductTypesComponent, ProductTypeComponent, ProductTypeGroupComponent, ProductTypeAttributeComponent,
      CatalogBrandComponent, CatalogCategoryComponent, CatalogProductsComponent, CatalogTypeComponent
    ]
})

export class CatalogPagesModule { }
