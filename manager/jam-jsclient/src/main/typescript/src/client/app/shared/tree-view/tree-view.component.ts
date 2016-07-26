import {Component, Input, Output, EventEmitter} from '@angular/core';

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
  directives: [TreeViewComponent]
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
