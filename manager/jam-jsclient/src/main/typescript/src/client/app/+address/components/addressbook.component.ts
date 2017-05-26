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
import { FormBuilder, Validators, ValidatorFn } from '@angular/forms';
import { CustomerService, I18nEventBus, Util } from './../../shared/services/index';
import { YcValidators } from './../../shared/validation/validators';
import { ShopVO, CustomerInfoVO, AddressBookVO, AddressVO, AttrValueVO, Pair } from './../../shared/model/index';
import { Futures, Future } from './../../shared/event/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { Config } from './../../shared/config/env.config';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';


@Component({
  selector: 'yc-addressbook',
  moduleId: module.id,
  templateUrl: 'addressbook.component.html',
})

export class AddressBookComponent implements OnInit, OnDestroy {

  private _customer:CustomerInfoVO;
  private addressFilter:string;
  private _reload:boolean = false;

  private _shops:ShopVO[] = [];

  private addressEdit:AddressVO = null;
  private addressBook:AddressBookVO = null;
  private countries:Pair<string, string>[] = [];
  private states:Pair<string, string>[] = [];
  private formattingShopId:number = 0;

  private addresses:AddressVO[] = [];

  private initialising:boolean = false; // tslint:disable-line:no-unused-variable
  private delayedChange:Future;

  private addressForm:any;
  private addressFormSub:any; // tslint:disable-line:no-unused-variable

  private addressFormConfig:any;

  private validForSave:boolean = false;

  @ViewChild('editModalDialog')
  private editModalDialog:ModalComponent;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private loading:boolean = false;

  constructor(private _customerService:CustomerService,
              fb: FormBuilder) {
    LogUtil.debug('AddressBookComponent constructed');

    this.addressForm = fb.group({
      'addressType': [''],
      'defaultAddress': [''],

      'countryCode': ['', Validators.required],
      'stateCode': [''],
      'city': ['', YcValidators.requiredNonBlankTrimmed],
      'postcode': ['', YcValidators.nonBlankTrimmed],
      'addrline1': ['', YcValidators.requiredNonBlankTrimmed],
      'addrline2': ['', YcValidators.nonBlankTrimmed],

      'salutation': ['', YcValidators.nonBlankTrimmed],
      'firstname': ['', YcValidators.requiredNonBlankTrimmed],
      'middlename': ['', YcValidators.nonBlankTrimmed],
      'lastname': ['', YcValidators.requiredNonBlankTrimmed],

      'phone1': ['', YcValidators.validPhone],
      'phone2': ['', YcValidators.validPhone],
      'mobile1': ['', YcValidators.validPhone],
      'mobile2': ['', YcValidators.validPhone],

      'email1': ['', YcValidators.validEmail],
      'email2': ['', YcValidators.validEmail],

      'custom0': [''],
      'custom1': [''],
      'custom2': [''],
      'custom3': [''],
      'custom4': [''],
      'custom5': [''],
      'custom6': [''],
      'custom7': [''],
      'custom8': [''],
      'custom9': [''],
    });

    let that = this;
    this.delayedChange = Futures.perpetual(function() {
      that.formChange();
    }, 200);

    this.delayedFiltering = Futures.perpetual(function() {
      that.filterAddresses();
    }, this.delayedFilteringMs);

  }


  formBind():void {
    UiUtil.formBind(this, 'addressForm', 'addressFormSub', 'delayedChange', 'initialising');
  }


  formUnbind():void {
    UiUtil.formUnbind(this, 'addressFormSub');
  }

  formChange():void {
    LogUtil.debug('AddressBookComponent formChange', this.addressForm.valid, this._customer);
    this.validForSave = this.addressForm.valid;
  }

