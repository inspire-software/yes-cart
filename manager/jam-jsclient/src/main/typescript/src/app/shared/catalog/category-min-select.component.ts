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
import { Component, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { CatalogService } from './../services/index';
import { CategoryVO } from './../model/index';
import { ITreeNode } from './../tree-view/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { FormValidationEvent } from './../event/index';
import { LRUCache } from '../model/internal/cache.model';
import { LogUtil } from './../log/index';

@Component({
  selector: 'cw-category-min-select',
  templateUrl: 'category-min-select.component.html',
})

/**
 * Manage categories assigned to shop.
 */
export class CategoryMinSelectComponent implements OnInit {

  private static _cache:LRUCache = new LRUCache();

  @Output() dataSelected: EventEmitter<FormValidationEvent<CategoryVO>> = new EventEmitter<FormValidationEvent<CategoryVO>>();

  @ViewChild('catalogTreeModalDialog')
  private catalogTreeModalDialog:ModalComponent;

  public validForSelect:boolean = false;

  public nodes:Array<ITreeNode>;
  public selectedNode:ITreeNode;

  public loading:boolean = false;

  /**
   * Construct shop catalogues panel.
   * @param _categoryService
   */
  constructor(private _categoryService:CatalogService) {
    LogUtil.debug('CategoryMinSelectComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('CategoryMinSelectComponent ngOnInit');
  }

  /**
   * Select node.
   * @param node
   */
  onSelectNode(node:ITreeNode) {
    LogUtil.debug('CategoryMinSelectComponent selected node', node);
    if (node.disabled === false) {
      node.expanded = false; // collapse on selection, to prevent recursive selection (i.e. sub categories from same branch)
      this.selectedNode = node;
      this.validForSelect = true;
    }
  }

  onRequest(parent:ITreeNode) {
    LogUtil.debug('CategoryMinSelectComponent onRequest node', parent);
    parent.expanded = !parent.expanded;
    if (!parent.childrenLoaded) {
      this.loadData(parent.source.categoryId, true);
    }
  }

  onRefresh() {
    LogUtil.debug('CategoryMinSelectComponent onRefresh');
    this.loadData(0, true);
  }

  collectExpanded(nodes:Array<ITreeNode>, expanded:any) {
    nodes.forEach(node => {
      if (node.expanded) {
        expanded['ID' + node.id] = '1';
        expanded['ALL'].push(node.source.categoryId);
      }
      if (node.children != null && node.children.length > 0) {
        this.collectExpanded(node.children, expanded);
      }
    });
  }


  showDialog(current:number = 0) {
    LogUtil.debug('CategoryMinSelectComponent showDialog', current);
    this.loadData(current);
    this.catalogTreeModalDialog.show();
  }


  onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CategoryMinSelectComponent onSelectConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let selection = this.selectedNode != null ? this.selectedNode.source : null;
      this.dataSelected.emit({ source: selection, valid: true });
      this.selectedNode = null;
    }
  }

  /**
   * Load data and adapt time.
   */
  private loadData(current:number = 0, force:boolean = false, ensurePath:number[] = []) {
    LogUtil.debug('CategoryMinSelectComponent loading categories', current);

    let cacheKey = 'catalog';

    let cached = force ? null : CategoryMinSelectComponent._cache.getValue(cacheKey);
    if (cached) {

      this.nodes = cached;
      this.selectedNode = null;
      this.validForSelect = false;
      if (current > 0) {
        if (!this.expandCurrent(this.nodes, current)) {
          // if not expanded need to reload from root ensuring we load path to current
          this.loading = true;
          this._categoryService.getBranchesCategoriesPaths([ current ]).subscribe(
            cats => {
              LogUtil.debug('CategoryMinSelectComponent loading branch path', cats);
              this.loading = false;
              this.loadData(0, true, cats);
            }
          );

        }
      }

    } else {

      if (current > 0 && !this.existsCurrent(this.nodes, current)) {
        this.loading = true;
        this._categoryService.getBranchesCategoriesPaths([ current ]).subscribe(
          cats => {
            LogUtil.debug('CategoryMinSelectComponent loading branch path', cats);
            this.loading = false;
            this.loadData(0, true, cats);
          }
        );
      } else {

        let expanded: any = {ALL: []};
        if (this.nodes) {
          this.collectExpanded(this.nodes, expanded);
        }
        if (ensurePath != null && ensurePath.length > 0) {
          ensurePath.forEach(item => {
            expanded.ALL.push(item);
            expanded['ID' + item] = '1';
          });
        }
        LogUtil.debug('ContentSelectComponent loadData expanded', expanded);

        this.loading = true;
        this._categoryService.getBranchCategories(current, expanded.ALL).subscribe(
          cats => {
            LogUtil.debug('CategoryMinSelectComponent branch categories', cats, current);

            let branchNodes = this.adaptToTree(cats, expanded, current);

            LogUtil.debug('CategoryMinSelectComponent adaptToTree', branchNodes);

            let branch = this.resetCurrent(this.nodes, branchNodes[0]);
            if (branch == null) {
              this.nodes = branchNodes;
              LogUtil.debug('CategoryMinSelectComponent root categories', this.nodes);
            } else {
              LogUtil.debug('CategoryMinSelectComponent branch categories', this.nodes, branch);
            }

            CategoryMinSelectComponent._cache.putValue(cacheKey, this.nodes, 1800000); // 30 mins
            this.selectedNode = null;
            this.validForSelect = false;
            this.loading = false;
          }
        );
      }
    }
  }

  /**
   * Adapt given list of categories to tree items for representation.
   * @param vo branch
   * @param expanded expanded branches
   * @param current current node to select
   * @returns {Array<ITreeNode>}
   */
  private adaptToTree(vo:Array<CategoryVO>, expanded:any, current:number):Array<ITreeNode> {
    let rez:Array<ITreeNode> = [];
    for (let idx = 0; idx < vo.length; idx++) {
      let catVo:CategoryVO = vo[idx];
      let id:string = catVo.categoryId.toString();
      let node:ITreeNode = {
        'id': id,
        'name': catVo.name + (catVo.linkToName != null ? (' ( + ' + catVo.linkToName +' )') : ''),
        'children': [],
        'childrenLoaded': catVo.children != null,
        'expanded': catVo.categoryId === 100 || catVo.categoryId === current || expanded.hasOwnProperty('ID' + id), //the root is expanded by default
        'selected': catVo.categoryId === current,
        'disabled': false,
        'source': catVo
      };
      if (catVo.children !== null && catVo.children.length > 0) {
        node.children = this.adaptToTree(catVo.children, expanded, current);
        node.children.forEach(child => {
          if (child.selected || child.expanded) {
            node.expanded = true; // Expand parent if child is selected or expanded
          }
          if (child.source.parentId != catVo.categoryId) {
            child.name += ' ( + )';
          }
        });
      }
      rez.push(node);
    }
    return rez;
  }

  private existsCurrent(nodes:Array<ITreeNode>, current:number):boolean {
    if (nodes != null) {
      for (let idx = 0; idx < nodes.length; idx++) {
        let node: ITreeNode = nodes[idx];
        if (node.source.categoryId === current) {
          return true;
        }
        if (node.children != null && node.children.length > 0) {
          if (this.expandCurrent(node.children, current)) {
            return true;
          }
        }
      }
    }
    return false;
  }


  private expandCurrent(nodes:Array<ITreeNode>, current:number):boolean {
    for (let idx = 0; idx < nodes.length; idx++) {
      let node:ITreeNode = nodes[idx];
      if (node.source.categoryId === current) {
        node.expanded = true;
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

  private resetCurrent(nodes:Array<ITreeNode>, current:ITreeNode):ITreeNode {
    if (nodes != null) {

      LogUtil.debug('CategoryMinSelectComponent resetCurrent', nodes, current);

      for (let idx = 0; idx < nodes.length; idx++) {
        let node:ITreeNode = nodes[idx];
        LogUtil.debug('CategoryMinSelectComponent resetCurrent matching', node, current);
        if (node.id == current.id) {
          LogUtil.debug('CategoryMinSelectComponent resetCurrent matched', node, current);
          current.name = node.name; // preserve link formatting
          nodes[idx] = current;
          return current;
        }
        if (node.children != null && node.children.length > 0) {
          let child = this.resetCurrent(node.children, current);
          if (child != null) {
            return child;
          }
        }
      }
    }
    LogUtil.debug('CategoryMinSelectComponent resetCurrent no match');
    return null;
  }

}
