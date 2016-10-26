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
import {Component, OnInit, OnDestroy, Input, Output, ViewChild, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {FormBuilder, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../../shared/validation/validators';
import {PricingService, Util} from './../../shared/services/index';
import {PromotionVO, PromotionCouponVO} from './../../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {I18nComponent} from './../../shared/i18n/index';
import {UiUtil} from './../../shared/ui/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';
import {PromotionCouponsComponent} from './promotion-coupons.component';
import {Config} from './../../shared/config/env.config';


@Component({
  selector: 'yc-promotion',
  moduleId: module.id,
  templateUrl: 'promotion.component.html',
  directives: [NgIf, REACTIVE_FORM_DIRECTIVES, TAB_DIRECTIVES, I18nComponent, ModalComponent, PromotionCouponsComponent],
})

export class PromotionComponent implements OnInit, OnDestroy {

  _promotion:PromotionVO;
  coupons:PromotionCouponVO[] = [];
  selectedCoupon:PromotionCouponVO = null;

  couponFilter:string;
  private forceShowAll:boolean = false;
  private couponFilterRequired:boolean = true;
  private couponFilterCapped:boolean = false;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;
  filterNoCap:number = Config.UI_FILTER_NO_CAP;

  deleteValue:string = null;

  @Output() dataChanged: EventEmitter<FormValidationEvent<PromotionVO>> = new EventEmitter<FormValidationEvent<PromotionVO>>();

  changed:boolean = false;
  validForSave:boolean = false;

  validForGenerate:boolean = true;

  delayedChange:Future;

  promotionForm:any;
  promotionFormSub:any;

  couponForm:any;
  couponFormSub:any;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;


  @ViewChild('ruleHelpModalDialog')
  ruleHelpModalDialog:ModalComponent;
  selectedRuleTemplate:string;


  @ViewChild('generateCouponsModalDialog')
  generateCouponsModalDialog:ModalComponent;
  generateCoupons:PromotionCouponVO = null;

  cannotExport:boolean = true;

  constructor(private _promotionService:PricingService,
              fb: FormBuilder) {
    console.debug('PromotionComponent constructed');

    this.promotionForm = fb.group({
      'code': ['', YcValidators.requiredValidCode],
      'shopCode': ['', Validators.required],
      'currency': ['', Validators.required],
      'rank': ['', YcValidators.requiredRank],
      'promoType': ['', Validators.required],
      'promoAction': ['', Validators.required],
      'eligibilityCondition': [''],
      'promoActionContext': ['', Validators.required],
      'couponTriggered': [''],
      'canBeCombined': [''],
      'availablefrom': ['', YcValidators.validDate],
      'availableto': ['', YcValidators.validDate],
      'tag': ['', YcValidators.nonBlankTrimmed],
    });

    this.couponForm = fb.group({
      'code': ['', YcValidators.validCode],
      'usageLimit': ['', YcValidators.requiredNonZeroPositiveNumber],
      'usageLimitPerCustomer': ['', YcValidators.requiredPositiveWholeNumber],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredPromotionCoupons();
    }, this.delayedFilteringMs);

  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.promotionForm.controls) {
      this.promotionForm.controls[key]['_pristine'] = true;
      this.promotionForm.controls[key]['_touched'] = false;
    }
  }

  formResetCoupons():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.couponForm.controls) {
      this.couponForm.controls[key]['_pristine'] = true;
      this.couponForm.controls[key]['_touched'] = false;
    }
  }


  formBind():void {
    this.promotionFormSub = this.promotionForm.statusChanges.subscribe((data:any) => {
      this.validForSave = !this._promotion.enabled && this.promotionForm.valid;
      if (this.changed) {
        this.delayedChange.delay();
      }
    });
    this.couponFormSub = this.couponForm.statusChanges.subscribe((data:any) => {
      this.validForGenerate = this.couponForm.valid;
    });
  }


  formUnbind():void {
    if (this.promotionFormSub) {
      console.debug('PromotionComponent unbining form');
      this.promotionFormSub.unsubscribe();
    }
    if (this.couponFormSub) {
      console.debug('PromotionComponent unbining form');
      this.couponFormSub.unsubscribe();
    }
  }

  formChange():void {
    console.debug('PromotionComponent validating formGroup is valid: ' + this.validForSave, this._promotion);
    this.dataChanged.emit({ source: this._promotion, valid: this.validForSave });
  }

  @Input()
  set promotion(promotion:PromotionVO) {
    this._promotion = promotion;
    this.changed = false;
    this.formReset();
  }

  get promotion():PromotionVO {
    return this._promotion;
  }


  get availableto():string {
    return UiUtil.dateInputGetterProxy(this._promotion, 'enabledTo');
  }

  set availableto(availableto:string) {
    UiUtil.dateInputSetterProxy(this._promotion, 'enabledTo', availableto);
  }

  get availablefrom():string {
    return UiUtil.dateInputGetterProxy(this._promotion, 'enabledFrom');
  }

  set availablefrom(availablefrom:string) {
    UiUtil.dateInputSetterProxy(this._promotion, 'enabledFrom', availablefrom);
  }


  onMainDataChange(event:any) {
    console.debug('PromotionComponent main data changed', this._promotion);
    this.changed = true;
  }

  ngOnInit() {
    console.debug('PromotionComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('PromotionComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    console.debug('PromotionComponent tabSelected', tab);
  }

  protected onRuleHelpClick() {

    this.selectedRuleTemplate = null;
    this.ruleHelpModalDialog.show();

  }

  protected onSelectRuleTemplate(template:string) {

    this.selectedRuleTemplate = template;

  }

  protected onSelectRuleTemplateResult(modalresult: ModalResult) {
    console.debug('PromotionComponent onSelectRuleTemplate modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedRuleTemplate != null) {

        if (this.isBlank(this._promotion.eligibilityCondition)) {
          this._promotion.eligibilityCondition = this.selectedRuleTemplate;
        } else {
          this._promotion.eligibilityCondition = '(' + this._promotion.eligibilityCondition + ') && (' + this.selectedRuleTemplate + ')';
        }
        this.changed = true;
        this.formChange();
      }
    }
  }


  private isBlank(value:string):boolean {
    return value === null || value === '' || /^\s+$/.test(value);
  }


  protected onCouponSelected(data:PromotionCouponVO) {
    console.debug('PromotionComponent onCouponSelected', data);
    this.selectedCoupon = data;
  }

  protected onCouponDeleteSelected() {
    if (this.selectedCoupon != null) {
      this.deleteValue = this.selectedCoupon.code;
      this.deleteConfirmationModalDialog.show();
    }
  }



  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('PromotionComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCoupon != null) {
        console.debug('PromotionComponent onDeleteConfirmationResult', this.selectedCoupon);

        var _sub:any = this._promotionService.removePromotionCoupon(this.selectedCoupon).subscribe(res => {
          _sub.unsubscribe();
          console.debug('PromotionComponent removePromotionCoupon', this.selectedCoupon);
          this.selectedCoupon = null;
          this.getFilteredPromotionCoupons();
        });
      }
    }
  }


  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredPromotionCoupons();
  }

  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected getFilteredPromotionCoupons() {

    this.couponFilterRequired = !this.forceShowAll && (this.couponFilter == null || this.couponFilter.length < 2);

    console.debug('PromotionComponent getFilteredPromotionCoupons' + (this.forceShowAll ? ' forcefully': ''));

    if (this._promotion != null && !this.couponFilterRequired) {

      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._promotionService.getFilteredPromotionCoupons(this._promotion, this.couponFilter, max).subscribe(allcoupons => {
        console.debug('PromotionComponent getFilteredPromotionCoupons', allcoupons);
        this.coupons = allcoupons;
        this.selectedCoupon = null;
        this.couponFilterCapped = this.coupons.length >= max;
        this.cannotExport = this.coupons.length == 0;
        _sub.unsubscribe();
      });

    } else {
      this.coupons = [];
      this.selectedCoupon = null;
      this.couponFilterCapped = false;
      this.cannotExport = true;
    }

  }

  protected onCouponGenerate() {

    if (this._promotion != null) {

      this.validForGenerate = true;
      this.formResetCoupons();
      this.generateCoupons = {
        promotioncouponId: 0,
        promotionId: this._promotion.promotionId,
        code: null, usageLimit: 1, usageLimitPerCustomer: 0, usageCount: 0
      };
      this.generateCouponsModalDialog.show();

    }

  }


  protected onGenerateConfirmationResult(modalresult: ModalResult) {
    console.debug('PromotionComponent onGenerateConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.generateCoupons != null) {
        console.debug('PromotionComponent onGenerateConfirmationResult', this.generateCoupons);

        var _sub:any = this._promotionService.createPromotionCoupons(this.generateCoupons).subscribe(allcoupons => {
          _sub.unsubscribe();
          console.debug('PromotionComponent removePromotionCoupon', this.selectedCoupon);
          this.coupons = allcoupons;
          this.selectedCoupon = null;
          this.couponFilterCapped = false;
          this.cannotExport = this.coupons.length == 0;
          _sub.unsubscribe();
        });
      }
    }
  }

  protected onDownloadHandler() {

    console.debug('PromotionComponent onDownloadHandler');

    if (!this.cannotExport) {

      let myWindow = window.open('', 'ExportPromotionCouponsInfo', 'width=800,height=600');

      var _csv:string = Util.toCsv(this.coupons, true);
      myWindow.document.write('<textarea style="width:100%; height:100%">' + _csv + '</textarea>');

    }
  }


}
