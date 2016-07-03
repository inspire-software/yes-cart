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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.shoppingcart.Total;

import java.io.Serializable;
import java.math.BigDecimal;

@Dto
public class CartTotalDTOImpl implements Total, Serializable {

    @DtoField(readOnly = true)
    private BigDecimal listSubTotal;
    @DtoField(readOnly = true)
    private BigDecimal saleSubTotal;
    @DtoField(readOnly = true)
    private BigDecimal nonSaleSubTotal;
    @DtoField(readOnly = true)
    private BigDecimal priceSubTotal;
    @DtoField(readOnly = true)
    private boolean orderPromoApplied;
    @DtoField(readOnly = true)
    private String appliedOrderPromo;
    @DtoField(readOnly = true)
    private BigDecimal subTotal;
    @DtoField(readOnly = true)
    private BigDecimal subTotalTax;
    @DtoField(readOnly = true)
    private BigDecimal subTotalAmount;

    @DtoField(readOnly = true)
    private BigDecimal deliveryListCost;
    @DtoField(readOnly = true)
    private BigDecimal deliveryCost;
    @DtoField(readOnly = true)
    private boolean deliveryPromoApplied;
    @DtoField(readOnly = true)
    private String appliedDeliveryPromo;
    @DtoField(readOnly = true)
    private BigDecimal deliveryTax;
    @DtoField(readOnly = true)
    private BigDecimal deliveryCostAmount;

    @DtoField(readOnly = true)
    private BigDecimal total;
    @DtoField(readOnly = true)
    private BigDecimal totalTax;
    @DtoField(readOnly = true)
    private BigDecimal listTotalAmount;
    @DtoField(readOnly = true)
    private BigDecimal totalAmount;


    public CartTotalDTOImpl() {
    }

    public BigDecimal getListSubTotal() {
        return listSubTotal;
    }

    public BigDecimal getSaleSubTotal() {
        return saleSubTotal;
    }

    public BigDecimal getNonSaleSubTotal() {
        return nonSaleSubTotal;
    }

    public BigDecimal getPriceSubTotal() {
        return priceSubTotal;
    }

    public boolean isOrderPromoApplied() {
        return orderPromoApplied;
    }

    public String getAppliedOrderPromo() {
        return appliedOrderPromo;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public BigDecimal getSubTotalTax() {
        return subTotalTax;
    }

    public BigDecimal getSubTotalAmount() {
        return subTotalAmount;
    }

    public BigDecimal getDeliveryListCost() {
        return deliveryListCost;
    }

    public BigDecimal getDeliveryCost() {
        return deliveryCost;
    }

    public boolean isDeliveryPromoApplied() {
        return deliveryPromoApplied;
    }

    public String getAppliedDeliveryPromo() {
        return appliedDeliveryPromo;
    }

    public BigDecimal getDeliveryTax() {
        return deliveryTax;
    }

    public BigDecimal getDeliveryCostAmount() {
        return deliveryCostAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public BigDecimal getListTotalAmount() {
        return listTotalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setListSubTotal(final BigDecimal listSubTotal) {
        this.listSubTotal = listSubTotal;
    }

    public void setSaleSubTotal(final BigDecimal saleSubTotal) {
        this.saleSubTotal = saleSubTotal;
    }

    public void setNonSaleSubTotal(final BigDecimal nonSaleSubTotal) {
        this.nonSaleSubTotal = nonSaleSubTotal;
    }

    public void setPriceSubTotal(final BigDecimal priceSubTotal) {
        this.priceSubTotal = priceSubTotal;
    }

    public void setOrderPromoApplied(final boolean orderPromoApplied) {
        this.orderPromoApplied = orderPromoApplied;
    }

    public void setAppliedOrderPromo(final String appliedOrderPromo) {
        this.appliedOrderPromo = appliedOrderPromo;
    }

    public void setSubTotal(final BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public void setSubTotalTax(final BigDecimal subTotalTax) {
        this.subTotalTax = subTotalTax;
    }

    public void setSubTotalAmount(final BigDecimal subTotalAmount) {
        this.subTotalAmount = subTotalAmount;
    }

    public void setDeliveryListCost(final BigDecimal deliveryListCost) {
        this.deliveryListCost = deliveryListCost;
    }

    public void setDeliveryCost(final BigDecimal deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public void setDeliveryPromoApplied(final boolean deliveryPromoApplied) {
        this.deliveryPromoApplied = deliveryPromoApplied;
    }

    public void setAppliedDeliveryPromo(final String appliedDeliveryPromo) {
        this.appliedDeliveryPromo = appliedDeliveryPromo;
    }

    public void setDeliveryTax(final BigDecimal deliveryTax) {
        this.deliveryTax = deliveryTax;
    }

    public void setDeliveryCostAmount(final BigDecimal deliveryCostAmount) {
        this.deliveryCostAmount = deliveryCostAmount;
    }

    public void setTotal(final BigDecimal total) {
        this.total = total;
    }

    public void setTotalTax(final BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public void setListTotalAmount(final BigDecimal listTotalAmount) {
        this.listTotalAmount = listTotalAmount;
    }

    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Total add(final Total summand) {
        throw new UnsupportedOperationException("DTO objects must not be manipulated");
    }
}
