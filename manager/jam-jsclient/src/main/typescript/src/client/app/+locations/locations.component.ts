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
import {CountryVO} from './../shared/model/index';
import {Router, ActivatedRoute} from '@angular/router';
import {TAB_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';

import { CountriesComponent } from './components/index';

@Component({
  selector: 'yc-locations',
  moduleId: module.id,
  templateUrl: 'locations.component.html',
  directives: [TAB_DIRECTIVES, NgIf, CountriesComponent ],
})

export class LocationsComponent implements OnInit, OnDestroy {

  private selectedCountry:CountryVO;

  private countryEdit:boolean = false;

  constructor() {
    console.debug('LocationsComponent constructed');
  }

  ngOnInit() {
    console.debug('LocationsComponent ngOnInit');
  }

  ngOnDestroy() {
    console.debug('LocationsComponent ngOnDestroy');
  }

  onCountrySelected(data:any) {
    console.debug('LocationsComponent onCountrySelected', data);
  }

}
