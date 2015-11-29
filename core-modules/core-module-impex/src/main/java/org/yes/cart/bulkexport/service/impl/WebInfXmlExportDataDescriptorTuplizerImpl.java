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

package org.yes.cart.bulkexport.service.impl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.yes.cart.bulkcommon.service.DataDescriptorResolver;
import org.yes.cart.bulkcommon.service.DataDescriptorTuplizer;
import org.yes.cart.bulkexport.model.ExportDescriptor;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: denispavlov
 * Date: 02/06/2015
 * Time: 14:04
 */
public class WebInfXmlExportDataDescriptorTuplizerImpl implements DataDescriptorTuplizer<ExportDescriptor>, ApplicationContextAware {


    private final String pathToExportDescriptors;
    private XStreamProvider<ExportDescriptor> importDescriptorXStreamProvider;

    private ApplicationContext applicationContext;

    public WebInfXmlExportDataDescriptorTuplizerImpl(final String pathToExportDescriptors) {
        this.pathToExportDescriptors = pathToExportDescriptors;
    }


    /**
     * {@inheritDoc}
     */
    public boolean supports(final DataDescriptor dataDescriptor) {
        return dataDescriptor != null && DataDescriptor.TYPE_WEBINF_XML.equals(dataDescriptor.getType());
    }

    /**
     * {@inheritDoc}
     */
    public ExportDescriptor toDescriptorObject(final DataDescriptor dataDescriptor) {
        if (supports(dataDescriptor)) {

            final String path = "WEB-INF/" + pathToExportDescriptors + "/" + dataDescriptor.getValue();
            final Resource res = applicationContext.getResource(path);

            try {
                return getExportDescriptorFromXML(res.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Unable to load import descriptor configuration from WEB-INF: " + path + " for " + dataDescriptor.getName());
            }
        }
        return null;
    }



    /**
     * IoC. XStream provider for import descriptor files.
     *
     * @param importDescriptorXStreamProvider xStream provider
     */
    public void setExportDescriptorXStreamProvider(final XStreamProvider importDescriptorXStreamProvider) {
        this.importDescriptorXStreamProvider = importDescriptorXStreamProvider;
    }

    protected ExportDescriptor getExportDescriptorFromXML(InputStream is) {
        return importDescriptorXStreamProvider.fromXML(is);
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public void setDataDescriptorResolver(final DataDescriptorResolver<ExportDescriptor> resolver) {
        resolver.register(this);
    }

}
