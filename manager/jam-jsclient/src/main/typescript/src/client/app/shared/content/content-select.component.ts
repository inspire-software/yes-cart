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
import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { ContentService } from './../services/index';
import { ContentVO, ShopVO } from './../model/index';
import { ITreeNode } from './../tree-view/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { FormValidationEvent } from './../event/index';
import { LRUCache } from '../model/internal/cache.model';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-content-select',
  moduleId: module.id,
  templateUrl: 'content-select.component.html',
})

/**
 * Manage categories assigned to shop.
 */
export class ContentSelectComponent implements OnInit {

  private static _cache:LRUCache = new LRUCache();

  @Input() shop:ShopVO;

  @Output() dataSelected: EventEmitter<FormValidationEvent<ContentVO>> = new EventEmitter<FormValidationEvent<ContentVO>>();

  private changed:boolean = false;

  @ViewChild('contentTreeModalDialog')
  private contentTreeModalDialog:ModalComponent;

  private validForSelect:boolean = false;

  private nodes:Array<ITreeNode>;
  private selectedNode:ITreeNode;

  private loading:boolean = false;

  /**
   * Construct shop content panel.
   * @param _categoryService
   */
  constructor(private _categoryService:ContentService) {
    LogUtil.debug('ContentSelectComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('ContentSelectComponent ngOnInit');
  }

  /**
   * Select node.
   * @param node
   */
  onSelectNode(node:ITreeNode) {
    LogUtil.debug('ContentSelectComponent selected node', node);
    if (node.disabled === false) {
      node.expanded = false; // collapse on selection, to prevent recursive selection (i.e. sub categories from same branch)
      this.selectedNode = node;
      this.validForSelect = true;
    }
  }

  onRequest(parent:ITreeNode) {
    LogUtil.debug('ContentSelectComponent onRequest node', parent);
  }

  onRefresh() {
    LogUtil.debug('ContentSelectComponent onRefresh');
    this.loadData(true);
  }

  collectExpanded(nodes:Array<ITreeNode>, expanded:any) {
    nodes.forEach(node => {
      if (node.expanded) {
         expanded['ID' + node.id] = '1';
      }
      if (node.children != null && node.children.length > 0) {
        this.collectExpanded(node.children, expanded);
      }
    });
  }


  public showDialog() {
    LogUtil.debug('ContentSelectComponent showDialog');
    this.loadData();
    this.contentTreeModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ContentSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let selection = this.selectedNode != null ? this.selectedNode.source : null;
      this.dataSelected.emit({ source: selection, valid: true });
      this.selectedNode = null;
    }
  }

  /**
   * Load data and adapt time.
   */
  private loadData(force:boolean = false) {
    LogUtil.debug('ContentSelectComponent loading categories');
    if (this.shop != null) {

      let cacheKey = this.shop.code;

      let cached = force ? null : ContentSelectComponent._cache.getValue(cacheKey);
      if (cached) {

        this.nodes = cached;
        this.selectedNode = null;
        this.changed = false;
        this.validForSelect = false;

      } else {

        let expanded:any = {};
        if (this.nodes) {
          this.collectExpanded(this.nodes, expanded);
        }
        LogUtil.debug('ContentSelectComponent loadData expanded', expanded);

        this.loading = true;
        var _subc:any = this._categoryService.getAllShopContent(this.shop.shopId).subscribe(
            cats => {
            LogUtil.debug('ContentSelectComponent all categories', cats);
            this.nodes = this.adaptToTree(cats, expanded);
            ContentSelectComponent._cache.putValue(cacheKey, this.nodes, 1800000); // 30 mins
            this.selectedNode = null;
            this.changed = false;
            this.validForSelect = false;
            this.loading = false;
            _subc.unsubscribe();
          }
        );

      }
    }
  }

  /**
   * Adapt given list of categories to tree items for representation.
   * @param vo
   * @returns {Array<ITreeNode>}
   */
  private adaptToTree(vo:Array<ContentVO>, expanded:any):Array<ITreeNode> {
    var rez:Array<ITreeNode> = [];
    for (var idx = 0; idx < vo.length; idx++) {
      var catVo:ContentVO = vo[idx];
      var id:string = catVo.contentId.toString();
      var node:ITreeNode = {
        'id': id,
        'name': catVo.name,
        'children': [],
        'expanded': catVo.parentId === 0 || expanded.hasOwnProperty('ID' + id), //the root is expanded by default
        'selected': catVo.parentId === 0, //treat root as already selected
        'disabled': false,
        'source': catVo
      };
      if (catVo.children !== null && catVo.children.length > 0) {
        node.children = this.adaptToTree(catVo.children, expanded);
      }
      rez.push(node);
    }
    return rez;
  }

}
