/*
 * Copyright 2009 Inspire-Software.com
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
import org.yes.cart.domain.entity.Content;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ContentService;

import java.io.OutputStreamWriter;
import java.util.List;

/**
 * User: denispavlov
 * Date: 16/05/2019
 * Time: 18:04
 */
public class ShopContentCms1XmlEntityHandler extends AbstractXmlEntityHandler<Shop> {

    private ContentService contentService;
    private CategoryService categoryService;

    private final ContentCms1XmlEntityHandler cms1XmlEntityHandler = new ContentCms1XmlEntityHandler();


    public ShopContentCms1XmlEntityHandler() {
        super("cms");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, Shop> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer) throws Exception {

        final Content root = this.contentService.getRootContent(tuple.getData().getShopId());
        if (root != null) {

            final Category cms1root = this.categoryService.getById(root.getContentId());
            this.handle(statusListener, xmlExportDescriptor, cms1root, xmlValueAdapter, fileToExport, writer);

        }

    }

    private void handle(final JobStatusListener statusListener,
                        final XmlExportDescriptor xmlExportDescriptor,
                        final Category category,
                        final XmlValueAdapter xmlValueAdapter,
                        final String fileToExport,
                        final OutputStreamWriter writer) throws Exception {

        final List<Category> children =
                this.categoryService.findChildCategoriesWithAvailability(category.getCategoryId(), false);

        final XmlExportTuple tuple = new XmlExportTupleImpl(category);
        this.cms1XmlEntityHandler.handle(statusListener, xmlExportDescriptor, (ImpExTuple) tuple, xmlValueAdapter, fileToExport, writer);

        for (final Category child : children) {
            handle(statusListener, xmlExportDescriptor, child, xmlValueAdapter, fileToExport, writer);
        }

    }

    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        super.setPrettyPrint(prettyPrint);
        this.cms1XmlEntityHandler.setPrettyPrint(prettyPrint);
    }


    /**
     * Spring IoC.
     *
     * @param categoryService category service
     */
    public void setCategoryService(final CategoryService categoryService) {
        this.categoryService = categoryService;
        this.cms1XmlEntityHandler.setCategoryService(categoryService);
    }


    /**
     * Spring IoC.
     *
     * @param contentService content service
     */
    public void setContentService(final ContentService contentService) {
        this.contentService = contentService;
    }

}
