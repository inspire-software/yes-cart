import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { ImportManagerComponent, ExportManagerComponent } from './index';

@NgModule({
    imports: [CommonModule, SharedModule, ServicesModule],
    declarations: [
      ImportManagerComponent, ExportManagerComponent,
    ],
    exports: [
      ImportManagerComponent, ExportManagerComponent,
    ]
})

export class ImpexPagesModule { }
