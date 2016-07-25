import {Component,  OnInit, OnDestroy} from '@angular/core';
import {NgFor} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {ShopVO} from '../model/index';
import {ShopEventBus, ShopService} from '../services/index';



@Component({
  selector: 'shoplist',
  moduleId: module.id,
  templateUrl: 'shop-list.component.html',
  styleUrls: ['shop-list.component.css'],
  directives: [ROUTER_DIRECTIVES, NgFor],
  providers: [HTTP_PROVIDERS, ShopService, ShopEventBus]
})

export class ShopListComponent implements OnInit, OnDestroy {

  private shops : ShopVO[];
  private selectedShop : ShopVO;

  private shopSub:any;

  constructor (private _shopService : ShopService) {
    console.debug('ShopListComponent constructed selectedShop ' + this.selectedShop);
    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.reloadShopList(shopevt);
    });
  }

  getAllShops() {
    var _sub:any = this._shopService.getAllShops().subscribe( allshops => {
      console.debug('ShopListComponent getAllShops', allshops);
      this.shops = allshops;
      _sub.unsubscribe();
    });
  }

  ngOnDestroy() {
    console.debug('ShopListComponent ngOnDestroy');
    if (this.shopSub) {
      this.shopSub.unsubscribe();
    }
  }

  ngOnInit() {
    console.debug('ShopListComponent ngOnInit');
    this.getAllShops();
  }

  onSelect(shop: ShopVO) {
    console.debug('ShopListComponent onSelect', shop);
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
