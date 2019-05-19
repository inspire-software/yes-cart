/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.bulkexport.xml.impl;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.bulkexport.xml.XmlExportTuple;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CategoryService;

import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 16/05/2019
 * Time: 18:04
 */
public class CategoryTreeXmlEntityHandler extends AbstractXmlEntityHandler<Category> {

    private final CategoryService categoryService;
    private final CategoryXmlEntityHandler entityHandler;


    public CategoryTreeXmlEntityHandler(final CategoryService categoryService) {
        super("categories");
        this.categoryService = categoryService;
        this.entityHandler = new CategoryXmlEntityHandler(categoryService);
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Category> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        this.handle(statusListener, xmlExportDescriptor, tuple.getData(), xmlValueAdapter, fileToExport, writer, entityCount);

    }

    private void handle(final JobStatusListener statusListener,
                        final XmlExportDescriptor xmlExportDescriptor,
                        final Category category,
                        final XmlValueAdapter xmlValueAdapter,
                        final String fileToExport,
                        final OutputStreamWriter writer,
                        final Map<String, Integer> entityCount) throws Exception {

        final List<Category> children =
                this.categoryService.findChildCategoriesWithAvailability(category.getCategoryId(), false);

        final XmlExportTuple tuple = new XmlExportTupleImpl(category);
        this.entityHandler.handle(statusListener, xmlExportDescriptor, (ImpExTuple) tuple, xmlValueAdapter, fileToExport, writer, entityCount);

        for (final Category child : children) {
            handle(statusListener, xmlExportDescriptor, child, xmlValueAdapter, fileToExport, writer, entityCount);
        }

    }

    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        super.setPrettyPrint(prettyPrint);
        this.entityHandler.setPrettyPrint(prettyPrint);
    }
}
