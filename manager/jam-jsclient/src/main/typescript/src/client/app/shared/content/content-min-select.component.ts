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
import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { ContentService } from './../services/index';
import { ContentVO, ShopVO } from './../model/index';
import { ITreeNode } from './../tree-view/index';
import { ModalComponent, ModalResult, ModalAction } from './../modal/index';
import { FormValidationEvent } from './../event/index';
import { LRUCache } from '../model/internal/cache.model';
import { LogUtil } from './../log/index';

@Component({
  selector: 'yc-content-min-select',
  moduleId: module.id,
  templateUrl: 'content-min-select.component.html',
})

/**
 * Manage categories assigned to shop.
 */
export class ContentMinSelectComponent implements OnInit {

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
   * @param _contentService
   */
  constructor(private _contentService:ContentService) {
    LogUtil.debug('ContentMinSelectComponent constructed');
  }

  ngOnInit() {
    LogUtil.debug('ContentMinSelectComponent ngOnInit');
  }

  /**
   * Select node.
   * @param node
   */
  onSelectNode(node:ITreeNode) {
    LogUtil.debug('ContentMinSelectComponent selected node', node);
    if (node.disabled === false) {
      node.expanded = false; // collapse on selection, to prevent recursive selection (i.e. sub categories from same branch)
      this.selectedNode = node;
      this.validForSelect = true;
    }
  }

  onRequest(parent:ITreeNode) {
    LogUtil.debug('ContentMinSelectComponent onRequest node', parent);
    parent.expanded = !parent.expanded;
    if (!parent.childrenLoaded) {
      this.loadData(parent.source.contentId, true);
    }
  }

  onRefresh() {
    LogUtil.debug('ContentMinSelectComponent onRefresh');
    this.loadData(0, true);
  }

  collectExpanded(nodes:Array<ITreeNode>, expanded:any) {
    nodes.forEach(node => {
      if (node.expanded) {
         expanded['ID' + node.id] = '1';
         expanded['ALL'].push(node.source.contentId);
      }
      if (node.children != null && node.children.length > 0) {
        this.collectExpanded(node.children, expanded);
      }
    });
  }


  public showDialog(current:number = 0) {
    LogUtil.debug('ContentMinSelectComponent showDialog', current);
    this.loadData(current);
    this.contentTreeModalDialog.show();
  }


  protected onSelectConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('ContentMinSelectComponent onSelectConfirmationResult modal result is ', modalresult);
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
    LogUtil.debug('ContentMinSelectComponent loading categories');
    if (this.shop != null) {

      let cacheKey = this.shop.code;

      let cached = force ? null : ContentMinSelectComponent._cache.getValue(cacheKey);
      if (cached) {

        this.nodes = cached;
        this.selectedNode = null;
        this.changed = false;
        this.validForSelect = false;
        if (current > 0) {
          if (!this.expandCurrent(this.nodes, current)) {
            // if not expanded need to reload from root ensuring we load path to current
            this.loading = true;
            let _subc: any = this._contentService.getShopBranchesContentPaths(this.shop.shopId, [ current ]).subscribe(
              cats => {
                LogUtil.debug('ContentMinSelectComponent loading branch path', cats);
                this.loading = false;
                this.loadData(0, true, cats);
                _subc.unsubscribe();
              }
            );

          }
        }

      } else {

        if (current > 0 && !this.existsCurrent(this.nodes, current)) {
          this.loading = true;
          let _subc: any = this._contentService.getShopBranchesContentPaths(this.shop.shopId, [ current ]).subscribe(
            cats => {
              LogUtil.debug('ContentMinSelectComponent loading branch path', cats);
              this.loading = false;
              this.loadData(0, true, cats);
              _subc.unsubscribe();
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
          LogUtil.debug('ContentMinSelectComponent loadData expanded', expanded);

          this.loading = true;
          let _subc: any = this._contentService.getShopBranchContent(this.shop.shopId, current, expanded.ALL).subscribe(
            cats => {
              LogUtil.debug('ContentMinSelectComponent branch categories', cats, current);

              let branchNodes = this.adaptToTree(cats, expanded, current);

              LogUtil.debug('ContentMinSelectComponent adaptToTree', branchNodes);

              let branch = this.resetCurrent(this.nodes, branchNodes[0]);
              if (branch == null) {
                this.nodes = branchNodes;
                LogUtil.debug('ContentMinSelectComponent root categories', this.nodes);
              } else {
                LogUtil.debug('ContentMinSelectComponent branch categories', this.nodes, branch);
              }

              ContentMinSelectComponent._cache.putValue(cacheKey, this.nodes, 1800000); // 30 mins
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
  }

  /**
   * Adapt given list of categories to tree items for representation.
   * @param vo branch
   * @param expanded expanded branches
   * @param current current node to select
   * @returns {Array<ITreeNode>}
   */
  private adaptToTree(vo:Array<ContentVO>, expanded:any, current:number):Array<ITreeNode> {
    let rez:Array<ITreeNode> = [];
    for (let idx = 0; idx < vo.length; idx++) {
      let catVo:ContentVO = vo[idx];
      let id:string = catVo.contentId.toString();
      let node:ITreeNode = {
        'id': id,
        'name': catVo.name,
        'children': [],
        'childrenLoaded': catVo.children != null,
        'expanded': catVo.parentId === 0 || expanded.hasOwnProperty('ID' + id), //the root is expanded by default
        'selected': catVo.contentId === current,
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
      rez.push(node);
    }
    return rez;
  }

  private existsCurrent(nodes:Array<ITreeNode>, current:number):boolean {
    if (nodes != null) {
      for (let idx = 0; idx < nodes.length; idx++) {
        let node: ITreeNode = nodes[idx];
        if (node.source.contentId === current) {
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
      if (node.source.contentId === current) {
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

      LogUtil.debug('ContentMinSelectComponent resetCurrent', nodes, current);

      for (let idx = 0; idx < nodes.length; idx++) {
        let node:ITreeNode = nodes[idx];
        LogUtil.debug('ContentMinSelectComponent resetCurrent matching', node, current);
        if (node.id == current.id) {
          LogUtil.debug('ContentMinSelectComponent resetCurrent matched', node, current);
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
    LogUtil.debug('ContentMinSelectComponent resetCurrent no match');
    return null;
  }

}
