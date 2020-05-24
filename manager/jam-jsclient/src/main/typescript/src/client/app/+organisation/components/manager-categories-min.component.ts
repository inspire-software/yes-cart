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
import { Component, OnInit, OnDestroy, Input, ViewChild, Output, EventEmitter } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { YcValidators } from './../../shared/validation/validators';
import { CatalogService } from './../../shared/services/index';
import { ManagerVO, ManagerCategoryCatalogVO, CategoryVO, BasicCategoryVO, ValidationRequestVO } from './../../shared/model/index';
import { ITreeNode } from './../../shared/tree-view/index';
import { ModalComponent, ModalResult, ModalAction } from './../../shared/modal/index';
import { UiUtil } from './../../shared/ui/index';
import { LogUtil } from './../../shared/log/index';

@Component({
  selector: 'yc-manager-categories-min',
  moduleId: module.id,
  templateUrl: 'manager-categories-min.component.html',
})

/**
 * Manage categories assigned to manager.
 */
export class ManagerCategoryMinComponent implements OnInit, OnDestroy {

  @Output() dataChanged: EventEmitter<Array<ManagerCategoryCatalogVO>> = new EventEmitter<Array<ManagerCategoryCatalogVO>>();

  private _manager:ManagerVO;
  private _reload:boolean = false;

  private changed:boolean = false;
  private existingManager:boolean = false;

  private newCategory:BasicCategoryVO;
  @ViewChild('editNewCategoryName')
  private editNewCategoryName:ModalComponent;

  private newCategoryForm:any;
  private validForSave:boolean = false;

  private nodes:Array<ITreeNode>;
  private selectedNode:ITreeNode;

  private assigned:Array<ManagerCategoryCatalogVO> = null;

  private loading:boolean = false;

  /**
   * Construct manager catalogues panel.
   * @param _categoryService category service
   * @param fb form builder
   */
  constructor(private _categoryService:CatalogService,
              fb: FormBuilder) {
    LogUtil.debug('ManagerCategoryMinComponent constructed');

    this.newCategory = this.newCategoryInstance();

    let that = this;

    let validCode = function(control:any):any {

      let code = control.value;
      if (code == null || code == '' || that.newCategory == null || !that.newCategoryForm || !that.newCategoryForm.dirty) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        let req:ValidationRequestVO = { subject: 'category', subjectId: 0, field: 'guid', value: code };
        return YcValidators.validRemoteCheck(control, req);
      }
      return basic;
    };


