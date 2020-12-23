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
import { FormBuilder } from '@angular/forms';
import { CustomerService, I18nEventBus } from './../../shared/services/index';
import { CustomValidators } from './../../shared/validation/validators';
import { ShopVO, CustomerVO, CustomerShopLinkVO, AttrValueCustomerVO, Pair, ValidationRequestVO } from './../../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../../shared/event/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'cw-customer',
  templateUrl: 'customer.component.html',
})

export class CustomerComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<FormValidationEvent<Pair<CustomerVO, Array<Pair<AttrValueCustomerVO, boolean>>>>> = new EventEmitter<FormValidationEvent<Pair<CustomerVO, Array<Pair<AttrValueCustomerVO, boolean>>>>>();

  @Output() passwordReset: EventEmitter<Pair<CustomerVO, ShopVO>> = new EventEmitter<Pair<CustomerVO, ShopVO>>();

  private _customer:CustomerVO;
  private _attributes:AttrValueCustomerVO[] = [];
  public attributeFilter:string;
  public attributeSort:Pair<string, boolean> = { first: 'name', second: false };

  public addressShops:ShopVO[] = [];
  private _shops:any = {};
  public resetShopName:string = null;
  public resetShop:ShopVO = null;

  public availableShops:Array<Pair<ShopVO, CustomerShopLinkVO>> = [];
  public supportedShops:Array<Pair<ShopVO, CustomerShopLinkVO>> = [];

  private _changes:Array<Pair<AttrValueCustomerVO, boolean>>;

  public selectedRow:AttrValueCustomerVO;

  public imageOnlyMode:boolean = false;

  public delayedChange:Future;

  public customerForm:any;

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  @ViewChild('resetConfirmationModalDialog')
  private resetConfirmationModalDialog:ModalComponent;

  @ViewChild('typeHelpModalDialog')
  private typeHelpModalDialog:ModalComponent;

  public availableCustomerTypes:Pair<string, string>[] = null;
  public selectedCustomerType:string = null;

  public searchHelpShow:boolean = false;

  public reloadAddressbook:boolean = false;

  public mustSelectShop:boolean = false;

  constructor(private _customerService:CustomerService,
              fb: FormBuilder) {
    LogUtil.debug('CustomerComponent constructed');

    let that = this;

    let validLoginInput = function(control:any):any {

      let basic = CustomValidators.validLoginLoose(control);
      if (basic == null) {

        let login = control.value;
        if (that._customer == null || !that.customerForm || (!that.customerForm.dirty && that._customer.customerId > 0)) {
          return null;
        }

        let shopCodes = '';
        that.supportedShops.forEach(item => {
          shopCodes += item.first.code + ',';
        });

        let req:ValidationRequestVO = { subject: 'customer', subjectId: that._customer.customerId, field: 'login', value: login, context: { shopCodes: shopCodes } };
        return CustomValidators.validRemoteCheck(control, req);
      }
      return basic;
    };

    this.customerForm = fb.group({
      'code': [''],
      'login': ['', validLoginInput],
      'email': ['', CustomValidators.validEmail],
      'phone': ['', CustomValidators.nonBlankTrimmed25],
      'salutation': ['', CustomValidators.nonBlankTrimmed128],
      'firstname': ['', CustomValidators.requiredNonBlankTrimmed128],
      'lastname': ['', CustomValidators.requiredNonBlankTrimmed128],
      'middlename': ['', CustomValidators.nonBlankTrimmed128],
      'tag': ['', CustomValidators.nonBlankTrimmed255],
      'customerType': ['', CustomValidators.validCode255],
      'pricingPolicy': ['', CustomValidators.validCode255],
      'companyName1': ['', CustomValidators.nonBlankTrimmed255],
      'companyName2': ['', CustomValidators.nonBlankTrimmed255],
      'companyDepartment': ['', CustomValidators.nonBlankTrimmed255],
    });

    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

  }


  formBind():void {
    UiUtil.formBind(this, 'customerForm', 'delayedChange');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'customerForm');
  }

  formChange():void {
    LogUtil.debug('CustomerComponent formChange', this.customerForm.valid, this._customer);
    this.dataChanged.emit({ source: new Pair(this._customer, this._changes), valid: this.customerForm.valid && !this.mustSelectShop });
  }

  @Input()
  set customer(customer:CustomerVO) {

    UiUtil.formInitialise(this, 'customerForm', '_customer', customer, customer != null && customer.customerId > 0, []);
    this._changes = [];
    this.tabSelected('Main');
    this.recalculateShops();

  }

  get customer():CustomerVO {
    return this._customer;
  }

  @Input()
  set attributes(attributes:AttrValueCustomerVO[]) {
    this._attributes = attributes;
  }

  get attributes():AttrValueCustomerVO[] {
    return this._attributes;
  }


  @Input()
  set shops(shops:Array<ShopVO>) {
    if (shops != null) {
      shops.forEach(shop => {
        this._shops['S' + shop.shopId] = shop;
      });
    }
    LogUtil.debug('CustomerComponent mapped shops', this._shops);
  }

  onAttributeDataChanged(event:any) {
    LogUtil.debug('CustomerComponent attr data changed', this._customer);
    this._changes = event.source;
    this.delayedChange.delay();
  }

  ngOnInit() {
    LogUtil.debug('CustomerComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('CustomerComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('CustomerComponent tabSelected', tab);
    this.reloadAddressbook = tab === 'Addressbook';
  }

  onImageOnlyMode() {
    this.imageOnlyMode = !this.imageOnlyMode;
  }

  onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowDeleteSelected();
    }
  }

  onRowEditSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowEditSelected();
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('CustomerComponent onPageSelected', page);
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('CustomerComponent ononSortSelected', sort);
    if (sort == null) {
      this.attributeSort = { first: 'name', second: false };
    } else {
      this.attributeSort = sort;
    }
  }

  onSelectRow(row:AttrValueCustomerVO) {
    LogUtil.debug('CustomerComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  onSupportedShopClick(supported:Pair<ShopVO, CustomerShopLinkVO>) {
    LogUtil.debug('CustomerComponent remove supported', supported);
    let idx = this._customer.customerShops.findIndex(link =>
      link.shopId == supported.first.shopId
    );
    if (idx != -1) {
      this._customer.customerShops[idx].disabled = true;
      this.recalculateShops();
      this.formChange();
    }
  }

  onAvailableShopClick(available:Pair<ShopVO, CustomerShopLinkVO>) {
    LogUtil.debug('CustomerComponent add supported', available);
    let idx = this._customer.customerShops.findIndex(link =>
      link.shopId == available.first.shopId
    );
    available.second.disabled = false;
    if (idx != -1) {
      this._customer.customerShops[idx].disabled = false;
    } else {
      this._customer.customerShops.push(available.second);
    }
    this.recalculateShops();
    this.formChange();
  }

  onRowReset(shop:ShopVO) {
    this.resetShopName = shop.code + ' ' + shop.name;
    this.resetShop = shop;
    this.resetConfirmationModalDialog.show();
  }

  onResetConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CustomerComponent onResetConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      this.passwordReset.emit(new Pair(this._customer, this.resetShop));

    }
  }


  onClearFilter() {

    this.attributeFilter = '';

  }

  onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  onSearchValues() {
    this.searchHelpShow = false;
    this.attributeFilter = '###';
  }

  onSearchValuesNew() {
    this.searchHelpShow = false;
    this.attributeFilter = '##0';
  }

  onSearchValuesNewOnly() {
    this.searchHelpShow = false;
    this.attributeFilter = '#00';
  }

  onSearchValuesChanges() {
    this.searchHelpShow = false;
    this.attributeFilter = '#0#';
  }

  onTypeHelpClick() {

    if (this._customer != null) {
      let lang = I18nEventBus.getI18nEventBus().current();
      this._customerService.getCustomerTypes(this._customer.customerId, lang).subscribe((types: Pair<string, string>[]) => {
        LogUtil.debug('CustomerComponent available types', types);
        this.availableCustomerTypes = types;

        this.typeHelpModalDialog.show();
      });
    }

  }

  onSelectCustomerType(customerType:Pair<string, string> ) {
    this.selectedCustomerType = customerType.first;
  }

  onSelectCustomerTypeResult(modalresult: ModalResult) {
    LogUtil.debug('CustomerComponent onSelectCustomerTypeResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this._customer.customerType = this.selectedCustomerType;
      this.selectedCustomerType = null;
      this.formChange();
    }
  }

  private recalculateShops():void {

    if (this._customer) {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = this.getSupportedShopNames();
    } else {
      this.availableShops = this.getAvailableShopNames();
      this.supportedShops = [];
    }

    let addressShops:ShopVO[] = [];
    this.supportedShops.forEach(item => {
      addressShops.push(item.first);
    });
    this.addressShops = addressShops;

    this.mustSelectShop = this._customer != null && !(this._customer.customerId > 0) && (this._customer.customerShops == null || this._customer.customerShops.length == 0);
  }

  private getAvailableShopNames():Array<Pair<ShopVO, CustomerShopLinkVO>> {

    let supported = this._customer != null ? this._customer.customerShops : [];
    let disabledShops = <Array<number>>[];
    let skipKeys = <Array<string>>[];
    let keepObjs:any = {};
    if (supported) {
      supported.forEach(customershop => {
        if (!customershop.disabled) {
          skipKeys.push('S' + customershop.shopId);
        } else {
          disabledShops.push(customershop.shopId);
        }
        keepObjs['S' + customershop.shopId] = customershop;
      });
    }
    LogUtil.debug('CustomerComponent supported shops', skipKeys);

    let labels = <Array<Pair<ShopVO, CustomerShopLinkVO>>>[];
    for (let key in this._shops) {
      if (skipKeys.indexOf(key) == -1) {
        let shop = this._shops[key];
        let customershop:CustomerShopLinkVO = keepObjs.hasOwnProperty(key) ? keepObjs[key] :
            { customerId: this._customer != null ? this._customer.customerId : 0, shopId: shop.shopId, disabled: false };
        labels.push({
          first: shop,
          second: customershop
        });
      }
    }

    LogUtil.debug('CustomerComponent available shops', labels);
    return labels;
  }

  private getSupportedShopNames():Array<Pair<ShopVO, CustomerShopLinkVO>> {
    let supported = this._customer.customerShops;
    let keepKeys = <Array<string>>[];
    let keepObjs:any = {};
    if (supported) {
      supported.forEach(customershop => {
        if (!customershop.disabled) {
          keepKeys.push('S' + customershop.shopId);
          keepObjs['S' + customershop.shopId] = customershop;
        }
      });
    }
    LogUtil.debug('CustomerComponent supported', keepKeys);

    let labels = <Array<Pair<ShopVO, CustomerShopLinkVO>>>[];
    for (let key in this._shops) {
      let idx = keepKeys.indexOf(key);
      if (idx != -1) {
        let shop = this._shops[key];
        labels.push({
          first: shop,
          second: keepObjs[key]
        });
      }
    }

    LogUtil.debug('CustomerComponent supported', labels);
    return labels;
  }


}
