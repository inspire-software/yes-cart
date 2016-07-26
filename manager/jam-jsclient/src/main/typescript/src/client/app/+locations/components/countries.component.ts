/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import {Component, OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {CountryVO} from './../../shared/model/index';
import {Router, ActivatedRoute} from '@angular/router';
import {LocationService} from './../../shared/services/index';
import {PaginationComponent} from './../../shared/pagination/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';


@Component({
  selector: 'yc-countries',
  moduleId: module.id,
  templateUrl: 'countries.component.html',
  directives: [NgIf, PaginationComponent, ModalComponent],
})

export class CountriesComponent implements OnInit, OnDestroy {

  private countries:Array<CountryVO>;

  @Input() selectedCountry:CountryVO;

  @Output() dataSelected: EventEmitter<CountryVO> = new EventEmitter<CountryVO>();

  @Output() dataChange: EventEmitter<CountryVO> = new EventEmitter<CountryVO>();

  //paging
  maxSize:number = 5;
  itemsPerPage:number = 10;
  totalItems:number = 0;
  currentPage:number = 1;

  deleteConfirmationModalDialog:ModalComponent;
  editModalDialog:ModalComponent;

  constructor(private _locationService:LocationService) {
    console.debug('CountriesComponent constructed');
  }

  ngOnInit() {
    console.debug('CountriesComponent ngOnInit');
    this.getAllLocations();
  }

  getAllLocations() {
    var _sub:any = this._locationService.getAllCountries().subscribe( allcountries => {
      console.debug('CountriesComponent getAllCountries', allcountries);
      this.countries = allcountries;
      this.selectedCountry = null;
      this.totalItems = this.countries.length;
      this.currentPage = 1;
      _sub.unsubscribe();
      this.dataSelected.emit(this.selectedCountry);
    });
  }

  ngOnDestroy() {
    console.debug('CountriesComponent ngOnDestroy');
  }

  protected onSelectRow(row:CountryVO) {
    console.debug('CountriesComponent onSelectRow handler', row);
    if (row == this.selectedCountry) {
      this.selectedCountry = null;
    } else {
      this.selectedCountry = row;
    }
    this.dataSelected.emit(this.selectedCountry);
  }

  protected onRefreshHandler() {
    console.debug('CountriesComponent refresh handler');
    this.getAllLocations();
  }


  protected onRowNew() {
    console.debug('CountriesComponent onRowNew handler');
    //this.formReset();
    //this.urlToEdit = {'urlId': 0, 'url': '', 'theme' : '', 'primary': false};
    this.editModalDialog.show();
  }

  protected onRowDelete(row:CountryVO) {
    console.debug('CountriesComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedCountry != null) {
      this.onRowDelete(this.selectedCountry);
    }
  }


  protected onRowEdit(row:CountryVO) {
    console.debug('CountriesComponent onRowEdit handler', row);
    //this.formReset();
    //this.validForSave = false;
    //this.urlToEdit = Util.clone(row) ;
    this.editModalDialog.show();
  }

  protected onRowEditSelected() {
    if (this.selectedCountry != null) {
      this.onRowEdit(this.selectedCountry);
    }
  }

  protected deleteConfirmationModalDialogLoaded(modal: ModalComponent) {
    console.debug('CountriesComponent deleteConfirmationModalDialogLoaded');
    // Here you get a reference to the modal so you can control it programmatically
    this.deleteConfirmationModalDialog = modal;
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('CountriesComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      console.debug('ShopUrlComponent onDeleteConfirmationResult', this.selectedCountry);

      var _sub:any = this._locationService.removeCountry(this.selectedCountry).subscribe( res => {
        console.debug('CountriesComponent removeCountry', this.selectedCountry);
        let idx = this.countries.indexOf(this.selectedCountry);
        this.countries.splice(idx, 1);
        this.selectedCountry = null;
        this.totalItems = this.countries.length;
        this.currentPage = 1;
        _sub.unsubscribe();
        this.dataSelected.emit(this.selectedCountry);
      });
    }
  }


}
