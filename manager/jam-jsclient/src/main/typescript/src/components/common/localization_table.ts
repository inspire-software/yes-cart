import {Component, OnInit, Input, Output, EventEmitter} from 'angular2/core';
import {NgClass, NgIf} from 'angular2/common';

import {Pair} from '../../model/common';
import {Util} from '../../service/util';

import {ShopService} from '../../service/shop_service';


@Component({
  selector: 'localization-table',
  moduleId: module.id,
  templateUrl: './localization_table.html',
  styleUrls: ['./localization_table.css'],
  directives: [NgClass, NgIf],
  providers: [ShopService]
})

export class LocalizationTable implements OnInit {

  @Input()  title : string;

  @Input()  source:Object;

  @Input()  i18n : string ;

  @Input()  value : string;

  @Output() dataChanged: EventEmitter<any> = new EventEmitter<any>();

  dataI18n:Array<Pair<string, string>> = [];
  dataValue:string = '';

  selectedRow:Pair<string,string> = new Pair('','');

  constructor() {
    console.debug('Localization table ' + this.title + ' constructed');
  }

  ngOnInit() {
    console.debug('Values', this.source);
    if (this.source[this.i18n] !== null) {
      this.dataI18n = this.source[this.i18n];
    }
    if (this.source[this.value] !== null) {
      this.dataValue = this.source[this.value];
    }
  }

  onRowSelect(selectedRow:Pair<string,string>) {
    console.debug('onRowSelect', selectedRow);
    this.selectedRow = Util.clone(selectedRow);
  }

  onRowDelete(selectedRow:Pair<string,string>) {
    console.debug('onRowDelete', selectedRow);
    for(var idx = 0 ; idx < this.dataI18n.length; idx++) {
      var pair = this.dataI18n[idx];
      if(pair.first === selectedRow.first ) {
        this.dataI18n.splice(idx,1);
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
    console.debug('Added', this.dataI18n);
  }

  onDefaultValueChange():void {
    console.debug('default value', this.dataValue);
    if (!this.isBlank(this.dataValue)) {
      this.source[this.value] = this.dataValue;
    } else {
      this.source[this.value] = null;
    }
    this.dataChanged.emit(null);
  }

  private isBlank(value:string):boolean {
    return value === null || value === '' || /\s+/.test(value);
  }

}
