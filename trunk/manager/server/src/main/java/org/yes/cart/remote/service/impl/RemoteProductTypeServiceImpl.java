/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.remote.service.RemoteProductTypeService;
import org.yes.cart.service.dto.DtoProductTypeService;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductTypeServiceImpl
        extends AbstractRemoteService<ProductTypeDTO>
        implements RemoteProductTypeService {


    /**
     * Construct remote service.
     *
     * @param dtoProductTypeService dto service to use.
     */
    public RemoteProductTypeServiceImpl(final DtoProductTypeService dtoProductTypeService) {
        super(dtoProductTypeService);
    }


}
