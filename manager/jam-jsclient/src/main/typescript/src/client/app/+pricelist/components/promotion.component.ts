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
import { Component, OnInit, OnDestroy, Input, Output, ViewChild, EventEmitter } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { PricingService, Util } from './../../shared/services/index';
import { PromotionVO, PromotionCouponVO, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-promotion',
  moduleId: module.id,
  templateUrl: 'promotion.component.html',
})

export class PromotionComponent implements OnInit, OnDestroy {

  /* tslint:disable */
  private static TEMPLATES:any = {
    PROMOTION_CONDITION_RULE_TEMPLATE_BUY_X_QTY: "shoppingCartItem.qty >= X",
    PROMOTION_CONDITION_RULE_TEMPLATE_BUY_ONE_OF: "['X', 'Y', 'Z'].contains(shoppingCartItem.productSkuCode)",
    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_HAS_TAG: "customerTags.contains('X')",
    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_REGISTERED: "registered",
    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_FIRSTNAME: "customer.firstname == 'X'",
    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_EMAIL_CONTAINS: "customer.email?.contains('X.com')",

    PROMOTION_CONDITION_RULE_TEMPLATE_ITEM_TOTAL_MORE_THAN: "shoppingCartItemTotal.priceSubTotal > X",

    PROMOTION_CONDITION_RULE_TEMPLATE_ORDER_TOTAL_MORE_THAN: "shoppingCartOrderTotal.subTotal > X",

    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_TAG_FOR_COUNTRY_X: "def address = customer.getDefaultAddress('S');\naddress != null && address.countryCode == 'XX'"
  };
  /* tslint:enable */

  @Output() dataChanged: EventEmitter<FormValidationEvent<PromotionVO>> = new EventEmitter<FormValidationEvent<PromotionVO>>();

  private _promotion:PromotionVO;
  private coupons:PromotionCouponVO[] = [];
  private selectedCoupon:PromotionCouponVO = null;

  private couponFilter:string;
  private forceShowAll:boolean = false;
  private couponFilterRequired:boolean = true;
  private couponFilterCapped:boolean = false;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;
  private filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private deleteValue:string = null;

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private initialising2:boolean = false; // tslint:disable-line:no-unused-variable

  private validForGenerate:boolean = true;
  private delayedChange:Future;

  private promotionForm:any;
  private promotionFormSub:any; // tslint:disable-line:no-unused-variable

  private couponForm:any;
  private couponFormSub:any; // tslint:disable-line:no-unused-variable

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('ruleHelpModalDialog')
  private ruleHelpModalDialog:ModalComponent;
  private selectedRuleTemplate:string;
  private selectedRuleTemplateName:string;

  @ViewChild('generateCouponsModalDialog')
  private generateCouponsModalDialog:ModalComponent;
  private generateCoupons:PromotionCouponVO = null;

  private cannotExport:boolean = true;

  private loading:boolean = false;

  constructor(private _promotionService:PricingService,
              fb: FormBuilder) {
    LogUtil.debug('PromotionComponent constructed');

    let that = this;
    let validCode = function(control:any):any {

      let basic = Validators.required(control);
      if (basic == null) {

        let code = control.value;
        if (that._promotion == null || !that.promotionForm || (!that.promotionForm.dirty && that._promotion.promotionId > 0)) {
          return null;
        }

        basic = YcValidators.validCode(control);
        if (basic == null) {
          var req:ValidationRequestVO = {
            subject: 'promotion',
            subjectId: that._promotion.promotionId,
            field: 'code',
            value: code
          };
          return YcValidators.validRemoteCheck(control, req);
        }
      }
      return basic;
    };


    this.promotionForm = fb.group({
      'code': ['', validCode],
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
      'name': [''],
      'description': [''],
    });

    this.couponForm = fb.group({
      'code': ['', YcValidators.validCode],
      'usageLimit': ['', YcValidators.requiredNonZeroPositiveNumber],
      'usageLimitPerCustomer': ['', YcValidators.requiredPositiveWholeNumber],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredPromotionCoupons();
    }, this.delayedFilteringMs);

  }

