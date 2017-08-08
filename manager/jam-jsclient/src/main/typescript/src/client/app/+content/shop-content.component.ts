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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ShopService, ContentService, ShopEventBus } from './../shared/services/index';
import { ContentSelectComponent } from './../shared/content/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ShopVO, ShopUrlVO, ContentWithBodyVO, ContentVO, AttrValueContentVO, Pair } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-shop-content',
  moduleId: module.id,
  templateUrl: 'shop-content.component.html',
})

export class ShopContentComponent implements OnInit, OnDestroy {

  private static DEFAULT_PREVIEW_URL:string = 'http://localhost:8080/yes-shop/';
  private static DEFAULT_PREVIEW_CSS:string = 'wicket/resource/org.yes.cart.web.page.HomePage/::/::/::/::/::/style/yc-preview.css';

  private static CONTENTS:string = 'contents';
  private static CONTENT:string = 'content';

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = ShopContentComponent.CONTENTS;

  private shop:ShopVO = null;
  private shopUrl:ShopUrlVO = null;
  private shopPreviewUrl = ShopContentComponent.DEFAULT_PREVIEW_URL;
  private shopPreviewCss = ShopContentComponent.DEFAULT_PREVIEW_CSS;

  private shopIdSub:any;
  private shopSub:any;

  private contents:Array<ContentVO> = [];
  private contentFilter:string;
  private contentFilterRequired:boolean = true;
  private contentFilterCapped:boolean = false;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  private filterCap:number = Config.UI_FILTER_CAP;
  private filterNoCap:number = Config.UI_FILTER_NO_CAP;

  private selectedContent:ContentVO;

