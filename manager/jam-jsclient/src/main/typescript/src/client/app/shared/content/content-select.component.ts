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
import {ShopService, ContentService, ShopEventBus, Util} from './../services/index';
import {ContentVO, ShopVO} from './../model/index';
import {TreeViewComponent, ITreeNode} from './../tree-view/index';
import {ModalComponent, ModalResult, ModalAction} from './../modal/index';
import {FormValidationEvent} from './../event/index';

@Component({
  selector: 'yc-content-select',
  moduleId: module.id,
  templateUrl: 'content-select.component.html',
  directives: [TreeViewComponent, ModalComponent],
})

/**
 * Manage categories assigned to shop.
 */
export class ContentSelectComponent implements OnInit {

  changed:boolean = false;

  @Input()
  shop:ShopVO;

  @ViewChild('contentTreeModalDialog')
  contentTreeModalDialog:ModalComponent;

  validForSelect:boolean = false;

  nodes:Array<ITreeNode>;
  selectedNode:ITreeNode;

  @Output() dataSelected: EventEmitter<FormValidationEvent<ContentVO>> = new EventEmitter<FormValidationEvent<ContentVO>>();

  /**
   * Construct shop content panel.
   * @param _categoryService
   */
  constructor(private _categoryService:ContentService) {
    console.debug('ContentSelectComponent constructed');
  }

  ngOnInit() {
    console.debug('ContentSelectComponent ngOnInit');
  }

  /**
   * Load data and adapt time.
   */
  loadData() {
    console.debug('ContentSelectComponent loading categories');
    if (this.shop != null) {
      var _subc:any = this._categoryService.getAllShopContent(this.shop.shopId).subscribe(
          cats => {
          console.debug('ContentSelectComponent all categories', cats);
          this.nodes = this.adaptToTree(cats);
          this.selectedNode = null;
          this.changed = false;
          this.validForSelect = false;
          _subc.unsubscribe();
        }
      );
    }
  }

  /**
   * Adapt given list of categories to tree items for representation.
   * @param vo
   * @returns {Array<ITreeNode>}
     */
  adaptToTree(vo:Array<ContentVO>):Array<ITreeNode> {
    var rez:Array<ITreeNode> = [];
    for (var idx = 0; idx < vo.length; idx++) {
      var catVo:ContentVO = vo[idx];
      var node:ITreeNode = {
        'id': catVo.contentId.toString(),
        'name': catVo.name,
        'children': [],
        'expanded': catVo.contentId === 0, //the root is expanded by default
        'selected': catVo.contentId === 0, //treat root as already selected
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
    console.debug('ContentSelectComponent selected node', node);
    if (node.disabled === false) {
      node.expanded = false; // collapse on selection, to prevent recursive selection (i.e. sub categories from same branch)
      this.selectedNode = node;
      this.validForSelect = true;
    }
  }

  onRequest(parent:ITreeNode) {
    console.debug('ContentSelectComponent onRequest node', parent);
  }


  public showDialog() {
    console.debug('ContentSelectComponent showDialog');
    this.loadData();
    this.contentTreeModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    console.debug('ContentSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let selection = this.selectedNode != null ? this.selectedNode.source : null;
      this.dataSelected.emit({ source: selection, valid: true });
      this.selectedNode = null;
    }
  }

}
