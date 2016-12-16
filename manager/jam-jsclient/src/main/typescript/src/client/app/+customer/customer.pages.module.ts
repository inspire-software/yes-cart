import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { CustomerComponent, CustomersComponent } from './components/index';
import { AllCustomersComponent } from './index';

@NgModule({
    imports: [CommonModule, SharedModule, ServicesModule],
    declarations: [
      CustomerComponent, CustomersComponent,
      AllCustomersComponent
    ],
    exports: [
      CustomerComponent, CustomersComponent,
      AllCustomersComponent
    ]
})

export class CustomerPagesModule { }
