/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { PIMService, UserEventBus, Util } from './../shared/services/index';
import { CategoryMinSelectComponent, BrandSelectComponent, ProductTypeSelectComponent } from './../shared/catalog/index';
import { UiUtil } from './../shared/ui/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { CategoryVO, ProductTypeInfoVO, BrandVO, ProductVO, ProductWithLinksVO, ProductSkuVO, AttrValueProductVO, AttrValueProductSkuVO, Pair, SearchResultVO } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-catalog-products',
  moduleId: module.id,
  templateUrl: 'catalog-products.component.html',
})

export class CatalogProductsComponent implements OnInit, OnDestroy {

  private static PRODUCTS:string = 'products';
  private static PRODUCT:string = 'product';
  private static SKU:string = 'sku';

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = CatalogProductsComponent.PRODUCTS;

  private products:SearchResultVO<ProductVO>;
  private productFilter:string;
  private productFilterRequired:boolean = true;

  private delayedFilteringProduct:Future;
  private delayedFilteringProductMs:number = Config.UI_INPUT_DELAY;

  private selectedProduct:ProductVO;

  private productEdit:ProductWithLinksVO;
  private productEditAttributes:AttrValueProductVO[] = [];
  private productAttributesUpdate:Array<Pair<AttrValueProductVO, boolean>>;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private selectedSku:ProductSkuVO;

  private skuEdit:ProductSkuVO;
  private skuEditAttributes:AttrValueProductSkuVO[] = [];
  private skuAttributesUpdate:Array<Pair<AttrValueProductSkuVO, boolean>>;

  private deleteValue:String;

  @ViewChild('categorySelectComponent')
  private categorySelectComponent:CategoryMinSelectComponent;

  @ViewChild('brandSelectComponent')
  private brandSelectComponent:BrandSelectComponent;

