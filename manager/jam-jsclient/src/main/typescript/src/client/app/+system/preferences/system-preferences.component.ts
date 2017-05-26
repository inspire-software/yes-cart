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
import { Component, OnInit, OnChanges, ViewChild } from '@angular/core';
import { AttrValueSystemVO, Pair } from './../../shared/model/index';
import { SystemService } from './../../shared/services/index';
import { AttributeValuesComponent } from './../../shared/attributes/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-system-preferences',
  moduleId: module.id,
  templateUrl: 'system-preferences.component.html',
})

export class SystemPreferencesComponent implements OnInit, OnChanges {

  private system:any = { systemId: 100, name: 'YC' }; // tslint:disable-line:no-unused-variable

  private systemAttributes:Array<AttrValueSystemVO>;
  private attributeFilter:string;

  private changed:boolean = false;
  private validForSave:boolean = false;

  @ViewChild('attributeValuesComponent')
  private attributeValuesComponent:AttributeValuesComponent;

  private selectedRow:AttrValueSystemVO;

  private update:Array<Pair<AttrValueSystemVO, boolean>>;

  private searchHelpShow:boolean = false;

  private loading:boolean = false;

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

  protected onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowDeleteSelected();
    }
  }

  protected onRowEditSelected() {
    if (this.selectedRow != null) {
      this.attributeValuesComponent.onRowEditSelected();
    }
  }

  protected onSelectRow(row:AttrValueSystemVO) {
    LogUtil.debug('ShopAttributeComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
    }
  }

  protected onDataChange(event:any) {
    this.validForSave = event.valid;
    this.update = event.source;
    this.changed = true;
    LogUtil.debug('ShopAttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  protected onSaveHandler() {
    LogUtil.debug('ShopAttributeComponent Save handler');
    if (this.update) {

      LogUtil.debug('ShopAttributeComponent Save handler update', this.update);

      var _sub:any = this._systemService.saveSystemAttributes(this.update).subscribe(
          rez => {
            LogUtil.debug('ShopAttributeComponent attributes', rez);
            this.systemAttributes = rez;
            this.changed = false;
            this.validForSave = false;
            this.selectedRow = null;
            _sub.unsubscribe();
        }
      );
    }
  }

  protected onDiscardEventHandler() {
    LogUtil.debug('ShopAttributeComponent discard handler');
    this.onRefreshHandler();
  }

  protected onRefreshHandler() {
    LogUtil.debug('ShopAttributeComponent refresh handler');
    this.getSystemPreferences();
  }


  protected onClearFilter() {

    this.attributeFilter = '';

  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onSearchValues() {
    this.searchHelpShow = false;
    this.attributeFilter = '###';
  }

  protected onSearchValuesNew() {
    this.searchHelpShow = false;
    this.attributeFilter = '##0';
  }

  protected onSearchValuesNewOnly() {
    this.searchHelpShow = false;
    this.attributeFilter = '#00';
  }

  protected onSearchValuesChanges() {
    this.searchHelpShow = false;
    this.attributeFilter = '#0#';
  }


  private getSystemPreferences() {
    LogUtil.debug('ShopAttributeComponent get attributes');

    this.loading = true;
    var _sub:any = this._systemService.getSystemPreferences().subscribe(systemAttributes => {

      LogUtil.debug('ShopAttributeComponent attributes', systemAttributes);
      this.systemAttributes = systemAttributes;
      this.changed = false;
      this.selectedRow = null;
      this.loading = false;
      _sub.unsubscribe();

    });

  }

}
