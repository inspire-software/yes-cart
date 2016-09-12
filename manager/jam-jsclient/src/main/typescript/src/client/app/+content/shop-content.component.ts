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
import {Component, OnInit, OnDestroy, ViewChild} from '@angular/core';
import {NgIf} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {Router, ActivatedRoute} from '@angular/router';
import {ShopService, ContentService, ShopEventBus, Util} from './../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {ContentsComponent, ContentComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ContentSelectComponent} from './../shared/content/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {ShopVO, ContentWithBodyVO, ContentVO, AttrValueContentVO, Pair} from './../shared/model/index';
import {FormValidationEvent, Futures, Future} from './../shared/event/index';
import {Config} from './../shared/config/env.config';

@Component({
  selector: 'yc-shop-content',
  moduleId: module.id,
  templateUrl: 'shop-content.component.html',
  directives: [TAB_DIRECTIVES, NgIf, ContentsComponent, ContentSelectComponent, ContentComponent, ModalComponent, DataControlComponent ],
})

export class ShopContentComponent implements OnInit, OnDestroy {

  private static CONTENTS:string = 'contents';
  private static CONTENT:string = 'content';

  private forceShowAll:boolean = false;
  private viewMode:string = ShopContentComponent.CONTENTS;

  shop:ShopVO = null;

  private shopIdSub:any;
  private shopSub:any;

  private contents:Array<ContentVO> = [];
  private contentFilter:string;
  private contentFilterRequired:boolean = true;
  private contentFilterCapped:boolean = false;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;
  filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedContent:ContentVO;

  private contentEdit:ContentWithBodyVO;
  private contentEditAttributes:AttrValueContentVO[] = [];
  private contentAttributesUpdate:Array<Pair<AttrValueContentVO, boolean>>;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  @ViewChild('contentSelectComponent')
  contentSelectComponent:ContentSelectComponent;

