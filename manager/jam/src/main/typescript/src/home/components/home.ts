import {Component} from 'angular2/core';
import {Alert} from 'ng2-bootstrap/ng2-bootstrap';

@Component({
  selector: 'home',
  moduleId: module.id,
  templateUrl: './home.html',
  directives: [Alert],
  styleUrls: ['./home.css']
})
export class HomeCmp {}