  formBind():void {
    UiUtil.formBind(this, 'promotionForm', 'promotionFormSub', 'delayedChange', 'initialising');
    UiUtil.formBind(this, 'couponForm', 'couponFormSub', 'formChangeCoupons', 'initialising2', false);
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'promotionFormSub');
    UiUtil.formUnbind(this, 'couponFormSub');
  }

  formChange():void {
    LogUtil.debug('PromotionComponent formChange', this.promotionForm.valid, this._promotion);
    this.dataChanged.emit({ source: this._promotion, valid: !this._promotion.enabled && this.promotionForm.valid });
  }

  formChangeCoupons():void {
    LogUtil.debug('PromotionComponent formChangeCoupons', this.couponForm.valid, this.couponForm.value);
    this.validForGenerate = this.couponForm.valid;
  }

  @Input()
  set promotion(promotion:PromotionVO) {

    UiUtil.formInitialise(this, 'initialising', 'promotionForm', '_promotion', promotion, promotion != null && promotion.promotionId > 0, ['code', 'promoType', 'promoAction', ]);

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

  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'promotionForm', 'name', event);
  }

  onDescriptionDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'promotionForm', 'description', event);
  }


  ngOnInit() {
    LogUtil.debug('PromotionComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('PromotionComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('PromotionComponent tabSelected', tab);
  }

  protected onRuleHelpClick() {

    this.selectedRuleTemplate = null;
    this.selectedRuleTemplateName = null;
    this.ruleHelpModalDialog.show();

  }

  protected onSelectRuleTemplate(template:string) {

    if (PromotionComponent.TEMPLATES.hasOwnProperty(template)) {
      this.selectedRuleTemplate = PromotionComponent.TEMPLATES[template];
      this.selectedRuleTemplateName = template;
    }

  }

  protected onSelectRuleTemplateResult(modalresult: ModalResult) {
    LogUtil.debug('PromotionComponent onSelectRuleTemplate modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedRuleTemplate != null) {

        if (this.isBlank(this._promotion.eligibilityCondition)) {
          this._promotion.eligibilityCondition = this.selectedRuleTemplate;
        } else {
          this._promotion.eligibilityCondition = '(' + this._promotion.eligibilityCondition + ') && (' + this.selectedRuleTemplate + ')';
        }
        this.formChange();
      }
    }
  }


  protected onCouponSelected(data:PromotionCouponVO) {
    LogUtil.debug('PromotionComponent onCouponSelected', data);
    this.selectedCoupon = data;
  }

  protected onCouponDeleteSelected() {
    if (this.selectedCoupon != null) {
      this.deleteValue = this.selectedCoupon.code;
      this.deleteConfirmationModalDialog.show();
    }
  }



  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('PromotionComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCoupon != null) {
        LogUtil.debug('PromotionComponent onDeleteConfirmationResult', this.selectedCoupon);

        var _sub:any = this._promotionService.removePromotionCoupon(this.selectedCoupon).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('PromotionComponent removePromotionCoupon', this.selectedCoupon);
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

  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onCouponGenerate() {

    if (this._promotion != null) {

      this.validForGenerate = true;

      let couponConfig:PromotionCouponVO = {
        promotioncouponId: 0,
        promotionId: this._promotion.promotionId,
        code: null, usageLimit: 1, usageLimitPerCustomer: 0, usageCount: 0
      };

      UiUtil.formInitialise(this, 'initialising2', 'couponForm', 'generateCoupons', couponConfig);

      this.generateCouponsModalDialog.show();

    }

  }


  protected onGenerateConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('PromotionComponent onGenerateConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.generateCoupons != null) {
        LogUtil.debug('PromotionComponent onGenerateConfirmationResult', this.generateCoupons);

        var _sub:any = this._promotionService.createPromotionCoupons(this.generateCoupons).subscribe(allcoupons => {
          _sub.unsubscribe();
          LogUtil.debug('PromotionComponent removePromotionCoupon', this.selectedCoupon);
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

    LogUtil.debug('PromotionComponent onDownloadHandler');

    if (!this.cannotExport) {

      let myWindow = window.open('', 'ExportPromotionCouponsInfo', 'width=800,height=600');

      var _csv:string = Util.toCsv(this.coupons, true);
      myWindow.document.write('<textarea style="width:100%; height:100%">' + _csv + '</textarea>');

    }
  }


  protected onClearFilter() {

    this.couponFilter = '';
    this.delayedFiltering.delay();

  }

  private isBlank(value:string):boolean {
    return value === null || value === '' || /^\s+$/.test(value);
  }


  private getFilteredPromotionCoupons() {

    this.couponFilterRequired = !this.forceShowAll && (this.couponFilter == null || this.couponFilter.length < 2);

    LogUtil.debug('PromotionComponent getFilteredPromotionCoupons' + (this.forceShowAll ? ' forcefully': ''));

    if (this._promotion != null && !this.couponFilterRequired) {

      this.loading = true;
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._promotionService.getFilteredPromotionCoupons(this._promotion, this.couponFilter, max).subscribe(allcoupons => {
        LogUtil.debug('PromotionComponent getFilteredPromotionCoupons', allcoupons);
        this.coupons = allcoupons;
        this.selectedCoupon = null;
        this.couponFilterCapped = this.coupons.length >= max;
        this.cannotExport = this.coupons.length == 0;
        this.loading = false;
        _sub.unsubscribe();
      });

    } else {
      this.coupons = [];
      this.selectedCoupon = null;
      this.couponFilterCapped = false;
      this.cannotExport = true;
    }

  }

}
