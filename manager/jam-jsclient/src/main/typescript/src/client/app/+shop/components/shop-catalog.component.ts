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
import {Component, OnInit, OnDestroy, Input, ViewChild} from '@angular/core';
import {FormBuilder, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from './../../shared/validation/validators';
import {ShopService, CatalogService} from './../../shared/services/index';
import {ShopVO, CategoryVO, BasicCategoryVO, ValidationRequestVO} from './../../shared/model/index';
import {DataControlComponent} from './../../shared/sidebar/index';
import {TreeViewComponent, ITreeNode} from './../../shared/tree-view/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';

@Component({
  selector: 'yc-shop-catalog',
  moduleId: module.id,
  templateUrl: 'shop-catalog.component.html',
  directives: [DataControlComponent, TreeViewComponent, REACTIVE_FORM_DIRECTIVES, ModalComponent],
})

/**
 * Manage categories assigned to shop.
 */
export class ShopCatalogComponent implements OnInit, OnDestroy {

  private _shop:ShopVO;
  private _reload:boolean = false;

  changed:boolean = false;
  existingShop:boolean = false;

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
  assigned:Array<CategoryVO> = null;

  /**
   * Construct shop catalogues panel.
   * @param _categoryService
   * @param _shopService
   * @param _routeParams
   */
  constructor(private _categoryService:CatalogService,
              private _shopService:ShopService,
              fb: FormBuilder) {
    console.debug('ShopCatalogComponent constructed');

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
      this.reloadData();
    }
  }

  @Input()
  set shop(shop:ShopVO) {
    this._shop = shop;
    if (this._reload || this.assigned != null) {
      this.reloadData();
    }
  }

  get shop():ShopVO  {
    return this._shop;
  }

  get categories():Array<CategoryVO> {
    return ShopCatalogComponent._categories;
  }

  set categories(categories:Array<CategoryVO>) {
    ShopCatalogComponent._categories = categories;
  }

  ngOnInit() {
    console.debug('ShopCatalogComponent ngOnInit shop', this.shop);
    this.formBind();
  }

  ngOnDestroy() {
    console.debug('ShopCatalogComponent ngOnDestroy');
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
      console.debug('ShopCatalogComponent unbining form');
      this.newCategoryFormSub.unsubscribe();
    }
  }


  onFormDataChange(event:any) {
    console.debug('ShopCatalogComponent data changed', event);
    this.changedSingle = true;
  }


  /**
   * Load data and adapt time.
   */
  loadData() {
    console.debug('ShopCatalogComponent loading categories', this.shop);
    this.existingShop = this.shop.shopId > 0;
    if (this.existingShop) {
      var _subs:any = this._shopService.getShopCategories(this.shop.shopId).subscribe(
          cats => {
            console.debug('ShopCatalogComponent assigned categories', cats);
            this.assigned = cats;
            _subs.unsubscribe();
            var _assignedIds:Array<number> = this.adaptToIds(cats);

            var _subc:any = this._categoryService.getAllCategories().subscribe(
                cats => {
                  console.debug('ShopCatalogComponent all categories', cats);
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
      );
    }
  }

  adaptToIds(vo:Array<CategoryVO>):Array<number> {
    var rez:Array<number> = [];
    for (var idx = 0; idx < vo.length; idx++) {
      var catVo:CategoryVO = vo[idx];
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
   * Assign selected category to shop.
   * @param node
   */
  assignToShopClick(node:ITreeNode) {
    console.debug('ShopCatalogComponent assignToShop ', node);
    let catVo = node.source;
    this.assigned.push(catVo);
    console.debug('ShopCatalogComponent disabled node', node);
    node.disabled = true;
    node.expanded = false;
    this.selectedNode = null;
    this.changed = true;
  }

  /**
   * Un-assign from shop.
   * @param cat category
     */
  onAssignedClick(cat:CategoryVO) {
    console.debug('ShopCatalogComponent onAssigned', cat);
    for (var idx = 0; idx < this.assigned.length; idx++) {
      var catVo : CategoryVO = this.assigned[idx];
      if (catVo.categoryId === cat.categoryId) {
        console.debug('ShopCatalogComponent remove from assigned', catVo);
        this.assigned.splice(idx, 1);
        this.changeDisabledState(catVo, this.nodes);
        this.changed = true;
        break;
      }
    }
  }

  changeDisabledState(cat:CategoryVO, nodes:Array<ITreeNode>):boolean {
    for (var idx = 0; idx < nodes.length; idx++) {
      var node:ITreeNode = nodes[idx];
      if (node.id === cat.categoryId.toString()) {
        node.disabled = false;
        console.debug('ShopCatalogComponent enabled node', node);
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
    console.debug('ShopCatalogComponent selected node', node);
    if (node.disabled === false) {
      node.expanded = false; // collapse on selection, to prevent recursive selection (i.e. sub categories from same branch)
      this.selectedNode = node;
    }
  }

  onRequest(parent:ITreeNode) {
    console.debug('ShopCatalogComponent onRequest node', parent);
  }


  /**
   * Fast create new category.
   * @param parent parent of new catecory
   */
  createNew(parent:ITreeNode) {
    console.debug('ShopCatalogComponent createNew for parent', parent);
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
    console.debug('ShopCatalogComponent editNewCategoryNameModalResult modal result', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      this._categoryService.createCategory(this.newCategory, +this.selectedNode.id).subscribe(
        catVo => {
          this.changedSingle = false;
          this.validForSave = false;
          this.onRefreshHandler();
        }
      );

    }
  }

  /**
   * Save assigned categories.
   */
  onSaveHandler() {
    console.debug('ShopCatalogComponent Save handler for shop', this.shop);
    if (this.shop.shopId > 0) {
      var _sub:any = this._shopService.saveShopCategories(this.shop.shopId, this.assigned).subscribe(
          cats => {
            this.assigned = cats;
            this.changed = false;
            _sub.unsubscribe();
        }
      );
    }
  }

  /**
   * Discard changes.
   */
  onDiscardEventHandler() {
    console.debug('ShopCatalogComponent Discard handler for shop', this.shop);
    if (this.shop.shopId > 0) {
      var _subs:any = this._shopService.getShopCategories(this.shop.shopId).subscribe(
          cats => {
            console.debug('ShopCatalogComponent assigned categories', cats);
            this.assigned = cats;
            _subs.unsubscribe();
            var _assignedIds:Array<number> = this.adaptToIds(cats);

            this.nodes = this.adaptToTree(this.categories, _assignedIds);
            this.selectedNode = null;
            this.newCategory = this.newCategoryInstance();
            this.changed = false;
            this._reload = false;
        }
      );
    }

  }

  /**
   * Load fresh data.
   */
  onRefreshHandler() {
    console.debug('ShopCatalogComponent Refresh handler ', this.shop);
    this.loadData();
  }

  private reloadData() {
    if (this.categories == null) {
      this.onRefreshHandler();
    } else {
      this.onDiscardEventHandler();
    }
  }

}
