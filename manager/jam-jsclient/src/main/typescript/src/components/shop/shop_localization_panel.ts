import {Component} from 'angular2/core';
import {OnInit} from 'angular2/core';
import {RouteParams} from 'angular2/router';
import {ShopService} from '../../service/shop_service';
import {ShopLocaleVO} from '../../model/shop';
import {LocalizationTable} from '../common/localization_table';
import {DataControl} from '../common/data_control';

@Component({
  selector: 'shop-localization-panel',
  moduleId: module.id,
  templateUrl: './shop_localization_panel.html',
  styleUrls: ['./shop_localization_panel.css'],
  directives: [LocalizationTable, DataControl],
  providers: [ShopService]
})

export class ShopLocalizationPanel implements OnInit {

  shopLocalization:ShopLocaleVO;

  changed:boolean = false;

  constructor(private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Shop localization panel constructed');
  }

  ngOnInit() {

    let shopId = this._routeParams.get('shopId');
    console.debug('shopId from params is ' + shopId);

    this._shopService.getShopLocalization(+shopId).subscribe(shopLocalization => {
      console.debug('Get i18n', shopLocalization);
      this.shopLocalization = shopLocalization;
      this.changed = false;
    });
  }

  onDataChanged() {
    console.debug('Child data change');
    this.changed = true;
  }

  onSaveHandler() {
    console.debug('Save handler for shop id ' + this.shopLocalization.shopId);
    this._shopService.saveShopLocalization(this.shopLocalization).subscribe(shopLocalization => {
      console.debug('Saved i18n', shopLocalization);
      this.shopLocalization = shopLocalization;
      this.changed = false;
    });
  }

  onDiscardEvent() {
    console.debug('Discard handler for shop id ' + this.shopLocalization.shopId);
    this._shopService.getShopLocalization(this.shopLocalization.shopId).subscribe(shopLocalization => {
      console.debug('Refreshed i18n', shopLocalization);
      this.shopLocalization = shopLocalization;
      this.changed = false;
    });
  }

  onRefreshHandler() {
    console.debug('Refresh handler');
    this.onDiscardEvent();
  }

}
