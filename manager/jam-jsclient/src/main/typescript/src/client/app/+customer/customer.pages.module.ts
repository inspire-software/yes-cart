import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { AddressBookPagesModule } from '../+address/address.pages.module';

import { CustomerComponent, CustomersComponent } from './components/index';
import { AllCustomersComponent } from './index';

@NgModule({
    imports: [CommonModule, SharedModule, ServicesModule, AddressBookPagesModule],
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
