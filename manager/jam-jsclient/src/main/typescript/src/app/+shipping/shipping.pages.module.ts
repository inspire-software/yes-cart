import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { ShippingRoutingModule } from './shipping-routing.module';
import { CarriersComponent, CarrierComponent, SlasComponent, SlaComponent } from './components/index';
import { ShippingComponent } from './index';

@NgModule({
    imports: [ShippingRoutingModule, CommonModule, SharedModule, ServicesModule],
    declarations: [
      CarriersComponent, CarrierComponent, SlasComponent, SlaComponent,
      ShippingComponent,
    ],
    exports: [
      CarriersComponent, CarrierComponent, SlasComponent, SlaComponent,
      ShippingComponent,
    ]
})

export class ShippingPagesModule { }
