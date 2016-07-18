import {Component,  OnInit, OnDestroy} from 'angular2/core';
import {NgFor} from 'angular2/common';
import {HTTP_PROVIDERS}    from 'angular2/http';
import {ROUTER_DIRECTIVES} from 'angular2/router';
import {ShopVO} from '../../model/shop';
import {ShopService} from '../../service/shop_service';
import {ShopEventBus} from '../../service/shop_event_bus';
import {AppCmp} from '../../app/components/app';



@Component({
  selector: 'shoplist',
  moduleId: module.id,
  templateUrl: './shop_list.html',
  styleUrls: ['./shop_list.css'],
  directives: [ROUTER_DIRECTIVES, NgFor],
  providers: [HTTP_PROVIDERS, ShopService, ShopEventBus]
})

export class ShopList implements OnInit, OnDestroy {

  shops : ShopVO[];

  selectedShop : ShopVO;

  constructor (private _shopService : ShopService) {
    console.debug('Shop list constructed selectedShop ' + this.selectedShop);
    AppCmp.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.reloadShopList(shopevt);
    });
  }

  getAllShops() {
    this._shopService.getAllShops().subscribe( allshops => { this.shops = allshops; }   );
  }

  ngOnDestroy() {
    console.debug('Shop list ngOnDestroy');
  }

  ngOnInit() {
    console.debug('Shop list ngOnInit');
    this.getAllShops();
  }

  onSelect(shop: ShopVO) {
    this.selectedShop = shop;
  }

  /**
   * Reload list of shops
   * @param shopVo shop that was changed or added
   */
  reloadShopList(shopVo : ShopVO) {
   this.getAllShops();
  }

  isShopDisabled(shop: ShopVO):boolean {
    return shop.disabled;
  }

}
