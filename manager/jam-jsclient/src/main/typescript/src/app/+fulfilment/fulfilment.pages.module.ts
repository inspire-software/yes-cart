import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { FulfilmentRoutingModule } from './fulfilment-routing.module';
import { FulfilmentCentresComponent, FulfilmentCentreComponent, InventoryComponent, OfferComponent } from './components/index';
import { FulfilmentComponent, CentreInventoryComponent } from './index';

@NgModule({
    imports: [FulfilmentRoutingModule, CommonModule, SharedModule, ServicesModule],
    declarations: [
      FulfilmentCentresComponent, FulfilmentCentreComponent, InventoryComponent, OfferComponent,
      FulfilmentComponent, CentreInventoryComponent,
    ],
    exports: [
      FulfilmentCentresComponent, FulfilmentCentreComponent, InventoryComponent, OfferComponent,
      FulfilmentComponent, CentreInventoryComponent,
    ]
})

export class FulfilmentPagesModule { }
