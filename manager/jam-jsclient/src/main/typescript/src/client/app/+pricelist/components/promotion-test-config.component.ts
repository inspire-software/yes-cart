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
import { Component, OnInit, OnDestroy, Output, EventEmitter, ViewChild } from '@angular/core';
import { I18nEventBus } from './../../shared/services/index';
import {FormBuilder, Validators} from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { PromotionTestVO, ProductSkuVO, CarrierSlaVO, FulfilmentCentreInfoVO } from './../../shared/model/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { ProductSkuSelectComponent } from './../../shared/catalog/index';
import { CarrierSlaSelectComponent } from './../../shared/shipping/index';
import { FulfilmentCentreSelectComponent } from './../../shared/fulfilment/index';
import { FormValidationEvent } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-promotion-test-config',
  moduleId: module.id,
  templateUrl: 'promotion-test-config.component.html',
})

export class PromotionTestConfigComponent implements OnInit, OnDestroy {

  @Output() dataConfigured: EventEmitter<PromotionTestVO> = new EventEmitter<PromotionTestVO>();

  private testConfig:PromotionTestVO;

  @ViewChild('testConfigModalDialog')
  private testConfigModalDialog:ModalComponent;

  @ViewChild('selectCentreModalDialog')
  private selectCentreModalDialog:FulfilmentCentreSelectComponent;

  @ViewChild('productSkuSelectDialog')
  private productSkuSelectDialog:ProductSkuSelectComponent;

  @ViewChild('carrierSlaSelectDialog')
  private carrierSlaSelectDialog:CarrierSlaSelectComponent;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private testRulesForm:any;
  private testRulesFormSub:any; // tslint:disable-line:no-unused-variable
  private validForTest:boolean = false;

  constructor(fb: FormBuilder) {
    LogUtil.debug('PromotionTestConfigComponent constructed');
    this.testConfig = { supplier: null, customer: null, shipping: null, coupons: null, sku: null, language: null, time: null };
    this.testRulesForm = fb.group({
      'testSupplier': ['', Validators.required],
      'testCustomer': [''],
      'testShipping': [''],
      'testCoupons': [''],
      'testSku': ['', Validators.required],
      'testTime': ['', YcValidators.validDate],
    });
  }

  ngOnInit() {
    LogUtil.debug('PromotionTestConfigComponent ngOnInit');
    this.formBind();
    UiUtil.formInitialise(this, 'initialising', 'testRulesForm', 'testSku', '');
  }

  ngOnDestroy() {
    LogUtil.debug('PromotionTestConfigComponent ngOnDestroy');
    this.formUnbind();
  }

  showDialog() {
    this.testConfigModalDialog.show();
  }

  formBind():void {
    UiUtil.formBind(this, 'testRulesForm', 'testRulesFormSub', 'formChange', 'initialising', false);
  }

  formUnbind():void {
    UiUtil.formUnbind(this, 'testRulesFormSub');
  }

  formChange():void {
    LogUtil.debug('PromotionTestConfigComponent formChange', this.testRulesForm.valid, this.testConfig);
    this.validForTest = this.testRulesForm.valid;
  }


  get testTime():string {
    return UiUtil.dateInputGetterProxy(this.testConfig, 'time');
  }

  set testTime(testTime:string) {
    UiUtil.dateInputSetterProxy(this.testConfig, 'time', testTime);
  }


  protected onSearchSKU() {
    this.productSkuSelectDialog.showDialog();
  }

  protected onProductSkuSelected(event:FormValidationEvent<ProductSkuVO>) {
    LogUtil.debug('ShopPriceRulesComponent onProductSkuSelected');
    if (event.valid) {
      if (this.testConfig.sku) {
        this.testConfig.sku += ',' + event.source.code + '=1';
      } else {
        this.testConfig.sku = event.source.code + '=1';
      }
    }
  }

  protected onSearchSLA() {
    this.carrierSlaSelectDialog.showDialog();
  }


  protected onCarrierSlaSelected(event:FormValidationEvent<CarrierSlaVO>) {
    LogUtil.debug('PromotionTestConfigComponent onCarrierSlaSelected');
    if (event.valid) {
      this.testConfig.shipping = event.source.code;
      this.formChange();
    }
  }

  protected onSearchFC() {
    this.selectCentreModalDialog.showDialog();
  }

  protected onFulfilmentCentreSelected(event:FormValidationEvent<FulfilmentCentreInfoVO>) {
    LogUtil.debug('CentreInventoryComponent onFulfilmentCentreSelected');
    if (event.valid) {
      this.testConfig.supplier = event.source.code;
      this.formChange();
    }
  }

  onRunTestResult(modalresult:ModalResult) {
    LogUtil.debug('PromotionTestConfigComponent onRunTestResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this.testConfig.language = I18nEventBus.getI18nEventBus().current();
      this.dataConfigured.emit(this.testConfig);
    }
  }


}
