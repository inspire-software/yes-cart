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
import {Component, OnInit, OnDestroy} from '@angular/core';
import {NgIf} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {Router, ActivatedRoute} from '@angular/router';
import {LocationService, Util} from './../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {CountriesComponent, CountryComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {CountryVO} from './../shared/model/index';

@Component({
  selector: 'yc-locations',
  moduleId: module.id,
  templateUrl: 'locations.component.html',
  directives: [TAB_DIRECTIVES, NgIf, CountriesComponent, CountryComponent, ModalComponent, DataControlComponent ],
})

export class LocationsComponent implements OnInit, OnDestroy {

  private countries:Array<CountryVO> = [];
  private countryFilter:string;

  private selectedCountry:CountryVO;

  private countryEdit:CountryVO;

  deleteConfirmationModalDialog:ModalComponent;

  constructor(private _locationService:LocationService) {
    console.debug('LocationsComponent constructed');
  }

  changed:boolean = false;
  validForSave:boolean = false;

  newCountryInstance():CountryVO {
    return { countryId: 0, countryCode: '',  isoCode: '', name: '', displayName: '' };
  }

  ngOnInit() {
    console.debug('LocationsComponent ngOnInit');
    this.onRefreshHandler();
  }

  ngOnDestroy() {
    console.debug('LocationsComponent ngOnDestroy');
  }


  getAllLocations() {
    var _sub:any = this._locationService.getAllCountries().subscribe( allcountries => {
      console.debug('LocationsComponent getAllCountries', allcountries);
      this.countries = allcountries;
      this.selectedCountry = null;
      this.countryEdit = null;
      this.changed = false;
      this.validForSave = false;
      _sub.unsubscribe();
    });
  }

  protected onRefreshHandler() {
    console.debug('LocationsComponent refresh handler');
    this.getAllLocations();
  }

  onCountrySelected(data:any) {
    console.debug('LocationsComponent onCountrySelected', data);
    this.selectedCountry = data;
  }

  onCountryChanged(data:any) {
    console.debug('LocationsComponent onCountryChanged', data);
    this.changed = true;
    this.validForSave = data.valid;
    this.countryEdit = data.country;
  }

  protected onBackToList() {
    console.debug('LocationsComponent onBackToList handler');
    this.countryEdit = null;
  }

  protected onRowNew() {
    console.debug('LocationsComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    this.countryEdit = this.newCountryInstance();
  }

  protected onRowDelete(row:CountryVO) {
    console.debug('LocationsComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedCountry != null) {
      this.onRowDelete(this.selectedCountry);
    }
  }


  protected onRowEdit(row:CountryVO) {
    console.debug('LocationsComponent onRowEdit handler', row);
    this.countryEdit = Util.clone(this.selectedCountry);
    this.changed = false;
    this.validForSave = false;
  }

  protected onRowEditSelected() {
    if (this.selectedCountry != null) {
      this.onRowEdit(this.selectedCountry);
    }
  }

  protected onSaveHandler() {
    console.debug('LocationsComponent Save handler', this.countryEdit);
    if (this.validForSave && this.changed && this.countryEdit) {

        var _sub:any = this._locationService.saveCountry(this.countryEdit).subscribe(
            rez => {
              if (this.countryEdit.countryId > 0) {
                let idx = this.countries.findIndex(rez => rez.countryId == this.countryEdit.countryId);
                if (idx !== -1) {
                  this.countries[idx] = rez;
                  console.debug('LocationsComponent changed', rez);
                }
              } else {
                this.countries.push(rez);
                this.countryFilter = rez.countryCode;
                console.debug('LocationsComponent added', rez);
              }
              this.changed = false;
              this.selectedCountry = rez;
              this.countryEdit = null;
              _sub.unsubscribe();
            }
        );
    }
  }

  protected onDiscardEventHandler() {
    console.debug('LocationsComponent discard handler');
    if (this.selectedCountry != null) {
      this.onRowEditSelected();
    } else {
      this.onRowNew();
    }
  }

  protected deleteConfirmationModalDialogLoaded(modal: ModalComponent) {
    console.debug('LocationsComponent deleteConfirmationModalDialogLoaded');
    // Here you get a reference to the modal so you can control it programmatically
    this.deleteConfirmationModalDialog = modal;
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('LocationsComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      console.debug('LocationsComponent onDeleteConfirmationResult', this.selectedCountry);

      var _sub:any = this._locationService.removeCountry(this.selectedCountry).subscribe( res => {
        console.debug('LocationsComponent removeCountry', this.selectedCountry);
        let idx = this.countries.indexOf(this.selectedCountry);
        this.countries.splice(idx, 1);
        this.countries = this.countries.slice(0, this.countries.length); // reset to propagate changes
        this.selectedCountry = null;
        this.countryEdit = null;
        _sub.unsubscribe();
      });
    }
  }


}
