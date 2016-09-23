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
import {HTTP_PROVIDERS}    from '@angular/http';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../shared/validation/validators';
import {PricingService, Util} from './../shared/services/index';
import {UiUtil} from './../shared/ui/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {TaxesComponent, TaxConfigsComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ShopSelectComponent} from './../shared/shop/index';
import {CurrencySelectComponent} from './../shared/price/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {TaxVO, ShopVO, TaxConfigVO} from './../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-shop-taxes',
  moduleId: module.id,
  templateUrl: 'shop-taxes.component.html',
  directives: [REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, NgIf, TaxesComponent, TaxConfigsComponent, ModalComponent, DataControlComponent, ShopSelectComponent, CurrencySelectComponent ],
})

export class ShopTaxesComponent implements OnInit, OnDestroy {

  private static TAXES:string = 'taxes';
  private static CONFIGS:string = 'taxconfigs';

  private viewMode:string = ShopTaxesComponent.TAXES;

  private searchHelpTaxShow:boolean = false;
  private searchHelpTaxConfigShow:boolean = false;
  private forceShowAll:boolean = false;

  private taxes:Array<TaxVO> = [];
  private taxesFilter:string;
  private taxesFilterRequired:boolean = true;
  private taxesFilterCapped:boolean = false;

  private taxconfigs:Array<TaxConfigVO> = [];
  private taxconfigsFilter:string;
  private taxconfigsFilterRequired:boolean = true;
  private taxconfigsFilterCapped:boolean = false;

  delayedFilteringTax:Future;
  delayedFilteringTaxConfig:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;
  filterNoCap:number = Config.UI_FILTER_NO_CAP;

  static _selectedShop:ShopVO;
  static _selectedCurrency:string;
  private selectedTax:TaxVO;
  private selectedTaxconfig:TaxConfigVO;

  private taxEdit:TaxVO;
  taxEditForm:any;
  taxEditFormSub:any;
  changedSingleTax:boolean = true;
  validForSaveTax:boolean = false;

  private taxconfigEdit:TaxConfigVO;
  taxconfigEditForm:any;
  taxconfigEditFormSub:any;
  changedSingleTaxconfig:boolean = true;
  validForSaveTaxconfig:boolean = true;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('editTaxModalDialog')
  editTaxModalDialog:ModalComponent;

  @ViewChild('editTaxconfigModalDialog')
  editTaxconfigModalDialog:ModalComponent;

  @ViewChild('selectShopModalDialog')
  selectShopModalDialog:ModalComponent;

  @ViewChild('selectCurrencyModalDialog')
  selectCurrencyModalDialog:ModalComponent;

  private deleteValue:String;

  constructor(private _taxService:PricingService,
              fb: FormBuilder) {
    console.debug('ShopTaxesComponent constructed');

    this.taxEditForm = fb.group({
      'code': ['', YcValidators.requiredValidCode],
      'description': ['', YcValidators.requiredNonBlankTrimmed],
      'exclusiveOfPrice': [''],
      'taxRate': ['', YcValidators.requiredPositiveNumber],
      'shopCode': ['', Validators.required],
      'currency': ['', Validators.required],
    });

    this.taxconfigEditForm = fb.group({
       'productCode': ['', YcValidators.validCode],
       'stateCode': ['', YcValidators.validCode],
       'countryCode': ['', YcValidators.validCountryCode],
    });
  }

  get selectedShop():ShopVO {
     return ShopTaxesComponent._selectedShop;
  }

  set selectedShop(selectedShop:ShopVO) {
    ShopTaxesComponent._selectedShop = selectedShop;
  }

  get selectedCurrency():string {
     return ShopTaxesComponent._selectedCurrency;
  }

  set selectedCurrency(selectedCurrency:string) {
    ShopTaxesComponent._selectedCurrency = selectedCurrency;
  }

  newTaxInstance():TaxVO {
    return { taxId: 0, taxRate: 0, exclusiveOfPrice: false, shopCode: this.selectedShop.code, currency: this.selectedCurrency, code: '', guid: null, description: null};
  }

  newTaxConfigInstance():TaxConfigVO {
    return { taxConfigId: 0, taxId: this.selectedTax.taxId, productCode: null, stateCode: null, countryCode: null, guid: null};
  }

