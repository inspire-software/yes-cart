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
import {Component, OnInit, OnDestroy, OnChanges, Input} from '@angular/core';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {NgIf, NgFor} from '@angular/common';
import {ShopVO, ShopCarrierVO, CarrierLocaleVO} from './../../shared/model/index';
import {ShippingService, ShopEventBus, Util} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';

@Component({
  selector: 'yc-shop-carrier',
  moduleId: module.id,
  templateUrl: './shop-carrier.component.html',
  directives: [ NgIf, NgFor, DataControlComponent, REACTIVE_FORM_DIRECTIVES, ModalComponent],
})

export class ShopCarrierComponent implements OnInit, OnChanges {

  @Input() shop:ShopVO;

  shopCarriersVO:Array<ShopCarrierVO>;
  availableCarriers:Array<ShopCarrierVO>;
  selectedCarriers:Array<ShopCarrierVO>;

  changed:boolean = false;

  newCarrier:CarrierLocaleVO;
  editNewCarrierName:ModalComponent;
  newCarrierForm:any;

  validForSave:boolean = false;

  constructor(private _shippingService:ShippingService,
              fb: FormBuilder) {
    console.debug('ShopCarrierComponent constructor');

    this.newCarrier = this.newCarrierInstance();

    this.newCarrierForm = fb.group({
      'name': ['', Validators.compose([Validators.required, Validators.pattern('\\S+.*\\S+')])],
    });
  }

  ngOnInit() {
    console.debug('ShopCarrierComponent ngOnInit shop', this.shop);
    this.onRefreshHandler();
  }

  ngOnChanges(changes:any) {
    console.log('ShopCarrierComponent ngOnChanges', changes);
    this.onRefreshHandler();
  }

  newCarrierInstance():CarrierLocaleVO {
    return { carrierId: 0, name: '', description: null, displayNames: [], displayDescriptions: [] };
  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.newCarrierForm.controls) {
      this.newCarrierForm.controls[key]['_pristine'] = true;
      this.newCarrierForm.controls[key]['_touched'] = false;
    }
  }

  onFormDataChange(event:any) {
    var _sub:any = this.newCarrierForm.valueChanges.subscribe((data:any) => {
      this.validForSave = this.newCarrierForm.valid;
      console.debug('ShopCarrierComponent form changed  and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), data);
      _sub.unsubscribe();
    });
    console.debug('ShopCarrierComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  onDataChange() {
    console.debug('ShopCarrierComponent data changed');
    this.changed = true;
  }

  private remapCarriers() {

    var availableCarriers:Array<ShopCarrierVO> = [];
    var selectedCarriers:Array<ShopCarrierVO> = [];

    this.shopCarriersVO.forEach(carrier => {
      if (carrier.carrierShop.disabled) {
        availableCarriers.push(carrier);
      } else {
        selectedCarriers.push(carrier);
      }
    });

    var _sort = function(a:ShopCarrierVO, b:ShopCarrierVO):number {
      if (a.name < b.name)
        return -1;
      if (a.name > b.name)
        return 1;
      return 0;
    };

    availableCarriers.sort(_sort);
    selectedCarriers.sort(_sort);

    this.selectedCarriers = selectedCarriers;
    this.availableCarriers = availableCarriers;

  }
  /**
   * Fast create new category.
   * @param parent parent of new catecory
   */
  createNew() {
    console.debug('ShopCarrierComponent createNew');
    this.newCarrier = this.newCarrierInstance();
    this.editNewCarrierName.show();
  }


  editNewCarrierNameModalLoaded(modal:ModalComponent) {
    console.debug('ShopCarrierComponent editNewCarrierNameModalLoaded');
    this.editNewCarrierName = modal;
  }

  /**
   * Handle result of new category modal dialog.
   * @param modalresult
   */
  editNewCarrierNameModalResult(modalresult:ModalResult) {
    console.debug('ShopCarrierComponent editNewCarrierNameModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this._shippingService.createCarrier(this.newCarrier, this.shop.shopId).subscribe(
          carVo => {
          this.onRefreshHandler();
        }
      );

    }
  }


  onSaveHandler() {
    console.debug('ShopCarrierComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopCarriersVO) {
      var _sub:any = this._shippingService.saveShopCarriers(this.shopCarriersVO).subscribe(shopLanguagesVo => {
        this.shopCarriersVO = Util.clone(shopLanguagesVo);
        this.remapCarriers();
        this.changed = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEventHandler() {
    console.debug('ShopCarrierComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  onRefreshHandler() {
    console.debug('ShopCarrierComponent refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      var _sub:any = this._shippingService.getShopCarriers(this.shop.shopId).subscribe(shopCarriersVo => {
        console.debug('ShopCarrierComponent getShopCarriers', shopCarriersVo);
        this.shopCarriersVO  = Util.clone(shopCarriersVo);
        this.remapCarriers();
        this.changed = false;
        _sub.unsubscribe();
      });
    } else {
      this.shopCarriersVO = null;
    }
  }

  onAvailableCarrierClick(event:any) {
    console.debug('ShopCarrierComponent onAvailableCarrierClick', event);
    event.carrierShop.disabled = false;
    this.remapCarriers();
    this.changed = true;
  }

  onSupportedCarrierClick(event:any) {
    console.debug('ShopCarrierComponent onSupportedCarrierClick', event);
    event.carrierShop.disabled = true;
    this.remapCarriers();
    this.changed = true;
  }

}
