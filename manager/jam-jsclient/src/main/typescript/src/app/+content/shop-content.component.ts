/*
 * Copyright 2009 Inspire-Software.com
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
import { ShopService, ContentService, UserEventBus, ShopEventBus, I18nEventBus } from './../shared/services/index';
import { ContentMinSelectComponent } from './../shared/content/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { ShopVO, ShopUrlVO, ShopLanguagesVO, ContentWithBodyVO, ContentVO, AttrValueContentVO, Pair, SearchResultVO, ShopSummaryEmailTemplateVO } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../../environments/environment';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'cw-shop-content',
  templateUrl: 'shop-content.component.html',
})

export class ShopContentComponent implements OnInit, OnDestroy {

  private static DEFAULT_PREVIEW_URL:string = 'http://localhost:8080/';
  private static DEFAULT_PREVIEW_CSS:string = 'wicket/resource/org.yes.cart.web.page.HomePage/::/::/::/::/::/style/preview.css';

  private static CONTENTS:string = 'contents';
  private static CONTENT:string = 'content';
  private static MAIL:string = 'mail';

  public searchHelpShow:boolean = false;
  public forceShowAll:boolean = false;
  public viewMode:string = ShopContentComponent.CONTENTS;

  private shopId:string = null;
  public shop:ShopVO = null;
  public shopUrl:ShopUrlVO = null;
  public shopPreviewUrl = ShopContentComponent.DEFAULT_PREVIEW_URL;
  public shopPreviewCss = ShopContentComponent.DEFAULT_PREVIEW_CSS;
  public shopLanguages:ShopLanguagesVO = null;
  public shopSupportedLanguages: Array<string> = [];
  public shopTemplates: Array<ShopSummaryEmailTemplateVO> = [];

  private shopSub:any;
  private userSub:any;

  public contents:SearchResultVO<ContentVO>;
  public contentFilter:string;
  public contentFilterRequired:boolean = true;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  public selectedContent:ContentVO;

  public contentEdit:ContentWithBodyVO;
  public contentEditAttributes:AttrValueContentVO[] = [];
  private contentAttributesUpdate:Array<Pair<AttrValueContentVO, boolean>>;

  public mailFilter:string;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  public deleteValue:String;

  @ViewChild('contentSelectComponent')
  private contentSelectComponent:ContentMinSelectComponent;

  public loading:boolean = false;

  public changed:boolean = false;
  public validForSave:boolean = false;

  constructor(private _contentService:ContentService,
              private _shopService:ShopService,
              private _route: ActivatedRoute,
              private _router: Router) {
    LogUtil.debug('ShopContentComponent constructed');
    this.contents = this.newSearchResultInstance();
    this.shopSub = ShopEventBus.getShopEventBus().shopUpdated$.subscribe(shopevt => {
      LogUtil.debug('ShopContentComponent new shop event', shopevt);
      this.shop = shopevt;
      //this.forceShowAll = false;
      this.getShopConfigs();
      this.onRefreshHandler();
    });
    this.userSub = UserEventBus.getUserEventBus().userUpdated$.subscribe(userevt => {
      if (userevt != null) {
        LogUtil.debug('ShopContentComponent new user event, retrieving shop by shopId', userevt, this.shopId);
        this.emitShopByIdIfNecessary();
      }
    });
  }

  newContentInstance():ContentWithBodyVO {
    return {
      contentId: 0,
      parentId: 0, parentName: null,
      rank: 500,
      name: '', guid: null, displayNames: [], description: null,
      uitemplate: 'content',
      disabled: false, availablefrom: null, availableto: null,
      uri: null, title: null, metakeywords: null, metadescription: null, displayTitles: [], displayMetakeywords: [], displayMetadescriptions: [],
      children: [],
      contentBodies: []
    };
  }

  newSearchResultInstance():SearchResultVO<ContentVO> {
    return {
      searchContext: {
        parameters: {
          filter: [],
          statuses: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: null,
        sortDesc: false
      },
      items: [],
      total: 0
    };
  }

  ngOnInit() {
    LogUtil.debug('ShopContentComponent ngOnInit');

    this._route.params.subscribe(params => {
      this.shopId = params['shopId'];
      LogUtil.debug('ShopContentComponent shopId from params is ' + this.shopId);
      this.emitShopByIdIfNecessary();
    });

    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredContents();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    LogUtil.debug('ShopContentComponent ngOnDestroy');
    this.shopSub.unsubscribe();
    this.userSub.unsubscribe();
  }


  onContentFilterChange(event:any) {
    this.contents.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFiltering.delay();
  }

  onMailFilterChange(event:any) {
    this.mailFilter = event;
  }

  onRefreshHandler() {
    LogUtil.debug('ShopContentComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      if (this.viewMode == ShopContentComponent.MAIL) {
        this.getShopEmailTemplates();
      } else {
        this.getFilteredContents();
      }
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('ShopContentComponent onPageSelected', page);
    this.contents.searchContext.start = page;
    this.delayedFiltering.delay();
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('ShopContentComponent ononSortSelected', sort);
    if (sort == null) {
      this.contents.searchContext.sortBy = null;
      this.contents.searchContext.sortDesc = false;
    } else {
      this.contents.searchContext.sortBy = sort.first;
      this.contents.searchContext.sortDesc = sort.second;
    }
    this.delayedFiltering.delay();
  }

  onContentSelected(data:ContentVO) {
    LogUtil.debug('ShopContentComponent onContentSelected', data);
    this.selectedContent = data;
  }

  onContentChanged(event:FormValidationEvent<Pair<ContentWithBodyVO, Array<Pair<AttrValueContentVO, boolean>>>>) {
    LogUtil.debug('ShopContentComponent onContentChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.contentEdit = event.source.first;
    this.contentAttributesUpdate = event.source.second;
  }

  onTemplateSelected(event:Pair<ShopSummaryEmailTemplateVO, string>) {
    this.contentFilter = event.second;
    this.viewMode = ShopContentComponent.CONTENTS;
    this.getFilteredContents();
  }

  onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  onSearchURI() {
    this.contentFilter = '@';
    this.searchHelpShow = false;
  }

  onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredContents();
  }

  onBackToList() {
    LogUtil.debug('ShopContentComponent onBackToList handler');
    if (this.viewMode !== ShopContentComponent.CONTENTS) {
      this.contentEdit = null;
      this.viewMode = ShopContentComponent.CONTENTS;
    }
  }

  onMailClick() {
    if (this.viewMode == ShopContentComponent.CONTENTS) {
      this.viewMode = ShopContentComponent.MAIL;
    } else {
      this.viewMode = ShopContentComponent.CONTENTS;
    }
    this.onRefreshHandler();
  }

  onViewTree() {
    LogUtil.debug('ShopContentComponent onViewTree handler', this.selectedContent);
    this.contentSelectComponent.showDialog(this.selectedContent != null ? this.selectedContent.contentId : 0);
  }

  onContentTreeDataSelected(event:FormValidationEvent<ContentVO>) {
    LogUtil.debug('ShopContentComponent onContentTreeDataSelected handler', event);
    if (event.valid) {
      this.contentFilter = '^' + event.source.guid;
      this.getFilteredContents();
    }
  }

  onRowNew() {
    LogUtil.debug('ShopContentComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === ShopContentComponent.CONTENTS) {
      this.contentEdit = this.newContentInstance();
      this.contentEditAttributes = [];
      this.viewMode = ShopContentComponent.CONTENT;
    }
  }

  onRowDelete(row:any) {
    LogUtil.debug('ShopContentComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  onRowDeleteSelected() {
    if (this.selectedContent != null) {
      this.onRowDelete(this.selectedContent);
    }
  }


  onRowEditContent(row:ContentVO) {
    LogUtil.debug('ShopContentComponent onRowEditContent handler', row);
    this.loading = true;
    this._contentService.getContentById(this.selectedContent.contentId).subscribe(content => {
      LogUtil.debug('ShopContentComponent getContentById', content);
      this.contentEdit = content;
      this.contentEditAttributes = [];
      this.changed = false;
      this.validForSave = false;
      this.viewMode = ShopContentComponent.CONTENT;
      this._contentService.getContentAttributes(this.contentEdit.contentId).subscribe(attrs => {
        this.contentEditAttributes = attrs;
        this.loading = false;
      });
    });
  }

  onRowEditSelected() {
    if (this.selectedContent != null) {
      this.onRowEditContent(this.selectedContent);
    }
  }

  onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.contentEdit != null) {

        LogUtil.debug('ShopContentComponent Save handler content', this.contentEdit);

        this.loading = true;
        this._contentService.saveContent(this.contentEdit).subscribe(
            rez => {
              let pk = this.contentEdit.contentId;
              LogUtil.debug('ShopContentComponent content changed', rez);
              this.changed = false;
              this.selectedContent = rez;
              this.contentEdit = null;
              this.loading = false;
              this.viewMode = ShopContentComponent.CONTENTS;

              if (pk > 0 && this.contentAttributesUpdate != null && this.contentAttributesUpdate.length > 0) {

                this.loading = true;
                this._contentService.saveContentAttributes(this.contentAttributesUpdate).subscribe(rez => {
                  LogUtil.debug('ShopContentComponent content attributes updated', rez);
                  this.contentAttributesUpdate = null;
                  this.loading = false;
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

  onDiscardEventHandler() {
    LogUtil.debug('ShopContentComponent discard handler');
    if (this.viewMode === ShopContentComponent.CONTENT) {
      if (this.selectedContent != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ShopContentComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedContent != null) {
        LogUtil.debug('ShopContentComponent onDeleteConfirmationResult', this.selectedContent);

        this.loading = true;
        this._contentService.removeContent(this.selectedContent).subscribe(res => {
          LogUtil.debug('ShopContentComponent removeContent', this.selectedContent);
          this.selectedContent = null;
          this.contentEdit = null;
          this.loading = false;
          this.getFilteredContents();
        });
      }
    }
  }

  onClearContentFilter() {

    this.contentFilter = '';
    this.getFilteredContents();

  }

  onClearMailFilter() {

    this.mailFilter = '';

  }

  private emitShopByIdIfNecessary():void {
    LogUtil.debug('ShopContentComponent getShopByIdIfNecessary', this.shopId);
    if (this.shopId != null  && (this.shop == null || this.shop.shopId != +this.shopId)) {
      this.loading = true;
      this._shopService.getShop(+this.shopId).subscribe(shop => {
        LogUtil.debug('ShopContentComponent getShopByIdIfNecessary res', this.shopId, shop);
        ShopEventBus.getShopEventBus().emit(shop);
        this.loading = false;
      });
    }
  }

  private getFilteredContents() {
    this.contentFilterRequired = !this.forceShowAll && (this.contentFilter == null || this.contentFilter.length < 2);

    LogUtil.debug('ShopContentComponent getFilteredContents' + (this.forceShowAll ? ' forcefully': ''));

    if (this.shop != null && !this.contentFilterRequired) {
      this.loading = true;

      this.contents.searchContext.parameters.filter = [ this.contentFilter ];
      this.contents.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

      this._contentService.getFilteredContent(this.shop.shopId, this.contents.searchContext).subscribe( allcontents => {
        LogUtil.debug('ShopContentComponent getFilteredContent', allcontents);
        this.contents = allcontents;
        this.selectedContent = null;
        this.contentEdit = null;
        this.viewMode = ShopContentComponent.CONTENTS;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
      });
    } else {
      this.contents = this.newSearchResultInstance();
      this.selectedContent = null;
      this.contentEdit = null;
      this.contentEditAttributes = null;
      this.viewMode = ShopContentComponent.CONTENTS;
      this.changed = false;
      this.validForSave = false;
    }
  }

  private getShopConfigs() {
    LogUtil.debug('ShopContentComponent getShopConfigs', this.shop);

    this.shopUrl = null;
    this.shopPreviewUrl = ShopContentComponent.DEFAULT_PREVIEW_URL;
    this.shopPreviewCss = ShopContentComponent.DEFAULT_PREVIEW_CSS;

    if (this.shop != null && this.shop.shopId > 0) {

      this._shopService.getShopUrls(this.shop.shopId).subscribe(shopUrl => {

        LogUtil.debug('ShopContentComponent urls', shopUrl);
        this.shopUrl = shopUrl;
        this.shopPreviewUrl = shopUrl.previewUrl;
        this.shopPreviewCss = shopUrl.previewCss;

      });

      this._shopService.getShopLanguages(this.shop.shopId).subscribe( langs => {

        LogUtil.debug('ShopContentComponent langs', langs);
        this.shopLanguages = langs;
        this.shopSupportedLanguages = langs.supported;

      });

    }

  }

  private getShopEmailTemplates() {
    LogUtil.debug('ShopContentComponent getShopEmailTemplates', this.shop);

    this.shopTemplates = [];

    if (this.shop != null && this.shop.shopId > 0) {

      let lang = I18nEventBus.getI18nEventBus().current();

      this.loading = true;
      this._shopService.getShopSummary(this.shop.shopId, lang).subscribe(summary => {

        this.shopTemplates = summary != null && summary.emailTemplates != null ? summary.emailTemplates : [];
        LogUtil.debug('ShopContentComponent summary', summary, this.shopTemplates);
        this.loading = false;

      });

    }

  }


}
