import {Component} from 'angular2/core';
import {ShopVO} from './../../model/shop';
import {OnInit} from 'angular2/core';
import {RouteParams} from 'angular2/router';
import {ShopService} from '../../service/shop_service';
import {DataControl} from '../common/data_control';
import {HTTP_PROVIDERS}    from 'angular2/http';

@Component({
  selector: 'shop-panel',
  moduleId: module.id,
  templateUrl: './shop_panel.html',
  styleUrls: ['./shop_panel.css'],
  directives: [DataControl],
  providers: [HTTP_PROVIDERS, ShopService]
})

export class ShopPanel implements OnInit {

  shop:ShopVO;

  changed:boolean = false;

  constructor(private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Shop list constructed');
  }

  ngOnInit() {

    let shopId = this._routeParams.get('shopId');
    console.debug('shopId from params is ' + shopId);

    if (shopId === 'new') {
      this._shopService.createShop().then(shop => {
        this.shop = shop;
        this.changed = false;
      });
    } else {
      this._shopService.getShop(+shopId).subscribe(shop => {
        this.shop = shop;
        this.changed = false;
      });
    }
  }

  onDataChange() {
    console.debug('data changed');
    this.changed = true;
  }

  onSaveHandler() {
    console.debug('Save handler for shop id ' + this.shop.shopId);
    this._shopService.saveShop(this.shop).subscribe(shop => {
      this.shop = shop;
      this.changed = false;
    });
  }

  onDiscardEvent() {
    console.debug('Discard hander for shop id ' + this.shop.shopId);
    this._shopService.getShop(this.shop.shopId).subscribe(shop => {
      this.shop = shop;
      this.changed = false;
    });
  }

  onRefreshHandler() {
    console.debug('Refresh handler');
    this.onDiscardEvent();
  }


}