  formConfigureValidator(formField:AddressFormField, defaultValidator:ValidatorFn):ValidatorFn {
    if (formField != null) {
      if (formField.regex) {
        if (formField.mandatory) {
          return Validators.compose([Validators.required, Validators.pattern(formField.regex)]);
        }
        return Validators.pattern(formField.regex);
      } else if (defaultValidator != null) {
        if (formField.mandatory) {
          return Validators.compose([Validators.required, defaultValidator]);
        }
        return defaultValidator;
      } else if (formField.mandatory) {
        return Validators.required;
      }
    }
    return defaultValidator;
  }

  formConfigure(addressType:string):void {
    if (addressType != null && this.addressFormConfig[addressType] != null) {

      let cfg:any = this.addressFormConfig[addressType];

      this.addressForm.controls['postcode'].validator = this.formConfigureValidator(cfg['postcode'], YcValidators.nonBlankTrimmed);
      this.addressForm.controls['addrline2'].validator = this.formConfigureValidator(cfg['addrline2'], YcValidators.nonBlankTrimmed);

      this.addressForm.controls['middlename'].validator = this.formConfigureValidator(cfg['middlename'], YcValidators.nonBlankTrimmed);

      this.addressForm.controls['phone1'].validator = this.formConfigureValidator(cfg['phone1'], YcValidators.validPhone);
      this.addressForm.controls['phone2'].validator = this.formConfigureValidator(cfg['phone2'], YcValidators.validPhone);
      this.addressForm.controls['mobile1'].validator = this.formConfigureValidator(cfg['mobile1'], YcValidators.validPhone);
      this.addressForm.controls['mobile2'].validator = this.formConfigureValidator(cfg['mobile2'], YcValidators.validPhone);

      this.addressForm.controls['email1'].validator = this.formConfigureValidator(cfg['email1'], YcValidators.validEmail);
      this.addressForm.controls['email2'].validator = this.formConfigureValidator(cfg['email2'], YcValidators.validEmail);

      this.addressForm.controls['custom0'].validator = this.formConfigureValidator(cfg['custom0'], null);
      this.addressForm.controls['custom1'].validator = this.formConfigureValidator(cfg['custom1'], null);
      this.addressForm.controls['custom2'].validator = this.formConfigureValidator(cfg['custom2'], null);
      this.addressForm.controls['custom3'].validator = this.formConfigureValidator(cfg['custom3'], null);
      this.addressForm.controls['custom4'].validator = this.formConfigureValidator(cfg['custom4'], null);
      this.addressForm.controls['custom5'].validator = this.formConfigureValidator(cfg['custom5'], null);
      this.addressForm.controls['custom6'].validator = this.formConfigureValidator(cfg['custom6'], null);
      this.addressForm.controls['custom7'].validator = this.formConfigureValidator(cfg['custom7'], null);
      this.addressForm.controls['custom8'].validator = this.formConfigureValidator(cfg['custom8'], null);
      this.addressForm.controls['custom9'].validator = this.formConfigureValidator(cfg['custom9'], null);

    } else {

      this.addressForm.controls['postcode'].validator = YcValidators.nonBlankTrimmed;
      this.addressForm.controls['addrline2'].validator = YcValidators.nonBlankTrimmed;

      this.addressForm.controls['middlename'].validator = YcValidators.nonBlankTrimmed;

      this.addressForm.controls['phone1'].validator = YcValidators.validPhone;
      this.addressForm.controls['phone2'].validator = YcValidators.validPhone;
      this.addressForm.controls['mobile1'].validator = YcValidators.validPhone;
      this.addressForm.controls['mobile2'].validator = YcValidators.validPhone;

      this.addressForm.controls['email1'].validator = YcValidators.validEmail;
      this.addressForm.controls['email2'].validator = YcValidators.validEmail;

      this.addressForm.controls['custom0'].validator = null;
      this.addressForm.controls['custom1'].validator = null;
      this.addressForm.controls['custom2'].validator = null;
      this.addressForm.controls['custom3'].validator = null;
      this.addressForm.controls['custom4'].validator = null;
      this.addressForm.controls['custom5'].validator = null;
      this.addressForm.controls['custom6'].validator = null;
      this.addressForm.controls['custom7'].validator = null;
      this.addressForm.controls['custom8'].validator = null;
      this.addressForm.controls['custom9'].validator = null;

    }
  }

