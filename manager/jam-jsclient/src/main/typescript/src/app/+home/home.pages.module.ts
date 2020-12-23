import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { HomeRoutingModule } from './home-routing.module';
import { EmptyComponent, HomeComponent, ReportsComponent } from './index';

import {
  WidgetNoopComponent,
  WidgetAlertComponent,
  WidgetOrdersComponent,
  WidgetPgcallbacksComponent,
  WidgetCustomersComponent,
  WidgetCacheComponent,
  WidgetReindexComponent,
  WidgetUiSettingsComponent,
  WidgetDirective
} from './components/index';
import { WidgetContainerComponent } from './widget-container.component';


@NgModule({
    imports: [HomeRoutingModule, CommonModule, SharedModule, ServicesModule],
    declarations: [
      EmptyComponent, HomeComponent, ReportsComponent,
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
      EmptyComponent, HomeComponent, ReportsComponent,
    ]
})

export class HomePagesModule { }