    this.newCategoryForm = fb.group({
      'guid': ['', validCode],
      'name': ['', YcValidators.requiredNonBlankTrimmed],
    });

  }

  @Input()
  set reload(reload:boolean) {
    if (reload && !this._reload) {
      this._reload = true;
      this.loadData();
    }
  }

  @Input()
  set manager(manager:ManagerVO) {
    this._manager = manager;
    if (this._reload || this.assigned != null) {
      this.loadData();
    }
  }

  get manager():ManagerVO  {
    return this._manager;
  }

  ngOnInit() {
    LogUtil.debug('ManagerCategoryMinComponent ngOnInit manager', this.manager);
    this.formBindAdd();
  }

  ngOnDestroy() {
    LogUtil.debug('ManagerCategoryMinComponent ngOnDestroy');
    this.formUnbindAdd();
  }

  newCategoryInstance():BasicCategoryVO {
    return { name: '', guid: '' };
  }

  newManagerCategoryInstance():ManagerCategoryCatalogVO {
    return { managerId : 0, categoryId : 0, code : '', name : '' };
  }

  formBindAdd():void {
    UiUtil.formBind(this, 'newCategoryForm', 'formChangeAdd', false);
  }

  formUnbindAdd():void {
    UiUtil.formUnbind(this, 'newCategoryForm');
  }

  formChangeAdd():void {
    LogUtil.debug('ManagerCategoryMinComponent formChangeAdd', this.newCategoryForm.valid, this.newCategory);
    this.validForSave = this.newCategoryForm.valid;
  }

  /**
   * Load data and adapt time.
   */
  loadData(current:number = 0) {
    LogUtil.debug('ManagerCategoryMinComponent loading categories', this.manager);
    this.existingManager = this.manager != null;
    if (this.existingManager) {

      this.assigned = this._manager.managerCategoryCatalogs;
      let _assignedIds:Array<number> = this.adaptToIds(this.assigned);

        if (current > 0) {
          // expanding single node
          this.loading = true;
          let _subc:any = this._categoryService.getBranchCategories(current, []).subscribe(
            cats => {
              LogUtil.debug('ManagerCategoryMinComponent branch categories', cats, _assignedIds);
              let branchNodes = this.adaptToTree(cats, _assignedIds);

              LogUtil.debug('ManagerCategoryMinComponent adaptToTree', branchNodes);

              let branch = this.resetCurrent(this.nodes, branchNodes[0]);
              if (branch == null) {
                this.nodes = branchNodes;
                LogUtil.debug('ManagerCategoryMinComponent root categories', this.nodes);
              } else {
                LogUtil.debug('ManagerCategoryMinComponent branch categories', this.nodes, branch);
              }

              // this.selectedNode = null;
              // UiUtil.formInitialise(this, 'newCategoryForm', 'newCategory', this.newCategoryInstance());
              // this.changed = false;
              // this._reload = false;
              this.loading = false;
              _subc.unsubscribe();
            }
          );

        } else {
          // expanding initial
          this.loading = true;
          let _subc:any = this._categoryService.getBranchesCategoriesPaths(_assignedIds).subscribe(
            cats => {
              LogUtil.debug('ManagerCategoryMinComponent loading branch path', cats);
              _subc.unsubscribe();

              this.loading = true;

              let _subc2:any = this._categoryService.getBranchCategories(0, cats).subscribe(
                cats => {
                  LogUtil.debug('ManagerCategoryMinComponent initial categories', cats, _assignedIds);
                  this.nodes = this.adaptToTree(cats, _assignedIds);
                  this.selectedNode = null;
                  UiUtil.formInitialise(this, 'newCategoryForm', 'newCategory', this.newCategoryInstance());
                  this.changed = false;
                  this._reload = false;
                  this.loading = false;
                  _subc2.unsubscribe();
                }
              );

            }
          );


        }

    }
  }

  adaptToIds(vo:Array<ManagerCategoryCatalogVO>):Array<number> {
    let rez:Array<number> = [];
    for (let idx = 0; idx < vo.length; idx++) {
      let catVo:ManagerCategoryCatalogVO = vo[idx];
      rez.push(catVo.categoryId);
    }
    return rez;
  }

  /**
   * Adapt given list of categories to tree items for representation.
   * @param vo branch
   * @param disabled nodes
   * @returns {Array<ITreeNode>}
     */
  adaptToTree(vo:Array<CategoryVO>, disabled:Array<number>):Array<ITreeNode> {
    let rez:Array<ITreeNode> = [];
    for (let idx = 0; idx < vo.length; idx++) {
      let catVo:CategoryVO = vo[idx];
      let node:ITreeNode = {
        'id': catVo.categoryId.toString(),
        'name': catVo.name,
        'children': [],
        'childrenLoaded': catVo.children != null,
        'expanded': catVo.categoryId === 100, //the root is expanded by default
        'selected': catVo.categoryId === 100, //treat root as already selected
        'disabled': disabled.indexOf(catVo.categoryId) !== -1,
        'source': catVo
      };
      if (catVo.children !== null && catVo.children.length > 0) {
        node.children = this.adaptToTree(catVo.children, disabled);
        node.children.forEach(child => {
          if (child.disabled || child.expanded) {
            node.expanded = true; // Expand parent if child is selected or expanded
          }
        });
      }
      rez.push(node);
    }
    return rez;
  }


  resetCurrent(nodes:Array<ITreeNode>, current:ITreeNode):ITreeNode {
    if (nodes != null) {

      LogUtil.debug('ManagerCategoryMinComponent resetCurrent', nodes, current);

      for (let idx = 0; idx < nodes.length; idx++) {
        let node:ITreeNode = nodes[idx];
        LogUtil.debug('ManagerCategoryMinComponent resetCurrent matching', node, current);
        if (node.id == current.id) {
          LogUtil.debug('ManagerCategoryMinComponent resetCurrent matched', node, current);
          current.expanded = true;
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
    LogUtil.debug('ManagerCategoryMinComponent resetCurrent no match');
    return null;
  }


  /**
   * Assign selected category to manager.
   * @param node
   */
  assignToManagerClick(node:ITreeNode) {
    LogUtil.debug('ManagerCategoryMinComponent assignToManager ', node);
    let catVo = node.source;
    this.assigned.push({ managerId: this._manager.managerId, categoryId: catVo.categoryId, code: catVo.guid, name: catVo.name });
    LogUtil.debug('ManagerCategoryMinComponent disabled node', node);
    this.changeDisabledState(catVo, this.nodes, true);
    this.selectedNode = null;
    this.changed = true;
    this.dataChanged.emit(this.assigned);
  }

  /**
   * Un-assign from manager.
   * @param cat category
     */
  onAssignedClick(cat:CategoryVO) {
    LogUtil.debug('ManagerCategoryMinComponent onAssigned', cat);
    for (let idx = 0; idx < this.assigned.length; idx++) {
      let catVo : ManagerCategoryCatalogVO = this.assigned[idx];
      if (catVo.categoryId === cat.categoryId) {
        LogUtil.debug('ManagerCategoryMinComponent remove from assigned', catVo);
        this.assigned.splice(idx, 1);
        this.changeDisabledState(catVo, this.nodes, false);
        this.changed = true;
        this.dataChanged.emit(this.assigned);
        break;
      }
    }
  }

  changeDisabledState(cat:ManagerCategoryCatalogVO, nodes:Array<ITreeNode>, disabled:boolean):boolean {
    let changed = false;
    for (let idx = 0; idx < nodes.length; idx++) {
      let node:ITreeNode = nodes[idx];
      if (node.id === cat.categoryId.toString()) {
        node.disabled = disabled;
        if (disabled) {
          node.expanded = false;
        }
        LogUtil.debug('ManagerCategoryMinComponent enabled node', node);
        changed = true;
      } else if (node.children && this.changeDisabledState(cat, node.children, disabled)) {
        changed = true;
      }
    }
    return changed;
  }

  /**
   * Select node.
   * @param node
   */
  onSelectNode(node:ITreeNode) {
    LogUtil.debug('ManagerCategoryMinComponent selected node', node);
    if (node.disabled === false) {
      node.expanded = false; // collapse on selection, to prevent recursive selection (i.e. sub categories from same branch)
      this.selectedNode = node;
    }
  }

  onRequest(parent:ITreeNode) {
    LogUtil.debug('ManagerCategoryMinComponent onRequest node', parent);
    parent.expanded = !parent.expanded;
    if (!parent.childrenLoaded) {
      this.loadData(parent.source.categoryId);
    }

  }


  /**
   * Fast create new category.
   * @param parent parent of new catecory
   */
  createNew(parent:ITreeNode) {
    LogUtil.debug('ManagerCategoryMinComponent createNew for parent', parent);
    this.validForSave = false;
    UiUtil.formInitialise(this, 'newCategoryForm', 'newCategory', this.newCategoryInstance());
    this.editNewCategoryName.show();
  }

  /**
   * Handle result of new category modal dialog.
   * @param modalresult
     */
  editNewCategoryNameModalResult(modalresult:ModalResult) {
    LogUtil.debug('ManagerCategoryMinComponent editNewCategoryNameModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this._categoryService.createCategory(this.newCategory, +this.selectedNode.id).subscribe(
        catVo => {
          this.validForSave = false;
          this.loadData();
        }
      );

    }
  }

}
