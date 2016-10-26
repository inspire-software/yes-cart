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
import {Component, OnInit, OnChanges, ViewChild} from '@angular/core';
import {CORE_DIRECTIVES } from '@angular/common';
import {REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {PaginationComponent} from './../../shared/pagination/index';
import {AttrValueSystemVO, Pair} from './../../shared/model/index';
import {SystemService} from './../../shared/services/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {AttributeValuesComponent} from './../../shared/attributes/index';
import {ModalComponent} from './../../shared/modal/index';

@Component({
  selector: 'yc-system-preferences',
  moduleId: module.id,
  templateUrl: 'system-preferences.component.html',
  directives: [DataControlComponent, PaginationComponent, REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES, ModalComponent, AttributeValuesComponent]
})

export class SystemPreferencesComponent implements OnInit, OnChanges {

  system:any = { systemId: 100, name: 'YC' };

  systemAttributes:Array<AttrValueSystemVO>;
  attributeFilter:string;

  changed:boolean = false;
  validForSave:boolean = false;

  @ViewChild('attributeValuesComponent')
  attributeValuesComponent:AttributeValuesComponent;

  selectedRow:AttrValueSystemVO;

  update:Array<Pair<AttrValueSystemVO, boolean>>;

  /**
   * Construct shop attribute panel
   *
   * @param _systemService system service
   */
  constructor(private _systemService:SystemService) {
    console.debug('ShopAttributeComponent constructed');

    this.update = [];

  }

  /** {@inheritDoc} */
  public ngOnInit() {
    console.debug('ShopAttributeComponent ngOnInit');
    this.onRefreshHandler();
  }

  ngOnChanges(changes:any) {
    console.debug('ShopAttributeComponent ngOnChanges', changes);
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
    console.debug('ShopAttributeComponent onSelectRow handler', row);
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
    console.debug('ShopAttributeComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }

  protected onSaveHandler() {
    console.debug('ShopAttributeComponent Save handler');
    if (this.update) {

      console.debug('ShopAttributeComponent Save handler update', this.update);

      var _sub:any = this._systemService.saveShopAttributes(this.update).subscribe(
          rez => {
            console.debug('ShopAttributeComponent attributes', rez);
            this.systemAttributes = rez;
            this.changed = false;
            this.selectedRow = null;
            _sub.unsubscribe();
        }
      );
    }
  }

  protected onDiscardEventHandler() {
    console.debug('ShopAttributeComponent discard handler');
    this.onRefreshHandler();
  }

  protected onRefreshHandler() {
    console.debug('ShopAttributeComponent refresh handler');
    this.getSystemPreferences();
  }

  /**
   * Read attributes.
   */
  private getSystemPreferences() {
    console.debug('ShopAttributeComponent get attributes');

    var _sub:any = this._systemService.getSystemPreferences().subscribe(systemAttributes => {

      console.debug('ShopAttributeComponent attributes', systemAttributes);
      this.systemAttributes = systemAttributes;
      this.changed = false;
      this.selectedRow = null;
      _sub.unsubscribe();

    });

  }

}
