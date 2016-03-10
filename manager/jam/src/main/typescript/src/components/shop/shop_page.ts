import {Component, OnInit, AfterContentInit} from 'angular2/core';
import {ShopVO} from './../../model/shop';
import {RouteParams} from 'angular2/router';
import {ShopService} from '../../service/shop_service';
import {Tab, TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {ShopPanel} from './shop_panel';
import {ShopUrlPanel} from './shop_url_panel';
import {ShopLocalizationPanel} from './shop_localization_panel';

@Component({
  selector: 'shop',
  moduleId: module.id,
  templateUrl: './shop_page.html',
  styleUrls: ['./shop_page.css'],
  directives: [TAB_DIRECTIVES, ShopPanel, ShopLocalizationPanel, ShopUrlPanel],
  providers: [ShopService]
})

export class ShopPage implements OnInit, AfterContentInit {
  shop:ShopVO;

  constructor(private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Shop page constructed');
  }

  ngOnInit() {

    let shopId = this._routeParams.get('shopId');
    console.debug('shopId from params is ' + shopId);

    if (shopId === 'new') {
      this._shopService.createShop().then(shop => {
        this.shop = shop;
      });
    } else {
      this._shopService.getShop(+shopId).then(shop => {
        this.shop = shop;
      });
    }
  }



  ngAfterContentInit() {
    console.debug('ngAfterContentInit');
  }


  tabSelected(tab:Tab) {
    console.debug('tabSelected ' + tab); //.heading
  }


}
