import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ImportManagerComponent, ExportManagerComponent } from './index';

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
      }
    ])
  ],
  exports: [RouterModule]
})
export class ImpexRoutingModule { }
