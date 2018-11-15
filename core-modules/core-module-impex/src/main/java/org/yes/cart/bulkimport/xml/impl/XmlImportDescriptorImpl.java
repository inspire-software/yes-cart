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

package org.yes.cart.bulkimport.xml.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkimport.xml.XmlImportContext;
import org.yes.cart.bulkimport.xml.XmlImportDescriptor;
import org.yes.cart.bulkimport.xml.XmlImportFile;

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 13:25
 */
public class XmlImportDescriptorImpl implements XmlImportDescriptor, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(XmlImportDescriptorImpl.class);

    private XmlImportFile importFileDescriptor;

    private String importDirectory;

    private XmlImportContext context;
    private String entityType;
    private Class entityTypeClass;
    private String xmlHandler;

    /**
     * Default constructor.
     */
    public XmlImportDescriptorImpl() {
        importFileDescriptor = new XmlImportFileImpl();
        context = new XmlImportContextImpl();
    }

    /** {@inheritDoc} */
    @Override
    public XmlImportContext getContext() {
        if (context == null) {
            context = new XmlImportContextImpl();
        }
        return context;
    }

    /**
     * @param context import context
     */
    public void setContext(final XmlImportContext context) {
        this.context = context;
    }

    /** {@inheritDoc} */
    @Override
    public String getEntityType() {
        return entityType;
    }

    /** {@inheritDoc} */
    @Override
    public Class getEntityTypeClass() {
        if (entityTypeClass == null) {
            if (StringUtils.isNotBlank(entityType)) {
                try {
                    entityTypeClass = Class.forName(entityType);
                } catch (ClassNotFoundException e) {
                    LOG.error("Unable to work out entity type for descriptor {}", this);
                    entityTypeClass = Object.class;
                }
            } else {
                LOG.error("Entity type is not specified for descriptor {}", this);
                entityTypeClass = Object.class;
            }
        }
        return entityTypeClass;
    }

    /**
     * @param entityInterface entity interface for factory
     */
    public void setEntityType(final String entityInterface) {
        this.entityType = entityInterface;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlImportFile getImportFileDescriptor() {
        return importFileDescriptor;
    }

    /**
     * Set the {@link org.yes.cart.bulkexport.model.ExportFile}
     * for more details.
     *
     * @param importFileDescriptor import file descriptor.
     */
    protected void setImportFileDescriptor(XmlImportFile importFileDescriptor) {
        this.importFileDescriptor = importFileDescriptor;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getImportDirectory() {
        return importDirectory;
    }

    /**
     * Set the import folder.
     *
     * @param importDirectory import folder to use.
     */
    @Override
    public void setImportDirectory(final String importDirectory) {
        this.importDirectory = importDirectory;
    }

    /** {@inheritDoc} */
    @Override
    public String getXmlHandler() {
        return xmlHandler;
    }

    /**
     * Set bean name.
     *
     * @param xmlHandler handler
     */
    public void setXmlHandler(final String xmlHandler) {
        this.xmlHandler = xmlHandler;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "XmlImportDescriptorImpl{" +
                "exportFileDescriptor=" + importFileDescriptor +
                ", entityType='" + entityType + '\'' +
                ", xmlHandler='" + xmlHandler + '\'' +
                '}';
    }
}
