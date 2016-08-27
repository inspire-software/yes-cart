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
import {Component, OnInit, OnDestroy, OnChanges, Input, Output, EventEmitter, ViewChild} from '@angular/core';
import {ShopService, CatalogService, ShopEventBus, Util} from './../services/index';
import {CategoryVO, BasicCategoryVO} from './../model/index';
import {TreeViewComponent, ITreeNode} from './../tree-view/index';
import {ModalComponent, ModalResult, ModalAction} from './../modal/index';
import {FormValidationEvent} from './../event/index';

@Component({
  selector: 'yc-category-select',
  moduleId: module.id,
  templateUrl: 'category-select.component.html',
  directives: [TreeViewComponent, ModalComponent],
})

/**
 * Manage categories assigned to shop.
 */
export class CategorySelectComponent implements OnInit {

  changed:boolean = false;

  @ViewChild('catalogTreeModalDialog')
  catalogTreeModalDialog:ModalComponent;

  validForSelect:boolean = false;

  nodes:Array<ITreeNode>;
  selectedNode:ITreeNode;

  @Output() dataSelected: EventEmitter<FormValidationEvent<CategoryVO>> = new EventEmitter<FormValidationEvent<CategoryVO>>();

  /**
   * Construct shop catalogues panel.
   * @param _categoryService
   */
  constructor(private _categoryService:CatalogService) {
    console.debug('CategorySelectComponent constructed');
  }

  ngOnInit() {
    console.debug('CategorySelectComponent ngOnInit');
  }

  /**
   * Load data and adapt time.
   */
  loadData() {
    console.debug('CategorySelectComponent loading categories');
    var _subc:any = this._categoryService.getAllCategories().subscribe(
        cats => {
          console.debug('CategorySelectComponent all categories', cats);
          this.nodes = this.adaptToTree(cats);
          this.selectedNode = null;
          this.changed = false;
          this.validForSelect = false;
          _subc.unsubscribe();
      }
    );
  }

  /**
   * Adapt given list of categories to tree items for representation.
   * @param vo
   * @returns {Array<ITreeNode>}
     */
  adaptToTree(vo:Array<CategoryVO>):Array<ITreeNode> {
    var rez:Array<ITreeNode> = [];
    for (var idx = 0; idx < vo.length; idx++) {
      var catVo:CategoryVO = vo[idx];
      var node:ITreeNode = {
        'id': catVo.categoryId.toString(),
        'name': catVo.name,
        'children': [],
        'expanded': catVo.categoryId === 100, //the root is expanded by default
        'selected': catVo.categoryId === 100, //treat root as already selected
        'disabled': false,
        'source': catVo
      };
      if (catVo.children !== null && catVo.children.length > 0) {
        node.children = this.adaptToTree(catVo.children);
      }
      rez.push(node);
    }
    return rez;
  }

  /**
   * Select node.
   * @param node
   */
  onSelectNode(node:ITreeNode) {
    console.debug('CategorySelectComponent selected node', node);
    if (node.disabled === false) {
      node.expanded = false; // collapse on selection, to prevent recursive selection (i.e. sub categories from same branch)
      this.selectedNode = node;
      this.validForSelect = true;
    }
  }

  onRequest(parent:ITreeNode) {
    console.debug('CategorySelectComponent onRequest node', parent);
  }


  public showDialog() {
    console.debug('CategorySelectComponent showDialog');
    this.loadData();
    this.catalogTreeModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    console.debug('CategorySelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let selection = this.selectedNode != null ? this.selectedNode.source : null;
      this.dataSelected.emit({ source: selection, valid: true });
      this.selectedNode = null;
    }
  }

}
