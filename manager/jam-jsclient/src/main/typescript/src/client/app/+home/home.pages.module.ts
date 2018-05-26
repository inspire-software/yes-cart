import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent, ReportsComponent } from './index';

import {
  WidgetNoopComponent,
  WidgetAlertComponent,
  WidgetOrdersComponent,
  WidgetPgcallbacksComponent,
  WidgetCustomersComponent,
  WidgetCacheComponent,
  WidgetReindexComponent,
  WidgetUiSettingsComponent,
} from './components/index';
import { WidgetDirective, WidgetContainerComponent } from './index';

@NgModule({
    imports: [HomeRoutingModule, CommonModule, SharedModule, ServicesModule],
    declarations: [
      HomeComponent, ReportsComponent,
      WidgetNoopComponent,
      WidgetAlertComponent,
      WidgetOrdersComponent,
      WidgetPgcallbacksComponent,
      WidgetCustomersComponent,
      WidgetCacheComponent,
      WidgetCacheComponent,
      WidgetReindexComponent,
      WidgetUiSettingsComponent,
      WidgetDirective, WidgetContainerComponent
    ],
    entryComponents: [
      WidgetNoopComponent,
      WidgetAlertComponent,
      WidgetOrdersComponent,
      WidgetPgcallbacksComponent,
      WidgetCustomersComponent,
      WidgetCacheComponent,
      WidgetReindexComponent,
      WidgetUiSettingsComponent,
    ],
    exports: [
      HomeComponent, ReportsComponent,
    ]
})

export class HomePagesModule { }
