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
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { ShopVO, ShopSummaryEmailTemplateVO, Pair } from './../../shared/model/index';
import { MailPreviewComponent } from './../../shared/content/index';
import { Futures, Future } from './../../shared/event/index';
import { Config } from './../../shared/config/env.config';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-mailtemplates',
  moduleId: module.id,
  templateUrl: 'mailtemplates.component.html',
})

export class ShopMailTemplatesComponent implements OnInit, OnDestroy {

  @Output() dataSelected: EventEmitter<Pair<ShopSummaryEmailTemplateVO, string>> = new EventEmitter<Pair<ShopSummaryEmailTemplateVO, string>>();

  private _shop:ShopVO;
  private _emailTemplates:Array<ShopSummaryEmailTemplateVO>;
  private _filter:string;
  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private filteredTemplates:Array<ShopSummaryEmailTemplateVO>;

  //paging
  private maxSize:number = Config.UI_TABLE_PAGE_NUMS; // tslint:disable-line:no-unused-variable
  private itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  private totalItems:number = 0;
  private currentPage:number = 1; // tslint:disable-line:no-unused-variable
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  private pageStart:number = 0;
  private pageEnd:number = this.itemsPerPage;

  private selectedName:string;

  @ViewChild('mailPreviewComponent')
  private mailPreviewComponent:MailPreviewComponent;

  constructor() {
    LogUtil.debug('ShopMailTemplatesComponent constructed');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterTemplates();
    }, this.delayedFilteringMs);
  }

  @Input()
  set emailTemplates(emailTemplates:Array<ShopSummaryEmailTemplateVO>) {
    this._emailTemplates = emailTemplates;
    this.delayedFiltering.delay();
  }

  get emailTemplates(): Array<ShopSummaryEmailTemplateVO> {
    return this._emailTemplates;
  }

  @Input()
  set shop(shop:ShopVO) {
    this._shop = shop;
  }

  get shop():ShopVO  {
    return this._shop;
  }

  @Input()
  set filter(filter:string) {
    this._filter = filter ? filter.toLowerCase() : null;
    this.delayedFiltering.delay();
  }

  ngOnInit() {
    LogUtil.debug('ShopMailTemplatesComponent ngOnInit shop', this.shop);
  }

  ngOnDestroy() {
    LogUtil.debug('ShopMailTemplatesComponent ngOnDestroy');
  }

  onClickCms(template:ShopSummaryEmailTemplateVO, name:string):void {
    LogUtil.debug('ShopMailTemplatesComponent onClickCms', template);
    this.dataSelected.emit({ first: template, second: name });
  }

  onEmailPreview(template:ShopSummaryEmailTemplateVO):void {
    if (template != null && !template.part && !template.image) {
      this.selectedName = template.name;
      this.mailPreviewComponent.showDialog(this.shop.shopId, template.name, '^' + this.shop.code);
    }
  }



  protected resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onPageChanged(event:any) {
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }


  private filterTemplates() {

    if (this._emailTemplates) {
      if (this._filter) {
        this.filteredTemplates = this._emailTemplates.filter(template =>
          template.name.toLowerCase().indexOf(this._filter) !== -1
        );
      } else {
        this.filteredTemplates = this._emailTemplates.slice(0, this._emailTemplates.length);
      }
    } else {
      this.filteredTemplates = [];
    }

    if (this.filteredTemplates === null) {
      this.filteredTemplates = [];
    }

    let _sortProp = 'name';
    let _sortOrder = 1;

    let _sort = function(a:any, b:any):number {
      return (a[_sortProp] > b[_sortProp] ? 1 : -1) * _sortOrder;
    };

    this.filteredTemplates.sort(_sort);

    let _total = this.filteredTemplates.length;
    this.totalItems = _total;

    LogUtil.debug('filter', this.filteredTemplates, _total, this.pageStart, this.pageEnd);

    if (_total > 0) {
      this.resetLastPageEnd();
    }

    LogUtil.debug('filter', this.filteredTemplates, _total, this.pageStart, this.pageEnd);

  }

}
