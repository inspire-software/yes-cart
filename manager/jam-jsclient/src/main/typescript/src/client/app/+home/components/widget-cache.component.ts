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
import { Component, Input } from '@angular/core';
import { DashboardWidgetVO } from '../../shared/model/index';

@Component({
  selector: 'yc-widget-cache',
  template: `
    <div class="col-lg-3 col-md-4 col-sm-6">
      <div class="panel  {{ widget.data.hasHotCaches ? 'panel-red' : 'panel-green'}}">
        <div class="panel-heading widget-body">
          <div class="row">
            <div class="col-xs-3">
              <i class="fa fa-gears fa-5x"></i>
            </div>
            <div class="col-xs-9 text-right">
              <div *ngFor="let node of widget.data.caches">{{ node.first }} {{ node.second }}</div>
            </div>
          </div>
        </div>
        <a [routerLink]="['/system/cache']">
          <div class="panel-footer">
            <span class="pull-left">{{ 'PANEL_SYSTEM_CACHE' | translate }}</span>
            <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
            <div class="clearfix"></div>
          </div>
        </a>
      </div>
  `
})
export class WidgetCacheComponent {

  @Input() widget: DashboardWidgetVO;

}
