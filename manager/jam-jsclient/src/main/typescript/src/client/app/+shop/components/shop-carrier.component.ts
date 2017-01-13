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
import { Component, OnInit, OnDestroy, Input, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { ShopVO, ShopCarrierVO, CarrierLocaleVO } from './../../shared/model/index';
import { ShippingService, Util } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-carrier',
  moduleId: module.id,
  templateUrl: './shop-carrier.component.html',
})

export class ShopCarrierComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  private shopCarriersVO:Array<ShopCarrierVO>;
  private availableCarriers:Array<ShopCarrierVO>;
  private selectedCarriers:Array<ShopCarrierVO>;

  private changed:boolean = false;

  private newCarrier:CarrierLocaleVO;
  @ViewChild('editNewCarrierName')
  private editNewCarrierName:ModalComponent;
  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private newCarrierForm:any;
  private newCarrierFormSub:any; // tslint:disable-line:no-unused-variable
  private validForSave:boolean = false;

  private loading:boolean = false;

  constructor(private _shippingService:ShippingService,
              fb: FormBuilder) {
    LogUtil.debug('ShopCarrierComponent constructor');

    this.newCarrier = this.newCarrierInstance();

    this.newCarrierForm = fb.group({
      'name': ['', YcValidators.requiredNonBlankTrimmed],
    });
  }

  @Input()
  set reload(reload:boolean) {
    if (reload && !this._reload) {
      this._reload = true;
      this.onRefreshHandler();
    }
  }

  @Input()
  set shop(shop:ShopVO) {
    this._shop = shop;
    if (this._reload || this.shopCarriersVO != null) {
      this.onRefreshHandler();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  ngOnInit() {
    LogUtil.debug('ShopCarrierComponent ngOnInit shop', this.shop);
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ShopCarrierComponent ngOnDestroy');
    this.formUnbind();
  }

  newCarrierInstance():CarrierLocaleVO {
    return { carrierId: 0, name: '', description: null, displayNames: [], displayDescriptions: [] };
  }

  formBind():void {
    UiUtil.formBind(this, 'newCarrierForm', 'newCarrierFormSub', 'formChange', 'initialising', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'newCarrierFormSub');
  }

  formChange():void {
    LogUtil.debug('ShopCarrierComponent formChange', this.newCarrierForm.valid, this.newCarrier);
    this.validForSave = this.newCarrierForm.valid;
  }

  onDataChange() {
    LogUtil.debug('ShopCarrierComponent data changed');
    this.changed = true;
  }

  /**
   * Fast create new category.
   * @param parent parent of new catecory
   */
  createNew() {
    LogUtil.debug('ShopCarrierComponent createNew');
    this.validForSave = false;
    UiUtil.formInitialise(this, 'initialising', 'newCarrierForm', 'newCarrier', this.newCarrierInstance());
    this.editNewCarrierName.show();
  }

  /**
   * Handle result of new category modal dialog.
   * @param modalresult
   */
  editNewCarrierNameModalResult(modalresult:ModalResult) {
    LogUtil.debug('ShopCarrierComponent editNewCarrierNameModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this._shippingService.createCarrier(this.newCarrier, this.shop.shopId).subscribe(
          carVo => {
            this.validForSave = false;
            this.onRefreshHandler();
        }
      );

    }
  }


  onSaveHandler() {
    LogUtil.debug('ShopCarrierComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopCarriersVO) {
      var _sub:any = this._shippingService.saveShopCarriers(this.shopCarriersVO).subscribe(shopLanguagesVo => {
        this.shopCarriersVO = Util.clone(shopLanguagesVo);
        this.remapCarriers();
        this.changed = false;
        this._reload = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEventHandler() {
    LogUtil.debug('ShopCarrierComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  onRefreshHandler() {
    LogUtil.debug('ShopCarrierComponent refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      this.loading = true;
      var _sub:any = this._shippingService.getShopCarriers(this.shop.shopId).subscribe(shopCarriersVo => {
        LogUtil.debug('ShopCarrierComponent getShopCarriers', shopCarriersVo);
        this.shopCarriersVO  = Util.clone(shopCarriersVo);
        this.remapCarriers();
        this.changed = false;
        this._reload = false;
        _sub.unsubscribe();
        this.loading = false;
      });
    } else {
      this.shopCarriersVO = null;
    }
  }

  onAvailableCarrierClick(event:any) {
    LogUtil.debug('ShopCarrierComponent onAvailableCarrierClick', event);
    event.carrierShop.disabled = false;
    this.remapCarriers();
    this.changed = true;
  }

  onSupportedCarrierClick(event:any) {
    LogUtil.debug('ShopCarrierComponent onSupportedCarrierClick', event);
    event.carrierShop.disabled = true;
    this.remapCarriers();
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

}
