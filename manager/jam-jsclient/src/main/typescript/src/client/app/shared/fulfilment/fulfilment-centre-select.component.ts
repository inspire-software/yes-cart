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
import {Component,  OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgFor} from '@angular/common';
import {HTTP_PROVIDERS}    from '@angular/http';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {FulfilmentCentreInfoVO} from './../model/index';
import {FulfilmentService} from './../services/index';
import {Futures, Future} from './../event/index';
import {Config} from './../config/env.config';

@Component({
  selector: 'yc-fulfilment-centre-select',
  moduleId: module.id,
  templateUrl: 'fulfilment-centre-select.component.html',
  directives: [ROUTER_DIRECTIVES, NgFor],
})

export class FulfilmentCentreSelectComponent implements OnInit, OnDestroy {

  private centres : FulfilmentCentreInfoVO[] = [];
  private filteredCentres : FulfilmentCentreInfoVO[] = [];
  private centreFilter : string;

  private selectedCentre : FulfilmentCentreInfoVO = null;

  delayedFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;
  filterCap:number = Config.UI_FILTER_CAP;

  @Output() dataSelected: EventEmitter<FulfilmentCentreInfoVO> = new EventEmitter<FulfilmentCentreInfoVO>();

  constructor (private _centreService : FulfilmentService) {
    console.debug('FulfilmentCentreSelectComponent constructed');
  }

  getAllCentres() {

    var _sub:any = this._centreService.getAllFulfilmentCentres().subscribe( allcentres => {
      console.debug('FulfilmentCentreSelectComponent getAllCentres', allcentres);
      this.centres = allcentres;
      this.filterCentres();
      _sub.unsubscribe();
    });
  }


  private filterCentres() {

    let _filter = this.centreFilter ? this.centreFilter.toLowerCase() : null;

    if (_filter) {
      this.filteredCentres = this.centres.filter(centre =>
        centre.code.toLowerCase().indexOf(_filter) !== -1 ||
        centre.name.toLowerCase().indexOf(_filter) !== -1 ||
        centre.description && centre.description.toLowerCase().indexOf(_filter) !== -1
      );
      console.debug('FulfilmentCentresComponent filterCentres', _filter);
    } else {
      this.filteredCentres = this.centres;
      console.debug('FulfilmentCentresComponent filterCentres no filter');
    }

    if (this.filteredCentres === null) {
      this.filteredCentres = [];
    }

  }


  ngOnDestroy() {
    console.debug('FulfilmentCentreSelectComponent ngOnDestroy');
  }

  ngOnInit() {
    console.debug('FulfilmentCentreSelectComponent ngOnInit');
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.filterCentres();
    }, this.delayedFilteringMs);

    this.getAllCentres();

  }

  onSelectClick(centre: FulfilmentCentreInfoVO) {
    console.debug('FulfilmentCentreSelectComponent onSelectClick', centre);
    this.selectedCentre = centre;
    this.dataSelected.emit(this.selectedCentre);
  }

  protected onFilterChange() {

    this.delayedFiltering.delay();

  }

}
