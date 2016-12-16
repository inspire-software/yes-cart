import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { CountriesComponent, CountryComponent, StatesComponent, StateComponent } from './components/index';
import { LocationsComponent } from './index';

@NgModule({
    imports: [CommonModule, SharedModule, ServicesModule],
    declarations: [
      CountriesComponent, CountryComponent, StatesComponent, StateComponent,
      LocationsComponent,
    ],
    exports: [
      CountriesComponent, CountryComponent, StatesComponent, StateComponent,
      LocationsComponent,
    ]
})

export class LocationsPagesModule { }
