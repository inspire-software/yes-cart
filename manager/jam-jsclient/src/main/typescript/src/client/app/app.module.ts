import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { APP_BASE_HREF } from '@angular/common';
import { HttpModule } from '@angular/http';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';

import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

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

import { SharedModule } from './shared/shared.module';

import { ServicesModule } from './shared/services/services.module';

export function createTranslateLoader(http: HttpClient) {
  return new TranslateHttpLoader(http, './i18n/', '.json');
}

@NgModule({
  imports: [
    BrowserModule, HttpModule, AppRoutingModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: createTranslateLoader,
        deps: [HttpClient]
      }
    }),
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
    HomePagesModule,
    SharedModule.forRoot(),
    ServicesModule.forRoot(),
  ],
  declarations: [AppComponent],
  providers: [{
    provide: APP_BASE_HREF,
    useValue: '<%= APP_BASE %>'
  }],
  bootstrap: [AppComponent]

})

export class AppModule { }
