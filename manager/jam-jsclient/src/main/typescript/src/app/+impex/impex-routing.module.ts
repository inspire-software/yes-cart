import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ImportManagerComponent, ExportManagerComponent, ImpexDataGroupsComponent, ImpexDataDescriptorsComponent } from './index';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'impex/import',
        component: ImportManagerComponent
      },
      {
        path: 'impex/export',
        component: ExportManagerComponent
      },
      {
        path: 'impex/datagroups',
        component: ImpexDataGroupsComponent
      },
      {
        path: 'impex/datadescriptors',
        component: ImpexDataDescriptorsComponent
      }
    ])
  ],
  exports: [RouterModule]
})
export class ImpexRoutingModule { }
