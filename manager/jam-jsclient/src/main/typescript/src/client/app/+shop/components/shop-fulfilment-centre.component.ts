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
import {Component, OnInit, OnDestroy, OnChanges, Input, ViewChild} from '@angular/core';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {NgIf, NgFor} from '@angular/common';
import {ShopVO, ShopFulfilmentCentreVO, FulfilmentCentreInfoVO} from './../../shared/model/index';
import {FulfilmentService, ShopEventBus, Util} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';

@Component({
  selector: 'yc-shop-fulfilment-centre',
  moduleId: module.id,
  templateUrl: './shop-fulfilment-centre.component.html',
  directives: [ NgIf, NgFor, DataControlComponent, REACTIVE_FORM_DIRECTIVES, ModalComponent],
})

export class ShopFulfilmentCentreComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  shopCentresVO:Array<ShopFulfilmentCentreVO>;
  availableCentres:Array<ShopFulfilmentCentreVO>;
  selectedCentres:Array<ShopFulfilmentCentreVO>;

  changed:boolean = false;

  newCentre:FulfilmentCentreInfoVO;
  @ViewChild('editNewCentreName')
  editNewCentreName:ModalComponent;
  newCentreForm:any;
  newCentreFormSub:any;
  changedSingle:boolean = true;
  validForSave:boolean = false;

  constructor(private _fulfilmentService:FulfilmentService,
              fb: FormBuilder) {
    console.debug('ShopFulfilmentCentreComponent constructor');

    this.newCentre = this.newCentreInstance();

    this.newCentreForm = fb.group({
      'code': ['', Validators.compose([Validators.required, Validators.pattern('[A-Za-z0-9]+')])],
      'name': ['', Validators.compose([Validators.required, Validators.pattern('\\S+.*\\S+')])],
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
    console.debug('ShopFulfilmentCentreComponent ngOnInit shop', this.shop);
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ShopFulfilmentCentreComponent ngOnDestroy');
    this.formUnbind();
  }

  newCentreInstance():FulfilmentCentreInfoVO {
    return { warehouseId: 0, code: '', name: '', description: null, countryCode: null, stateCode: null, city: null, postcode: null };
  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.newCentreForm.controls) {
      this.newCentreForm.controls[key]['_pristine'] = true;
      this.newCentreForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.newCentreFormSub = this.newCentreForm.valueChanges.subscribe((data:any) => {
      if (this.changedSingle) {
        this.validForSave = this.newCentreForm.valid;
      }
    });
  }

  formUnbind():void {
    if (this.newCentreFormSub) {
      console.debug('ShopFulfilmentCentreComponent unbining form');
      this.newCentreFormSub.unsubscribe();
    }
  }


  onFormDataChange(event:any) {
    console.debug('ShopFulfilmentCentreComponent data changed', event);
    this.changedSingle = true;
  }

  onDataChange() {
    console.debug('ShopFulfilmentCentreComponent data changed');
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
  /**
   * Fast create new category.
   * @param parent parent of new catecory
   */
  createNew() {
    console.debug('ShopFulfilmentCentreComponent createNew');
    this.changedSingle = false;
    this.validForSave = false;
    this.newCentre = this.newCentreInstance();
    this.formReset();
    this.editNewCentreName.show();
  }

  /**
   * Handle result of new category modal dialog.
   * @param modalresult
   */
  editNewCentreNameModalResult(modalresult:ModalResult) {
    console.debug('ShopFulfilmentCentreComponent editNewCentreNameModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this._fulfilmentService.createFulfilmentCentre(this.newCentre, this.shop.shopId).subscribe(
          carVo => {
          this.onRefreshHandler();
        }
      );

    }
  }


  onSaveHandler() {
    console.debug('ShopFulfilmentCentreComponent Save handler', this.shop);
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
    console.debug('ShopFulfilmentCentreComponent discard handler', this.shop);
    this.onRefreshHandler();
  }

  onRefreshHandler() {
    console.debug('ShopFulfilmentCentreComponent refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      var _sub:any = this._fulfilmentService.getShopFulfilmentCentres(this.shop.shopId).subscribe(shopCentresVo => {
        console.debug('ShopFulfilmentCentreComponent getShopCentres', shopCentresVo);
        this.shopCentresVO  = Util.clone(shopCentresVo);
        this.remapCentres();
        this.changed = false;
        this._reload = false;
        _sub.unsubscribe();
      });
    } else {
      this.shopCentresVO = null;
    }
  }

  onAvailableCentreClick(event:any) {
    console.debug('ShopFulfilmentCentreComponent onAvailableCentreClick', event);
    event.fulfilmentShop.disabled = false;
    this.remapCentres();
    this.changed = true;
  }

  onSupportedCentreClick(event:any) {
    console.debug('ShopFulfilmentCentreComponent onSupportedCentreClick', event);
    event.fulfilmentShop.disabled = true;
    this.remapCentres();
    this.changed = true;
  }

}
