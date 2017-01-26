import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import {
  ErrorEventBus, ShopEventBus, I18nEventBus, WindowMessageEventBus, UserEventBus,
  ValidationService,
  ShopService,
  ShippingService, LocationService,
  OrganisationService,
  CatalogService,
  PIMService,
  ContentService,
  FulfilmentService,
  PricingService,
  CustomerOrderService,
  CustomerService,
  ManagementService,
  PaymentService,
  AttributeService,
  SystemService,
  ImpexService,
  ReportsService,
  } from './index';

/**
 * Do not specify providers for modules that might be imported by a lazy loaded module.
 */

@NgModule({
  imports: [CommonModule, RouterModule],
  declarations: [ ],
  exports: [CommonModule, FormsModule, RouterModule]
})
export class ServicesModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: ServicesModule,
      providers: [
        ErrorEventBus, ShopEventBus, I18nEventBus, WindowMessageEventBus, UserEventBus,
        ValidationService,
        ShopService,
        ShippingService, LocationService,
        OrganisationService,
        CatalogService,
        PIMService,
        ContentService,
        FulfilmentService,
        PricingService,
        CustomerOrderService,
        CustomerService,
        ManagementService,
        PaymentService,
        AttributeService,
        SystemService,
        ImpexService,
        ReportsService,
      ]
    };
  }
}