  @Input()
  set customer(customer:CustomerInfoVO) {

    if (this._customer == null || customer == null || this._customer.customerId != customer.customerId) {
      this._shops = [];
      this.addressBook = null;
      this._reload = true;
    }
    this._customer = customer;

  }

  get customer():CustomerInfoVO {
    return this._customer;
  }

  @Input()
  set reload(reload:boolean) {
    if (reload && !this._reload) {
      this._reload = true;
      this.reloadAddressbook();
    }
  }

  @Input()
  set shops(shops:ShopVO[]) {
    this._shops = shops;
    if (this._reload) {
      this.reloadAddressbook();
    }
  }

  get shops():ShopVO[] {
    return this._shops;
  }

  ngOnInit() {
    LogUtil.debug('AddressBookComponent ngOnInit');
    this.formBind();
  }

  ngOnDestroy() {
    LogUtil.debug('AddressBookComponent ngOnDestroy');
    this.formUnbind();
  }

  tabSelected(tab:any) {
    LogUtil.debug('AddressBookComponent tabSelected', tab);
  }

  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onFormatShopChange(event:any) {

    this.reloadAddressbook();

  }

  protected onRowAddSelected():void {

    let address:AddressVO = {
      addressId: 0, customerId: this._customer.customerId,
      addressType: 'S', defaultAddress: true,
      city: null, countryCode: null, stateCode: null,
      salutation: this._customer.salutation, firstname: this._customer.firstname, middlename: this._customer.middlename, lastname: this._customer.lastname,
      postcode: null, addrline1: null, addrline2: null,
      phone1: null, phone2: null,
      mobile1: null, mobile2: null,
      email1: null, email2: null,
      custom0: null, custom1: null, custom2: null, custom3: null, custom4: null,
      custom5: null, custom6: null, custom7: null, custom8: null, custom9: null
    };

    this.onRowEditSelected(address);

  }

  protected onRowEditSelected(address:AddressVO) {
    if (address != null) {
      this.validForSave = false;
      UiUtil.formInitialise(this, 'initialising', 'addressForm', 'addressEdit', Util.clone(address), address != null && address.addressId > 0, ['addressType']);
      this.reloadCountries();
      this.reloadStates();
      this.formConfigure(address.addressType);
      this.editModalDialog.show();
    }
  }

  protected onAddressTypeChange(event:any) {

    this.reloadCountries();
    this.formConfigure(this.addressEdit.addressType);

  }

  protected onCountryChange(event:any) {

    this.reloadStates();

  }

  protected reloadCountries():void {

    if (this.addressEdit != null) {
      let countryPair = this.addressBook.countries.find((country:Pair<string, Pair<string, string>[]>) => {
        return country.first == this.addressEdit.addressType;
      });
      if (countryPair != null) {
        this.countries = countryPair.second;
        this.states = [];
      } else {
        this.countries = [];
        this.states = [];
      }

    } else {
      this.countries = [];
      this.states = [];
    }
    LogUtil.debug('AddressBookComponent reloadCountries', this.countries);
  }


  protected reloadStates():void {

    if (this.addressEdit != null && this.addressEdit.countryCode != null) {
      let statePair = this.addressBook.states.find((state:Pair<string, Pair<string, string>[]>) => {
        return state.first == this.addressEdit.countryCode;
      });
      if (statePair != null) {
        this.states = statePair.second;
      } else {
        this.states = [];
      }

    } else {
      this.states = [];
    }
    LogUtil.debug('AddressBookComponent reloadStates', this.states);
  }



