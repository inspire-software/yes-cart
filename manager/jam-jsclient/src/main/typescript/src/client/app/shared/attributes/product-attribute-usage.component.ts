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
import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { AttributeService } from './../services/index';
import { Pair } from './../model/index';
import { ModalComponent, ModalResult } from './../modal/index';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-product-attribute-usage',
  moduleId: module.id,
  templateUrl: 'product-attribute-usage.component.html',
})

/**
 * Manage categories assigned to shop.
 */
export class ProductAttributeUsageComponent implements OnInit {

  @ViewChild('usageModalDialog')
  private usageModalDialog:ModalComponent;

  private _attributeCode:string;

  private usages:Array<Pair<number, string>> = [];

  /**
   * Construct panel.
   * @param _attributeService
   */
  constructor(private _attributeService:AttributeService) {
    LogUtil.debug('ProductAttributeUsageComponent constructed');
  }

  @Input()
  set attributeCode(attributeCode:string) {
    this._attributeCode = attributeCode;
  }

  get attributeCode():string {
    return this._attributeCode;
  }

  ngOnInit() {
    LogUtil.debug('ProductAttributeUsageComponent ngOnInit');
  }


  public showDialog() {
    LogUtil.debug('ProductAttributeUsageComponent showDialog');
    this.loadData();
    this.usageModalDialog.show();
  }


  protected onConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ProductAttributeUsageComponent onSelectConfirmationResult modal result is ', modalresult);
  }

  /**
   * Load data and adapt time.
   */
  private loadData() {
    LogUtil.debug('ProductAttributeUsageComponent loading usages', this._attributeCode);
    var _subc:any = this._attributeService.getProductTypesByAttributeCode(this._attributeCode).subscribe(
        usage => {
        LogUtil.debug('ProductAttributeUsageComponent all usages', usage);
        this.usages = usage;
        _subc.unsubscribe();
      }
    );
  }

}
