import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { SystemRoutingModule } from './system-routing.module';
import { AttributesComponent, AttributeGroupsComponent, AttributeComponent } from './attributes/components/index';
import { AttributeDefinitionsComponent } from './attributes/index';
import { CacheMonitoringComponent } from './cache/index';
import { ClusterComponent } from './cluster/index';
import { GatewaysComponent, ParameterValuesComponent } from './payment/components/index';
import { PaymentGatewaysComponent } from './payment/index';
import { SystemPreferencesComponent, SystemConfigurationComponent } from './preferences/index';
import { QueryComponent } from './query/index';
import { ReindexComponent } from './reindex/index';

@NgModule({
    imports: [SystemRoutingModule, CommonModule, SharedModule, ServicesModule],
    declarations: [
      AttributesComponent, AttributeGroupsComponent, AttributeComponent, AttributeDefinitionsComponent,
      CacheMonitoringComponent,
      ClusterComponent,
      GatewaysComponent, ParameterValuesComponent, PaymentGatewaysComponent,
      SystemPreferencesComponent, SystemConfigurationComponent,
      QueryComponent,
      ReindexComponent,
    ],
    exports: [
      AttributesComponent, AttributeGroupsComponent, AttributeComponent, AttributeDefinitionsComponent,
      CacheMonitoringComponent,
      ClusterComponent,
      GatewaysComponent, ParameterValuesComponent, PaymentGatewaysComponent,
      SystemPreferencesComponent, SystemConfigurationComponent,
      QueryComponent,
      ReindexComponent,
    ]
})

export class SystemPagesModule { }
