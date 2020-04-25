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
import org.yes.cart.domain.entity.Content;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.ContentService;

import java.io.OutputStreamWriter;
import java.util.List;

/**
 * User: denispavlov
 * Date: 16/05/2019
 * Time: 18:04
 */
public class ShopContentCms3XmlEntityHandler extends AbstractXmlEntityHandler<Shop> {

    private ContentService contentService;

    private final ContentCms3XmlEntityHandler cms3XmlEntityHandler = new ContentCms3XmlEntityHandler();


    public ShopContentCms3XmlEntityHandler() {
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

            this.handle(statusListener, xmlExportDescriptor, root, xmlValueAdapter, fileToExport, writer);

        }

    }

    private void handle(final JobStatusListener statusListener,
                        final XmlExportDescriptor xmlExportDescriptor,
                        final Content content,
                        final XmlValueAdapter xmlValueAdapter,
                        final String fileToExport,
                        final OutputStreamWriter writer) throws Exception {

        final List<Content> children =
                this.contentService.findChildContentWithAvailability(content.getContentId(), false);

        final XmlExportTuple tuple = new XmlExportTupleImpl(content);
        this.cms3XmlEntityHandler.handle(statusListener, xmlExportDescriptor, (ImpExTuple) tuple, xmlValueAdapter, fileToExport, writer);

        for (final Content child : children) {
            handle(statusListener, xmlExportDescriptor, child, xmlValueAdapter, fileToExport, writer);
        }

    }

    @Override
    public void setPrettyPrint(final boolean prettyPrint) {
        super.setPrettyPrint(prettyPrint);
        this.cms3XmlEntityHandler.setPrettyPrint(prettyPrint);
    }

    /**
     * Spring IoC.
     *
     * @param contentService content service
     */
    public void setContentService(final ContentService contentService) {
        this.contentService = contentService;
        this.cms3XmlEntityHandler.setContentService(contentService);
    }

}
