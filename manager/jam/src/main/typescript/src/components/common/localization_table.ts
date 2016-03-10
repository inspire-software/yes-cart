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

  @Input()  data : Array<Pair<string, string>> ;

  @Input()  defaultvalue : string;

  @Output() dataChanged: EventEmitter<any> = new EventEmitter<any>();

  selectedRow:Pair<string,string> = new Pair('','');

  constructor() {
    console.debug('Localization table constructed');
  }

  ngOnInit() {
    console.debug('ngOnInit');
  }

  onRowSelect(selectedRow:Pair<string,string>) {
    console.debug('onRowSelect  ' + selectedRow);
    this.selectedRow = Util.clone(selectedRow);
  }

  onRowDelete(selectedRow:Pair<string,string>) {
    console.debug('onRowDelete  ' + selectedRow);
    for(var idx = 0 ; idx < this.data.length; idx++) {
      var pair = this.data[idx];
      if(pair.first === selectedRow.first ) {
        this.data.splice(idx,1);
        this.dataChanged.emit(null);
        return;
      }
    }
  }

  onRowAdd() {
    for(var idx = 0 ; idx < this.data.length; idx++) {
      var pair = this.data[idx];
      if(pair.first === this.selectedRow.first ) {
        pair.second = this.selectedRow.second;
        this.selectedRow = new Pair('','');
        this.dataChanged.emit(null);
        return;
      }
    }

    this.data.push(this.selectedRow);
    this.selectedRow = new Pair('','');
    this.dataChanged.emit(null);
  }

}
