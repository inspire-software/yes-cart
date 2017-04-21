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
import { PIMService, Util } from './../shared/services/index';
import { UiUtil } from './../shared/ui/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ProductVO, ProductWithLinksVO, ProductSkuVO, AttrValueProductVO, AttrValueProductSkuVO, Pair } from './../shared/model/index';
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
  private static SKUS:string = 'skus';
  private static SKU:string = 'sku';

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = CatalogProductsComponent.PRODUCTS;

  private products:Array<ProductVO> = [];
  private productFilter:string;
  private productFilterRequired:boolean = true;
  private productFilterCapped:boolean = false;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;
  private filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedProduct:ProductVO;

  private productEdit:ProductWithLinksVO;
  private productEditAttributes:AttrValueProductVO[] = [];
  private productAttributesUpdate:Array<Pair<AttrValueProductVO, boolean>>;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private skus:Array<ProductSkuVO> = [];
  private skuFilter:string;

  private selectedSku:ProductSkuVO;

  private skuEdit:ProductSkuVO;
  private skuEditAttributes:AttrValueProductSkuVO[] = [];
  private skuAttributesUpdate:Array<Pair<AttrValueProductSkuVO, boolean>>;

  private deleteValue:String;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _pimService:PIMService) {
    LogUtil.debug('CatalogProductsComponent constructed');
  }

  newProductInstance():ProductWithLinksVO {
    return {
      productId: 0,
      guid: null, code: null,
      manufacturerCode: null, manufacturerPartCode: null,
      supplierCode: null, supplierCatalogCode: null,
      pimCode: null,
      tag: null,
      availablefrom: null, availableto: null,
      availability: 1,
      brand: null, productType: null, productCategories: [],
      name: '', description: null, displayNames: [],
      featured: false,
      minOrderQuantity: undefined, maxOrderQuantity: undefined, stepOrderQuantity: undefined,
      uri: null,
      title: null, displayTitles: [],
      metakeywords: null, displayMetakeywords: [],
      metadescription: null, displayMetadescriptions: [],
      sku: [],
      associations: []
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
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredProducts();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    LogUtil.debug('CatalogProductsComponent ngOnDestroy');
  }


  protected onProductFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onRefreshHandler() {
    LogUtil.debug('CatalogProductsComponent refresh handler');
    if (this.viewMode === CatalogProductsComponent.PRODUCTS ||
        this.viewMode === CatalogProductsComponent.PRODUCT ||
        this.selectedProduct == null) {
      this.getFilteredProducts();
    } else {
      this.getProductSkus();
    }
  }

  protected onProductSelected(data:ProductVO) {
    LogUtil.debug('CatalogProductsComponent onProductSelected', data);
    this.selectedProduct = data;
    this.skuFilter = '';
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
    this.searchHelpShow = false;
    this.productFilter = '?';
  }

  protected onSearchCategories() {
    this.searchHelpShow = false;
    this.productFilter = '^';
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
      } else {
        this.viewMode = CatalogProductsComponent.SKUS;
      }
    } else if (this.viewMode === CatalogProductsComponent.SKUS) {
      this.skuEdit = null;
      this.selectedSku = null;
      this.viewMode = CatalogProductsComponent.PRODUCTS;
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
    } else if (this.viewMode === CatalogProductsComponent.SKUS || this.viewMode == CatalogProductsComponent.PRODUCT) {
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
    var _sub:any = this._pimService.getProductById(row.productId).subscribe(res => {
      LogUtil.debug('CatalogProductsComponent getProductById', res);
      this.productEdit = res;
      this.changed = false;
      this.validForSave = false;
      this.viewMode = CatalogProductsComponent.PRODUCT;
      _sub.unsubscribe();
      if (this.productEdit.productId > 0) {
        var _sub2:any = this._pimService.getProductAttributes(this.productEdit.productId).subscribe(attrs => {
          this.productEditAttributes = attrs;
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
      var _sub:any = this._pimService.getSKUAttributes(this.skuEdit.skuId).subscribe(attrs => {
        this.skuEditAttributes = attrs;
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


  protected onRowList(row:ProductVO) {
    LogUtil.debug('CatalogProductsComponent onRowList handler', row);
    this.getProductSkus();
  }


  protected onRowListSelected() {
    if (this.selectedProduct != null) {
      this.onRowList(this.selectedProduct);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.skuEdit != null) {

        LogUtil.debug('CatalogProductsComponent Save handler sku', this.skuEdit);

        var _sub:any = this._pimService.saveSKU(this.skuEdit).subscribe(
            rez => {
              let pk = this.skuEdit.skuId;
              if (this.skuEdit.skuId > 0) {
                let idx = this.skus.findIndex(rez => rez.skuId == this.skuEdit.skuId);
                if (idx !== -1) {
                  this.skus[idx] = rez;
                  this.skus = this.skus.slice(0, this.skus.length); // reset to propagate changes
                  LogUtil.debug('CatalogProductsComponent sku changed', rez);
                }
                if (this.productEdit != null) {
                  let idx = this.productEdit.sku.findIndex(rez => rez.skuId == this.skuEdit.skuId);
                  if (idx !== -1) {
                    this.productEdit.sku[idx] = rez;
                    this.productEdit.sku = this.productEdit.sku.slice(0, this.productEdit.sku.length); // reset to propagate changes
                  }
                }
              } else {
                this.skus.push(rez);
                this.skuFilter = rez.name;
                if (this.productEdit != null) {
                  this.productEdit.sku.push(rez);
                  this.productEdit.sku = this.productEdit.sku.slice(0, this.productEdit.sku.length); // reset to propagate changes
                }
                LogUtil.debug('CatalogProductsComponent sku added', rez);
              }
              this.changed = false;
              this.selectedSku = rez;
              this.skuEdit = null;
              _sub.unsubscribe();

              if (pk > 0 && this.skuAttributesUpdate != null && this.skuAttributesUpdate.length > 0) {

                var _sub2:any = this._pimService.saveSKUAttributes(this.skuAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  LogUtil.debug('CatalogProductsComponent SKU attributes updated', rez);
                  this.skuAttributesUpdate = null;
                  this.onBackToList();
                });
              } else {
                this.onBackToList();
              }

            }
        );
      } else if (this.productEdit != null) {

        LogUtil.debug('CatalogProductsComponent Save handler product', this.productEdit);

        var _sub:any = this._pimService.saveProduct(this.productEdit).subscribe(
            rez => {
              let pk = this.productEdit.productId;
              if (this.productEdit.productId > 0) {
                let idx = this.products.findIndex(rez => rez.productId == this.productEdit.productId);
                if (idx !== -1) {
                  this.products[idx] = rez;
                  this.products = this.products.slice(0, this.products.length); // reset to propagate changes
                  LogUtil.debug('CatalogProductsComponent product changed', rez);
                }
              } else {
                this.products.push(rez);
                this.productFilter = rez.productCode;
                LogUtil.debug('CatalogProductsComponent product added', rez);
              }
              this.changed = false;
              this.selectedProduct = rez;
              this.productEdit = null;
              _sub.unsubscribe();

              if (pk > 0 && this.productAttributesUpdate != null && this.productAttributesUpdate.length > 0) {

                var _sub2:any = this._pimService.saveProductAttributes(this.productAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  LogUtil.debug('CatalogProductsComponent product attributes updated', rez);
                  this.productAttributesUpdate = null;
                  this.viewMode = CatalogProductsComponent.PRODUCTS;
                });
              } else {
                this.viewMode = CatalogProductsComponent.PRODUCTS;
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

        var _sub:any = this._pimService.removeSKU(this.selectedSku).subscribe(res => {
          LogUtil.debug('CatalogProductsComponent removeSku', this.selectedSku);
          let pk = this.selectedSku.skuId;
          let idx = this.skus.indexOf(this.selectedSku);
          this.skus.splice(idx, 1);
          this.skus = this.skus.slice(0, this.skus.length); // reset to propagate changes
          this.skuEdit = null;
          if (this.productEdit != null) {
            let idx2 = this.productEdit.sku.findIndex(rez => rez.skuId == pk);
            if (idx2 !== -1) {
              this.productEdit.sku.splice(idx2, 1);
              this.productEdit.sku = this.productEdit.sku.slice(0, this.productEdit.sku.length); // reset to propagate changes
            }
          }
          this.selectedSku = null;
          _sub.unsubscribe();
        });

      } else if (this.selectedProduct != null) {
        LogUtil.debug('CatalogProductsComponent onDeleteConfirmationResult', this.selectedProduct);

        var _sub:any = this._pimService.removeProduct(this.selectedProduct).subscribe(res => {
          LogUtil.debug('CatalogProductsComponent removeProduct', this.selectedProduct);
          let idx = this.products.indexOf(this.selectedProduct);
          this.products.splice(idx, 1);
          this.products = this.products.slice(0, this.products.length); // reset to propagate changes
          this.selectedProduct = null;
          this.productEdit = null;
          _sub.unsubscribe();
        });
      }
    }
  }

  protected onClearFilterProduct() {

    this.productFilter = '';
    this.getFilteredProducts();

  }

  protected onClearFilterSKU() {

    this.skuFilter = '';

  }

  private getFilteredProducts() {

    this.productFilterRequired = !this.forceShowAll && (this.productFilter == null || this.productFilter.length < 2);

    LogUtil.debug('CatalogBrandComponent getFilteredBrands' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.productFilterRequired) {
      this.loading = true;
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._pimService.getFilteredProducts(this.productFilter, max).subscribe(allproducts => {
        LogUtil.debug('CatalogProductsComponent getAllCountries', allproducts);
        this.products = allproducts;
        this.selectedProduct = null;
        this.productEdit = null;
        this.viewMode = CatalogProductsComponent.PRODUCTS;
        this.changed = false;
        this.validForSave = false;
        this.productFilterCapped = this.products.length >= max;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.products = [];
      this.selectedProduct = null;
      this.productEdit = null;
      this.viewMode = CatalogProductsComponent.PRODUCTS;
      this.changed = false;
      this.validForSave = false;
      this.productFilterCapped = false;
    }
  }

  private getProductSkus() {
    if (this.selectedProduct != null) {
      this.loading = true;
      var _sub:any = this._pimService.getProductSkuAll(this.selectedProduct).subscribe(allskus => {
        LogUtil.debug('CatalogProductsComponent getProductSkus', allskus);
        this.skus = allskus;
        this.selectedSku = null;
        this.skuEdit = null;
        this.productEdit = null;
        this.viewMode = CatalogProductsComponent.SKUS;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.skus = [];
      this.selectedSku = null;
      this.skuEdit = null;
      this.productEdit = null;
      this.viewMode = CatalogProductsComponent.SKUS;
      this.changed = false;
      this.validForSave = false;
    }
  }

}
