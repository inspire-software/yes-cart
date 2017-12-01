import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent, ReportsComponent } from './index';

@NgModule({
    imports: [HomeRoutingModule, CommonModule, SharedModule, ServicesModule],
    declarations: [
      HomeComponent, ReportsComponent,
    ],
    exports: [
      HomeComponent, ReportsComponent,
    ]
})

export class HomePagesModule { }
