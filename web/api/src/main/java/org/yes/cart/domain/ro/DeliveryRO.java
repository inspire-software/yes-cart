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

package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 02/04/2015
 * Time: 22:40
 */
@Dto
@XmlRootElement(name = "delivery")
public class DeliveryRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(value = "customerOrderDeliveryId", readOnly = true)
    private long customerOrderDeliveryId;

    @DtoField(value = "deliveryNum", readOnly = true)
    private String deliveryNum;

    @DtoField(value = "refNo", readOnly = true)
    private String refNo;

    @DtoField(value = "carrierSla.carrierslaId", readOnly = true)
    private long carrierSlaId;
    @DtoField(value = "carrierSla.name", readOnly = true)
    private String carrierSlaName;
    @DtoField(value = "carrierSla.displayName", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> carrierSlaDisplayNames;
    @DtoField(value = "carrierSla.carrier.carrierId", readOnly = true)
    private long carrierId;
    @DtoField(value = "carrierSla.carrier.name", readOnly = true)
    private String carrierName;
    @DtoField(value = "carrierSla.carrier.displayName", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> carrierDisplayNames;

    @DtoField(value = "deliveryStatus", readOnly = true)
    private String deliveryStatus;
    @DtoField(value = "deliveryGroup", readOnly = true)
    private String deliveryGroup;

    @DtoField(value = "deliveryRemarks", readOnly = true)
    private String deliveryRemarks;
    @DtoField(value = "deliveryEstimatedMin", readOnly = true)
    private Date deliveryEstimatedMin;
    @DtoField(value = "deliveryEstimatedMax", readOnly = true)
    private Date deliveryEstimatedMax;
    @DtoField(value = "deliveryGuaranteed", readOnly = true)
    private Date deliveryGuaranteed;

    @DtoField(value = "requestedDeliveryDate", readOnly = true)
    private Date requestedDeliveryDate;

    private CartTotalRO total;

    @DtoCollection(
            value = "detail",
            dtoBeanKey = "org.yes.cart.domain.ro.DeliveryItemRO",
            entityGenericType = CustomerOrderDeliveryDet.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<DeliveryItemRO> deliveryItems = new ArrayList<DeliveryItemRO>();

    @XmlAttribute(name = "customer-order-delivery-id")
    public long getCustomerOrderDeliveryId() {
        return customerOrderDeliveryId;
    }

    public void setCustomerOrderDeliveryId(final long customerOrderDeliveryId) {
        this.customerOrderDeliveryId = customerOrderDeliveryId;
    }

    @XmlAttribute(name = "delivery-num")
    public String getDeliveryNum() {
        return deliveryNum;
    }

    public void setDeliveryNum(final String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    @XmlAttribute(name = "delivery-ref")
    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(final String refNo) {
        this.refNo = refNo;
    }

    @XmlAttribute(name = "carriersla-id")
    public long getCarrierSlaId() {
        return carrierSlaId;
    }

    public void setCarrierSlaId(final long carrierSlaId) {
        this.carrierSlaId = carrierSlaId;
    }

    @XmlElement(name = "carriersla-name")
    public String getCarrierSlaName() {
        return carrierSlaName;
    }

    public void setCarrierSlaName(final String carrierSlaName) {
        this.carrierSlaName = carrierSlaName;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "carriersla-display-names")
    public Map<String, String> getCarrierSlaDisplayNames() {
        return carrierSlaDisplayNames;
    }

    public void setCarrierSlaDisplayNames(final Map<String, String> carrierSlaDisplayNames) {
        this.carrierSlaDisplayNames = carrierSlaDisplayNames;
    }

    @XmlAttribute(name = "carrier-id")
    public long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(final long carrierId) {
        this.carrierId = carrierId;
    }

    @XmlElement(name = "carrier-name")
    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(final String carrierName) {
        this.carrierName = carrierName;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "carrier-display-names")
    public Map<String, String> getCarrierDisplayNames() {
        return carrierDisplayNames;
    }

    public void setCarrierDisplayNames(final Map<String, String> carrierDisplayNames) {
        this.carrierDisplayNames = carrierDisplayNames;
    }

    @XmlAttribute(name = "delivery-status")
    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(final String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    @XmlAttribute(name = "delivery-group")
    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }

    @XmlElement(name = "delivery-remarks")
    public String getDeliveryRemarks() {
        return deliveryRemarks;
    }

    public void setDeliveryRemarks(final String deliveryRemarks) {
        this.deliveryRemarks = deliveryRemarks;
    }

    @XmlAttribute(name = "delivery-estimated-min")
    public Date getDeliveryEstimatedMin() {
        return deliveryEstimatedMin;
    }

    public void setDeliveryEstimatedMin(final Date deliveryEstimatedMin) {
        this.deliveryEstimatedMin = deliveryEstimatedMin;
    }

    @XmlAttribute(name = "delivery-estimated-max")
    public Date getDeliveryEstimatedMax() {
        return deliveryEstimatedMax;
    }

    public void setDeliveryEstimatedMax(final Date deliveryEstimatedMax) {
        this.deliveryEstimatedMax = deliveryEstimatedMax;
    }

    @XmlAttribute(name = "delivery-guaranteed")
    public Date getDeliveryGuaranteed() {
        return deliveryGuaranteed;
    }

    public void setDeliveryGuaranteed(final Date deliveryGuaranteed) {
        this.deliveryGuaranteed = deliveryGuaranteed;
    }

    @XmlAttribute(name = "requested-delivery-date")
    public Date getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(final Date requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    public CartTotalRO getTotal() {
        return total;
    }

    public void setTotal(final CartTotalRO total) {
        this.total = total;
    }

    @XmlElementWrapper(name = "delivery-items")
    @XmlElement(name = "delivery-item")
    public List<DeliveryItemRO> getDeliveryItems() {
        return deliveryItems;
    }

    public void setDeliveryItems(final List<DeliveryItemRO> deliveryItems) {
        this.deliveryItems = deliveryItems;
    }
}
