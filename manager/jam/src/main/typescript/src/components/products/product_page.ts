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
import {Component, OnInit, AfterContentInit} from 'angular2/core';
import {RouteParams} from 'angular2/router';
import {ShopService} from '../../service/shop_service';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {HTTP_PROVIDERS}    from 'angular2/http';


@Component({
  selector: 'products',
  moduleId: module.id,
  templateUrl: './product_page.html',
  styleUrls: ['./product_page.css'],
  directives: [TAB_DIRECTIVES],
  providers: [HTTP_PROVIDERS, ShopService]
})

export class ProductPage implements OnInit, AfterContentInit {

  hi:string = 'Hi There ! I am product page';

  constructor(private _shopService:ShopService,
              private _routeParams:RouteParams) {
    console.debug('Product page constructed');
  }

  ngOnInit() {
    console.debug('Product page on init');
  }

  ngAfterContentInit() {
    console.debug('ngAfterContentInit');
  }

}
