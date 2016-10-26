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
import {Component, OnInit, Input, ViewChild} from '@angular/core';
import {AttributeService} from './../services/index';
import {Pair} from './../model/index';
import {ModalComponent, ModalResult} from './../modal/index';

@Component({
  selector: 'yc-product-attribute-usage',
  moduleId: module.id,
  templateUrl: 'product-attribute-usage.component.html',
  directives: [ModalComponent],
})

/**
 * Manage categories assigned to shop.
 */
export class ProductAttributeUsageComponent implements OnInit {

  changed:boolean = false;

  @ViewChild('usageModalDialog')
  usageModalDialog:ModalComponent;

  _attributeCode:string;

  private usages:Array<Pair<number, string>> = [];

  /**
   * Construct panel.
   * @param _attributeService
   */
  constructor(private _attributeService:AttributeService) {
    console.debug('ProductAttributeUsageComponent constructed');
  }

  @Input()
  set attributeCode(attributeCode:string) {
    this._attributeCode = attributeCode;
    this.loadData();
  }

  get attributeCode():string {
    return this._attributeCode;
  }

  ngOnInit() {
    console.debug('ProductAttributeUsageComponent ngOnInit');
  }

  /**
   * Load data and adapt time.
   */
  loadData() {
    console.debug('ProductAttributeUsageComponent loading usages', this._attributeCode);
    var _subc:any = this._attributeService.getProductTypesByAttributeCode(this._attributeCode).subscribe(
        usage => {
          console.debug('ProductAttributeUsageComponent all usages', usage);
          this.usages = usage;
          _subc.unsubscribe();
      }
    );
  }


  public showDialog() {
    console.debug('ProductAttributeUsageComponent showDialog');
    this.usageModalDialog.show();
  }


  protected onConfirmationResult(modalresult: ModalResult) {
    console.debug('ProductAttributeUsageComponent onSelectConfirmationResult modal result is ', modalresult);
  }

}
