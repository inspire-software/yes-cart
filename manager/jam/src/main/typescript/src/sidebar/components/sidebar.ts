import {Component} from 'angular2/core';
import {Accordion, ACCORDION_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {ROUTER_DIRECTIVES} from 'angular2/router';
import {ShopList} from '../../components/shop/shop_list';

@Component({
  selector: 'sidebar',
  moduleId: module.id,
  templateUrl: './sidebar.html',
  directives: [ROUTER_DIRECTIVES, Accordion, ShopList, ACCORDION_DIRECTIVES]
})

export class Sidebar {
}
