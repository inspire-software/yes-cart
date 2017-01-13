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
import { Component, OnInit, Input } from '@angular/core';
import { ShopVO, ShopLanguagesVO } from './../../shared/model/index';
import { ShopService, Util } from './../../shared/services/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-shop-language',
  moduleId: module.id,
  templateUrl: './shop-language.component.html',
})

export class ShopLanguageComponent implements OnInit {

  private _shop:ShopVO;
  private _reload:boolean = false;

  private shopLanguagesVO:ShopLanguagesVO;
  private lang:ShopLanguagesVO;

  private changed:boolean = false;

  private loading:boolean = false;

  constructor(private _shopService:ShopService) {
    LogUtil.debug('ShopLanguageComponent constructor');
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
    if (this._reload || this.shopLanguagesVO != null) {
      this.onRefreshHandler();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  ngOnInit() {
    LogUtil.debug('ShopLanguageComponent ngOnInit shop', this.shop);
  }


  onDataChange() {
    LogUtil.debug('ShopLanguageComponent data changed');
    this.changed = true;
  }

  onSaveHandler() {
    LogUtil.debug('ShopLanguageComponent Save handler', this.shop);
    if (this.shop.shopId > 0 && this.lang) {
      var _sub:any = this._shopService.saveShopLanguages(this.lang).subscribe(shopLanguagesVo => {
        this.shopLanguagesVO = Util.clone(shopLanguagesVo);
        this.lang = Util.clone(shopLanguagesVo);
        this.lang.all = this.lang.all.filter(obj => {
          return this.lang.supported.indexOf(obj.first) === -1;
        });
        this.changed = false;
        this._reload = false;
        _sub.unsubscribe();
      });
    }
  }

  onDiscardEventHandler() {
    LogUtil.debug('ShopLanguageComponent discard handler', this.shop);
    if (this.shop.shopId > 0 && this.shopLanguagesVO) {
      this.lang = Util.clone(this.shopLanguagesVO);
      this.lang.all = this.lang.all.filter(obj => {
        return this.lang.supported.indexOf(obj.first) === -1;
      });
      this.changed = false;
    }
  }

  onRefreshHandler() {
    LogUtil.debug('ShopLanguageComponent refresh handler', this.shop);
    if (this.shop.shopId > 0) {
      this.loading = true;
      var _sub:any = this._shopService.getShopLanguages(this.shop.shopId).subscribe(shopLanguagesVo => {
        this.shopLanguagesVO  = Util.clone(shopLanguagesVo);
        this.lang  = Util.clone(shopLanguagesVo);
        this.lang.all = this.lang.all.filter( obj => {
          return this.lang.supported.indexOf(obj.first) === -1;
        });
        this.changed = false;
        this._reload = false;
        _sub.unsubscribe();
        this.loading = false;
      });
    } else {
      this.lang = null;
    }
  }

  onAvailableLanguageClick(event:any) {
    LogUtil.debug('ShopLanguageComponent onAvailableLanguageClick', event);
    this.lang.supported.push(event.first);
    this.lang.all = this.lang.all.filter( obj => { return this.lang.supported.indexOf(obj.first) === -1; } );
    this.changed = true;
  }

  onSupportedLanguageClick(event:any) {
    LogUtil.debug('ShopLanguageComponent onSupportedLanguageClick', event);
    this.lang.supported = this.lang.supported.filter( obj => {return obj !== event;});
    this.lang.all = Util.clone(this.shopLanguagesVO.all);
    this.lang.all = this.lang.all.filter( obj => { return this.lang.supported.indexOf(obj.first) === -1; } );
    this.changed = true;
  }

}
