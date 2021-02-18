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
import { Component, OnInit, OnDestroy, Input, Output, ViewChild, EventEmitter } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { CustomValidators } from './../../shared/validation/validators';
import { PricingService, ReportsService, I18nEventBus } from './../../shared/services/index';
import { AttributeVO, PromotionVO, PromotionCouponVO, ValidationRequestVO, BrandVO, CategoryVO, Pair, SearchResultVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { UiUtil } from './../../shared/ui/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { BrandSelectComponent, CategoryMinSelectComponent } from './../../shared/catalog/index';
import { Config } from './../../../environments/environment';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'cw-promotion',
  templateUrl: 'promotion.component.html',
})

export class PromotionComponent implements OnInit, OnDestroy {

  /* tslint:disable */
  private static TEMPLATES:any = {
    PROMOTION_CONDITION_RULE_TEMPLATE_BUY_X_QTY: "shoppingCartItem.qty >= X",
    PROMOTION_CONDITION_RULE_TEMPLATE_BUY_ONE_OF: "['X', 'Y', 'Z'].contains(shoppingCartItem.productSkuCode)",
    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_HAS_TAG: "customerTags.contains('X')",
    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_REGISTERED: "registered",
    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_FIRSTNAME: "customer?.firstname == 'X'",
    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_EMAIL_CONTAINS: "customer?.email?.contains('X.com')",
    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_TYPE_IS: "customerType == 'B2C'",
    PROMOTION_CONDITION_RULE_TEMPLATE_PRICING_POLICY_IS: "pricingPolicy.contains('X')",

    PROMOTION_CONDITION_RULE_TEMPLATE_ITEM_PRODUCT_IS_FEATURED: "product(SKU)?.featured",
    PROMOTION_CONDITION_RULE_TEMPLATE_ITEM_PRODUCT_TYPE_IS_DIGITAL: "product(SKU)?.producttype?.digital",
    PROMOTION_CONDITION_RULE_TEMPLATE_ITEM_IS_OF_BRAND: "isSKUofBrand(SKU, 'X', 'Y', 'Z')",
    PROMOTION_CONDITION_RULE_TEMPLATE_ITEM_IS_OF_CATEGORY: "isSKUinCategory(SKU, 'X', 'Y', 'Z')",
    PROMOTION_CONDITION_RULE_TEMPLATE_ITEM_PRODUCT_HAS_ATTRIBUTE: "hasProductAttribute(SKU, 'X')",
    PROMOTION_CONDITION_RULE_TEMPLATE_ITEM_PRODUCT_ATTRIBUTE_VALUE: "productAttributeValue(SKU, 'X') == 'Y'",

    PROMOTION_CONDITION_RULE_TEMPLATE_ITEM_TOTAL_MORE_THAN: "shoppingCartItemTotal.priceSubTotal > X",

    PROMOTION_CONDITION_RULE_TEMPLATE_ORDER_TOTAL_MORE_THAN: "shoppingCartOrderTotal.subTotal > X",
    PROMOTION_CONDITION_RULE_TEMPLATE_ORDER_NET_TOTAL_MORE_THAN: "(shoppingCartOrderTotal.subTotalAmount - shoppingCartOrderTotal.subTotalTax) > X",

    PROMOTION_CONDITION_RULE_TEMPLATE_CUSTOMER_TAG_FOR_COUNTRY_X: "def address = customer.getDefaultAddress('S');\naddress != null && address.countryCode == 'XX'"
  };
  /* tslint:enable */

  private static _promoTypes:Pair<AttributeVO, boolean>[] = [];

  private static _promoActions:Pair<AttributeVO, boolean>[] = [];

  private static _promoOptions:any = {};

  @Output() dataChanged: EventEmitter<FormValidationEvent<PromotionVO>> = new EventEmitter<FormValidationEvent<PromotionVO>>();

  private _promotion:PromotionVO;
  public coupons:SearchResultVO<PromotionCouponVO>;
  public selectedCoupon:PromotionCouponVO = null;

