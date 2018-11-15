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

import org.yes.cart.bulkimport.xml.internal.CollectionImportModeType;
import org.yes.cart.bulkimport.xml.internal.CustomAttributeType;
import org.yes.cart.bulkimport.xml.internal.CustomAttributesType;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.service.domain.AttributeService;

import java.util.Collection;
import java.util.Iterator;

/**
 * User: denispavlov
 * Date: 11/11/2018
 * Time: 14:43
 */
public abstract class AbstractAttributableXmlEntityHandler<T, E, M extends Attributable, C extends AttrValue> extends AbstractXmlEntityHandler<T, E> {

    private AttributeService attributeService;

    protected AbstractAttributableXmlEntityHandler(final String elementName) {
        super(elementName);
    }

    protected AbstractAttributableXmlEntityHandler(final String elementName, final String contextNamespace) {
        super(elementName, contextNamespace);
    }


    /**
     * Update SEO element.
     *
     * @param ext       SEO XML
     * @param existing  existing SEO
     */
    protected void updateExt(final CustomAttributesType ext, final M master, final Collection<C> existing) {
        if (ext != null) {
            final CollectionImportModeType collectionMode = ext.getImportMode() != null ? ext.getImportMode() : CollectionImportModeType.MERGE;
            if (collectionMode == CollectionImportModeType.REPLACE) {
                existing.clear();
            }

            for (final CustomAttributeType attr : ext.getCustomAttribute()) {
                final EntityImportModeType itemMode = attr.getImportMode() != null ? attr.getImportMode() : EntityImportModeType.MERGE;
                if (itemMode == EntityImportModeType.DELETE) {
                    if (attr.getAttribute() != null) {
                        updateExtRemove(existing, attr);
                    }
                } else {
                    updateExtSave(master, existing, attr);
                }
            }

        }

    }

    private void updateExtSave(final M master, final Collection<C> existing, final CustomAttributeType attr) {

        for (final C av : existing) {
            if (attr.getAttribute().equals(av.getAttributeCode())) {
                updateExtSaveBasic(attr, av);
                return;
            }
        }
        final C av = attributeService.getGenericDao().getEntityFactory().getByIface(getAvInterface());
        setMaster(master, av);
        av.setAttributeCode(attr.getAttribute());
        updateExtSaveBasic(attr, av);
        existing.add(av);

    }

    private void updateExtSaveBasic(final CustomAttributeType attr, final C av) {
        av.setVal(attr.getCustomValue());
        av.setDisplayVal(processI18n(attr.getCustomDisplayValue(), av.getDisplayVal()));
    }

    private void updateExtRemove(final Collection<C> existing, final CustomAttributeType attr) {
        final Iterator<C> it = existing.iterator();
        while (it.hasNext()) {
            final C next = it.next();
            if (attr.getAttribute().equals(next.getAttributeCode())) {
                it.remove();
                return;
            }
        }

    }

    /**
     * Extension hook.
     *
     * @param master master object
     * @param av     new AV
     */
    protected abstract void setMaster(M master, C av);

    /**
     * Extension hook.
     *
     * @return AV interface
     */
    protected abstract Class<C> getAvInterface();

    protected AttributeService getAttributeService() {
        return attributeService;
    }

    /**
     * Spring IoC.
     *
     * @param attributeService attribute service
     */
    public void setAttributeService(final AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
