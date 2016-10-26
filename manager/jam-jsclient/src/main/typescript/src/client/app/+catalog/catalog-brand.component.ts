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
import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {NgIf} from '@angular/common';
import {CatalogService, Util} from './../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {BrandsComponent, BrandComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {BrandVO, AttrValueBrandVO, Pair} from './../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-catalog-brands',
  moduleId: module.id,
  templateUrl: 'catalog-brand.component.html',
  directives: [TAB_DIRECTIVES, NgIf, BrandsComponent, BrandComponent, ModalComponent, DataControlComponent ],
})

export class CatalogBrandComponent implements OnInit, OnDestroy {

  private static BRANDS:string = 'brands';
  private static BRAND:string = 'brand';

  private forceShowAll:boolean = false;
  private viewMode:string = CatalogBrandComponent.BRANDS;

  private brands:Array<BrandVO> = [];
  private brandFilter:string;
  private brandFilterRequired:boolean = true;
  private brandFilterCapped:boolean = false;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;
  filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedBrand:BrandVO;

  private brandEdit:BrandVO;
  private brandEditAttributes:AttrValueBrandVO[] = [];
  private brandAttributesUpdate:Array<Pair<AttrValueBrandVO, boolean>>;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  constructor(private _brandService:CatalogService) {
    console.debug('CatalogBrandComponent constructed');
  }

  changed:boolean = false;
  validForSave:boolean = false;

  newBrandInstance():BrandVO {
    return { brandId: 0, name: '', description: null};
  }

  ngOnInit() {
    console.debug('CatalogBrandComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredBrands();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    console.debug('CatalogBrandComponent ngOnDestroy');
  }


  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  getFilteredBrands() {
    this.brandFilterRequired = !this.forceShowAll && (this.brandFilter == null || this.brandFilter.length < 2);

    console.debug('CatalogBrandComponent getFilteredBrands' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.brandFilterRequired) {
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._brandService.getFilteredBrands(this.brandFilter, max).subscribe( allbrands => {
        console.debug('CatalogBrandComponent getFilteredBrands', allbrands);
        this.brands = allbrands;
        this.selectedBrand = null;
        this.brandEdit = null;
        this.viewMode = CatalogBrandComponent.BRANDS;
        this.changed = false;
        this.validForSave = false;
        this.brandFilterCapped = this.brands.length >= max;
        _sub.unsubscribe();
      });
    } else {
      this.brands = [];
      this.selectedBrand = null;
      this.brandEdit = null;
      this.brandEditAttributes = null;
      this.viewMode = CatalogBrandComponent.BRANDS;
      this.changed = false;
      this.validForSave = false;
      this.brandFilterCapped = false;
    }
  }

  protected onRefreshHandler() {
    console.debug('CatalogBrandComponent refresh handler');
    this.getFilteredBrands();
  }

  onBrandSelected(data:BrandVO) {
    console.debug('CatalogBrandComponent onBrandSelected', data);
    this.selectedBrand = data;
  }

  onBrandChanged(event:FormValidationEvent<Pair<BrandVO, Array<Pair<AttrValueBrandVO, boolean>>>>) {
    console.debug('CatalogBrandComponent onBrandChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.brandEdit = event.source.first;
    this.brandAttributesUpdate = event.source.second;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredBrands();
  }

  protected onBackToList() {
    console.debug('CatalogBrandComponent onBackToList handler');
    if (this.viewMode === CatalogBrandComponent.BRAND) {
      this.brandEdit = null;
      this.viewMode = CatalogBrandComponent.BRANDS;
    }
  }

  protected onRowNew() {
    console.debug('CatalogBrandComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === CatalogBrandComponent.BRANDS) {
      this.brandEdit = this.newBrandInstance();
      this.brandEditAttributes = [];
      this.viewMode = CatalogBrandComponent.BRAND;
    }
  }

  protected onRowDelete(row:any) {
    console.debug('CatalogBrandComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedBrand != null) {
      this.onRowDelete(this.selectedBrand);
    }
  }


  protected onRowEditBrand(row:BrandVO) {
    console.debug('CatalogBrandComponent onRowEditBrand handler', row);
    this.brandEdit = Util.clone(row);
    this.brandEditAttributes = [];
    this.changed = false;
    this.validForSave = false;
    this.viewMode = CatalogBrandComponent.BRAND;
    if (this.brandEdit.brandId > 0) {
      var _sub:any = this._brandService.getBrandAttributes(this.brandEdit.brandId).subscribe(attrs => {
        this.brandEditAttributes = attrs;
        _sub.unsubscribe();
      });
    }
  }

  protected onRowEditSelected() {
    if (this.selectedBrand != null) {
      this.onRowEditBrand(this.selectedBrand);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.brandEdit != null) {

        console.debug('CatalogBrandComponent Save handler brand', this.brandEdit);

        var _sub:any = this._brandService.saveBrand(this.brandEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.brandEdit.brandId;
              console.debug('CatalogBrandComponent brand changed', rez);
              this.changed = false;
              this.selectedBrand = rez;
              this.brandEdit = null;
              this.viewMode = CatalogBrandComponent.BRANDS;

              if (pk > 0 && this.brandAttributesUpdate != null && this.brandAttributesUpdate.length > 0) {

                var _sub2:any = this._brandService.saveBrandAttributes(this.brandAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  console.debug('CatalogBrandComponent brand attributes updated', rez);
                  this.brandAttributesUpdate = null;
                  this.getFilteredBrands();
                });
              } else {
                this.brandFilter = rez.name;
                this.getFilteredBrands();
              }
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('CatalogBrandComponent discard handler');
    if (this.viewMode === CatalogBrandComponent.BRAND) {
      if (this.selectedBrand != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('CatalogBrandComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedBrand != null) {
        console.debug('CatalogBrandComponent onDeleteConfirmationResult', this.selectedBrand);

        var _sub:any = this._brandService.removeBrand(this.selectedBrand).subscribe(res => {
          _sub.unsubscribe();
          console.debug('CatalogBrandComponent removeBrand', this.selectedBrand);
          this.selectedBrand = null;
          this.brandEdit = null;
          this.getFilteredBrands();
        });
      }
    }
  }

}
