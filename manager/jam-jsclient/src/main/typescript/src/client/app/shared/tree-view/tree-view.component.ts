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
import { Component, Input, Output, EventEmitter } from '@angular/core';

export interface ITreeNode {
  id: string;
  name: string;
  children: Array<ITreeNode>;
  expanded: boolean;
  selected: boolean;
  disabled: boolean;
  source: any;
}

@Component({
  selector: 'yc-tree-view',
  moduleId: module.id,
  templateUrl: 'tree-view.component.html',
})

export class TreeViewComponent {

  @Input() level:number = 1;
  @Input() nodes:Array<ITreeNode>;
  @Input() selectedNode:ITreeNode;

  @Output() onSelectedChanged:EventEmitter<ITreeNode> = new EventEmitter<ITreeNode>();
  @Output() onRequestNodes:EventEmitter<ITreeNode> = new EventEmitter<ITreeNode>();

  onSelectNode(node:ITreeNode):void {
    if (!node.disabled) {
      this.onSelectedChanged.emit(node);
    }
  }

  onExpand(node:ITreeNode):void {
    if (!node.disabled) {
      node.expanded = !node.expanded;
      if (node.expanded && node.children.length === 0) {
        this.onRequestNodes.emit(node);
      }
    }
  }

  isExpanded(node:ITreeNode):boolean {
    return node.expanded;
  }

  isDisabled(node:ITreeNode):boolean {
    return node.disabled;
  }

}
