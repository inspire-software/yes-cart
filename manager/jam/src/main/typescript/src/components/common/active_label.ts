import {Component, Input, Output, EventEmitter} from 'angular2/core';
import {NgClass, NgIf} from 'angular2/common';



@Component({
  selector: 'active-label',
  moduleId: module.id,
  templateUrl: './active_label.html',
  styleUrls: ['./active_label.css'],
  directives: [NgClass, NgIf]
})

export class ActiveLabel {

  @Input()  label : string;

  @Output() labelEvent: EventEmitter<string> = new EventEmitter<string>();

  onClick() {
    this.labelEvent.emit(this.label);
  }

}
