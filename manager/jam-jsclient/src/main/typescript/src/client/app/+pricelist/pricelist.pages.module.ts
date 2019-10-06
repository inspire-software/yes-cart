import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';
import { ServicesModule } from '../shared/services/services.module';

import { PriceListRoutingModule } from './pricelist-routing.module';
import {
  PriceListComponent, PriceComponent,
  PromotionComponent, PromotionCouponsComponent, PromotionsComponent, PromotionTestConfigComponent, PromotionTestResultComponent,
  TaxConfigsComponent, TaxesComponent
} from './components/index';
import { ShopPriceListComponent, ShopPromotionsComponent, ShopTaxesComponent } from './index';

@NgModule({
    imports: [PriceListRoutingModule, CommonModule, SharedModule, ServicesModule],
    declarations: [
      PriceListComponent, PriceComponent,
      PromotionComponent, PromotionCouponsComponent, PromotionsComponent,
      TaxConfigsComponent, TaxesComponent,
      ShopPriceListComponent, ShopPromotionsComponent, ShopTaxesComponent, PromotionTestConfigComponent, PromotionTestResultComponent
    ],
    exports: [
      PriceListComponent, PriceComponent,
      PromotionComponent, PromotionCouponsComponent, PromotionsComponent,
      TaxConfigsComponent, TaxesComponent,
      ShopPriceListComponent, ShopPromotionsComponent, ShopTaxesComponent, PromotionTestConfigComponent, PromotionTestResultComponent
    ]
})

export class PriceListPagesModule { }
