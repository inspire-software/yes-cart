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

package org.yes.cart.bulkexport.image.impl;

import org.yes.cart.bulkcommon.service.ExportService;
import org.yes.cart.bulkexport.image.ImageExportDomainObjectStrategy;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.bulkexport.service.impl.AbstractExportService;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.federation.FederationFacade;

/**
 * User: denispavlov
 * Date: 27/11/2015
 * Time: 12:52
 */
public class ImagesBulkExportServiceImpl extends AbstractExportService<ExportDescriptor> implements ExportService {

    private final ImageExportDomainObjectStrategy[] strategies;

    /**
     * Construct bulk import service.
     *
     * @param federationFacade federation facade
     * @param strategies   domain strategies to associate image with domain model
     */
    public ImagesBulkExportServiceImpl(final FederationFacade federationFacade,
                                       final ImageExportDomainObjectStrategy[] strategies) {
        super(federationFacade);
        this.strategies = strategies;
    }


    /**
     * Perform export for single file.
     * @param statusListener            error report
     * @param imageExportDescriptorName file name of the descriptor
     * @param imageExportDescriptor     export descriptor.
     * @param fileToExport              file to export
     */
    protected void doExport(final JobStatusListener statusListener,
                            final String imageExportDescriptorName,
                            final ExportDescriptor imageExportDescriptor,
                            final String fileToExport) throws Exception {

        statusListener.notifyMessage("export file : {}", fileToExport);

        final String select = imageExportDescriptor.getSelectCmd();

        statusListener.notifyMessage("start images export with {} path using {}",
                imageExportDescriptor.getSelectCmd(),
                imageExportDescriptorName);

        for (final ImageExportDomainObjectStrategy domainStrategy : strategies) {
            if (domainStrategy.supports(select)) {
                domainStrategy.doImageExport(
                        statusListener,
                        fileToExport
                );
            }
        }

    }
}
