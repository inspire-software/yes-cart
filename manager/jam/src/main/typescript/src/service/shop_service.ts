import {mockShops, mockNewShop, mockShopLocalization} from './mock_data';
import {Injectable} from 'angular2/core';
import {Util} from './util';
import {ShopVO, ShopLocaleVO} from './../model/shop';
import {ShopUrlVO} from '../model/shop';
import {mockShopUls} from './mock_data';

@Injectable()
export class ShopService {

  getAllShops() {
    return Promise.resolve(mockShops);
  }

  getShop(id:number) {
    for (var idx = 0 ; idx < mockShops.length; idx++) {
      if (mockShops[idx].shopId === id) {
        return Promise.resolve(Util.clone(mockShops[idx]));
      }
    }
    return Promise.resolve(null);
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
}
