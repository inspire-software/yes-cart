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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.bulkexport.xml.XmlExportContext;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.bulkexport.xml.XmlExportFile;

import java.io.Serializable;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 13:25
 */
public class XmlExportDescriptorImpl implements XmlExportDescriptor, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(XmlExportDescriptorImpl.class);

    private XmlExportFile exportFileDescriptor;

    private XmlExportContext context;
    private String entityType;
    private Class entityTypeClass;
    private String xmlHandler;

    private String selectCmd;

    /**
     * Default constructor.
     */
    public XmlExportDescriptorImpl() {
        exportFileDescriptor = new XmlExportFileImpl();
        context = new XmlExportContextImpl();
    }

    /** {@inheritDoc} */
    @Override
    public XmlExportContext getContext() {
        if (context == null) {
            context = new XmlExportContextImpl();
        }
        return context;
    }

    /**
     * @param context import context
     */
    public void setContext(final XmlExportContext context) {
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

    /** {@inheritDoc} */
    @Override
    public String getSelectCmd() {
        return selectCmd;
    }

    /**
     * @param selectCmd select SQL to lookup existing records
     */
    public void setSelectCmd(final String selectCmd) {
        this.selectCmd = selectCmd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlExportFile getExportFileDescriptor() {
        return exportFileDescriptor;
    }

    /**
     * Set the {@link org.yes.cart.bulkexport.model.ExportFile}
     * for more details.
     *
     * @param exportFileDescriptor import file descriptor.
     */
    protected void setExportFileDescriptor(XmlExportFile exportFileDescriptor) {
        this.exportFileDescriptor = exportFileDescriptor;
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
        return "XmlExportDescriptorImpl{" +
                "exportFileDescriptor=" + exportFileDescriptor +
                ", entityType='" + entityType + '\'' +
                ", selectSql='" + selectCmd + '\'' +
                ", xmlHandler='" + xmlHandler + '\'' +
                '}';
    }
}
