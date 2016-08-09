import {Component, OnInit, OnChanges, Input, Output, EventEmitter} from '@angular/core';
import {NgClass, NgIf, NgFor} from '@angular/common';

import {Pair} from '../model/index';
import {Util} from '../services/index';


@Component({
  selector: 'yc-i18n-table',
  moduleId: module.id,
  templateUrl: 'i18n.component.html',
  directives: [NgClass, NgIf, NgFor]
})

export class I18nComponent implements OnInit, OnChanges {

  @Input()  title : string;

  @Input()  source:any;

  @Input()  i18n : string ;

  @Input()  value : string;

  @Output() dataChanged: EventEmitter<any> = new EventEmitter<any>();

  dataI18n:Array<Pair<string, string>> = [];
  dataValue:string = '';

  selectedRow:Pair<string,string> = new Pair('','');

  constructor() {
    console.debug('I18nComponent ' + this.title + ' constructed');
  }

  ngOnInit() {
    this.reloadModel();
  }

  reloadModel():void {
    console.debug('I18nComponent source', this.source);
    if (this.source[this.i18n] !== null) {
      this.dataI18n = this.source[this.i18n];
    } else {
      this.dataI18n = [];
    }
    if (this.source[this.value] !== null) {
      this.dataValue = this.source[this.value];
    } else {
      this.dataValue = '';
    }
  }

  ngOnChanges(changes:any) {
    console.debug('I18nComponent ngOnChanges', changes);
    this.reloadModel();
  }

  onRowSelect(selectedRow:Pair<string,string>) {
    console.debug('I18nComponent onRowSelect', selectedRow);
    this.selectedRow = Util.clone(selectedRow);
  }

  onRowDelete(selectedRow:Pair<string,string>) {
    console.debug('I18nComponent onRowDelete', selectedRow);
    for(var idx = 0 ; idx < this.dataI18n.length; idx++) {
      var pair = this.dataI18n[idx];
      if(pair.first === selectedRow.first ) {
        this.dataI18n.splice(idx,1);
        this.selectedRow = new Pair('','');
        this.dataChanged.emit(null);
        console.debug('Removed ' + idx, this.i18n);
        return;
      }
    }
  }

  onRowAdd():void {
    if (this.isBlank(this.selectedRow.second)) {
      this.onRowDelete(this.selectedRow);
      return;
    }
    for(var idx = 0 ; idx < this.dataI18n.length; idx++) {
      var pair = this.dataI18n[idx];
      if(pair.first === this.selectedRow.first ) {
        pair.second = this.selectedRow.second;
        this.selectedRow = new Pair('','');
        this.dataChanged.emit(null);
        console.debug('Replaced ' + idx, this.dataI18n);
        return;
      }
    }

    this.dataI18n.push(this.selectedRow);
    this.selectedRow = new Pair('','');
    this.dataChanged.emit(null);
    console.debug('I18nComponent Added', this.dataI18n);
  }

  onDefaultValueChange():void {
    if (!this.isBlank(this.dataValue)) {
      this.source[this.value] = this.dataValue;
      console.debug('I18nComponent default value is ' + this.dataValue + ' source', this.source);
    } else {
      this.source[this.value] = null;
      console.debug('I18nComponent default value is blank source', this.source);
    }
    this.dataChanged.emit(null);
  }

  private isBlank(value:string):boolean {
    return value === null || value === '' || /^\s+$/.test(value);
  }

}
