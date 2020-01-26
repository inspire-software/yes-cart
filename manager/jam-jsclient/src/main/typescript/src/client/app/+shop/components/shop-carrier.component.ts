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
import { ShopVO, CarrierInfoVO, ShopCarrierSlaVO, Pair } from './../../shared/model/index';
import { ShippingService, Util } from './../../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';
import { ShopCarrierAndSlaVO } from '../../shared/model/shipping.model';

@Component({
  selector: 'yc-shop-carrier',
  moduleId: module.id,
  templateUrl: './shop-carrier.component.html',
})

export class ShopCarrierComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  private shopCarriersVO:Array<ShopCarrierAndSlaVO>;
  private availableCarriers:Array<ShopCarrierAndSlaVO>;
  private selectedCarriers:Array<Pair<ShopCarrierAndSlaVO, ShopCarrierSlaVO>>;

  private changed:boolean = false;

  private newCarrier:CarrierInfoVO;
  @ViewChild('editNewCarrierName')
  private editNewCarrierName:ModalComponent;

  private editCarrierSla:ShopCarrierSlaVO;
  @ViewChild('editCarrierSlaRank')
  private editCarrierSlaRank:ModalComponent;

  private newCarrierForm:any;
  private validForSave:boolean = false;
  private editCarrierSlaForm:any;
  private validForEdit:boolean = false;

  private loading:boolean = false;

  constructor(private _shippingService:ShippingService,
              fb: FormBuilder) {
    LogUtil.debug('ShopCarrierComponent constructor');

    this.newCarrier = this.newCarrierInstance();
    this.editCarrierSla = this.newCarrierSlaInstance();

    this.newCarrierForm = fb.group({
      'name': ['', YcValidators.requiredNonBlankTrimmed],
    });

    this.editCarrierSlaForm = fb.group({
      'rank': ['', YcValidators.requiredPositiveWholeNumber],
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
    this.formBindEdit();
  }

  ngOnDestroy() {
    LogUtil.debug('ShopCarrierComponent ngOnDestroy');
    this.formUnbind();
    this.formUnbindEdit();
  }

  newCarrierInstance():CarrierInfoVO {
    return {
      carrierId: 0,
      code: null,
      name: '',
      description: null,
      displayNames: [],
      displayDescriptions: [],
      carrierShops: []
    };
  }

  newCarrierSlaInstance():ShopCarrierSlaVO {
    return {
      carrierslaId: 0,
      carrierId: 0,
      code: '',
      name: '',
      displayNames: [],
      description: null,
      displayDescriptions: [],
      maxDays: 0,
      minDays: 0,
      guaranteed: false,
      namedDay: false,
      slaType: 'F',
      externalRef: '',
      excludeWeekDays: [],
      excludeDates: [],
      excludeCustomerTypes: null,
      script: null,
      billingAddressNotRequired: false,
      deliveryAddressNotRequired: false,
      supportedPaymentGateways: [],
      supportedFulfilmentCentres: [],
      disabled: false,
      rank: 0
    };
  }

  formBind():void {
    UiUtil.formBind(this, 'newCarrierForm', 'formChange', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'newCarrierForm');
  }

  formChange():void {
    LogUtil.debug('ShopCarrierComponent formChange', this.newCarrierForm.valid, this.newCarrier);
    this.validForSave = this.newCarrierForm.valid;
  }

  onDataChange() {
    LogUtil.debug('ShopCarrierComponent data changed');
    this.changed = true;
  }


  formBindEdit():void {
    UiUtil.formBind(this, 'editCarrierSlaForm', 'formChangeEdit', false);
  }


  formUnbindEdit():void {
    UiUtil.formUnbind(this, 'editCarrierSlaForm');
  }


  formChangeEdit():void {
    LogUtil.debug('ProductCarrierSlaMinComponent formChangeEdit', this.editCarrierSlaForm.valid, this.editCarrierSla);
    this.validForEdit = this.editCarrierSlaForm.valid;
  }


  /**
   * Fast create new carrier.
   */
  createNew() {
    LogUtil.debug('ShopCarrierComponent createNew');
    this.validForSave = false;
    UiUtil.formInitialise(this, 'newCarrierForm', 'newCarrier', this.newCarrierInstance());
    this.editNewCarrierName.show();
  }

  /**
   * Handle result of new category modal dialog.
   * @param modalresult
   */
  editNewCarrierNameModalResult(modalresult:ModalResult) {
    LogUtil.debug('ShopCarrierComponent editNewCarrierNameModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.loading = true;
      this._shippingService.createCarrier(this.newCarrier, this.shop.shopId).subscribe(
          carVo => {
            this.validForSave = false;
            this.loading = false;
            this.onRefreshHandler();
        }
      );

    }
  }


  onSaveHandler() {
    LogUtil.debug('ShopCarrierComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.shopCarriersVO) {
      this.loading = true;
      let _sub:any = this._shippingService.saveShopCarriers(this.shopCarriersVO).subscribe(shopLanguagesVo => {
        this.changed = false;
        this._reload = false;
        this.loading = false;
        _sub.unsubscribe();
        this.onRefreshHandler();
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
      let _sub:any = this._shippingService.getShopCarriers(this.shop.shopId).subscribe(shopCarriersVo => {
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

  onSupportedCarrierSlaRankClick(event:any) {
    LogUtil.debug('ShopCarrierComponent onSupportedCarrierSlaRankClick', event);

    this.validForEdit = false;
    UiUtil.formInitialise(this, 'editCarrierSlaForm', 'editCarrierSla', Util.clone(event));
    this.editCarrierSlaRank.show();
  }


  /**
   * Handle result of new category modal dialog.
   * @param modalresult
   */
  editCarrierSlaRankModalResult(modalresult:ModalResult) {
    LogUtil.debug('ShopCarrierComponent editCarrierSlaRankModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      let _carrier = this.shopCarriersVO.find( carrier => {
        return carrier.carrierId == this.editCarrierSla.carrierId;
      });

      if (_carrier != null) {

        let _sla = _carrier.carrierSlas.find(sla => {
          return sla.carrierslaId == this.editCarrierSla.carrierslaId;
        });

        if (_sla != null) {
          _sla.rank = this.editCarrierSla.rank;
          this.remapCarriers();
          this.changed = true;
        }
      }

    }
  }



  onSupportedCarrierSlaDisableClick(event:any) {
    LogUtil.debug('ShopCarrierComponent onSupportedCarrierSlaDisableClick', event);
    event.disabled = !event.disabled;
    this.remapCarriers();
    this.changed = true;
  }

  private remapCarriers() {

    let availableCarriers:Array<ShopCarrierAndSlaVO> = [];
    let selectedCarriers:Array<Pair<ShopCarrierAndSlaVO, ShopCarrierSlaVO>> = [];

    this.shopCarriersVO.forEach(carrier => {
      if (carrier.carrierShop.disabled) {
        availableCarriers.push(carrier);
      } else {
        if (carrier.carrierSlas != null && carrier.carrierSlas.length > 0) {
          carrier.carrierSlas.forEach(sla => {
            selectedCarriers.push({ first: carrier, second: sla });
          });
        } else {
          selectedCarriers.push({ first: carrier, second: null });
        }
      }
    });

    availableCarriers.sort((a, b) => {
      return (a.name.toLowerCase() < b.name.toLowerCase()) ? -1 : 1;
    });
    selectedCarriers.sort((a, b) => {
      let aRank = a.second != null ? a.second.rank : 0;
      let bRank = b.second != null ? b.second.rank : 0;

      if (aRank == bRank) {
        let aName = a.second != null ? a.second.name : a.first.name;
        let bName = b.second != null ? b.second.name : a.first.name;
        return (aName.toLowerCase() < bName.toLowerCase()) ? -1 : 1;
      }

      return aRank < bRank ? -1 : 1;
    });

    this.selectedCarriers = selectedCarriers;
    this.availableCarriers = availableCarriers;

  }

}
