import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { AddressBookComponent } from './components/index';

@NgModule({
    imports: [CommonModule, SharedModule, ServicesModule],
    declarations: [
      AddressBookComponent
    ],
    exports: [
      AddressBookComponent
    ]
})

export class AddressBookPagesModule { }