  @ViewChild('productTypeSelectComponent')
  private productTypeSelectComponent:ProductTypeSelectComponent;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _pimService:PIMService) {
    LogUtil.debug('CatalogProductsComponent constructed');
    this.products = this.newSearchResultProductInstance();
  }

  newProductInstance():ProductWithLinksVO {
    return {
      productId: 0,
      guid: null, code: null,
      manufacturerCode: null, manufacturerPartCode: null,
      supplierCode: null, supplierCatalogCode: null,
      pimCode: null,
      pimDisabled: false,
      pimOutdated: false,
      pimUpdated: null,
      tag: null,
      brand: null, productType: null, productCategories: [],
      name: '', description: null, displayNames: [],
      uri: null,
      title: null, displayTitles: [],
      metakeywords: null, displayMetakeywords: [],
      metadescription: null, displayMetadescriptions: [],
      sku: [],
      associations: [],
      configurable: false,
      configurationOptions: []
    };
  }

  newSearchResultProductInstance():SearchResultVO<ProductVO> {
    return {
      searchContext: {
        parameters: {
          filter: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: null,
        sortDesc: false
      },
      items: [],
      total: 0
    };
  }


  newSkuInstance():ProductSkuVO {
    let product = this.selectedProduct != null ? this.selectedProduct.productId : 0;
    return {
      skuId: 0, productId: product,
      guid: null, code: null,
      manufacturerCode: null, manufacturerPartCode: null,
      supplierCode: null, supplierCatalogCode: null,
      barCode: null,
      rank: 0,
      tag: null,
      name: '', description: null, displayNames: [],
      uri: null,
      title: null, displayTitles: [],
      metakeywords: null, displayMetakeywords: [],
      metadescription: null, displayMetadescriptions: []
    };
  }

  ngOnInit() {
    LogUtil.debug('CatalogProductsComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFilteringProduct = Futures.perpetual(function() {
      that.getFilteredProducts();
    }, this.delayedFilteringProductMs);
  }

  ngOnDestroy() {
    LogUtil.debug('CatalogProductsComponent ngOnDestroy');
  }


  protected onProductFilterChange(event:any) {
    this.products.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFilteringProduct.delay();
  }

  protected onRefreshHandler() {
    LogUtil.debug('CatalogProductsComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getFilteredProducts();
    }
  }

  protected onPageSelected(page:number) {
    LogUtil.debug('CatalogProductsComponent onPageSelected', page);
    this.products.searchContext.start = page;
    this.delayedFilteringProduct.delay();
  }

  protected onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('CatalogProductsComponent ononSortSelected', sort);
    if (sort == null) {
      this.products.searchContext.sortBy = null;
      this.products.searchContext.sortDesc = false;
    } else {
      this.products.searchContext.sortBy = sort.first;
      this.products.searchContext.sortDesc = sort.second;
    }
    this.delayedFilteringProduct.delay();
  }

  protected onProductSelected(data:ProductVO) {
    LogUtil.debug('CatalogProductsComponent onProductSelected', data);
    this.selectedProduct = data;
  }

  protected onProductChanged(event:FormValidationEvent<Pair<ProductWithLinksVO, Array<Pair<AttrValueProductVO, boolean>>>>) {
    LogUtil.debug('CatalogProductsComponent onProductChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.productEdit = event.source.first;
    this.productAttributesUpdate = event.source.second;
  }

  protected onSkuSelected(data:ProductSkuVO) {
    LogUtil.debug('CatalogProductsComponent onSkuSelected', data);
    this.selectedSku = data;
  }

  protected onSkuAdd(data:ProductSkuVO) {
    LogUtil.debug('CatalogProductsComponent onSkuAdd', data);
    this.onRowNew();
  }

  protected onSkuEdit(data:ProductSkuVO) {
    LogUtil.debug('CatalogProductsComponent onSkuEdit', data);
    this.onRowEditSku(data);
  }

  protected onSkuDelete(data:ProductSkuVO) {
    LogUtil.debug('CatalogProductsComponent onSkuDelete', data);
    this.selectedSku = data;
    this.onRowDelete(data);
  }

  protected onSkuChanged(event:FormValidationEvent<Pair<ProductSkuVO, Array<Pair<AttrValueProductSkuVO, boolean>>>>) {
    LogUtil.debug('CatalogProductsComponent onSkuChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.skuEdit = event.source.first;
    this.skuAttributesUpdate = event.source.second;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredProducts();
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onSearchCodeExact() {
    this.searchHelpShow = false;
    this.productFilter = '!';
  }

  protected onSearchCode() {
    this.searchHelpShow = false;
    this.productFilter = '#';
  }

  protected onSearchType() {
    LogUtil.debug('CatalogProductsComponent onSearchType handler');
    this.productTypeSelectComponent.showDialog();
  }

  protected onProductTypeSelected(event:FormValidationEvent<ProductTypeInfoVO>) {
    LogUtil.debug('CatalogProductsComponent onProductTypeSelected', event);
    if (event.valid) {
      this.productFilter = '?' + event.source.guid;
      this.getFilteredProducts();
    }
  }

  protected onSearchBrand() {
    LogUtil.debug('CatalogProductsComponent onSearchBrand handler');
    this.brandSelectComponent.showDialog();
  }

  protected onBrandSelected(event:FormValidationEvent<BrandVO>) {
    LogUtil.debug('CatalogProductsComponent onBrandSelected', event);
    if (event.valid) {
      this.productFilter = '?' + event.source.name;
      this.getFilteredProducts();
    }
  }

  protected onViewTree() {
    LogUtil.debug('CatalogProductsComponent onViewTree handler');
    this.categorySelectComponent.showDialog();
  }

  protected onCatalogTreeDataSelected(event:FormValidationEvent<CategoryVO>) {
    LogUtil.debug('CatalogProductsComponent onCatalogTreeDataSelected handler', event);
    if (event.valid) {
      this.productFilter = '^' + event.source.guid;
      this.getFilteredProducts();
    }
  }

  protected onSearchDate() {
    this.searchHelpShow = false;
    this.productFilter = UiUtil.exampleDateSearch();
    this.getFilteredProducts();
  }

  protected onBackToList() {
    LogUtil.debug('CatalogProductsComponent onBackToList handler');
    if (this.viewMode === CatalogProductsComponent.SKU) {
      this.skuEdit = null;
      if (this.productEdit != null) {
        this.viewMode = CatalogProductsComponent.PRODUCT;
      }
    } else if (this.viewMode === CatalogProductsComponent.PRODUCT) {
      this.productEdit = null;
      this.skuEdit = null;
      this.selectedSku = null;
      this.viewMode = CatalogProductsComponent.PRODUCTS;
    }
  }

  protected onRowNew() {
    LogUtil.debug('CatalogProductsComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === CatalogProductsComponent.PRODUCTS) {
      this.productEdit = this.newProductInstance();
      this.viewMode = CatalogProductsComponent.PRODUCT;
    } else if (this.viewMode == CatalogProductsComponent.PRODUCT) {
      this.skuEdit = this.newSkuInstance();
      this.viewMode = CatalogProductsComponent.SKU;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('CatalogProductsComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedSku != null) {
      this.onRowDelete(this.selectedSku);
    } else if (this.selectedProduct != null) {
      this.onRowDelete(this.selectedProduct);
    }
  }


  protected onRowEditProduct(row:ProductVO) {
    LogUtil.debug('CatalogProductsComponent onRowEditProduct handler', row);
    this.loading = true;
    let _sub:any = this._pimService.getProductById(row.productId).subscribe(res => {
      LogUtil.debug('CatalogProductsComponent getProductById', res);
      this.productEdit = res;
      this.changed = false;
      this.validForSave = false;
      this.viewMode = CatalogProductsComponent.PRODUCT;
      this.loading = false;
      _sub.unsubscribe();
      if (this.productEdit.productId > 0) {
        this.loading = true;
        let _sub2:any = this._pimService.getProductAttributes(this.productEdit.productId).subscribe(attrs => {
          this.productEditAttributes = attrs;
          this.loading = false;
          _sub2.unsubscribe();
        });
      }
    });
  }

  protected onRowEditSku(row:ProductSkuVO) {
    LogUtil.debug('CatalogProductsComponent onRowEditSku handler', row);
    this.skuEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = CatalogProductsComponent.SKU;
    if (this.skuEdit.skuId > 0) {
      this.loading = true;
      let _sub:any = this._pimService.getSKUAttributes(this.skuEdit.skuId).subscribe(attrs => {
        this.skuEditAttributes = attrs;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }

  protected onRowEditSelected() {
    if (this.selectedSku != null) {
      this.onRowEditSku(this.selectedSku);
    } else if (this.selectedProduct != null) {
      this.onRowEditProduct(this.selectedProduct);
    }
  }


  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.skuEdit != null) {

        LogUtil.debug('CatalogProductsComponent Save handler sku', this.skuEdit);

        this.loading = true;
        let _sub:any = this._pimService.saveSKU(this.skuEdit).subscribe(
            rez => {
              let pk = this.skuEdit.skuId;
              if (pk > 0) {
                LogUtil.debug('CatalogProductsComponent sku edit', rez);
                if (this.productEdit != null) {
                  let idx = this.productEdit.sku.findIndex(rez => rez.skuId == this.skuEdit.skuId);
                  if (idx !== -1) {
                    this.productEdit.sku[idx] = rez;
                    this.productEdit.sku = this.productEdit.sku.slice(0, this.productEdit.sku.length); // reset to propagate changes
                  }
                }
              } else {
                if (this.productEdit != null) {
                  this.productEdit.sku.push(rez);
                  this.productEdit.sku = this.productEdit.sku.slice(0, this.productEdit.sku.length); // reset to propagate changes
                }
                LogUtil.debug('CatalogProductsComponent sku added', rez);
              }
              this.changed = false;
              this.selectedSku = rez;
              this.skuEdit = null;
              this.loading = false;
              _sub.unsubscribe();

              if (pk > 0 && this.skuAttributesUpdate != null && this.skuAttributesUpdate.length > 0) {

                this.loading = true;
                let _sub2:any = this._pimService.saveSKUAttributes(this.skuAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  LogUtil.debug('CatalogProductsComponent SKU attributes updated', rez);
                  this.skuAttributesUpdate = null;
                  this.loading = false;
                  this.viewMode = CatalogProductsComponent.PRODUCT;
                });
              } else {
                this.viewMode = CatalogProductsComponent.PRODUCT;
              }

            }
        );
      } else if (this.productEdit != null) {

        LogUtil.debug('CatalogProductsComponent Save handler product', this.productEdit);

        this.loading = true;
        let _sub:any = this._pimService.saveProduct(this.productEdit).subscribe(
            rez => {
              let pk = this.productEdit.productId;
              LogUtil.debug('CatalogProductsComponent product changed', rez);
              this.changed = false;
              this.selectedProduct = rez;
              this.productEdit = null;
              this.loading = false;
              _sub.unsubscribe();

              if (pk > 0 && this.productAttributesUpdate != null && this.productAttributesUpdate.length > 0) {

                this.loading = true;
                let _sub2:any = this._pimService.saveProductAttributes(this.productAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  LogUtil.debug('CatalogProductsComponent product attributes updated', rez);
                  this.productAttributesUpdate = null;
                  this.loading = false;
                  this.getFilteredProducts();
                });
              } else {
                this.getFilteredProducts();
              }
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('CatalogProductsComponent discard handler');
    if (this.viewMode === CatalogProductsComponent.SKU) {
      if (this.selectedSku != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
    if (this.viewMode === CatalogProductsComponent.PRODUCT) {
      if (this.selectedProduct != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CatalogProductsComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedSku != null) {
        LogUtil.debug('CatalogProductsComponent onDeleteConfirmationResult', this.selectedSku);

        this.loading = true;
        let _sub:any = this._pimService.removeSKU(this.selectedSku).subscribe(res => {
          LogUtil.debug('CatalogProductsComponent removeSku', this.selectedSku);
          let pk = this.selectedSku.skuId;
          this.skuEdit = null;
          if (this.productEdit != null) {
            let idx2 = this.productEdit.sku.findIndex(rez => rez.skuId == pk);
            if (idx2 !== -1) {
              this.productEdit.sku.splice(idx2, 1);
              this.productEdit.sku = this.productEdit.sku.slice(0, this.productEdit.sku.length); // reset to propagate changes
            }
          }
          this.selectedSku = null;
          this.loading = false;
          _sub.unsubscribe();
          this.viewMode = CatalogProductsComponent.PRODUCT;
        });

      } else if (this.selectedProduct != null) {
        LogUtil.debug('CatalogProductsComponent onDeleteConfirmationResult', this.selectedProduct);

        this.loading = true;
        let _sub:any = this._pimService.removeProduct(this.selectedProduct).subscribe(res => {
          LogUtil.debug('CatalogProductsComponent removeProduct', this.selectedProduct);
          this.selectedProduct = null;
          this.productEdit = null;
          this.loading = false;
          _sub.unsubscribe();
          this.getFilteredProducts();
        });
      }
    }
  }

  protected onClearFilterProduct() {

    this.productFilter = '';
    this.getFilteredProducts();

  }

  private getFilteredProducts() {

    this.productFilterRequired = !this.forceShowAll && (this.productFilter == null || this.productFilter.length < 2);

    LogUtil.debug('CatalogBrandComponent getFilteredBrands' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.productFilterRequired) {
      this.loading = true;

      this.products.searchContext.parameters.filter = [ this.productFilter ];
      this.products.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

      let _sub:any = this._pimService.getFilteredProducts(this.products.searchContext).subscribe(allproducts => {
        LogUtil.debug('CatalogProductsComponent getAllCountries', allproducts);
        this.products = allproducts;
        this.selectedProduct = null;
        this.productEdit = null;
        this.viewMode = CatalogProductsComponent.PRODUCTS;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.products = this.newSearchResultProductInstance();
      this.selectedProduct = null;
      this.productEdit = null;
      this.viewMode = CatalogProductsComponent.PRODUCTS;
      this.changed = false;
      this.validForSave = false;
    }
  }

}