  ngOnInit() {
    console.debug('ShopTaxesComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFilteringTax = Futures.perpetual(function() {
      that.getFilteredTax();
    }, this.delayedFilteringMs);
    this.delayedFilteringTaxConfig = Futures.perpetual(function() {
      that.getFilteredTaxConfig();
    }, this.delayedFilteringMs);
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ShopTaxesComponent ngOnDestroy');
    this.formUnbind();
  }


  formResetTax():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.taxEditForm.controls) {
      this.taxEditForm.controls[key]['_pristine'] = true;
      this.taxEditForm.controls[key]['_touched'] = false;
    }
  }

  formResetTaxconfig():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.taxconfigEditForm.controls) {
      this.taxconfigEditForm.controls[key]['_pristine'] = true;
      this.taxconfigEditForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.taxEditFormSub = this.taxEditForm.statusChanges.subscribe((data:any) => {
      if (this.changedSingleTax) {
        this.validForSaveTax = this.taxEditForm.valid;
      }
    });
    this.taxconfigEditFormSub = this.taxconfigEditForm.statusChanges.subscribe((data:any) => {
      if (this.changedSingleTaxconfig) {
        this.validForSaveTaxconfig = this.taxconfigEditForm.valid;
      }
    });
  }

  formUnbind():void {
    if (this.taxEditFormSub) {
      console.debug('ShopCatalogComponent unbining form');
      this.taxEditFormSub.unsubscribe();
    }
    if (this.taxconfigEditFormSub) {
      console.debug('ShopCatalogComponent unbining form');
      this.taxconfigEditFormSub.unsubscribe();
    }
  }


  onFormDataChangeTax(event:any) {
    console.debug('ShopTaxesComponent tax data changed', event);
    this.changedSingleTax = true;
  }


  onFormDataChangeTaxconfig(event:any) {
    console.debug('ShopTaxesComponent tax config data changed', event);
    this.changedSingleTaxconfig = true;
  }



  protected onShopSelect() {
    console.debug('ShopTaxesComponent onShopSelect');
    this.selectShopModalDialog.show();
  }

  protected onShopSelected(event:ShopVO) {
    console.debug('ShopTaxesComponent onShopSelected');
    this.selectedShop = event;
  }

  protected onSelectShopResult(modalresult: ModalResult) {
    console.debug('ShopTaxesComponent onSelectShopResult modal result is ', modalresult);
    if (this.selectedShop == null) {
      this.selectShopModalDialog.show();
    } else if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredTax();
    }
  }

  protected onCurrencySelect() {
    console.debug('ShopTaxesComponent onCurrencySelect');
    this.selectCurrencyModalDialog.show();
  }

  protected onCurrencySelected(event:string) {
    console.debug('ShopTaxesComponent onCurrencySelected');
    this.selectedCurrency = event;
  }

  protected onSelectCurrencyResult(modalresult: ModalResult) {
    console.debug('ShopTaxesComponent onSelectCurrencyResult modal result is ', modalresult);
    if (this.selectedCurrency == null) {
      this.selectCurrencyModalDialog.show();
    } else {
      this.getFilteredTax();
    }
  }

  onTaxFilterChange(event:any) {

    this.delayedFilteringTax.delay();

  }

  onTaxConfigFilterChange(event:any) {

    this.delayedFilteringTaxConfig.delay();

  }

  getFilteredTax() {
    this.taxesFilterRequired = !this.forceShowAll && (this.taxesFilter == null || this.taxesFilter.length < 2);

    console.debug('ShopTaxesComponent getFilteredTax' + (this.forceShowAll ? ' forcefully': ''));

    if (this.selectedShop != null && this.selectedCurrency != null && !this.taxesFilterRequired) {
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._taxService.getFilteredTax(this.selectedShop, this.selectedCurrency, this.taxesFilter, max).subscribe( alltaxes => {
        console.debug('ShopTaxesComponent getFilteredTax', alltaxes);
        this.taxes = alltaxes;
        this.selectedTax = null;
        this.taxEdit = null;
        this.changedSingleTax = false;
        this.validForSaveTax = false;
        this.taxesFilterCapped = this.taxes.length >= max;
        _sub.unsubscribe();
        this.viewMode = ShopTaxesComponent.TAXES;
      });
    } else {
      this.taxes = [];
      this.selectedTax = null;
      this.taxEdit = null;
      this.changedSingleTax = false;
      this.validForSaveTax = false;
      this.taxesFilterCapped = false;
      this.viewMode = ShopTaxesComponent.TAXES;
    }
  }

  getFilteredTaxConfig() {
    this.taxesFilterRequired = !this.forceShowAll && (this.taxesFilter == null || this.taxesFilter.length < 2);

    console.debug('ShopTaxesComponent getFilteredTaxConfig' + (this.forceShowAll ? ' forcefully': ''));

    if (this.selectedTax != null && !this.taxesFilterRequired) {
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._taxService.getFilteredTaxConfig(this.selectedTax, this.taxconfigsFilter, max).subscribe( alltaxes => {
        console.debug('ShopTaxesComponent getFilteredTaxConfig', alltaxes);
        this.taxconfigs = alltaxes;
        this.selectedTaxconfig = null;
        this.taxconfigEdit = null;
        this.changedSingleTaxconfig = false;
        this.validForSaveTaxconfig = true;
        this.taxesFilterCapped = this.taxes.length >= max;
        this.viewMode = ShopTaxesComponent.CONFIGS;
        _sub.unsubscribe();
      });
    } else {
      this.taxconfigs = [];
      this.selectedTaxconfig = null;
      this.taxconfigEdit = null;
      this.changedSingleTaxconfig = false;
      this.validForSaveTaxconfig = true;
      this.taxesFilterCapped = false;
      this.viewMode = ShopTaxesComponent.CONFIGS;
    }
  }

  protected onRefreshHandler() {
    console.debug('ShopTaxesComponent refresh handler');
    this.getFilteredTax();
  }

  onTaxSelected(data:TaxVO) {
    console.debug('ShopTaxesComponent onTaxSelected', data);
    this.selectedTax = data;
  }

  onTaxconfigSelected(data:TaxConfigVO) {
    console.debug('ShopTaxesComponent onTaxconfigSelected', data);
    this.selectedTaxconfig = data;
  }

  protected onSearchHelpToggleTax() {
    this.searchHelpTaxShow = !this.searchHelpTaxShow;
  }

  protected onSearchHelpToggleTaxConfig() {
    this.searchHelpTaxConfigShow = !this.searchHelpTaxConfigShow;
  }

  protected onSearchRate() {
    this.taxesFilter = '%20';
    this.searchHelpTaxShow = false;
  }

  protected onSearchType() {
    this.taxesFilter = '-keyword';
    this.searchHelpTaxShow = false;
  }

  protected onSearchLocation() {
    this.taxesFilter = '@location';
    this.searchHelpTaxConfigShow = false;
  }

  protected onSearchSKU() {
    this.taxesFilter = '#SKU';
    this.searchHelpTaxConfigShow = false;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    if (this.viewMode == ShopTaxesComponent.CONFIGS) {
      this.getFilteredTaxConfig();
    } else {
      this.getFilteredTax();
    }
  }

  protected onRowNew() {
    console.debug('ShopTaxesComponent onRowNew handler');
    this.changedSingleTax = false;
    this.validForSaveTax = false;
    if (this.viewMode == ShopTaxesComponent.CONFIGS) {
      this.formResetTaxconfig();
      this.taxconfigEdit = this.newTaxConfigInstance();
      this.editTaxconfigModalDialog.show();
    } else {
      this.formResetTax();
      this.taxEdit = this.newTaxInstance();
      this.editTaxModalDialog.show();
    }
  }

  protected onRowTaxDelete(row:TaxVO) {
    console.debug('ShopTaxesComponent onRowDelete handler', row);
    this.deleteValue = row.code + (row.description ? ': ' + row.description : '');
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowTaxConfigDelete(row:TaxConfigVO) {
    console.debug('ShopTaxesComponent onRowTaxConfigDelete handler', row);
    this.deleteValue =
      (row.countryCode ? row.countryCode + '/' : '-') +
      (row.stateCode ? row.stateCode + '/' : '-') +
      (row.productCode ? row.productCode : '-');
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedTaxconfig != null) {
      this.onRowTaxConfigDelete(this.selectedTaxconfig);
    } else if (this.selectedTax != null) {
      this.onRowTaxDelete(this.selectedTax);
    }
  }


  protected onRowEditTax(row:TaxVO) {
    console.debug('ShopTaxesComponent onRowEditTax handler', row);
    this.formResetTax();
    this.taxEdit = Util.clone(row);
    this.changedSingleTax = false;
    this.validForSaveTax = false;
    this.editTaxModalDialog.show();
  }

  protected onRowEditTaxconfig(row:TaxConfigVO) {
    console.debug('ShopTaxesComponent onRowEditTaxconfig handler', row);
    this.formResetTaxconfig();
    this.taxconfigEdit = Util.clone(row);
    this.changedSingleTaxconfig = false;
    this.validForSaveTaxconfig = true;
    this.editTaxconfigModalDialog.show();
  }

  protected onRowEditSelected() {
    if (this.selectedTax != null) {
      this.onRowEditTax(this.selectedTax);
    }
  }

  protected onRowCopySelected() {
    if (this.selectedTaxconfig != null) {
      var copyCfg:TaxConfigVO = Util.clone(this.selectedTaxconfig);
      copyCfg.taxConfigId = 0;
      this.onRowEditTaxconfig(copyCfg);
    } else if (this.selectedTax != null) {
      var copyTax:TaxVO = Util.clone(this.selectedTax);
      copyTax.taxId = 0;
      this.onRowEditTax(copyTax);
    }
  }


  protected onRowList(row:TaxVO) {
    console.debug('ShopTaxesComponent onRowList handler', row);
    this.viewMode = ShopTaxesComponent.CONFIGS;
    this.forceShowAll = false;
  }


  protected onRowListSelected() {
    if (this.selectedTax != null) {
      this.onRowList(this.selectedTax);
    }
  }


  protected onBackToList() {
    console.debug('ShopTaxesComponent onBackToList handler');
    if (this.viewMode === ShopTaxesComponent.CONFIGS) {
      this.taxEdit = null;
      this.viewMode = ShopTaxesComponent.TAXES;
    }
  }

  protected onSaveHandler() {

    if (this.taxconfigEdit != null) {

      if (this.validForSaveTaxconfig) {

        console.debug('ShopTaxesComponent Save handler config', this.taxconfigEdit);

        var _sub:any = this._taxService.createTaxConfig(this.taxconfigEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              console.debug('ShopTaxesComponent config changed', rez);
              this.changedSingleTaxconfig = false;
              this.selectedTaxconfig = rez;
              this.taxconfigEdit = null;
              this.taxesFilter = rez.code;
              this.getFilteredTaxConfig();
          }
        );
      }

    } else if (this.taxEdit != null) {

      if (this.validForSaveTax && this.changedSingleTax) {

        console.debug('ShopTaxesComponent Save handler tax', this.taxEdit);

        var _sub:any = this._taxService.saveTax(this.taxEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.taxEdit.taxId;
              console.debug('ShopTaxesComponent tax changed', rez);
              this.changedSingleTax = false;
              this.selectedTax = rez;
              this.taxEdit = null;
              if (pk == 0) {
                this.taxesFilter = rez.code;
              }
              this.getFilteredTax();
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('ShopTaxesComponent discard handler');
    if (this.selectedTax != null) {
      this.onRowEditSelected();
    } else {
      this.onRowNew();
    }
  }

  protected onEditTaxResult(modalresult: ModalResult) {
    console.debug('ShopTaxesComponent onEditTaxResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.onSaveHandler();

    } else {

      if (this.taxconfigEdit != null) {
        this.taxconfigEdit = null;
      } else {
        this.taxEdit = null;
      }

    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('ShopTaxesComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedTaxconfig != null) {
        console.debug('ShopTaxesComponent onDeleteConfirmationResult', this.selectedTaxconfig);

        var _sub:any = this._taxService.removeTaxConfig(this.selectedTaxconfig).subscribe(res => {
          _sub.unsubscribe();
          console.debug('ShopTaxesComponent removeTax', this.selectedTaxconfig);
          this.selectedTaxconfig = null;
          this.taxconfigEdit = null;
          this.getFilteredTaxConfig();
        });

      } else if (this.selectedTax != null) {
        console.debug('ShopTaxesComponent onDeleteConfirmationResult', this.selectedTax);

        var _sub:any = this._taxService.removeTax(this.selectedTax).subscribe(res => {
          _sub.unsubscribe();
          console.debug('ShopTaxesComponent removeTax', this.selectedTax);
          this.selectedTax = null;
          this.taxEdit = null;
          this.getFilteredTax();
        });
      }
    }
  }

}
