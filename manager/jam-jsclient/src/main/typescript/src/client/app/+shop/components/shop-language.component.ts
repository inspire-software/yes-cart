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
import {Component, OnInit, OnDestroy, OnChanges, Input} from '@angular/core';
import {NgIf, NgFor} from '@angular/common';
import {ShopVO, ShopLanguagesVO} from './../../shared/model/index';
import {ShopService, ShopEventBus, Util} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';

@Component({
  selector: 'yc-shop-language',
  moduleId: module.id,
  templateUrl: './shop-language.component.html',
  directives: [ NgIf, NgFor, DataControlComponent],
})

export class ShopLanguageComponent implements OnInit, OnChanges {

  @Input() shop:ShopVO;

  shopLanguagesVO:ShopLanguagesVO;
  lang:ShopLanguagesVO;

  changed:boolean = false;

  constructor(private _shopService:ShopService) {
    console.debug('ShopLanguageComponent constructor');
  }

  ngOnInit() {
    console.debug('ShopLanguageComponent ngOnInit shop', this.shop);
    this.onRefreshHandler();
  }

  ngOnChanges(changes:any) {
    console.log('ShopLanguageComponent ngOnChanges', changes);
    this.onRefreshHandler();
  }


  onDataChange() {
    console.debug('ShopLanguageComponent data changed');
    this.changed = true;
  }

  onSaveHandler() {
    console.debug('ShopLanguageComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.lang) {
      var _sub:any = this._shopService.saveShopLanguages(this.lang).subscribe(shopLanguagesVo => {
        this.shopLanguagesVO = Util.clone(shopLanguagesVo);
        this.lang = Util.clone(shopLanguagesVo);
        this.lang.all = this.lang.all.filter(obj => {
          return this.lang.supported.indexOf(obj.first) === -1;
        });
        this.changed = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEventHandler() {
    console.debug('ShopLanguageComponent discard handler', this.shop);
    if (this.shop.shopId > 0 && this.shopLanguagesVO) {
      this.lang = Util.clone(this.shopLanguagesVO);
      this.lang.all = this.lang.all.filter(obj => {
        return this.lang.supported.indexOf(obj.first) === -1;
      });
      this.changed = false;
    }
  }

  onRefreshHandler() {
    console.debug('ShopLanguageComponent refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      var _sub:any = this._shopService.getShopLanguages(this.shop.shopId).subscribe(shopLanguagesVo => {
        this.shopLanguagesVO  = Util.clone(shopLanguagesVo);
        this.lang  = Util.clone(shopLanguagesVo);
        this.lang.all = this.lang.all.filter( obj => {
          return this.lang.supported.indexOf(obj.first) === -1;
        });
        this.changed = false;
        _sub.unsubscribe();
      });
    } else {
      this.lang = null;
    }
  }

  onAvailableLanguageClick(event:any) {
    console.debug('ShopLanguageComponent onAvailableCurrencyClick', event);
    this.lang.supported.push(event.first);
    this.lang.all = this.lang.all.filter( obj => { return this.lang.supported.indexOf(obj.first) === -1; } );
    this.changed = true;
  }

  onSupportedLanguageClick(event:any) {
    console.debug('ShopLanguageComponent onSupportedCurrencyClick', event);
    this.lang.supported = this.lang.supported.filter( obj => {return obj !== event;});
    this.lang.all = Util.clone(this.shopLanguagesVO.all);
    this.lang.all = this.lang.all.filter( obj => { return this.lang.supported.indexOf(obj.first) === -1; } );
    this.changed = true;
  }

}
