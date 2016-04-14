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
//import {Util} from '../../service/util';
import {TreeComponent, ITreeNode} from '../common/tree_view';
import {CategoryVO} from '../../model/category';

@Component({
  selector: 'shop-catalogue',
  moduleId: module.id,
  templateUrl: './shop_catalogue.html',
  styleUrls: ['./shop_catalogue.css'],
  directives: [DataControl, TreeComponent],
  providers: [HTTP_PROVIDERS, ShopService, CategoryService]
})

export class ShopCatalogue implements OnInit {

  changed:boolean = false;

  nodes: Array<ITreeNode>;
  selectedNode: ITreeNode;

  categories : Array<CategoryVO>;


  constructor(private _categoryService:CategoryService,
              private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Shop catalogue');

  }

  ngOnInit() {
    let shopId = this._routeParams.get('shopId');
    console.debug('ngOnInit shopId from params is ' + shopId);
    this._categoryService.getAllCategories().subscribe(
      cats => {
        this.categories = cats;
        this.nodes = this.adapt(cats);
      }
    );
  }

  adapt(vo : Array<CategoryVO>) : Array<ITreeNode> {
    var rez : Array<ITreeNode> = new Array();
    for (var idx = 0; idx < vo.length; idx++) {
      var catVo : CategoryVO = vo[idx];
      var node : ITreeNode = this.adaptSingle(catVo);
      if (catVo.children !== null && catVo.children.length > 0) {
        node.children = this.adapt(catVo.children);
      }
      rez.push(node);
    }
    return rez;
  }

  adaptSingle(vo : CategoryVO) : ITreeNode {
    return <ITreeNode>{
      'id': vo.categoryId.toString(),
      'name': vo.name,
      'children': [],
      'expanded' : false,
      'selected' : false
    };
  }

  onSelectNode(node: ITreeNode) {
    this.selectedNode = node;
  }

  onRequest(parent: ITreeNode) {
    /*this.treeService.GetNodes(parent.id).subscribe(
      res => parent.children = res,
      error=> console.log(error));*/
  }





  onSaveHandler() {
    console.debug('Save handler for shop id ');
    this.changed = false;
  }

  onDiscardEventHandler() {
    console.debug('Discard handler for shop id ' );
    this.changed = false;
  }

  onRefreshHandler() {
    let shopId = this._routeParams.get('shopId');
    console.debug('Refresh handler ' + shopId);
    this.changed = false;
  }

}
