import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AttributeDefinitionsComponent } from './attributes/index';
import { SystemPreferencesComponent, SystemConfigurationComponent } from './preferences/index';
import { CacheMonitoringComponent } from './cache/index';
import { ClusterComponent } from './cluster/index';
import { ReindexComponent } from './reindex/index';
import { QueryComponent } from './query/index';
import { PaymentGatewaysComponent } from './payment/index';
import { LicenseComponent } from '../shared/license/index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'system/attributes',
        component: AttributeDefinitionsComponent
      },
      {
        path: 'system/preferences',
        component: SystemPreferencesComponent
      },
      {
        path: 'system/configuration',
        component: SystemConfigurationComponent
      },
      {
        path: 'system/cache',
        component: CacheMonitoringComponent
      },
      {
        path: 'system/cluster',
        component: ClusterComponent
      },
      {
        path: 'system/reindex',
        component: ReindexComponent
      },
      {
        path: 'system/query',
        component: QueryComponent
      },
      {
        path: 'system/payment',
        component: PaymentGatewaysComponent
      },
      {
        path: 'license',
        component: LicenseComponent
      }
    ])
  ],
  exports: [RouterModule]
})
export class SystemRoutingModule { }
