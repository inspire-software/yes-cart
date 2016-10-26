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
import {Component, OnInit, OnDestroy, Input, ViewChild, Output, EventEmitter} from '@angular/core';
import {FormBuilder, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../../shared/validation/validators';
import {CatalogService} from './../../shared/services/index';
import {ProductVO, ProductCategoryVO, CategoryVO, BasicCategoryVO, ValidationRequestVO} from './../../shared/model/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {TreeViewComponent, ITreeNode} from './../../shared/tree-view/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';

@Component({
  selector: 'yc-product-categories',
  moduleId: module.id,
  templateUrl: 'product-categories.component.html',
  directives: [DataControlComponent, TreeViewComponent, REACTIVE_FORM_DIRECTIVES, ModalComponent],
})

/**
 * Manage categories assigned to product.
 */
export class ProductCategoryComponent implements OnInit, OnDestroy {

  private _product:ProductVO;
  private _reload:boolean = false;

  changed:boolean = false;
  existingProduct:boolean = false;

  newCategory:BasicCategoryVO;
  @ViewChild('editNewCategoryName')
  editNewCategoryName:ModalComponent;
  newCategoryForm:any;
  newCategoryFormSub:any;
  changedSingle:boolean = true;
  validForSave:boolean = false;

  nodes:Array<ITreeNode>;
  selectedNode:ITreeNode;

  static _categories:Array<CategoryVO> = null;
  assigned:Array<ProductCategoryVO> = null;

  @Output() dataChanged: EventEmitter<Array<ProductCategoryVO>> = new EventEmitter<Array<ProductCategoryVO>>();

  /**
   * Construct product catalogues panel.
   * @param _categoryService
   * @param _routeParams
   */
  constructor(private _categoryService:CatalogService,
              fb: FormBuilder) {
    console.debug('ProductCategoryComponent constructed');

    this.newCategory = this.newCategoryInstance();

    let that = this;

    let validCode = function(control:any):any {

      let code = control.value;
      if (!that.changedSingle || code == null || code == '' || that.newCategory == null) {
        return null;
      }

      let basic = YcValidators.validCode(control);
      if (basic == null) {
        var req:ValidationRequestVO = { subject: 'category', subjectId: 0, field: 'guid', value: code };
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
  set product(product:ProductVO) {
    this._product = product;
    if (this._reload || this.assigned != null) {
      this.loadData();
    }
  }

  get product():ProductVO  {
    return this._product;
  }

  get categories():Array<CategoryVO> {
    return ProductCategoryComponent._categories;
  }

  set categories(categories:Array<CategoryVO>) {
    ProductCategoryComponent._categories = categories;
  }

  ngOnInit() {
    console.debug('ProductCategoryComponent ngOnInit product', this.product);
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ProductCategoryComponent ngOnDestroy');
    this.formUnbind();
  }

  newCategoryInstance():BasicCategoryVO {
    return { 'name': '', 'guid': '' };
  }

  formReset():void {
    // Hack to reset NG2 forms see https://github.com/angular/angular/issues/4933
    for(let key in this.newCategoryForm.controls) {
      this.newCategoryForm.controls[key]['_pristine'] = true;
      this.newCategoryForm.controls[key]['_touched'] = false;
    }
  }

  formBind():void {
    this.newCategoryFormSub = this.newCategoryForm.statusChanges.subscribe((data:any) => {
      if (this.changedSingle) {
        this.validForSave = this.newCategoryForm.valid;
      }
    });
  }

  formUnbind():void {
    if (this.newCategoryFormSub) {
      console.debug('ProductCategoryComponent unbining form');
      this.newCategoryFormSub.unsubscribe();
    }
  }


  onFormDataChange(event:any) {
    console.debug('ProductCategoryComponent data changed', event);
    this.changedSingle = true;
  }


  /**
   * Load data and adapt time.
   */
  loadData() {
    console.debug('ProductCategoryComponent loading categories', this.product);
    this.existingProduct = this.product != null && this.product.productId > 0;
    if (this.existingProduct) {
        this.assigned = this._product.productCategories;
        var _assignedIds:Array<number> = this.adaptToIds(this.assigned);

        var _subc:any = this._categoryService.getAllCategories().subscribe(
            cats => {
              console.debug('ProductCategoryComponent all categories', cats);
              this.categories = cats;
              this.nodes = this.adaptToTree(cats, _assignedIds);
              this.selectedNode = null;
              this.newCategory = { 'name': '', 'guid': '' };
              this.changed = false;
              this._reload = false;
              _subc.unsubscribe();
          }
        );
    }
  }

  adaptToIds(vo:Array<ProductCategoryVO>):Array<number> {
    var rez:Array<number> = [];
    for (var idx = 0; idx < vo.length; idx++) {
      var catVo:ProductCategoryVO = vo[idx];
      rez.push(catVo.categoryId);
    }
    return rez;
  }

  /**
   * Adapt given list of categories to tree items for representation.
   * @param vo
   * @returns {Array<ITreeNode>}
     */
  adaptToTree(vo:Array<CategoryVO>, disabled:Array<number>):Array<ITreeNode> {
    var rez:Array<ITreeNode> = [];
    for (var idx = 0; idx < vo.length; idx++) {
      var catVo:CategoryVO = vo[idx];
      var node:ITreeNode = {
        'id': catVo.categoryId.toString(),
        'name': catVo.name,
        'children': [],
        'expanded': catVo.categoryId === 100, //the root is expanded by default
        'selected': catVo.categoryId === 100, //treat root as already selected
        'disabled': disabled.indexOf(catVo.categoryId) !== -1,
        'source': catVo
      };
      if (catVo.children !== null && catVo.children.length > 0) {
        node.children = this.adaptToTree(catVo.children, disabled);
      }
      rez.push(node);
    }
    return rez;
  }

  /**
   * Assign selected category to product.
   * @param node
   */
  assignToProductClick(node:ITreeNode) {
    console.debug('ProductCategoryComponent assignToProduct ', node);
    let catVo = node.source;
    this.assigned.push({ productCategoryId: 0,  productId: this._product.productId, categoryId: catVo.categoryId, categoryName: catVo.name, rank: 0 });
    console.debug('ProductCategoryComponent disabled node', node);
    node.disabled = true;
    node.expanded = false;
    this.selectedNode = null;
    this.changed = true;
    this.dataChanged.emit(this.assigned);
  }

  /**
   * Un-assign from product.
   * @param cat category
     */
  onAssignedClick(cat:CategoryVO) {
    console.debug('ProductCategoryComponent onAssigned', cat);
    for (var idx = 0; idx < this.assigned.length; idx++) {
      var catVo : ProductCategoryVO = this.assigned[idx];
      if (catVo.categoryId === cat.categoryId) {
        console.debug('ProductCategoryComponent remove from assigned', catVo);
        this.assigned.splice(idx, 1);
        this.changeDisabledState(catVo, this.nodes);
        this.changed = true;
        this.dataChanged.emit(this.assigned);
        break;
      }
    }
  }

  changeDisabledState(cat:ProductCategoryVO, nodes:Array<ITreeNode>):boolean {
    for (var idx = 0; idx < nodes.length; idx++) {
      var node:ITreeNode = nodes[idx];
      if (node.id === cat.categoryId.toString()) {
        node.disabled = false;
        console.debug('ProductCategoryComponent enabled node', node);
        return true;
      }

      if (node.children && this.changeDisabledState(cat, node.children)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Select node.
   * @param node
   */
  onSelectNode(node:ITreeNode) {
    console.debug('ProductCategoryComponent selected node', node);
    if (node.disabled === false) {
      node.expanded = false; // collapse on selection, to prevent recursive selection (i.e. sub categories from same branch)
      this.selectedNode = node;
    }
  }

  onRequest(parent:ITreeNode) {
    console.debug('ProductCategoryComponent onRequest node', parent);
  }


  /**
   * Fast create new category.
   * @param parent parent of new catecory
   */
  createNew(parent:ITreeNode) {
    console.debug('ProductCategoryComponent createNew for parent', parent);
    this.changedSingle = false;
    this.validForSave = false;
    this.newCategory = this.newCategoryInstance();
    this.formReset();
    this.editNewCategoryName.show();
  }

  /**
   * Handle result of new category modal dialog.
   * @param modalresult
     */
  editNewCategoryNameModalResult(modalresult:ModalResult) {
    console.debug('ProductCategoryComponent editNewCategoryNameModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this._categoryService.createCategory(this.newCategory, +this.selectedNode.id).subscribe(
        catVo => {
          this.changedSingle = false;
          this.validForSave = false;
          this.loadData();
        }
      );

    }
  }

}
