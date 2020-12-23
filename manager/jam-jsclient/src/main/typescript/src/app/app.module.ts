import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// NGX Translate
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

// App specific modules
import { ServicesModule } from './shared/services/services.module';

import { SharedModule } from './shared/shared.module';

import { CatalogPagesModule } from './+catalog/catalog.pages.module';
import { ContentPagesModule } from './+content/content.pages.module';
import { AddressBookPagesModule } from './+address/address.pages.module';
import { CustomerPagesModule } from './+customer/customer.pages.module';
import { CustomerOrderPagesModule } from './+customerorder/customerorder.pages.module';
import { FulfilmentPagesModule } from './+fulfilment/fulfilment.pages.module';
import { HomePagesModule } from './+home/home.pages.module';
import { ImpexPagesModule } from './+impex/impex.pages.module';
import { LocationsPagesModule } from './+locations/locations.pages.module';
import { OrganisationPagesModule } from './+organisation/organisation.pages.module';
import { PriceListPagesModule } from './+pricelist/pricelist.pages.module';
import { ShippingPagesModule } from './+shipping/shipping.pages.module';
import { ShopPagesModule } from './+shop/shop.pages.module';
import { SystemPagesModule } from './+system/system.pages.module';

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    TranslateModule.forRoot({
      defaultLanguage: 'en',
      loader: {
          provide: TranslateLoader,
          useFactory: HttpLoaderFactory,
          deps: [HttpClient]
      }
    }),
    ServicesModule.forRoot(),
    SharedModule.forRoot(),
    CatalogPagesModule,
    ContentPagesModule,
    AddressBookPagesModule,
    CustomerPagesModule,
    CustomerOrderPagesModule,
    FulfilmentPagesModule,
    OrganisationPagesModule,
    PriceListPagesModule,
    LocationsPagesModule,
    ShippingPagesModule,
    ShopPagesModule,
    ImpexPagesModule,
    SystemPagesModule,
    HomePagesModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
