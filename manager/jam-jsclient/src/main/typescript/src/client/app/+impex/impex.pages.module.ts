import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { ImpexRoutingModule } from './impex-routing.module';
import { ImportManagerComponent, ExportManagerComponent, ImpexDataGroupsComponent, ImpexDataDescriptorsComponent } from './index';
import { DataGroupComponent, DataGroupsComponent, DataDescriptorComponent, DataDescriptorsComponent } from './components/index';

@NgModule({
    imports: [ImpexRoutingModule, CommonModule, SharedModule, ServicesModule],
    declarations: [
      ImportManagerComponent, ExportManagerComponent,
      ImpexDataGroupsComponent, ImpexDataDescriptorsComponent,
      DataGroupComponent, DataGroupsComponent, DataDescriptorComponent, DataDescriptorsComponent,
    ],
    exports: [
      ImportManagerComponent, ExportManagerComponent,
      ImpexDataGroupsComponent, ImpexDataDescriptorsComponent,
      DataGroupComponent, DataGroupsComponent, DataDescriptorComponent, DataDescriptorsComponent
    ]
})

export class ImpexPagesModule { }
