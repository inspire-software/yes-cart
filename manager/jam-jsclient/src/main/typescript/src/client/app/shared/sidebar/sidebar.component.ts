import {Component} from '@angular/core';
import {ACCORDION_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {ShopListComponent} from '../shop/index';

@Component({
  selector: 'sidebar',
  moduleId: module.id,
  templateUrl: 'sidebar.component.html',
  directives: [ROUTER_DIRECTIVES, ShopListComponent, ACCORDION_DIRECTIVES]
})

export class SidebarComponent {
}
