import {Component, OnInit, Output, EventEmitter, Input} from '@angular/core';
import {NgClass, NgIf} from '@angular/common';

@Component({
  selector: 'yc-data-control',
  moduleId: module.id,
  templateUrl: 'data-control.component.html',
  directives: [NgClass, NgIf]
})



export class DataControlComponent implements OnInit {

  @Input() changed : boolean;
  @Input() valid : boolean;

  @Output() saveEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() discardEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() refreshEvent: EventEmitter<any> = new EventEmitter<any>();


  constructor() {
    console.debug('DataControlComponent constructed');
  }

  ngOnInit() {
    console.debug('DataControlComponent ngOnInit changed=' + this.changed);
  }

  onRefresh() {
    console.debug('DataControlComponent refresh');
    this.refreshEvent.emit(null);
  }


  onSave() {
    console.debug('DataControlComponent save ' + this.valid);
    if (this.valid) {
      this.saveEvent.emit(null);
    }
  }


  onDiscard() {
    console.debug('DataControlComponent discard');
    this.discardEvent.emit(null);
  }

}
