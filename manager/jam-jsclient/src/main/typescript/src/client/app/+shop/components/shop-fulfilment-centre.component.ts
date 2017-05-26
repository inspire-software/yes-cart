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
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { ShopVO, ShopFulfilmentCentreVO, FulfilmentCentreInfoVO, ValidationRequestVO } from './../../shared/model/index';
import { FulfilmentService, Util } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-fulfilment-centre',
  moduleId: module.id,
  templateUrl: './shop-fulfilment-centre.component.html',
})

export class ShopFulfilmentCentreComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  private shopCentresVO:Array<ShopFulfilmentCentreVO>;
  private availableCentres:Array<ShopFulfilmentCentreVO>;
  private selectedCentres:Array<ShopFulfilmentCentreVO>;

  private changed:boolean = false;

  private newCentre:FulfilmentCentreInfoVO;
  @ViewChild('editNewCentreName')
  private editNewCentreName:ModalComponent;
  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private newCentreForm:any;
  private newCentreFormSub:any; // tslint:disable-line:no-unused-variable
  private validForSave:boolean = false;

  private loading:boolean = false;

  constructor(private _fulfilmentService:FulfilmentService,
              fb: FormBuilder) {
    LogUtil.debug('ShopFulfilmentCentreComponent constructor');

    this.newCentre = this.newCentreInstance();

    let that = this;

    let validCode = function(control:any):any {

      let basic = Validators.required(control);

      if (basic == null) {
        let code = control.value;
        if (that.newCentre == null || !that.newCentreForm || !that.newCentreForm.dirty) {
          return null;
        }

        basic = YcValidators.validCode(control);
        if (basic == null) {
          var req:ValidationRequestVO = {
            subject: 'warehouse',
            subjectId: 0,
            field: 'code',
            value: code
          };
          return YcValidators.validRemoteCheck(control, req);
        }
      }
      return basic;
    };

    this.newCentreForm = fb.group({
      'code': ['', validCode],
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
    if (this._reload || this.shopCentresVO != null) {
      this.onRefreshHandler();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  ngOnInit() {
    LogUtil.debug('ShopFulfilmentCentreComponent ngOnInit shop', this.shop);
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('ShopFulfilmentCentreComponent ngOnDestroy');
    this.formUnbind();
  }

  newCentreInstance():FulfilmentCentreInfoVO {
    return {
      warehouseId: 0, code: '', name: '', description: null,
      countryCode: null, stateCode: null, city: null, postcode: null,
      defaultStandardStockLeadTime: 0, defaultBackorderStockLeadTime: 0,
      multipleShippingSupported: false,
      displayNames: []
    };
  }

  formBind():void {
    UiUtil.formBind(this, 'newCentreForm', 'newCentreFormSub', 'formChange', 'initialising', false);
  }

  formUnbind():void {
    if (this.newCentreFormSub) {
      UiUtil.formUnbind(this, 'newCentreFormSub');
    }
  }

  formChange():void {
    LogUtil.debug('ShopFulfilmentCentreComponent formChange', this.newCentreForm.valid, this.newCentre);
    this.validForSave = this.newCentreForm.valid;
  }

  onDataChange() {
    LogUtil.debug('ShopFulfilmentCentreComponent data changed');
    this.changed = true;
  }

  /**
   * Fast create new category.
   * @param parent parent of new catecory
   */
  createNew() {
    LogUtil.debug('ShopFulfilmentCentreComponent createNew');
    this.validForSave = false;
    UiUtil.formInitialise(this, 'initialising', 'newCentreForm', 'newCentre', this.newCentreInstance());
    this.editNewCentreName.show();
  }

  /**
   * Handle result of new category modal dialog.
   * @param modalresult
   */
  editNewCentreNameModalResult(modalresult:ModalResult) {
    LogUtil.debug('ShopFulfilmentCentreComponent editNewCentreNameModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let _sub:any = this._fulfilmentService.createFulfilmentCentre(this.newCentre, this.shop.shopId).subscribe(
          carVo => {
            this.validForSave = false;
            _sub.unsubscribe();
            this.onRefreshHandler();
        }
      );

    }
  }


  onSaveHandler() {
    LogUtil.debug('ShopFulfilmentCentreComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopCentresVO) {
      var _sub:any = this._fulfilmentService.saveShopFulfilmentCentres(this.shopCentresVO).subscribe(shopLanguagesVo => {
        this.shopCentresVO = Util.clone(shopLanguagesVo);
        this.remapCentres();
        this.changed = false;
        this._reload = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEventHandler() {
    LogUtil.debug('ShopFulfilmentCentreComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  onRefreshHandler() {
    LogUtil.debug('ShopFulfilmentCentreComponent refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      this.loading = true;
      var _sub:any = this._fulfilmentService.getShopFulfilmentCentres(this.shop.shopId).subscribe(shopCentresVo => {
        LogUtil.debug('ShopFulfilmentCentreComponent getShopCentres', shopCentresVo);
        this.shopCentresVO  = Util.clone(shopCentresVo);
        this.remapCentres();
        this.changed = false;
        this._reload = false;
        _sub.unsubscribe();
        this.loading = false;
      });
    } else {
      this.shopCentresVO = null;
    }
  }

  onAvailableCentreClick(event:any) {
    LogUtil.debug('ShopFulfilmentCentreComponent onAvailableCentreClick', event);
    event.fulfilmentShop.disabled = false;
    this.remapCentres();
    this.changed = true;
  }

  onSupportedCentreClick(event:any) {
    LogUtil.debug('ShopFulfilmentCentreComponent onSupportedCentreClick', event);
    event.fulfilmentShop.disabled = true;
    this.remapCentres();
    this.changed = true;
  }

  private remapCentres() {

    var availableCentres:Array<ShopFulfilmentCentreVO> = [];
    var selectedCentres:Array<ShopFulfilmentCentreVO> = [];

    this.shopCentresVO.forEach(centre => {
      if (centre.fulfilmentShop.disabled) {
        availableCentres.push(centre);
      } else {
        selectedCentres.push(centre);
      }
    });

    var _sort = function(a:ShopFulfilmentCentreVO, b:ShopFulfilmentCentreVO):number {
      if (a.name < b.name)
        return -1;
      if (a.name > b.name)
        return 1;
      return 0;
    };

    availableCentres.sort(_sort);
    selectedCentres.sort(_sort);

    this.selectedCentres = selectedCentres;
    this.availableCentres = availableCentres;

  }

}
