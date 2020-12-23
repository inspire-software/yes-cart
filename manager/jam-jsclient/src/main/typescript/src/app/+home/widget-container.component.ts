/*
 * Copyright 2009 Inspire-Software.com
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
import { Component, Type, Input, OnInit, ViewChild, ComponentFactoryResolver } from '@angular/core';

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
import { DashboardWidgetVO } from '../shared/model/index';
import { LogUtil } from '../shared/log/index';

export class WidgetMapping {
  constructor(public component: Type<any>, public data: any) {}
}


@Component({
  selector: 'cw-widget-container',
  template: `<ng-template cwWidget></ng-template>`
})
export class WidgetContainerComponent implements OnInit {

  private static MAPPING:any = {
    'Alerts': new WidgetMapping(WidgetAlertComponent, 'Alerts'),
    'OrdersInShop': new WidgetMapping(WidgetOrdersComponent, 'OrdersInShop'),
    'UnprocessedPgCallbacks': new WidgetMapping(WidgetPgcallbacksComponent, 'UnprocessedPgCallbacks'),
    'CustomersInShop': new WidgetMapping(WidgetCustomersComponent, 'CustomersInShop'),
    'CacheOverview': new WidgetMapping(WidgetCacheComponent, 'CacheOverview'),
    'ReindexOverview': new WidgetMapping(WidgetReindexComponent, 'ReindexOverview'),
    'UiSettings': new WidgetMapping(WidgetUiSettingsComponent, 'UiSettings'),
  };

  @Input() widget: DashboardWidgetVO;
  @ViewChild(WidgetDirective, { static: true }) cwWidget: WidgetDirective;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) { }

  ngOnInit() {
    this.loadComponent();
  }

  loadComponent() {

    let widgetFullId = this.widget.widgetId;
    // Widgets with underscore treated as sub class e.g. "Chart_OrdersInShop" would render "Chart" widget
    let widgetId = widgetFullId.indexOf('_') != -1 ? widgetFullId.substring(0, widgetFullId.indexOf('_')) : widgetFullId;

    let comp = WidgetContainerComponent.MAPPING.hasOwnProperty(widgetId) ?
        WidgetContainerComponent.MAPPING[widgetId].component : WidgetNoopComponent;

    LogUtil.debug('WidgetContainerComponent loadComponent', widgetId, comp);

    let componentFactory = this.componentFactoryResolver.resolveComponentFactory(comp);

    let viewContainerRef = this.cwWidget.viewContainerRef;
    viewContainerRef.clear();

    let componentRef = viewContainerRef.createComponent(componentFactory);
    (<any>componentRef.instance).widget = this.widget;

  }

}
