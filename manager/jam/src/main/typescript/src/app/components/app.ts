import {Component, ViewEncapsulation} from 'angular2/core';
import {
  RouteConfig,
  ROUTER_DIRECTIVES
} from 'angular2/router';

import {HomeCmp} from '../../home/components/home';
import {AboutCmp} from '../../about/components/about';
import {NameList} from '../../shared/services/name_list';
import {Sidebar} from '../../sidebar/components/sidebar';
import {ShopPage} from '../../components/shop/shop_page';

@Component({
  selector: 'app',
  viewProviders: [NameList],
  moduleId: module.id,
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
  encapsulation: ViewEncapsulation.None,
  directives: [ROUTER_DIRECTIVES, Sidebar]
})
@RouteConfig([
  { path: '/', component: HomeCmp, name: 'Home' },
  { path: '/about', component: AboutCmp, name: 'About' },
  { path: '/shop', component: ShopPage, name: 'Shop' }
])
export class AppCmp {}

/*@Component({
  selector: 'wrapper',
  template: `<div id="wrapper">
      <div id="page-wrapper" style="min-height: 561px;">
        <ng-content></ng-content>
      </div>
    </div>`,
  directives: [CORE_DIRECTIVES]
})
export class WrapperCmp {
}*/