  protected onEditConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('AddressBookComponent onEditConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.addressEdit != null) {
        LogUtil.debug('AddressBookComponent onEditConfirmationResult', this.addressEdit);

        var _sub:any = this._customerService.saveAddress(this.addressEdit).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('AddressBookComponent saveAddress', this.addressEdit);
          this.addressEdit = null;
          this.reloadAddressbook();
        });
      }


    }
  }


  protected onRowDeleteSelected(address:AddressVO) {
    if (address != null) {
      this.addressEdit = address;
      this.deleteConfirmationModalDialog.show();
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('AddressBookComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.addressEdit != null) {
        LogUtil.debug('AddressBookComponent onDeleteConfirmationResult', this.addressEdit);

        var _sub:any = this._customerService.removeAddress(this.addressEdit).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('AddressBookComponent removeAddress', this.addressEdit);
          this.addressEdit = null;
          this.reloadAddressbook();
        });
      }


    }
  }


  protected onClearFilter() {

    this.addressFilter = '';
    this.filterAddresses();

  }

  protected getFormattedAddress(address:AddressVO) {

    if (this.addressBook != null) {
      let pk = address.addressId;
      let formatted = this.addressBook.formattedAddresses.find(item => {
        return pk == item.first;
      });

      if (formatted != null) {
        return formatted.second; // if not in <pre/> then => .replace('\r\n', '<br/>').replace('\r', '<br/>').replace('\n', '<br/>');
      }
    }

    return address.addrline1 + (address.addrline2 ? ' ' + address.addrline2 : '') + '\n' +
           address.city + ' ' + address.postcode + '\n' +
           address.countryCode;
  }

  protected onRefreshHandler() {
    LogUtil.debug('AddressBookComponent refresh handler');
    this.reloadAddressbook();
  }

  private reloadAddressbook():void {

    LogUtil.debug('AddressBookComponent reloadAddressbook', this._customer, this._shops);
    if (this._customer != null && this._customer.customerId > 0 && this._shops != null && this._shops.length > 0) {

      if (!(this.formattingShopId > 0)) {
        this.formattingShopId = this._shops[0].shopId;
      }

      this.loading = true;
      let lang = I18nEventBus.getI18nEventBus().current();
      let _sub:any = this._customerService.getAddressBook(this._customer, this.formattingShopId, lang).subscribe(rez => {
        LogUtil.debug('AddressBookComponent getAddressBook', rez);
        this.addressBook = rez;
        this.addressEdit = null;
        this.filterAddresses();
        this.buildAddressFormConfig();
        _sub.unsubscribe();
        this.loading = false;
        this._reload = false;
      });

    } else {
      this.addressBook = null;
      this.addressEdit = null;
      this.filterAddresses();
      this.buildAddressFormConfig();
    }
  }

  private filterAddresses():void {

    let _filteredAddresses:AddressVO[] = [];

    if (this.addressBook != null) {
      let _filter = this.addressFilter ? this.addressFilter.toLowerCase() : null;

      if (_filter) {
        _filteredAddresses = this.addressBook.addresses.filter((addr:AddressVO) =>
          addr.countryCode.toLowerCase().indexOf(_filter) !== -1 ||
          addr.postcode && addr.postcode.toLowerCase().indexOf(_filter) !== -1 ||
          addr.city && addr.city.toLowerCase().indexOf(_filter) !== -1 ||
          addr.addrline1 && addr.addrline1.toLowerCase().indexOf(_filter) !== -1 ||
          addr.addrline2 && addr.addrline2.toLowerCase().indexOf(_filter) !== -1 ||
          addr.stateCode && addr.stateCode.toLowerCase().indexOf(_filter) !== -1 ||
          addr.firstname && addr.firstname.toLowerCase().indexOf(_filter) !== -1 ||
          addr.middlename && addr.middlename.toLowerCase().indexOf(_filter) !== -1 ||
          addr.lastname && addr.lastname.toLowerCase().indexOf(_filter) !== -1 ||
          addr.email1 && addr.email1.toLowerCase().indexOf(_filter) !== -1 ||
          addr.email2 && addr.email2.toLowerCase().indexOf(_filter) !== -1 ||
          addr.phone1 && addr.phone1.toLowerCase().indexOf(_filter) !== -1 ||
          addr.phone2 && addr.phone2.toLowerCase().indexOf(_filter) !== -1 ||
          addr.mobile1 && addr.mobile1.toLowerCase().indexOf(_filter) !== -1 ||
          addr.mobile2 && addr.mobile2.toLowerCase().indexOf(_filter) !== -1 ||
          addr.custom0 && addr.custom0.toLowerCase().indexOf(_filter) !== -1 ||
          addr.custom1 && addr.custom1.toLowerCase().indexOf(_filter) !== -1 ||
          addr.custom2 && addr.custom2.toLowerCase().indexOf(_filter) !== -1 ||
          addr.custom3 && addr.custom3.toLowerCase().indexOf(_filter) !== -1 ||
          addr.custom4 && addr.custom4.toLowerCase().indexOf(_filter) !== -1 ||
          addr.custom5 && addr.custom5.toLowerCase().indexOf(_filter) !== -1 ||
          addr.custom6 && addr.custom6.toLowerCase().indexOf(_filter) !== -1 ||
          addr.custom7 && addr.custom7.toLowerCase().indexOf(_filter) !== -1 ||
          addr.custom8 && addr.custom8.toLowerCase().indexOf(_filter) !== -1 ||
          addr.custom9 && addr.custom9.toLowerCase().indexOf(_filter) !== -1
        );

      } else {
        _filteredAddresses = this.addressBook.addresses;
      }
    }

    this.addresses = _filteredAddresses;

  }

  private buildAddressFormConfig():void {

    this.addressFormConfig = {};
    if (this.addressBook != null) {

      let lang = I18nEventBus.getI18nEventBus().current();
      this.addressBook.addressForm.forEach((formCfg:Pair<string, AttrValueVO[]>) => {
        let fields:any = {};

        formCfg.second.forEach((field:AttrValueVO) => {

          let fieldCfg:AddressFormField = {
            name: field.attribute.val,
            label: this.getAttributeName(field.attribute.displayNames, field.attribute.name, lang),
            validationMessage: this.getAttributeName(field.attribute.validationFailedMessage, null, lang),
            mandatory: field.attribute.mandatory,
            regex: field.attribute.regexp,
            options: null
          };

          if (fieldCfg.name == 'salutation') {
            fieldCfg.options = this.getSalutationOptions(field.attribute.choiceData, lang);
          }

          fields[fieldCfg.name] = fieldCfg;

        });

        this.addressFormConfig[formCfg.first] = fields;

      });

    }

    LogUtil.debug('AddressBookComponent buildAddressFormConfig', this.addressFormConfig);

  }

  private getAttributeName(i18n:Pair<string, string>[], def:string, lang:string):string {

    if (i18n == null) {
      return def;
    }

    let namePair = i18n.find(_name => {
      return _name.first == lang;
    });

    if (namePair != null) {
      return namePair.second;
    }

    return def;
  }

  private getSalutationOptions(choice:Pair<string, string>[], lang:string):Pair<string, string>[] {
    let i18n = this.getAttributeName(choice, null, lang);
    if (i18n != null) {
      let salutations:Pair<string, string>[] = [];
      i18n.split(',').forEach(_option => {
        let valueAndName = _option.split('-', 2);
        if (valueAndName.length == 2) {
          salutations.push({first: valueAndName[0], second: valueAndName[1]});
        }
      });
      return salutations;
    }
    return [];
  }


}

interface AddressFormField {

  name: string;
  label: string;
  validationMessage: string;
  mandatory: boolean;
  regex: string;
  options: Pair<string, string>[];

}
