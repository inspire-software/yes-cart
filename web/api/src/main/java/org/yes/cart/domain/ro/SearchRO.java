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

import org.yes.cart.domain.ro.xml.impl.RequestParameterMapAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 22/03/2015
 * Time: 15:26
 */
@XmlRootElement(name = "search")
public class SearchRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private String category;

    private Map<String, List> parameters;

    private int pageNumber;
    private int pageSize;

    private String sortField;
    private boolean sortDescending;

    private boolean includeNavigation;

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    @XmlJavaTypeAdapter(RequestParameterMapAdapter.class)
    public Map<String, List> getParameters() {
        return parameters;
    }

    public void setParameters(final Map<String, List> parameters) {
        this.parameters = parameters;
    }

    @XmlElement(name = "page-number")
    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @XmlElement(name = "page-size")
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    @XmlElement(name = "sort-field")
    public String getSortField() {
        return sortField;
    }

    public void setSortField(final String sortField) {
        this.sortField = sortField;
    }

    @XmlElement(name = "sort-descending")
    public boolean getSortDescending() {
        return sortDescending;
    }

    public void setSortDescending(final boolean sortDescending) {
        this.sortDescending = sortDescending;
    }

    @XmlElement(name = "include-navigation")
    public boolean getIncludeNavigation() {
        return includeNavigation;
    }

    public void setIncludeNavigation(final boolean includeNavigation) {
        this.includeNavigation = includeNavigation;
    }
}
