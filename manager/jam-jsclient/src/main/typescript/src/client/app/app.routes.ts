import { provideRouter, RouterConfig } from '@angular/router';

import { HomeRoutes } from './+home/home.routes';
import { ShopRoutes } from './+shop/shop.routes';
import { LocationRoutes } from './+locations/locations.routes';
import { ShippingRoutes } from './+shipping/shipping.routes';
import { FulfilmentRoutes } from './+fulfilment/fulfilment.routes';
import { PriceListRoutes } from './+pricelist/pricelist.routes';
import { CustomerOrdersRoutes } from './+customerorder/customerorders.routes';
import { CustomerRoutes } from './+customer/customer.routes';
import { CatalogRoutes } from './+catalog/catalog.routes';
import { AttributeDefinitionRoutes } from './+system/attributes/attribute-definitions.routes';
import { SystemPreferencesRoutes } from './+system/preferences/system-preferences.routes';
import { CacheMonitoringRoutes } from './+system/cache/cache-monitoring.routes';
import { ClusterRoutes } from './+system/cluster/cluster.routes';
import { ReindexRoutes } from './+system/reindex/reindex.routes';
import { QueryRoutes } from './+system/query/query.routes';
import { PaymentGatewaysRoutes } from './+system/payment/payment-gateways.routes';
import { LicenseRoutes } from './shared/license/license.routes';
import { OrganisationRoutes } from './+organisation/organisation.routes';

const routes: RouterConfig = [
  ...HomeRoutes,
  ...ShopRoutes,
  ...LocationRoutes,
  ...ShippingRoutes,
  ...FulfilmentRoutes,
  ...PriceListRoutes,
  ...CustomerOrdersRoutes,
  ...CustomerRoutes,
  ...LicenseRoutes,
  ...CatalogRoutes,
  ...AttributeDefinitionRoutes,
  ...SystemPreferencesRoutes,
  ...CacheMonitoringRoutes,
  ...ClusterRoutes,
  ...ReindexRoutes,
  ...QueryRoutes,
  ...PaymentGatewaysRoutes,
  ...OrganisationRoutes,
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes),
];