  constructor(private _contentService:ContentService,
              private _shopService:ShopService,
              private _route: ActivatedRoute,
              private _router: Router) {
    console.debug('ShopContentComponent constructed');
    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.shop = shopevt;
      this.forceShowAll = false;
      this.getFilteredContents();
    });
  }

  changed:boolean = false;
  validForSave:boolean = false;

  newContentInstance():ContentWithBodyVO {
    return {
      contentId: 0,
      parentId: 0, parentName: null,
      rank: 500,
      name: '', guid: null, displayNames: [], description: null,
      uitemplate: 'content',
      availablefrom: null, availableto: null,
      uri: null, title: null, metakeywords: null, metadescription: null, displayTitles: [], displayMetakeywords: [], displayMetadescriptions: [],
      children: [],
      contentBodies: []
    };
  }

  ngOnInit() {
    console.debug('ShopContentComponent ngOnInit');

    this.shopIdSub = this._route.params.subscribe(params => {
      let shopId = params['shopId'];
      console.debug('ShopContentComponent shopId from params is ' + shopId);
      var _sub:any = this._shopService.getShop(+shopId).subscribe(shop => {
        console.debug('ShopContentComponent Retrieving existing shop', shop);
        ShopEventBus.getShopEventBus().emit(shop);
        _sub.unsubscribe();
      });
    });


    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredContents();
    }, this.delayedFilteringMs);
  }

  ngOnDestroy() {
    console.debug('ShopContentComponent ngOnDestroy');
    if (this.shopIdSub) {
      this.shopIdSub.unsubscribe();
    }
    this.shopSub.unsubscribe();
  }


  onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  getFilteredContents() {
    this.contentFilterRequired = !this.forceShowAll && (this.contentFilter == null || this.contentFilter.length < 2);

    console.debug('ShopContentComponent getFilteredContents' + (this.forceShowAll ? ' forcefully': ''));

    if (this.shop != null && !this.contentFilterRequired) {
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._contentService.getFilteredContent(this.shop.shopId, this.contentFilter, max).subscribe( allcontents => {
        console.debug('ShopContentComponent getFilteredContent', allcontents);
        this.contents = allcontents;
        this.selectedContent = null;
        this.contentEdit = null;
        this.viewMode = ShopContentComponent.CONTENTS;
        this.changed = false;
        this.validForSave = false;
        this.contentFilterCapped = this.contents.length >= max;
        _sub.unsubscribe();
      });
    } else {
      this.contents = [];
      this.selectedContent = null;
      this.contentEdit = null;
      this.contentEditAttributes = null;
      this.viewMode = ShopContentComponent.CONTENTS;
      this.changed = false;
      this.validForSave = false;
      this.contentFilterCapped = false;
    }
  }

  protected onRefreshHandler() {
    console.debug('ShopContentComponent refresh handler');
    this.getFilteredContents();
  }

  onContentSelected(data:ContentVO) {
    console.debug('ShopContentComponent onContentSelected', data);
    this.selectedContent = data;
  }

  onContentChanged(event:FormValidationEvent<Pair<ContentWithBodyVO, Array<Pair<AttrValueContentVO, boolean>>>>) {
    console.debug('ShopContentComponent onContentChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.contentEdit = event.source.first;
    this.contentAttributesUpdate = event.source.second;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredContents();
  }

  protected onBackToList() {
    console.debug('ShopContentComponent onBackToList handler');
    if (this.viewMode === ShopContentComponent.CONTENT) {
      this.contentEdit = null;
      this.viewMode = ShopContentComponent.CONTENTS;
    }
  }

  protected onViewTree() {
    console.debug('ShopContentComponent onViewTree handler');
    this.contentSelectComponent.showDialog();
  }

  protected onContentTreeDataSelected(event:FormValidationEvent<ContentVO>) {
    console.debug('ShopContentComponent onContentTreeDataSelected handler', event);
    if (event.valid) {
      this.contentFilter = event.source.name;
      this.getFilteredContents();
    }
  }

  protected onRowNew() {
    console.debug('ShopContentComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === ShopContentComponent.CONTENTS) {
      this.contentEdit = this.newContentInstance();
      this.contentEditAttributes = [];
      this.viewMode = ShopContentComponent.CONTENT;
    }
  }

  protected onRowDelete(row:any) {
    console.debug('ShopContentComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedContent != null) {
      this.onRowDelete(this.selectedContent);
    }
  }


  protected onRowEditContent(row:ContentVO) {
    console.debug('ShopContentComponent onRowEditContent handler', row);
    var _sub:any = this._contentService.getContentById(this.selectedContent.contentId).subscribe(content => {
      console.debug('ShopContentComponent getContentById', content);
      this.contentEdit = content;
      _sub.unsubscribe();
      this.contentEditAttributes = [];
      this.changed = false;
      this.validForSave = false;
      this.viewMode = ShopContentComponent.CONTENT;
      var _sub2:any = this._contentService.getContentAttributes(this.contentEdit.contentId).subscribe(attrs => {
        this.contentEditAttributes = attrs;
        _sub2.unsubscribe();
      });
    });
  }

  protected onRowEditSelected() {
    if (this.selectedContent != null) {
      this.onRowEditContent(this.selectedContent);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.contentEdit != null) {

        console.debug('ShopContentComponent Save handler content', this.contentEdit);

        var _sub:any = this._contentService.saveContent(this.contentEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.contentEdit.contentId;
              console.debug('ShopContentComponent content changed', rez);
              this.contentFilter = rez.guid;
              this.changed = false;
              this.selectedContent = rez;
              this.contentEdit = null;
              this.viewMode = ShopContentComponent.CONTENTS;

              if (pk > 0 && this.contentAttributesUpdate != null && this.contentAttributesUpdate.length > 0) {

                var _sub2:any = this._contentService.saveContentAttributes(this.contentAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  console.debug('ShopContentComponent content attributes updated', rez);
                  this.contentAttributesUpdate = null;
                  this.getFilteredContents();
                });
              } else {
                this.getFilteredContents();
              }
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    console.debug('ShopContentComponent discard handler');
    if (this.viewMode === ShopContentComponent.CONTENT) {
      if (this.selectedContent != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('ShopContentComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedContent != null) {
        console.debug('ShopContentComponent onDeleteConfirmationResult', this.selectedContent);

        var _sub:any = this._contentService.removeContent(this.selectedContent).subscribe(res => {
          _sub.unsubscribe();
          console.debug('ShopContentComponent removeContent', this.selectedContent);
          this.selectedContent = null;
          this.contentEdit = null;
          this.getFilteredContents();
        });
      }
    }
  }

}