  private contentEdit:ContentWithBodyVO;
  private contentEditAttributes:AttrValueContentVO[] = [];
  private contentAttributesUpdate:Array<Pair<AttrValueContentVO, boolean>>;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  @ViewChild('contentSelectComponent')
  private contentSelectComponent:ContentSelectComponent;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _contentService:ContentService,
              private _shopService:ShopService,
              private _route: ActivatedRoute,
              private _router: Router) {
    LogUtil.debug('ShopContentComponent constructed');
    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      this.shop = shopevt;
      this.forceShowAll = false;
      this.getShopUrls();
      this.getFilteredContents();
    });
  }

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
    LogUtil.debug('ShopContentComponent ngOnInit');

    this.shopIdSub = this._route.params.subscribe(params => {
      let shopId = params['shopId'];
      LogUtil.debug('ShopContentComponent shopId from params is ' + shopId);
      var _sub:any = this._shopService.getShop(+shopId).subscribe(shop => {
        LogUtil.debug('ShopContentComponent Retrieving existing shop', shop);
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
    LogUtil.debug('ShopContentComponent ngOnDestroy');
    if (this.shopIdSub) {
      this.shopIdSub.unsubscribe();
    }
    this.shopSub.unsubscribe();
  }


  protected onFilterChange(event:any) {

    this.delayedFiltering.delay();

  }

  protected onRefreshHandler() {
    LogUtil.debug('ShopContentComponent refresh handler');
    this.getFilteredContents();
  }

  protected onContentSelected(data:ContentVO) {
    LogUtil.debug('ShopContentComponent onContentSelected', data);
    this.selectedContent = data;
  }

  protected onContentChanged(event:FormValidationEvent<Pair<ContentWithBodyVO, Array<Pair<AttrValueContentVO, boolean>>>>) {
    LogUtil.debug('ShopContentComponent onContentChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.contentEdit = event.source.first;
    this.contentAttributesUpdate = event.source.second;
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }


  protected onSearchParent() {
    this.contentFilter = '^';
    this.searchHelpShow = false;
  }

  protected onSearchURI() {
    this.contentFilter = '@';
    this.searchHelpShow = false;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredContents();
  }

  protected onBackToList() {
    LogUtil.debug('ShopContentComponent onBackToList handler');
    if (this.viewMode === ShopContentComponent.CONTENT) {
      this.contentEdit = null;
      this.viewMode = ShopContentComponent.CONTENTS;
    }
  }

  protected onViewTree() {
    LogUtil.debug('ShopContentComponent onViewTree handler', this.selectedContent);
    this.contentSelectComponent.showDialog(this.selectedContent != null ? this.selectedContent.contentId : 0);
  }

  protected onContentTreeDataSelected(event:FormValidationEvent<ContentVO>) {
    LogUtil.debug('ShopContentComponent onContentTreeDataSelected handler', event);
    if (event.valid) {
      this.contentFilter = '^' + event.source.name;
      this.getFilteredContents();
    }
  }

  protected onRowNew() {
    LogUtil.debug('ShopContentComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === ShopContentComponent.CONTENTS) {
      this.contentEdit = this.newContentInstance();
      this.contentEditAttributes = [];
      this.viewMode = ShopContentComponent.CONTENT;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('ShopContentComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedContent != null) {
      this.onRowDelete(this.selectedContent);
    }
  }


  protected onRowEditContent(row:ContentVO) {
    LogUtil.debug('ShopContentComponent onRowEditContent handler', row);
    var _sub:any = this._contentService.getContentById(this.selectedContent.contentId).subscribe(content => {
      LogUtil.debug('ShopContentComponent getContentById', content);
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

        LogUtil.debug('ShopContentComponent Save handler content', this.contentEdit);

        var _sub:any = this._contentService.saveContent(this.contentEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.contentEdit.contentId;
              LogUtil.debug('ShopContentComponent content changed', rez);
              this.changed = false;
              this.selectedContent = rez;
              this.contentEdit = null;
              this.viewMode = ShopContentComponent.CONTENTS;

              if (pk > 0 && this.contentAttributesUpdate != null && this.contentAttributesUpdate.length > 0) {

                var _sub2:any = this._contentService.saveContentAttributes(this.contentAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  LogUtil.debug('ShopContentComponent content attributes updated', rez);
                  this.contentAttributesUpdate = null;
                  this.getFilteredContents();
                });
              } else {
                if (this.contentFilter == null || this.contentFilter == '') {
                  this.contentFilter = rez.guid;
                }
                this.getFilteredContents();
              }
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('ShopContentComponent discard handler');
    if (this.viewMode === ShopContentComponent.CONTENT) {
      if (this.selectedContent != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopContentComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedContent != null) {
        LogUtil.debug('ShopContentComponent onDeleteConfirmationResult', this.selectedContent);

        var _sub:any = this._contentService.removeContent(this.selectedContent).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('ShopContentComponent removeContent', this.selectedContent);
          this.selectedContent = null;
          this.contentEdit = null;
          this.getFilteredContents();
        });
      }
    }
  }

  protected onClearFilter() {

    this.contentFilter = '';
    this.getFilteredContents();

  }


  private getFilteredContents() {
    this.contentFilterRequired = !this.forceShowAll && (this.contentFilter == null || this.contentFilter.length < 2);

    LogUtil.debug('ShopContentComponent getFilteredContents' + (this.forceShowAll ? ' forcefully': ''));

    if (this.shop != null && !this.contentFilterRequired) {
      this.loading = true;
      let max = this.forceShowAll ? this.filterNoCap : this.filterCap;
      var _sub:any = this._contentService.getFilteredContent(this.shop.shopId, this.contentFilter, max).subscribe( allcontents => {
        LogUtil.debug('ShopContentComponent getFilteredContent', allcontents);
        this.contents = allcontents;
        this.selectedContent = null;
        this.contentEdit = null;
        this.viewMode = ShopContentComponent.CONTENTS;
        this.changed = false;
        this.validForSave = false;
        this.contentFilterCapped = this.contents.length >= max;
        this.loading = false;
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

  private getShopUrls() {
    LogUtil.debug('ShopContentComponent get urls', this.shop);

    this.shopUrl = null;
    this.shopPreviewUrl = ShopContentComponent.DEFAULT_PREVIEW_URL;
    this.shopPreviewCss = ShopContentComponent.DEFAULT_PREVIEW_CSS;

    if (this.shop != null && this.shop.shopId > 0) {

      this._shopService.getShopUrls(this.shop.shopId).subscribe(shopUrl => {

        LogUtil.debug('ShopContentComponent urls', this.shopUrl);
        this.shopUrl = shopUrl;
        this.shopPreviewUrl = shopUrl.previewUrl;
        this.shopPreviewCss = shopUrl.previewCss;

      });

    }

  }


}
