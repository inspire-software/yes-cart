import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { SystemPagesModule } from '../+system/system.pages.module';

import { ShopAttributesComponent, ShopCarrierComponent, ShopCatalogComponent, ShopCurrencyComponent,
  ShopFulfilmentCentreComponent, ShopLanguageComponent, ShopLocationComponent, ShopMainComponent,
  ShopPaymentGatewaysComponent, ShopSEOComponent, ShopUrlComponent, ShopSummaryComponent, ShopSubsComponent } from './components/index';
import { ShopComponent, SubShopComponent } from './index';

@NgModule({
    imports: [CommonModule, SharedModule, ServicesModule, SystemPagesModule],
    declarations: [
      ShopAttributesComponent, ShopCarrierComponent, ShopCatalogComponent, ShopCurrencyComponent,
      ShopFulfilmentCentreComponent, ShopLanguageComponent, ShopLocationComponent, ShopMainComponent,
      ShopPaymentGatewaysComponent, ShopSEOComponent, ShopUrlComponent, ShopSummaryComponent, ShopSubsComponent,
      ShopComponent, SubShopComponent
    ],
    exports: [
      ShopAttributesComponent, ShopCarrierComponent, ShopCatalogComponent, ShopCurrencyComponent,
      ShopFulfilmentCentreComponent, ShopLanguageComponent, ShopLocationComponent, ShopMainComponent,
      ShopPaymentGatewaysComponent, ShopSEOComponent, ShopUrlComponent, ShopSummaryComponent, ShopSubsComponent,
      ShopComponent, SubShopComponent
    ]
})

export class ShopPagesModule { }
