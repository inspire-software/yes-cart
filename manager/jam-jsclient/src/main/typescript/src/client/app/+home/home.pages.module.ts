import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { HomeComponent, ReportsComponent } from './index';

@NgModule({
    imports: [CommonModule, SharedModule, ServicesModule],
    declarations: [
      HomeComponent, ReportsComponent,
    ],
    exports: [
      HomeComponent, ReportsComponent,
    ]
})

export class HomePagesModule { }
