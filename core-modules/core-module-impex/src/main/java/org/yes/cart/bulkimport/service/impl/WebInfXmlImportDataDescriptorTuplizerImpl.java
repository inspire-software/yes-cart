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

package org.yes.cart.bulkimport.service.impl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.bulkcommon.service.DataDescriptorResolver;
import org.yes.cart.bulkcommon.service.DataDescriptorTuplizer;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: denispavlov
 * Date: 02/06/2015
 * Time: 14:04
 */
public class WebInfXmlImportDataDescriptorTuplizerImpl implements DataDescriptorTuplizer<ImportDescriptor>, ApplicationContextAware {


    private final String pathToImportDescriptors;
    private XStreamProvider<ImportDescriptor> importDescriptorXStreamProvider;

    private ApplicationContext applicationContext;

    public WebInfXmlImportDataDescriptorTuplizerImpl(final String pathToImportDescriptors) {
        this.pathToImportDescriptors = pathToImportDescriptors;
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
    public ImportDescriptor toDescriptorObject(final DataDescriptor dataDescriptor) {
        if (supports(dataDescriptor)) {

            final String path = "WEB-INF/" + pathToImportDescriptors + "/" + dataDescriptor.getValue();
            final Resource res = applicationContext.getResource(path);

            try {
                return getImportDescriptorFromXML(res.getInputStream());
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
    public void setImportDescriptorXStreamProvider(final XStreamProvider importDescriptorXStreamProvider) {
        this.importDescriptorXStreamProvider = importDescriptorXStreamProvider;
    }

    protected ImportDescriptor getImportDescriptorFromXML(InputStream is) {
        return importDescriptorXStreamProvider.fromXML(is);
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    public void setDataDescriptorResolver(final DataDescriptorResolver<ImportDescriptor> resolver) {
        resolver.register(this);
    }

}
