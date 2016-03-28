import {mockShops, mockNewShop, mockShopLocalization} from './mock_data';
import {Injectable} from 'angular2/core';
import {Http, Response} from 'angular2/http';
import {Util} from './util';
import {ShopVO, ShopLocaleVO} from './../model/shop';
import {ShopUrlVO} from '../model/shop';
import {mockShopUls} from './mock_data';
import {Observable}     from 'rxjs/Observable';
import 'rxjs/Rx';

@Injectable()
export class ShopService {

  private _heroesUrl = '../service/shop';  // URL to web api

  constructor (private http: Http) {
    console.debug('ShopService constructed');
  }

  getAllShops() {
    return this.http.get(this._heroesUrl + '/all')
      .map(res => <ShopVO[]> res.json())
      .catch(this.handleError);
  }

  getShop(id:number) {
    return this.http.get(this._heroesUrl + '/all')
      .map(res => <ShopVO[]> res.json())
      .catch(this.handleError);
  }

  saveShopshop(shop:ShopVO) {
    for (var idx = 0 ; idx < mockShops.length; idx++) {
      if (mockShops[idx].shopId === shop.shopId) {
        mockShops[idx] = shop;
        return Promise.resolve(Util.clone(mockShops[idx]));
      }
    }
    mockShops.push(shop);
    return Promise.resolve(Util.clone(mockShops[mockShops.length - 1]));
  }

  getShopLocalization(id:number) {
    for (var idx=0; idx < mockShopLocalization.length; idx++) {
      var localeVO : ShopLocaleVO  = mockShopLocalization[idx];
      if (localeVO.shopId === id) {
        return Promise.resolve(Util.clone(localeVO));
      }
    }
    return Promise.resolve(Util.clone(mockShopLocalization[0]));
  }

  saveShopLocalization(shopLocaleVO:ShopLocaleVO) {
    for (var idx=0; idx < mockShopLocalization.length; idx++) {
      var localeVO : ShopLocaleVO  = mockShopLocalization[idx];
      if (localeVO.shopId === shopLocaleVO.shopId) {
        mockShopLocalization[idx] = Util.clone(shopLocaleVO);
        return Promise.resolve(Util.clone(shopLocaleVO));
      }
    }
  }

  createShop() {
    var newShop : ShopVO = Util.clone(mockNewShop);
    mockNewShop.code = 'C-' + mockNewShop.shopId;
    return Promise.resolve(newShop);
  }

  getShopUrls(id:number) {
    for (var idx=0; idx < mockShopUls.length; idx++) {
      var shopUrlVO : ShopUrlVO  = mockShopUls[idx];
      if (shopUrlVO.shopId === id) {
        return Promise.resolve(Util.clone(shopUrlVO));
      }
    }
    var clonned : ShopUrlVO = Util.clone(mockShopUls[0]);
    clonned.shopId = id;
    return Promise.resolve(Util.clone(clonned));
  }

  saveShopUrls(shopUrl:ShopUrlVO) {

    for (var idx=0; idx < mockShopUls.length; idx++) {
      var shopUrlVO : ShopUrlVO  = mockShopUls[idx];
      if (shopUrlVO.shopId === shopUrl.shopId) {
        mockShopUls[idx] = shopUrl;
        return Promise.resolve(Util.clone(shopUrlVO));
      }
    }

    var clonned : ShopUrlVO = Util.clone(mockShopUls[0]);
    clonned.shopId = shopUrl.shopId;
    return Promise.resolve(Util.clone(clonned));

  }


  private handleError (error: Response) {
    // in a real world app, we may send the error to some remote logging infrastructure
    // instead of just logging it to the console
    console.error(error);
    return Observable.throw(error.json().error || 'Server error');
  }

}
