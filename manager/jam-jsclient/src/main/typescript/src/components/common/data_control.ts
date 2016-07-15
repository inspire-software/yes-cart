import {Component, OnInit, Output, EventEmitter, Input} from 'angular2/core';
import {NgClass, NgIf} from 'angular2/common';

@Component({
  selector: 'data-control',
  moduleId: module.id,
  templateUrl: './data_control.html',
  styleUrls: ['./data_control.css'],
  directives: [NgClass, NgIf]
})



export class DataControl implements OnInit {

  @Input() changed : boolean;

  @Output() saveEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() discardEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() refreshEvent: EventEmitter<any> = new EventEmitter<any>();


  constructor() {
    console.debug('DataControl  constructed');
  }

  ngOnInit() {
    console.debug('ngOnInit changed=' + this.changed);
  }

  onRefresh() {
    console.debug('refresh event emitted');
    this.refreshEvent.emit(null);
  }


  onSave() {
    console.debug('save event emitted');
    this.saveEvent.emit(null);
  }


  onDiscard() {
    console.debug('discard event emitted');
    this.discardEvent.emit(null);
  }

}
