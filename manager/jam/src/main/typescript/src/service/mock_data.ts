/**
 * Mocked data for test and development speedup.
 */

import {ShopVO, ShopLocaleVO} from './../model/shop';
import {Pair} from '../model/common';
import {ShopUrlVO} from '../model/shop';


export var mockShops: ShopVO[] = [
  {'shopId':10,'code':'SHOP10','name':'YesCart shop','description':'YesCart shop','fspointer':'ycdemo'},
  {'shopId': 11, 'code' : 'C11', 'name': 'Eleven-seven', 'description' : 'This is 11-7 megastore', 'fspointer' : 'boomerang'},
  {'shopId': 12, 'code' : 'CODE12', 'name': 'Midnight shop', 'description' : 'Midnight shop', 'fspointer' : 'boomerang'}
];

export var mockNewShop : ShopVO = {'shopId': 13, 'code' : 'CODE' + 13, 'name': '', 'description' : '', 'fspointer' : 'boomerang'};


var locale11 : ShopLocaleVO = {
  'shopId':11,
  'uri' : 'default',
  'title' : 'default title',
  'metakeywords' : 'metakeywords',
  'metadescription' : 'metadescription',
  'displayTitles' : [new Pair('en', 'title'), new Pair('uk', 'вікно')],
  'displayMetakeywords' : [new Pair('en', 'description'), new Pair('uk', 'опис')],
  'displayMetadescriptions' : [new Pair('en', 'meta, keywords'), new Pair('uk', 'слова')]
};

var locale12 : ShopLocaleVO = {
  'shopId':12,
  'uri' : 'default',
  'title' : 'default title',
  'metakeywords' : 'metakeywords',
  'metadescription' : 'metadescription',
  'displayTitles' : [new Pair('en', 'Midnight shop'), new Pair('uk', 'Нічний магазин ')],
  'displayMetakeywords' : [new Pair('en', 'Midnight shop, we are glad to serve 24 hours'),
    new Pair('uk', 'Нічний магазин працює 24 години на добу')],
  'displayMetadescriptions' : [new Pair('en', 'meta, keywords'), new Pair('uk', 'слова')]
};

export var mockShopLocalization : ShopLocaleVO[] = [
  locale11, locale12
];


var shopUrl111 : ShopUrlVO = <ShopUrlVO>{
  'shopId': 11,
  'urls': [
    {'urlId': 1, 'url': 'www.url1-11.com', 'theme' : 'theme 1 - 11'} ,
    {'urlId': 2, 'url': 'www.url2-11.com', 'theme' : 'theme 2 - 11'},
    {'urlId': 3, 'url': 'www.url3-11.com', 'theme' : 'theme 3 - 11'},
    {'urlId': 4, 'url': 'www.url4-11.com', 'theme' : 'theme 4 - 11'},
    {'urlId': 5, 'url': 'www.url5-11.com', 'theme' : 'theme 5 - 11'},
    {'urlId': 6, 'url': 'www.url6-11.com', 'theme' : 'theme 6 - 11'},
    {'urlId': 7, 'url': 'www.url7-11.com', 'theme' : 'theme 7 - 11'},
    {'urlId': 8, 'url': 'www.url8-11.com', 'theme' : 'theme 8 - 11'},
    {'urlId': 9, 'url': 'www.url9-11.com', 'theme' : 'theme 9 - 11'},
    {'urlId': 10, 'url': 'www.url10-11.com', 'theme' : 'theme 10 - 11'},
    {'urlId': 11, 'url': 'www.url11-11.com', 'theme' : 'theme 11 - 11'},
    {'urlId': 12, 'url': 'www.url12-11.com', 'theme' : 'theme 12 - 11'},
    {'urlId': 13, 'url': 'www.url13-11.com', 'theme' : 'theme 13 - 11'},
    {'urlId': 14, 'url': 'www.url14-11.com', 'theme' : 'theme 14 - 11'},
    {'urlId': 15, 'url': 'www.url15-11.com', 'theme' : 'theme 15 - 11'},
    {'urlId': 16, 'url': 'www.url16-11.com', 'theme' : 'theme 16 - 11'},
    {'urlId': 17, 'url': 'www.url17-11.com', 'theme' : 'theme 17 - 11'},
    {'urlId': 18, 'url': 'www.url18-11.com', 'theme' : 'theme 18 - 11'},
    {'urlId': 19, 'url': 'www.url19-11.com', 'theme' : 'theme 19 - 11'},
    {'urlId': 20, 'url': 'www.url20-11.com', 'theme' : 'theme 20 - 11'},
    {'urlId': 21, 'url': 'www.url21-11.com', 'theme' : 'theme 21 - 11'},
  ]
};


var shopUrl112 : ShopUrlVO = <ShopUrlVO>{
  'shopId': 12,
  'urls': [{'urlId': 3, 'url': 'www.url1-12.com','theme' :  'theme 1 - 12'}]
};

export var mockShopUls : ShopUrlVO[] = [
  shopUrl111, shopUrl112
];

