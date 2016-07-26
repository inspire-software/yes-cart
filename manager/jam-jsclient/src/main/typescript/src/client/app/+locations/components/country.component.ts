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
  selector: 'yc-country',
  moduleId: module.id,
  templateUrl: 'countries.component.html',
  directives: [NgIf, PaginationComponent, ModalComponent],
})

export class CountryComponent implements OnInit, OnDestroy {

  countryEdit:CountryVO;

  @Input() country:CountryVO;

  @Output() dataChanged: EventEmitter<CountryVO> = new EventEmitter<CountryVO>();

  constructor(private _locationService:LocationService) {
    console.debug('CountryComponent constructed');
  }

  ngOnInit() {
    console.debug('CountryComponent ngOnInit');
  }

  ngOnDestroy() {
    console.debug('CountryComponent ngOnDestroy');
  }

  protected onRefreshHandler() {
    console.debug('CountryComponent refresh handler');
  }

}
