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
import {OnInit, Component} from 'angular2/core';
import {RouteParams} from 'angular2/router';
import {ShopService} from '../../service/shop_service';
import {DataControl} from '../common/data_control';
import {Modal, ModalResult, ModalAction} from '../common/modal';
import {ShopUrlVO, UrlVO} from '../../model/shop';
import {PAGINATION_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {CORE_DIRECTIVES, FORM_DIRECTIVES} from 'angular2/common';
import {Util} from '../../service/util';

@Component({
  selector: 'shop-url-panel',
  moduleId: module.id,
  templateUrl: './shop_url_panel.html',
  styleUrls: ['./shop_url_panel.css'],
  directives: [DataControl, PAGINATION_DIRECTIVES, FORM_DIRECTIVES, CORE_DIRECTIVES, Modal],
  providers: [ShopService]
})

export class ShopUrlPanel implements OnInit {

  //paging
  protected maxSize:number = 10;
  protected totalItems:number = 0;
  protected currentPage:number = 1;


  protected shopUrl:ShopUrlVO;

  protected changed:boolean = false;

  protected deleteConfirmationModalDialog:Modal ;
  protected editUrlModalDialog:Modal ;


  protected urlToDelete:string ;
  protected urlToEdit:UrlVO = {'urlId': 0, 'url': '', 'theme' : ''} ;

  /**
   * Construct shop url panel
   *
   * @param _shopService shop service
   * @param _routeParams router parameters to read shop id
   */
  constructor(private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Shop url constructed');
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    this.getShopUrls();
  }


  /**
   * Row delete handler.
   * @param row url to delete.
   */
  protected onRowDelete(row:UrlVO) {
    console.debug('onRowDelete handler ' + JSON.stringify(row));
    this.urlToDelete = row.url;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowEdit(row:UrlVO) {
    console.debug('onRowEdit handler ' + JSON.stringify(row));
    this.urlToEdit = Util.clone(row) ;
    this.editUrlModalDialog.show();
  }

  protected onRowNew() {
    console.debug('onRowNew handler ');
    this.urlToEdit = Util.clone({'urlId': 0, 'url': '', 'theme' : ''});
    this.editUrlModalDialog.show();
  }


  protected onSaveHandler() {
    console.debug('Save handler for shop id ');
    this._shopService.saveShopUrls(this.shopUrl).subscribe(
      rez => {
        this.shopUrl = rez;
        this.changed = false;
        this.totalItems = this.shopUrl.urls.length;
        this.currentPage = 1;
      }
    );
  }

  protected onDiscardEventHandler() {
    console.debug('Discard hander');
    this.getShopUrls();
  }

  protected onRefreshHandler() {
    console.debug('Refresh handler');
    this.getShopUrls();
  }

  protected deleteConfirmationModalDialogLoaded(modal: Modal) {
    console.debug('deleteConfirmationModalDialogLoaded');
    // Here you get a reference to the modal so you can control it programmatically
    this.deleteConfirmationModalDialog = modal;
  }

  protected editUrlModalLoaded(modal: Modal) {
    console.debug('editUrlModalLoaded');
    this.editUrlModalDialog = modal;
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('onCancelConfirmation modal result is ' + JSON.stringify(modalresult));
    if (ModalAction.POSITIVE === modalresult.action) {
      let idx = this.shopUrl.urls.findIndex(urlVo =>  {return urlVo.url === this.urlToDelete;} );
      console.debug('onCancelConfirmation index in array of urls ' + idx);
      this.shopUrl.urls.splice(idx, 1);
      this.changed = true;
    }
  }

  protected onEditUrlModalResult(modalresult: ModalResult) {
    console.debug('onEditUrlModalResult modal result is ' + JSON.stringify(modalresult));
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.urlToEdit.urlId === 0) { // add new
        console.debug('onEditUrlModalResult add new url ');
        this.shopUrl.urls.push(this.urlToEdit);
      } else { // edit existing
        console.debug('onEditUrlModalResult update existing ');
        let idx = this.shopUrl.urls.findIndex(urlVo =>  {return urlVo.urlId === this.urlToEdit.urlId;} );
        this.shopUrl.urls[idx] = this.urlToEdit;
      }
      this.changed = true;
    }
  }

  /**
   * Read urls, that belong to shop.
   */
  private getShopUrls() {

    let shopId = this._routeParams.get('shopId');

    console.debug('shopId from params is ' + shopId);

    this._shopService.getShopUrls(+shopId).subscribe(shopUrl => {

      this.shopUrl = shopUrl;
      this.changed = false;

      this.totalItems = this.shopUrl.urls.length;
      this.currentPage = 1;

    });
  }

}
