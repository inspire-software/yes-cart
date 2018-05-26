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
import { Component, Input } from '@angular/core';
import { DashboardWidgetVO } from '../../shared/model/index';

@Component({
  selector: 'yc-widget-pgcallbacks',
  template: `
    <div class="col-lg-3 col-md-4 col-sm-6">
      <div class="panel {{ widget.data.hasUnprocessed ? 'panel-red' : 'panel-yellow'}}">
        <div class="panel-heading widget-body">
          <div class="row">
            <div class="col-xs-3">
              <i class="fa fa-rotate-left fa-5x"></i>
            </div>
            <div class="col-xs-9 text-right">
              <ng-template [ngIf]="!widget.data.hasUnprocessed">
                <div class="huge">0</div>
              </ng-template>
              <ng-template [ngIf]="widget.data.hasUnprocessed">
                <div *ngFor="let node of widget.data.unprocessed" title="{{ node.first }}">{{ node.first.length > 15 ? (node.first.substring(0, 15) + '...') : node.first }} {{ node.second }}</div>
              </ng-template>
            </div>
          </div>
        </div>
        <a>
          <div class="panel-footer">
            <span class="pull-left"><span class="label label-info">YCE</span> <span class="text-muted">{{ 'DASHBOARD_PG_CALLBACK' | translate }}</span></span>
            <span class="pull-right"><i class="fa fa-arrow-circle-right"></i></span>
            <div class="clearfix"></div>
          </div>
        </a>
      </div>
    </div>
  `
})
export class WidgetPgcallbacksComponent {

  @Input() widget: DashboardWidgetVO;

}
