import {Component, OnInit, OnChanges, Input, Output, EventEmitter} from '@angular/core';
import {NgClass, NgIf, NgFor} from '@angular/common';
import {FormBuilder, FormGroup, Validators, REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {YcValidators} from '../validation/validators';

import {Pair} from '../model/index';
import {Util} from '../services/index';


@Component({
  selector: 'yc-i18n-table',
  moduleId: module.id,
  templateUrl: 'i18n.component.html',
  directives: [NgClass, NgIf, NgFor, REACTIVE_FORM_DIRECTIVES]
})

export class I18nComponent {

  @Input()  title : string;

  private   _source:any;

  @Input()  i18n : string ;

  @Input()  value : string;

  @Output() dataChanged: EventEmitter<any> = new EventEmitter<any>();

  dataI18n:Array<Pair<string, string>> = [];
  dataValue:string = '';

  expandDefault:boolean = true;

  selectedRow:Pair<string,string> = new Pair('','');

  _defaultRequired : boolean = false;
  i18nForm:any;

  constructor(fb: FormBuilder) {
    console.debug('I18nComponent constructed', this.title);
    this.i18nForm = fb.group({
       'addLang':  ['', YcValidators.validLanguageCode],
       'addVal':  ['', YcValidators.nonBlankTrimmed],
       'dataValue':  ['', YcValidators.nonBlankTrimmed],
       'dataValueXL':  ['', YcValidators.nonBlankTrimmed]
    });
  }

  @Input()
  set formGroup(formGroup:FormGroup) {
    // This is a bit of the hack to make sure we have correct binding, we do not have parent formGroup until
    // this setter is called but then this is the only place to set it as other lifecycle methods are executed
    // multiple times for every render, so this place just as good as any
    if (formGroup.contains(this.value)) {
      formGroup.removeControl(this.value);
    }
    formGroup.addControl(this.value, this.i18nForm);
    console.debug('I18nComponent attaching self to parent control form', [ this.value, formGroup ]);
  }

  @Input()
  set defaultRequired(required:string) {
    this._defaultRequired = required === 'true';
    if (this._defaultRequired) {
      console.debug('I18nComponent resetting validator to required non-blank', this.value);
      this.i18nForm.controls['dataValue'].validator = YcValidators.requiredNonBlankTrimmed;
      this.i18nForm.controls['dataValueXL'].validator = YcValidators.requiredNonBlankTrimmed;
    } else {
      console.debug('I18nComponent resetting validator to non-blank', this.value);
      this.i18nForm.controls['dataValue'].validator = YcValidators.nonBlankTrimmed;
      this.i18nForm.controls['dataValueXL'].validator = YcValidators.nonBlankTrimmed;
    }
  }

  @Input()
  set source(source:any) {
    this._source = source;
    this.reloadModel();
  }

  get source():any {
    return this._source;
  }

  reloadModel():void {
    console.debug('I18nComponent source', this.source);
    if (this.source[this.i18n] !== null) {
      this.dataI18n = this.source[this.i18n];
    } else {
      this.dataI18n = [];
    }
    if (this.source[this.value] !== null) {
      this.dataValue = '' + this.source[this.value];
    } else {
      this.dataValue = '';
    }
  }

  onExpandDefault() {
    this.expandDefault = !this.expandDefault;
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
    if (this.isBlank(this.selectedRow.first)) {
      return;
    }
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
