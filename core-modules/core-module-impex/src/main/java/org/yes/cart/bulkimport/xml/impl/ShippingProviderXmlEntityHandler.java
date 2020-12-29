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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkimport.xml.XmlEntityImportHandler;
import org.yes.cart.bulkimport.xml.internal.EntityImportModeType;
import org.yes.cart.bulkimport.xml.internal.ShippingMethodType;
import org.yes.cart.bulkimport.xml.internal.ShippingProviderType;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.CarrierService;

/**
 * User: denispavlov
 * Date: 05/11/2018
 * Time: 22:23
 */
public class ShippingProviderXmlEntityHandler extends AbstractXmlEntityHandler<ShippingProviderType, Carrier> implements XmlEntityImportHandler<ShippingProviderType, Carrier> {

    private CarrierService carrierService;

    private XmlEntityImportHandler<ShippingMethodType, CarrierSla> shippingMethodXmlEntityImportHandler;

    public ShippingProviderXmlEntityHandler() {
        super("shipping-provider");
    }

    @Override
    protected void delete(final JobStatusListener statusListener, final Carrier carrier) {
        this.carrierService.delete(carrier);
        this.carrierService.getGenericDao().flush();
    }

    @Override
    protected void saveOrUpdate(final JobStatusListener statusListener, final Carrier domain, final ShippingProviderType xmlType, final EntityImportModeType mode) {

        if (xmlType.getConfiguration() != null) {
            domain.setWorldwide(xmlType.getConfiguration().isWorldwide());
            domain.setCountry(xmlType.getConfiguration().isCountry());
            domain.setState(xmlType.getConfiguration().isState());
            domain.setLocal(xmlType.getConfiguration().isLocal());
        }

        domain.setName(xmlType.getName());
        domain.setDisplayName(processI18n(xmlType.getDisplayName(), domain.getDisplayName()));
        domain.setDescription(xmlType.getDescription());
        domain.setDisplayDescription(processI18n(xmlType.getDisplayDescription(), domain.getDisplayDescription()));
        if (domain.getCarrierId() == 0L) {
            this.carrierService.create(domain);
        } else {
            this.carrierService.update(domain);
        }
        this.carrierService.getGenericDao().flush();
        this.carrierService.getGenericDao().evict(domain);

        if (xmlType.getShippingMethods() != null) {
            for (final ShippingMethodType xmlShippingMethodType : xmlType.getShippingMethods().getShippingMethod()) {

                xmlShippingMethodType.setProvider(domain.getGuid());
                shippingMethodXmlEntityImportHandler.handle(statusListener, null, (ImpExTuple) new XmlImportTupleImpl(xmlShippingMethodType.getGuid(), xmlShippingMethodType), null, null);

            }
        }

    }

    @Override
    protected Carrier getOrCreate(final JobStatusListener statusListener, final ShippingProviderType xmlType) {
        Carrier carrier = this.carrierService.findSingleByCriteria(" where e.guid = ?1", xmlType.getGuid());
        if (carrier != null) {
            return carrier;
        }
        carrier = this.carrierService.getGenericDao().getEntityFactory().getByIface(Carrier.class);
        carrier.setCreatedBy(xmlType.getCreatedBy());
        carrier.setCreatedTimestamp(processInstant(xmlType.getCreatedTimestamp()));
        carrier.setGuid(xmlType.getGuid());
        return carrier;
    }

    @Override
    protected EntityImportModeType determineImportMode(final ShippingProviderType xmlType) {
        return xmlType.getImportMode() != null ? xmlType.getImportMode() : EntityImportModeType.MERGE;
    }

    @Override
    protected boolean isNew(final Carrier domain) {
        return domain.getCarrierId() == 0L;
    }

    /**
     * Spring IoC.
     *
     * @param carrierService carrier service
     */
    public void setCarrierService(final CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    /**
     * Spring IoC.
     *
     * @param shippingMethodXmlEntityImportHandler shipping method handler
     */
    public void setShippingMethodXmlEntityImportHandler(final XmlEntityImportHandler<ShippingMethodType, CarrierSla> shippingMethodXmlEntityImportHandler) {
        this.shippingMethodXmlEntityImportHandler = shippingMethodXmlEntityImportHandler;
    }

}
