/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.vo.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.vo.VoCategory;
import org.yes.cart.service.vo.VoCategoryService;
import org.yes.cart.service.vo.VoShopService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Igor_Azarny on 4/13/2016.
 */
public class VoCategoryServiceImplTest  extends BaseCoreDBTestCase {

    private VoCategoryService voCategoryService;

    @Before
    public void setUp() {
        voCategoryService = (VoCategoryService) ctx().getBean("voCategoryService");
        super.setUp();
    }

    @Test
    public void testGetAll() throws Exception {

        List<VoCategory> categoryList = voCategoryService.getAll();
        assertNotNull(categoryList);

    }
}