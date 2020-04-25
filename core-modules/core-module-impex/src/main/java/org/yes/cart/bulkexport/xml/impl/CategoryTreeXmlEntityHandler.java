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

/**
 * User: denispavlov
 * Date: 16/05/2019
 * Time: 18:04
 */
public class CategoryTreeXmlEntityHandler extends AbstractXmlEntityHandler<Category> {

    private CategoryService categoryService;

    private final CategoryXmlEntityHandler entityHandler = new CategoryXmlEntityHandler();

    private boolean lookUpRoot = false;

    public CategoryTreeXmlEntityHandler() {
        super("categories");
    }

    public CategoryTreeXmlEntityHandler(final boolean lookUpRoot) {
        this();
        this.lookUpRoot = lookUpRoot;
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Category> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        handleCategory(statusListener, xmlExportDescriptor, tuple, xmlValueAdapter, fileToExport, writer);

    }

    private void handleCategory(final JobStatusListener statusListener,
                                final XmlExportDescriptor xmlExportDescriptor,
                                final ImpExTuple<String, Category> tuple,
                                final XmlValueAdapter xmlValueAdapter,
                                final String fileToExport,
                                final OutputStreamWriter writer) throws Exception {
        if (lookUpRoot) {
            this.handleUp(statusListener, xmlExportDescriptor, tuple.getData(), xmlValueAdapter, fileToExport, writer);
        }

        this.handleDown(statusListener, xmlExportDescriptor, tuple.getData(), xmlValueAdapter, fileToExport, writer);
    }

    private void handleDown(final JobStatusListener statusListener,
                            final XmlExportDescriptor xmlExportDescriptor,
                            final Category category,
                            final XmlValueAdapter xmlValueAdapter,
                            final String fileToExport,
                            final OutputStreamWriter writer) throws Exception {

        final List<Category> children =
                this.categoryService.findChildCategoriesWithAvailability(category.getCategoryId(), false);

        final XmlExportTuple tupleCategory = new XmlExportTupleImpl(category);
        this.entityHandler.handle(statusListener, xmlExportDescriptor, (ImpExTuple) tupleCategory, xmlValueAdapter, fileToExport, writer);

        for (final Category child : children) {
            handleDown(statusListener, xmlExportDescriptor, child, xmlValueAdapter, fileToExport, writer);
        }

        if (category.getLinkToId() != null) {

            final Category link =
                    this.categoryService.findById(category.getLinkToId());

            if (link != null) {

                final XmlExportTuple tupleLink = new XmlExportTupleImpl(link);

                handleCategory(statusListener, xmlExportDescriptor, (ImpExTuple) tupleLink, xmlValueAdapter, fileToExport, writer);

            }
        }

    }


    private void handleUp(final JobStatusListener statusListener,
                          final XmlExportDescriptor xmlExportDescriptor,
                          final Category category,
                          final XmlValueAdapter xmlValueAdapter,
                          final String fileToExport,
                          final OutputStreamWriter writer) throws Exception {

        final Category parent =
                this.categoryService.findById(category.getParentId());

        final XmlExportTuple tuple = new XmlExportTupleImpl(parent);
        this.entityHandler.handle(statusListener, xmlExportDescriptor, (ImpExTuple) tuple, xmlValueAdapter, fileToExport, writer);

        if (!parent.isRoot()) {
            handleUp(statusListener, xmlExportDescriptor, parent, xmlValueAdapter, fileToExport, writer);
        }

    }


    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        super.setPrettyPrint(prettyPrint);
        this.entityHandler.setPrettyPrint(prettyPrint);
    }

    /**
     * Trverse to root and include it in export.
     *
     * @param lookUpRoot look up root
     */
    public void setLookUpRoot(final boolean lookUpRoot) {
        this.lookUpRoot = lookUpRoot;
    }

    /**
     * Spring IoC.
     *
     * @param categoryService category service
     */
    public void setCategoryService(final CategoryService categoryService) {
        this.categoryService = categoryService;
        this.entityHandler.setCategoryService(categoryService);
    }

}
