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
import {Component} from 'angular2/core';
import {ShopLanguagesVO} from './../../model/shop';
import {OnInit} from 'angular2/core';
import {RouteParams} from 'angular2/router';
import {ShopService} from '../../service/shop_service';
import {DataControl} from '../common/data_control';
import {HTTP_PROVIDERS}    from 'angular2/http';
import {Util} from '../../service/util';

@Component({
  selector: 'shop-language',
  moduleId: module.id,
  templateUrl: './shop_language.html',
  styleUrls: ['./shop_language.css'],
  directives: [DataControl],
  providers: [HTTP_PROVIDERS, ShopService]
})

export class ShopLanguage implements OnInit {

  shopLanguagesVO:ShopLanguagesVO;
  lang:ShopLanguagesVO;

  changed:boolean = false;

  constructor(private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Shop language');
  }

  ngOnInit() {
    let shopId = this._routeParams.get('shopId');
    console.debug('ngOnInit shopId from params is ' + shopId);
    this.onRefreshHandler();
  }

  onSaveHandler() {
    console.debug('Save handler for shop id ');
    this._shopService.saveShopLanguages(this.lang).subscribe(shopLanguagesVo => {
      console.debug('>>> ' + JSON.stringify(shopLanguagesVo));
      this.shopLanguagesVO  = Util.clone(shopLanguagesVo);
      this.lang  = Util.clone(shopLanguagesVo);
      this.lang.all = this.lang.all.filter( obj => { return this.lang.supported.indexOf(obj.first) === -1; } );
      this.changed = false;
    });

  }

  onDiscardEventHandler() {
    console.debug('Discard handler for shop id ' );
    this.lang  = Util.clone(this.shopLanguagesVO);
    this.lang.all = this.lang.all.filter( obj => { return this.lang.supported.indexOf(obj.first) === -1; } );
    this.changed = false;
  }

  onRefreshHandler() {
    console.debug('Refresh handler');
    let shopId = this._routeParams.get('shopId');
    this._shopService.getShopLanguages(+shopId).subscribe(shopLanguagesVo => {
      this.shopLanguagesVO  = Util.clone(shopLanguagesVo);
      this.lang  = Util.clone(shopLanguagesVo);
      this.lang.all = this.lang.all.filter( obj => {
        console.debug('>>> Obj ' + JSON.stringify(obj) + ' idx in supported by first ' + this.lang.supported.indexOf(obj.first));
        return this.lang.supported.indexOf(obj.first) === -1; } );
      this.changed = false;
    });
  }

  onAvailableLanguageClick(event) {
    console.debug('onAvailableLanguageClick evt ' + event);
    this.lang.supported.push(event.first);
    this.lang.all = this.lang.all.filter( obj => { return this.lang.supported.indexOf(obj.first) === -1; } );
    this.changed = true;
  }

  onSupportedLanguageClick(event) {
    console.debug('onSupportedLanguageClick evt ' + event);
    this.lang.supported = this.lang.supported.filter( obj => {return obj !== event;});
    this.lang.all = Util.clone(this.shopLanguagesVO.all);
    this.lang.all = this.lang.all.filter( obj => { return this.lang.supported.indexOf(obj.first) === -1; } );
    this.changed = true;
  }

}