  public couponFilter:string;
  public forceShowAll:boolean = false;
  public couponFilterRequired:boolean = true;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public deleteValue:string = null;

  public validForGenerate:boolean = true;
  public delayedChange:Future;

  public promotionForm:any;

  public couponForm:any;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  @ViewChild('ruleHelpModalDialog')
  private ruleHelpModalDialog:ModalComponent;
  public selectedRuleTemplate:string;
  public selectedRuleTemplateName:string;

  @ViewChild('brandsModalDialog')
  private brandsModalDialog:BrandSelectComponent;

  @ViewChild('categorySelectComponent')
  private categorySelectComponent:CategoryMinSelectComponent;

  @ViewChild('generateCouponsModalDialog')
  private generateCouponsModalDialog:ModalComponent;
  public generateCoupons:PromotionCouponVO = null;

  public cannotExport:boolean = true;

  public loading:boolean = false;


  constructor(private _promotionService:PricingService,
              private _reportsService:ReportsService,
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

        basic = CustomValidators.validCode255(control);
        if (basic == null) {
          let req:ValidationRequestVO = {
            subject: 'promotion',
            subjectId: that._promotion.promotionId,
            field: 'code',
            value: code
          };
          return CustomValidators.validRemoteCheck(control, req);
        }
      }
      return basic;
    };


    this.promotionForm = fb.group({
      'code': ['', validCode],
      'shopCode': ['', Validators.required],
      'currency': ['', Validators.required],
      'rank': ['', CustomValidators.requiredRank],
      'promoType': ['', Validators.required],
      'promoAction': ['', Validators.required],
      'eligibilityCondition': [''],
      'promoActionContext': ['', Validators.required],
      'couponTriggered': [''],
      'canBeCombined': [''],
      'availablefrom': [''],
      'availableto': [''],
      'tag': ['', CustomValidators.nonBlankTrimmed],
      'name': [''],
      'description': [''],
    });

    this.couponForm = fb.group({
      'code': ['', CustomValidators.validCode],
      'usageLimit': ['', CustomValidators.requiredNonZeroPositiveNumber],
      'usageLimitPerCustomer': ['', CustomValidators.requiredPositiveWholeNumber],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredPromotionCoupons();
    }, this.delayedFilteringMs);

    this.coupons = this.newSearchResultInstance();
  }

  newSearchResultInstance():SearchResultVO<PromotionCouponVO> {
    return {
      searchContext: {
        parameters: {
          filter: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: null,
        sortDesc: false
      },
      items: [],
      total: 0
    };
  }


  formBind():void {
    UiUtil.formBind(this, 'promotionForm', 'delayedChange');
    UiUtil.formBind(this, 'couponForm', 'formChangeCoupons', false);
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'promotionForm');
    UiUtil.formUnbind(this, 'couponForm');
  }

  formChange():void {
    LogUtil.debug('PromotionComponent formChange', this.promotionForm.valid, this._promotion);
    this.dataChanged.emit({ source: this._promotion, valid: this.promotionForm.valid });
  }

  onActionTypeChange():void {
    let actionAvailable = this.isAvailable(this._promotion.promoType, this._promotion.promoAction);
    if (actionAvailable) {
      this.promotionForm.controls['promoAction'].setErrors(null);
    } else {
      this.promotionForm.controls['promoAction'].setErrors({ 'invalidValue': true });
    }
    LogUtil.debug('PromotionComponent onActionTypeChange', this.promotionForm.valid, this._promotion, this.promoOptions);
  }

  formChangeCoupons():void {
    LogUtil.debug('PromotionComponent formChangeCoupons', this.couponForm.valid, this.couponForm.value);
    this.validForGenerate = this.couponForm.valid;
  }

  @Input()
  set promotion(promotion:PromotionVO) {

    this.coupons = this.newSearchResultInstance();

    UiUtil.formInitialise(this, 'promotionForm', '_promotion', promotion, promotion != null && promotion.promotionId > 0,
      ['code', 'promoType', 'promoAction', ]);

  }

  get promotion():PromotionVO {
    return this._promotion;
  }


  onAvailableFrom(event:FormValidationEvent<any>) {
    if (event.valid) {
      this.promotion.enabledFrom = event.source;
    }
    UiUtil.formDataChange(this, 'promotionForm', 'availablefrom', event);
  }

  onAvailableTo(event:FormValidationEvent<any>) {
    if (event.valid) {
      this.promotion.enabledTo = event.source;
    }
    UiUtil.formDataChange(this, 'promotionForm', 'availableto', event);
  }


  onNameDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'promotionForm', 'name', event);
  }

  onDescriptionDataChange(event:FormValidationEvent<any>) {
    UiUtil.formI18nDataChange(this, 'promotionForm', 'description', event);
  }

  @Input()
  set promoTypes(value:Pair<AttributeVO, boolean>[]) {
    PromotionComponent._promoTypes = value;
  }

  get promoTypes():Pair<AttributeVO, boolean>[] {
    return PromotionComponent._promoTypes;
  }

  @Input()
  set promoActions(value:Pair<AttributeVO, boolean>[]) {
    PromotionComponent._promoActions = value;
  }

  get promoActions():Pair<AttributeVO, boolean>[] {
    return PromotionComponent._promoActions;
  }

  @Input()
  set promoOptions(value: any) {
    PromotionComponent._promoOptions = value;
  }

  get promoOptions(): any {
    return PromotionComponent._promoOptions;
  }



  getAttributeName(attr:AttributeVO):string {

    let lang = I18nEventBus.getI18nEventBus().current();
    let i18n = attr.displayNames;
    let def = attr.name != null ? attr.name : attr.code;

    return UiUtil.toI18nString(i18n, def, lang);

  }

  isAvailable(type:string, action:string):boolean {
    return this.promoOptions.hasOwnProperty(type) && this.promoOptions[type].indexOf(action) != -1;
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

  onRuleHelpClick() {

    this.selectedRuleTemplate = null;
    this.selectedRuleTemplateName = null;
    this.ruleHelpModalDialog.show();

  }

  onSelectRuleTemplate(template:string) {

    if (PromotionComponent.TEMPLATES.hasOwnProperty(template)) {
      this.selectedRuleTemplate = PromotionComponent.TEMPLATES[template];
      this.selectedRuleTemplateName = template;
    }

  }

  onSelectRuleTemplateResult(modalresult: ModalResult) {
    LogUtil.debug('PromotionComponent onSelectRuleTemplate modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedRuleTemplate != null && this._promotion != null) {

        if (this.isBlank(this._promotion.eligibilityCondition)) {
          this._promotion.eligibilityCondition = this.selectedRuleTemplate;
        } else {
          this._promotion.eligibilityCondition = '(' + this._promotion.eligibilityCondition + ') && (' + this.selectedRuleTemplate + ')';
        }
        this.formChange();
      }
    }
  }


  onCategoryClick() {

    this.categorySelectComponent.showDialog(0);

  }


  onCatalogTreeDataSelected(event:FormValidationEvent<CategoryVO>) {
    LogUtil.debug('PromotionComponent onCatalogTreeDataSelected handler', event);
    if (event.valid) {
      if (this._promotion != null && !this._promotion.enabled) {

        if (this.isBlank(this._promotion.eligibilityCondition)) {
          this._promotion.eligibilityCondition = '\n\'' + event.source.guid + '\'';
        } else {
          this._promotion.eligibilityCondition = this._promotion.eligibilityCondition + '\n\'' + event.source.guid + '\'';
        }
        this.formChange();
      }
    }
  }


  onBrandClick() {

    this.brandsModalDialog.showDialog();

  }

  onBrandSelected(event:FormValidationEvent<BrandVO>) {
    LogUtil.debug('PromotionComponent onBrandSelected', event);
    if (event.valid) {
      if (this._promotion != null && !this._promotion.enabled) {

        if (this.isBlank(this._promotion.eligibilityCondition)) {
          this._promotion.eligibilityCondition = '\n\'' + event.source.name + '\'';
        } else {
          this._promotion.eligibilityCondition = this._promotion.eligibilityCondition + '\n\'' + event.source.name + '\'';
        }
        this.formChange();
      }
    }
  }


  onCouponSelected(data:PromotionCouponVO) {
    LogUtil.debug('PromotionComponent onCouponSelected', data);
    this.selectedCoupon = data;
  }

  onCouponDeleteSelected() {
    if (this.selectedCoupon != null) {
      this.deleteValue = this.selectedCoupon.code;
      this.deleteConfirmationModalDialog.show();
    }
  }



  onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('PromotionComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCoupon != null) {
        LogUtil.debug('PromotionComponent onDeleteConfirmationResult', this.selectedCoupon);

        this._promotionService.removePromotionCoupon(this.selectedCoupon).subscribe(res => {
          LogUtil.debug('PromotionComponent removePromotionCoupon', this.selectedCoupon);
          this.selectedCoupon = null;
          this.getFilteredPromotionCoupons();
        });
      }
    }
  }


  onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredPromotionCoupons();
  }

  onFilterChange(event:any) {
    this.coupons.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFiltering.delay();
  }

  onRefreshHandler() {

    this.delayedFiltering.delay();

  }

  onPageSelected(page:number) {
    LogUtil.debug('PromotionComponent onPageSelected', page);
    this.coupons.searchContext.start = page;
    this.delayedFiltering.delay();
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('PromotionComponent ononSortSelected', sort);
    if (sort == null) {
      this.coupons.searchContext.sortBy = null;
      this.coupons.searchContext.sortDesc = false;
    } else {
      this.coupons.searchContext.sortBy = sort.first;
      this.coupons.searchContext.sortDesc = sort.second;
    }
    this.delayedFiltering.delay();
  }

  onCouponGenerate() {

    if (this._promotion != null) {

      this.validForGenerate = true;

      let couponConfig:PromotionCouponVO = {
        promotioncouponId: 0,
        promotionId: this._promotion.promotionId,
        code: null, usageLimit: 1, usageLimitPerCustomer: 0, usageCount: 0
      };

      UiUtil.formInitialise(this, 'couponForm', 'generateCoupons', couponConfig);

      this.generateCouponsModalDialog.show();

    }

  }


  onGenerateConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('PromotionComponent onGenerateConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.generateCoupons != null) {
        LogUtil.debug('PromotionComponent onGenerateConfirmationResult', this.generateCoupons);

        this._promotionService.createPromotionCoupons(this.generateCoupons).subscribe(allcoupons => {
          LogUtil.debug('PromotionComponent removePromotionCoupon', this.selectedCoupon);
          this.selectedCoupon = null;
          this.delayedFiltering.delay();
        });
      }
    }
  }

  onDownloadHandler() {

    LogUtil.debug('PromotionComponent onDownloadHandler');

    if (!this.cannotExport) {

      this._reportsService.downloadReportObject(this.coupons.items, this.promotion.code + '-coupons.csv').subscribe(res => {
        LogUtil.debug('PromotionComponent onDownloadHandler', res);
      });

    }
  }


  onClearFilter() {

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

      this.coupons.searchContext.parameters.filter = [ this.couponFilter ];
      this.coupons.searchContext.parameters.promotionId = [ this._promotion.promotionId ];
      this.coupons.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

      this._promotionService.getFilteredPromotionCoupons(this.coupons.searchContext).subscribe(allcoupons => {
        LogUtil.debug('PromotionComponent getFilteredPromotionCoupons', allcoupons);
        this.coupons = allcoupons;
        this.selectedCoupon = null;
        this.cannotExport = this.coupons == null || this.coupons.total == 0;
        this.loading = false;
      });
    } else {
      this.coupons = this.newSearchResultInstance();
      this.selectedCoupon = null;
      this.cannotExport = true;
    }
  }

}
