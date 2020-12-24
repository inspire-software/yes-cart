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
import { Component, OnInit, OnChanges, ViewChild } from '@angular/core';
import { AttrValueSystemVO, Pair } from './../../shared/model/index';
import { SystemService, UserEventBus } from './../../shared/services/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'cw-system-preferences',
  templateUrl: 'system-preferences.component.html',
})

export class SystemPreferencesComponent implements OnInit, OnChanges {

  public system:any = { systemId: 100, name: 'YC' };

  public systemAttributes:Array<AttrValueSystemVO>;
  public attributeFilter:string;
  public attributeSort:Pair<string, boolean> = { first: 'name', second: false };

  public changed:boolean = false;
  public validForSave:boolean = false;

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  public selectedRow:AttrValueSystemVO;

  public imageOnlyMode:boolean = false;

  private update:Array<Pair<AttrValueSystemVO, boolean>>;

  public searchHelpShow:boolean = false;

  public loading:boolean = false;

  public includeSecure:boolean = false;
  private changeIncludeSecure:boolean = false;

  /**
   * Construct shop attribute panel
   *
   * @param _systemService system service
   */
  constructor(private _systemService:SystemService) {
    LogUtil.debug('ShopAttributeComponent constructed');

    this.update = [];

  }

  /** {@inheritDoc} */
  public ngOnInit() {
    LogUtil.debug('ShopAttributeComponent ngOnInit');
    this.onRefreshHandler();
  }

  ngOnChanges(changes:any) {
    LogUtil.debug('ShopAttributeComponent ngOnChanges', changes);
    this.onRefreshHandler();
  }

  onIncludeSecure() {
    this.changeIncludeSecure = !this.includeSecure;
    this.getSystemPreferences();
  }

  onImageOnlyMode() {
    this.imageOnlyMode = !this.imageOnlyMode;
  }

  onRowDeleteSelected() {
    if (this.selectedRow != null && !this.selectedRow.attribute.mandatory) {
      this.attributeValuesComponent.onRowDeleteSelected();
    }
  }

  onRowEditSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowEditSelected();
    }
  }

  onPageSelected(page:number) {
    LogUtil.debug('ShopAttributeComponent onPageSelected', page);
  }

  onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('ShopAttributeComponent ononSortSelected', sort);
    if (sort == null) {
      this.attributeSort = { first: 'name', second: false };
    } else {
      this.attributeSort = sort;
    }
  }

  onSelectRow(row:AttrValueSystemVO) {
    LogUtil.debug('ShopAttributeComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  onDataChange(event:any) {
    this.validForSave = event.valid;
    this.update = event.source;
    this.changed = true;
    LogUtil.debug('ShopAttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  onSaveHandler() {
    LogUtil.debug('ShopAttributeComponent Save handler');
    if (this.update) {

      LogUtil.debug('ShopAttributeComponent Save handler update', this.update);

      this.loading = true;
      this._systemService.saveSystemAttributes(this.update, this.includeSecure).subscribe(
          rez => {
            LogUtil.debug('ShopAttributeComponent attributes', rez);
            this.systemAttributes = rez;
            this.changed = false;
            this.validForSave = false;
            this.selectedRow = null;
            this.loading = false;
        }
      );
    }
  }

  onDiscardEventHandler() {
    LogUtil.debug('ShopAttributeComponent discard handler');
    this.onRefreshHandler();
  }

  onRefreshHandler() {
    LogUtil.debug('ShopAttributeComponent refresh handler');
    this.changeIncludeSecure = this.includeSecure;
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getSystemPreferences();
    }
  }

  onClearFilter() {

    this.attributeFilter = '';

  }

  onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  onSearchValues() {
    this.searchHelpShow = false;
    this.attributeFilter = '###';
  }

  onSearchValuesNew() {
    this.searchHelpShow = false;
    this.attributeFilter = '##0';
  }

  onSearchValuesNewOnly() {
    this.searchHelpShow = false;
    this.attributeFilter = '#00';
  }

  onSearchValuesChanges() {
    this.searchHelpShow = false;
    this.attributeFilter = '#0#';
  }


  private getSystemPreferences() {
    LogUtil.debug('ShopAttributeComponent get attributes');

    this.loading = true;
    this._systemService.getSystemPreferences(this.changeIncludeSecure).subscribe(systemAttributes => {

      LogUtil.debug('ShopAttributeComponent attributes', systemAttributes);
      this.systemAttributes = systemAttributes;
      this.changed = false;
      this.selectedRow = null;
      this.loading = false;
      this.includeSecure = this.changeIncludeSecure; // change only if we get successful result
    });

  }

}
