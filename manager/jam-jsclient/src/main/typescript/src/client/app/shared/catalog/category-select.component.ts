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
import { Component, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { CatalogService } from './../services/index';
import { CategoryVO } from './../model/index';
import { ITreeNode } from './../tree-view/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { FormValidationEvent } from './../event/index';
import { LRUCache } from '../model/internal/cache.model';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-category-select',
  moduleId: module.id,
  templateUrl: 'category-select.component.html',
})

/**
 * Manage categories assigned to shop.
 */
export class CategorySelectComponent implements OnInit {

  private static _cache:LRUCache = new LRUCache();

  @Output() dataSelected: EventEmitter<FormValidationEvent<CategoryVO>> = new EventEmitter<FormValidationEvent<CategoryVO>>();

  private changed:boolean = false;

  @ViewChild('catalogTreeModalDialog')
  private catalogTreeModalDialog:ModalComponent;

  private validForSelect:boolean = false;

  private nodes:Array<ITreeNode>;
  private selectedNode:ITreeNode;

  private loading:boolean = false;

  /**
   * Construct shop catalogues panel.
   * @param _categoryService
   */
  constructor(private _categoryService:CatalogService) {
    LogUtil.debug('CategorySelectComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('CategorySelectComponent ngOnInit');
  }

  /**
   * Select node.
   * @param node
   */
  onSelectNode(node:ITreeNode) {
    LogUtil.debug('CategorySelectComponent selected node', node);
    if (node.disabled === false) {
      node.expanded = false; // collapse on selection, to prevent recursive selection (i.e. sub categories from same branch)
      this.selectedNode = node;
      this.validForSelect = true;
    }
  }

  onRequest(parent:ITreeNode) {
    LogUtil.debug('CategorySelectComponent onRequest node', parent);
  }

  onRefresh() {
    LogUtil.debug('CategorySelectComponent onRefresh');
    this.loadData(0, true);
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


  public showDialog(current:number = 0) {
    LogUtil.debug('CategorySelectComponent showDialog', current);
    this.loadData(current);
    this.catalogTreeModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CategorySelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let selection = this.selectedNode != null ? this.selectedNode.source : null;
      this.dataSelected.emit({ source: selection, valid: true });
      this.selectedNode = null;
    }
  }

  /**
   * Load data and adapt time.
   */
  private loadData(current:number = 0, force:boolean = false) {
    LogUtil.debug('CategorySelectComponent loading categories');

    let cacheKey = 'catalog';

    let cached = force ? null : CategorySelectComponent._cache.getValue(cacheKey);
    if (cached) {

      this.nodes = cached;
      this.selectedNode = null;
      this.changed = false;
      this.validForSelect = false;
      if (current > 0) {
        this.expandCurrent(this.nodes, current);
      }

    } else {

      let expanded:any = {};
      if (this.nodes) {
        this.collectExpanded(this.nodes, expanded);
      }
      LogUtil.debug('ContentSelectComponent loadData expanded', expanded);

      this.loading = true;
      var _subc:any = this._categoryService.getAllCategories().subscribe(
          cats => {
          LogUtil.debug('CategorySelectComponent all categories', cats);
          this.nodes = this.adaptToTree(cats, expanded, current);
          CategorySelectComponent._cache.putValue(cacheKey, this.nodes, 1800000); // 30 mins
          this.selectedNode = null;
          this.changed = false;
          this.validForSelect = false;
          this.loading = false;
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
  private adaptToTree(vo:Array<CategoryVO>, expanded:any, current:number):Array<ITreeNode> {
    var rez:Array<ITreeNode> = [];
    for (var idx = 0; idx < vo.length; idx++) {
      var catVo:CategoryVO = vo[idx];
      var id:string = catVo.categoryId.toString();
      var node:ITreeNode = {
        'id': id,
        'name': catVo.name,
        'children': [],
        'expanded': catVo.categoryId === 100 || catVo.parentId === current || expanded.hasOwnProperty('ID' + id), //the root is expanded by default
        'selected': catVo.categoryId === current, //treat root as already selected
        'disabled': false,
        'source': catVo
      };
      if (catVo.children !== null && catVo.children.length > 0) {
        node.children = this.adaptToTree(catVo.children, expanded, current);
        node.children.forEach(child => {
          if (child.selected || child.expanded) {
            node.expanded = true; // Expand parent if child is selected or expanded
          }
        });
      }
      if (node.selected) {
        this.onSelectNode(node);
      }
      rez.push(node);
    }
    return rez;
  }

  private expandCurrent(nodes:Array<ITreeNode>, current:number):boolean {
    for (var idx = 0; idx < nodes.length; idx++) {
      let node:ITreeNode = nodes[idx];
      if (node.source.categoryId === current) {
        this.onSelectNode(node);
        return true;
      }
      if (node.children != null && node.children.length > 0) {
        if (this.expandCurrent(node.children, current)) {
          node.expanded = true;
          return true;
        }
      }
    }
    return false;
  }

}
