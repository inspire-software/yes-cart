/*
 * Copyright 2009 Inspire-Software.com
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
import { CustomValidators } from './../../shared/validation/validators';
import { ShopVO, ShopFulfilmentCentreVO, FulfilmentCentreInfoVO, ValidationRequestVO } from './../../shared/model/index';
import { FulfilmentService, Util } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-shop-fulfilment-centre',
  templateUrl: './shop-fulfilment-centre.component.html',
})

export class ShopFulfilmentCentreComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  public shopCentresVO:Array<ShopFulfilmentCentreVO>;
  public availableCentres:Array<ShopFulfilmentCentreVO>;
  public selectedCentres:Array<ShopFulfilmentCentreVO>;

  public changed:boolean = false;

  public newCentre:FulfilmentCentreInfoVO;
  @ViewChild('editNewCentreName')
  private editNewCentreName:ModalComponent;
  public newCentreForm:any;
  public validForSave:boolean = false;

  public loading:boolean = false;

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

        basic = CustomValidators.validCode(control);
        if (basic == null) {
          let req:ValidationRequestVO = {
            subject: 'warehouse',
            subjectId: 0,
            field: 'code',
            value: code
          };
          return CustomValidators.validRemoteCheck(control, req);
        }
      }
      return basic;
    };

    this.newCentreForm = fb.group({
      'code': ['', validCode],
      'name': ['', CustomValidators.requiredNonBlankTrimmed],
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
      displayNames: [], fulfilmentShops: []
    };
  }

  formBind():void {
    UiUtil.formBind(this, 'newCentreForm', 'formChange', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'newCentreForm');
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
   */
  createNew() {
    LogUtil.debug('ShopFulfilmentCentreComponent createNew');
    this.validForSave = false;
    UiUtil.formInitialise(this, 'newCentreForm', 'newCentre', this.newCentreInstance());
    this.editNewCentreName.show();
  }

  /**
   * Handle result of new category modal dialog.
   * @param modalresult
   */
  editNewCentreNameModalResult(modalresult:ModalResult) {
    LogUtil.debug('ShopFulfilmentCentreComponent editNewCentreNameModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.loading = true;
      this._fulfilmentService.createFulfilmentCentre(this.newCentre, this.shop.shopId).subscribe(
          carVo => {
            this.validForSave = false;
            this.loading = false;
            this.onRefreshHandler();
        }
      );

    }
  }


  onSaveHandler() {
    LogUtil.debug('ShopFulfilmentCentreComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopCentresVO) {
      this.loading = true;
      this._fulfilmentService.saveShopFulfilmentCentres(this.shopCentresVO).subscribe(shopLanguagesVo => {
        this.shopCentresVO = Util.clone(shopLanguagesVo);
        this.remapCentres();
        this.changed = false;
        this._reload = false;
        this.loading = false;
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
      this._fulfilmentService.getShopFulfilmentCentres(this.shop.shopId).subscribe(shopCentresVo => {
        LogUtil.debug('ShopFulfilmentCentreComponent getShopCentres', shopCentresVo);
        this.shopCentresVO  = Util.clone(shopCentresVo);
        this.remapCentres();
        this.changed = false;
        this._reload = false;
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

    let availableCentres:Array<ShopFulfilmentCentreVO> = [];
    let selectedCentres:Array<ShopFulfilmentCentreVO> = [];

    this.shopCentresVO.forEach(centre => {
      if (centre.fulfilmentShop.disabled) {
        availableCentres.push(centre);
      } else {
        selectedCentres.push(centre);
      }
    });

    let _sort = function(a:ShopFulfilmentCentreVO, b:ShopFulfilmentCentreVO):number {
      return (a.name.toLowerCase() < b.name.toLowerCase()) ? -1 : 1;
    };

    availableCentres.sort(_sort);
    selectedCentres.sort(_sort);

    this.selectedCentres = selectedCentres;
    this.availableCentres = availableCentres;

  }

}
