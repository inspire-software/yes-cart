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

package org.yes.cart.payment.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaySeraRequest {
	private String projectId;
	private String orderId;
    private String acceptUrl;
    private String cancelUrl;
    private String callbackUrl;
    private String version;
    private String language;
    private Integer amount;
    private String currency;
    private String payment;
    private String country;
    private String payText;
    private String payerFirstName;
    private String payerLastName;
    private String email;
    private String payerStreet;
    private String payerCity;
    private String payerState;
    private String payerZip;
    private String payerCountryCode;
    private List<String> onlyPayment = new ArrayList<String>();
    private List<String> disallowPayments = new ArrayList<String>();
    private Boolean test;
    private String timeLimit;
    private String personCode;
    
    public PaySeraRequest() {}
    
    /**
     * https://developers.paysera.com/en/checkout/integrations/integration-specification
     * @return
     */
    public String toBase64String() {
    	Map<String, String> dataQueryParams = new HashMap<String, String>();
    	
    	dataQueryParams.put("projectid", getProjectId());
    	dataQueryParams.put("orderid", getOrderId());
    	dataQueryParams.put("accepturl", getAcceptUrl());
    	dataQueryParams.put("cancelurl", getCancelUrl());
    	dataQueryParams.put("callbackurl", getCallbackUrl());
    	dataQueryParams.put("version", getVersion());
    	dataQueryParams.put("lang", getLanguage());
    	dataQueryParams.put("amount", PaySeraUtils.toQueryParam(getAmount()));
    	dataQueryParams.put("currency", getCurrency());
    	dataQueryParams.put("payment", getPayment());
    	dataQueryParams.put("country", getCountry());
    	dataQueryParams.put("paytext", getPayText());
    	dataQueryParams.put("p_firstname", getPayerFirstName());
    	dataQueryParams.put("p_lastname", getPayerLastName());
    	dataQueryParams.put("p_email", getEmail());
    	dataQueryParams.put("p_street", getPayerStreet());
    	dataQueryParams.put("p_city", getPayerCity());
    	dataQueryParams.put("p_state", getPayerState());
    	dataQueryParams.put("p_zip", getPayerZip());
    	dataQueryParams.put("p_countrycode", getPayerCountryCode());
    	dataQueryParams.put("only_payments", PaySeraUtils.toQueryParam(getOnlyPayment()));
    	dataQueryParams.put("disallow_payments", PaySeraUtils.toQueryParam(getDisallowPayments()));
    	dataQueryParams.put("test", PaySeraUtils.toQueryParam(isTest()));
    	dataQueryParams.put("time_limit", getTimeLimit());
    	dataQueryParams.put("personcode", getPersonCode());
    	
    	String dataQuery = PaySeraUtils.buildQueryString(dataQueryParams);
    	String dataQueryAsBase64 = PaySeraUtils.encodeBase64UrlSafe(dataQuery);
    	
    	return dataQueryAsBase64;
    }

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAcceptUrl() {
		return acceptUrl;
	}

	public void setAcceptUrl(String acceptUrl) {
		this.acceptUrl = acceptUrl;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPayText() {
		return payText;
	}

	public void setPayText(String payText) {
		this.payText = payText;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getDisallowPayments() {
		return disallowPayments;
	}

	public void setDisallowPayments(List<String> disallowPayments) {
		this.disallowPayments = disallowPayments;
	}

	public Boolean isTest() {
		return test;
	}

	public void setTest(Boolean test) {
		this.test = test;
	}

	public String getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}

	public String getPersonCode() {
		return personCode;
	}

	public void setPersonCode(String personCode) {
		this.personCode = personCode;
	}

	public String getPayerFirstName() {
		return payerFirstName;
	}

	public void setPayerFirstName(String payerFirstName) {
		this.payerFirstName = payerFirstName;
	}

	public String getPayerLastName() {
		return payerLastName;
	}

	public void setPayerLastName(String payerLastName) {
		this.payerLastName = payerLastName;
	}

	public String getPayerStreet() {
		return payerStreet;
	}

	public void setPayerStreet(String payerStreet) {
		this.payerStreet = payerStreet;
	}

	public String getPayerCity() {
		return payerCity;
	}

	public void setPayerCity(String payerCity) {
		this.payerCity = payerCity;
	}

	public String getPayerState() {
		return payerState;
	}

	public void setPayerState(String payerState) {
		this.payerState = payerState;
	}

	public String getPayerZip() {
		return payerZip;
	}

	public void setPayerZip(String payerZip) {
		this.payerZip = payerZip;
	}

	public String getPayerCountryCode() {
		return payerCountryCode;
	}

	public void setPayerCountryCode(String payerCountryCode) {
		this.payerCountryCode = payerCountryCode;
	}

	public List<String> getOnlyPayment() {
		return onlyPayment;
	}

	public void setOnlyPayment(List<String> onlyPayment) {
		this.onlyPayment = onlyPayment;
	}
}
