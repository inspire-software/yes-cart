import {Component,  OnInit} from 'angular2/core';
import {NgFor} from 'angular2/common';

import {ROUTER_DIRECTIVES} from 'angular2/router';

import {ShopVO} from '../../model/shop';
import {ShopService} from '../../service/shop_service';



@Component({
  selector: 'shoplist',
  moduleId: module.id,
  templateUrl: './shop_list.html',
  styleUrls: ['./shop_list.css'],
  directives: [ROUTER_DIRECTIVES, NgFor],
  providers : [ShopService]
})

export class ShopList implements OnInit {

  shops : ShopVO[];

  selectedShop : ShopVO;

  constructor (private _shopService : ShopService) {
    console.debug('Shop list constructed');
  }

  getAllShops() {
    this._shopService.getAllShops().then( allshops => { this.shops = allshops; }   );
  }

  ngOnInit() {
    console.debug('Shop list ngOnInit');
    this.getAllShops();
  }

  onSelect(shop: ShopVO) {
    this.selectedShop = shop;
  }

}
