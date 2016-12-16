import { Route } from '@angular/router';

import { ShopContentComponent } from './index';

export const ContentRoutes: Route[] = [
  {
    path: 'content/:shopId',
    component: ShopContentComponent
  }
];
