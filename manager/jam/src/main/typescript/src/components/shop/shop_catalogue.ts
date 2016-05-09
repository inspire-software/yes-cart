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
import {Component} from 'angular2/core';
import {OnInit} from 'angular2/core';
import {RouteParams} from 'angular2/router';
import {ShopService} from '../../service/shop_service';
import {CategoryService} from '../../service/category_service';
import {DataControl} from '../common/data_control';
import {HTTP_PROVIDERS}    from 'angular2/http';
import {TreeComponent, ITreeNode} from '../common/tree_view';
import {CategoryVO} from '../../model/category';
import {Modal, ModalResult, ModalAction} from '../common/modal';

@Component({
  selector: 'shop-catalogue',
  moduleId: module.id,
  templateUrl: './shop_catalogue.html',
  styleUrls: ['./shop_catalogue.css'],
  directives: [DataControl, TreeComponent, Modal],
  providers: [HTTP_PROVIDERS, ShopService, CategoryService]
})

export class ShopCatalogue implements OnInit {

  changed:boolean = false;

  newCategoryName:string;
  editNewCategoryName:Modal;

  nodes:Array<ITreeNode>;
  selectedNode:ITreeNode;

  categories:Array<CategoryVO>;
  assigned:Array<CategoryVO>;


  constructor(private _categoryService:CategoryService,
              private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Shop catalogue');

  }

  ngOnInit() {

    let shopId = this._routeParams.get('shopId');

    console.debug('ngOnInit shopId from params is ' + shopId);

    this.loadData();
  }

  loadData() {
    let shopId = this._routeParams.get('shopId');
    this._categoryService.getAllCategories().subscribe(
      cats => {
        this.categories = cats;
        this.nodes = this.adapt(cats);
        this.selectedNode = null;
        this.newCategoryName = null;
      }
    );

    this._shopService.getShopCategories(+shopId).subscribe(
      cats => {
        this.assigned = cats;
      }
    );
  };

  adapt(vo:Array<CategoryVO>):Array<ITreeNode> {
    var rez:Array<ITreeNode> = new Array();
    for (var idx = 0; idx < vo.length; idx++) {
      var catVo:CategoryVO = vo[idx];
      var node:ITreeNode = {
        'id': catVo.categoryId.toString(),
        'name': catVo.name,
        'children': [],
        'expanded': catVo.categoryId === 100, //the root is expanded by default
        'selected': false
      };
      if (catVo.children !== null && catVo.children.length > 0) {
        node.children = this.adapt(catVo.children);
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
    console.debug('assignToShop ' + JSON.stringify(node));
    var catVo = this.findCategoryById(this.categories, +node.id);
    if (catVo !== null) {
      this.assigned.push(catVo);
      this.changed = true;
    } else {
      console.error('Cannot find category for given id ' + node.id);
    }
  }

  findCategoryById(vo:Array<CategoryVO>, catId:number) : CategoryVO {
    var rez : CategoryVO = null;
    for (var catVo of vo) {
      if (catVo.categoryId === catId) {
        rez = catVo;
        break;
      } else if (catVo.children.length > 0) {
        rez = this.findCategoryById(catVo.children, catId);
        if (rez !== null) {
          break;
        }
      }
    }
    return rez;
  }

  /**
   * Unassign from shop.
   * @param cat category
     */
  onAssignedClick(cat:CategoryVO) {
    console.debug('onAssigned ' + JSON.stringify(cat));
    for (var idx = 0; idx < this.assigned.length; idx++) {
      var catVo : CategoryVO = this.assigned[idx];
      if (catVo.categoryId === cat.categoryId) {
        console.debug('remove ' + catVo.categoryId + ' from assigned');
        this.assigned.splice(idx, 1);
        this.changed = true;
        break;
      }
    }
  }

  /**
   * Select node.
   * @param node
   */
  onSelectNode(node:ITreeNode) {
    this.selectedNode = node;
  }

  onRequest(parent:ITreeNode) {
    console.debug('onRequest ' + JSON.stringify(parent));
  }


  /**
   * Fast create new category.
   * @param parent parent of new catecory
   */
  createNew(parent:ITreeNode) {
    console.debug('createNew for parent ' + JSON.stringify(parent));
    this.editNewCategoryName.show();
  }


  editNewCategoryNameModalLoaded(modal:Modal) {
    console.debug('editNewCategoryNameModalLoaded');
    this.editNewCategoryName = modal;
  }

  editNewCategoryNameModalResult(modalresult:ModalResult) {
    console.debug('editNewCategoryNameModalResult modal result is ' + JSON.stringify(modalresult));
    if (ModalAction.POSITIVE === modalresult.action) {
      console.debug('modalresult.action modal result is positive ' + JSON.stringify(modalresult));
      this._categoryService.createCategory(this.newCategoryName, +this.selectedNode.id).subscribe(
        catVo => {
          var node:ITreeNode = {
            'id': catVo.categoryId.toString(),
            'name': catVo.name,
            'children': [],
            'expanded': false,
            'selected': false
          };
          this.selectedNode.children.push(node);
          this.newCategoryName = null;
        }
      );

    }
  }

  onSaveHandler() {
    let shopId = this._routeParams.get('shopId');
    console.debug('Save handler for shop id ' + shopId);
    this._shopService.saveShopCategories(+shopId, this.assigned).subscribe(
      cats => {
        this.assigned = cats;
      }
    );
    this.changed = false;
  }

  onDiscardEventHandler() {
    console.debug('Discard handler for shop id ');
    this.nodes = this.adapt(this.categories);
    this.selectedNode = null;
    this.changed = false;
  }

  onRefreshHandler() {
    let shopId = this._routeParams.get('shopId');
    console.debug('Refresh handler ' + shopId);
    this.loadData();
    this.changed = false;
  }

}
