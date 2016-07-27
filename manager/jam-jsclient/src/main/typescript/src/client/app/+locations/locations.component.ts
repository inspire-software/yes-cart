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
import {LocationService, Util} from './../shared/services/index';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {CountriesComponent, CountryComponent, StatesComponent} from './components/index';
import {DataControlComponent} from './../shared/sidebar/index';
import {ModalComponent, ModalResult, ModalAction} from './../shared/modal/index';
import {CountryVO, StateVO} from './../shared/model/index';

@Component({
  selector: 'yc-locations',
  moduleId: module.id,
  templateUrl: 'locations.component.html',
  directives: [TAB_DIRECTIVES, NgIf, CountriesComponent, CountryComponent, StatesComponent, ModalComponent, DataControlComponent ],
})

export class LocationsComponent implements OnInit, OnDestroy {

  private static COUNTRIES:string = 'countries';
  private static COUNTRY:string = 'country';
  private static STATES:string = 'states';
  private static STATE:string = 'state';

  private viewMode:string = LocationsComponent.COUNTRIES;

  private countries:Array<CountryVO> = [];
  private countryFilter:string;

  private selectedCountry:CountryVO;

  private countryEdit:CountryVO;

  deleteConfirmationModalDialog:ModalComponent;

  private states:Array<StateVO> = [];
  private stateFilter:string;

  private selectedState:StateVO;

  private stateEdit:StateVO;

  private deleteValue:String;

  constructor(private _locationService:LocationService) {
    console.debug('LocationsComponent constructed');
  }

  changed:boolean = false;
  validForSave:boolean = false;

  newCountryInstance():CountryVO {
    return { countryId: 0, countryCode: '',  isoCode: '', name: '', displayName: '' };
  }

  newStateInstance():StateVO {
    return { stateId: 0, countryCode: '', stateCode: '', name: '', displayName: ''};
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
      this.viewMode = LocationsComponent.COUNTRIES;
      this.changed = false;
      this.validForSave = false;
      _sub.unsubscribe();
    });
  }

  getAllStates() {
    if (this.selectedCountry != null) {
      var _sub:any = this._locationService.getAllStates(this.selectedCountry).subscribe(allstates => {
        console.debug('LocationsComponent getAllStates', allstates);
        this.states = allstates;
        this.selectedState = null;
        this.stateEdit = null;
        this.viewMode = LocationsComponent.STATES;
        this.changed = false;
        this.validForSave = false;
        _sub.unsubscribe();
      });
    }
  }

  protected onRefreshHandler() {
    console.debug('LocationsComponent refresh handler');
    if (this.viewMode === LocationsComponent.COUNTRIES ||
        this.viewMode === LocationsComponent.COUNTRY ||
        this.selectedCountry == null) {
      this.getAllLocations();
    } else {
      this.getAllStates();
    }
  }

  onCountrySelected(data:CountryVO) {
    console.debug('LocationsComponent onCountrySelected', data);
    this.selectedCountry = data;
    this.stateFilter = '';
  }

  onCountryChanged(data:any) {
    console.debug('LocationsComponent onCountryChanged', data);
    this.changed = true;
    this.validForSave = data.valid;
    this.countryEdit = data.country;
  }

  onStateSelected(data:StateVO) {
    console.debug('LocationsComponent onStateSelected', data);
    this.selectedState = data;
  }

  onStateChanged(data:any) {
    console.debug('LocationsComponent onStateChanged', data);
    this.changed = true;
    this.validForSave = data.valid;
    this.stateEdit = data.state;
  }

  protected onBackToList() {
    console.debug('LocationsComponent onBackToList handler');
    if (this.viewMode === LocationsComponent.STATE) {
      this.stateEdit = null;
      this.viewMode = LocationsComponent.STATES;
    } else if (this.viewMode === LocationsComponent.STATES) {
      this.viewMode = LocationsComponent.COUNTRIES;
    } else if (this.viewMode === LocationsComponent.COUNTRY) {
      this.countryEdit = null;
      this.viewMode = LocationsComponent.COUNTRIES;
    }
  }

  protected onRowNew() {
    console.debug('LocationsComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === LocationsComponent.COUNTRIES) {
      this.countryEdit = this.newCountryInstance();
      this.viewMode = LocationsComponent.COUNTRY;
    } else if (this.viewMode === LocationsComponent.STATES) {
      this.stateEdit = this.newStateInstance();
      this.viewMode = LocationsComponent.STATE;
    }
  }

  protected onRowDelete(row:any) {
    console.debug('LocationsComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedState != null) {
      this.onRowDelete(this.selectedState);
    } else if (this.selectedCountry != null) {
      this.onRowDelete(this.selectedCountry);
    }
  }


  protected onRowEditCountry(row:CountryVO) {
    console.debug('LocationsComponent onRowEditCountry handler', row);
    this.countryEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = LocationsComponent.COUNTRY;
  }

  protected onRowEditState(row:StateVO) {
    console.debug('LocationsComponent onRowEditState handler', row);
    this.stateEdit = Util.clone(row);
    this.changed = false;
    this.validForSave = false;
    this.viewMode = LocationsComponent.STATE;
  }

  protected onRowEditSelected() {
    if (this.selectedState != null) {
      this.onRowEditState(this.selectedState);
    } else if (this.selectedCountry != null) {
      this.onRowEditCountry(this.selectedCountry);
    }
  }


  protected onRowList(row:CountryVO) {
    console.debug('LocationsComponent onRowList handler', row);
    this.getAllStates();
  }


  protected onRowListSelected() {
    if (this.selectedCountry != null) {
      this.onRowList(this.selectedCountry);
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
              this.viewMode = LocationsComponent.COUNTRIES;
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

      if (this.selectedState != null) {
        console.debug('LocationsComponent onDeleteConfirmationResult', this.selectedState);

        var _sub:any = this._locationService.removeState(this.selectedState).subscribe(res => {
          console.debug('LocationsComponent removeState', this.selectedState);
          let idx = this.states.indexOf(this.selectedState);
          this.states.splice(idx, 1);
          this.states = this.states.slice(0, this.states.length); // reset to propagate changes
          this.selectedState = null;
          this.stateEdit = null;
          _sub.unsubscribe();
        });

      } else if (this.selectedCountry) {
        console.debug('LocationsComponent onDeleteConfirmationResult', this.selectedCountry);

        var _sub:any = this._locationService.removeCountry(this.selectedCountry).subscribe(res => {
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


}
