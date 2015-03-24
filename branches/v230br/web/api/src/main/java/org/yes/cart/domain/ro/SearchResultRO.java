/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * User: denispavlov
 * Date: 22/03/2015
 * Time: 15:29
 */
@XmlRootElement(name = "search-result")
public class SearchResultRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private SearchRO search;

    private int totalResults;

    private String productImageWidth;
    private String productImageHeight;

    private List<String> pageAvailableSize;

    private List<ProductSearchResultRO> products;

    private FilteredNavigationRO filteredNavigation;

    public SearchRO getSearch() {
        return search;
    }

    public void setSearch(final SearchRO search) {
        this.search = search;
    }

    @XmlElement(name = "total-results")
    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(final int totalResults) {
        this.totalResults = totalResults;
    }

    @XmlElement(name = "product-image-width")
    public String getProductImageWidth() {
        return productImageWidth;
    }

    public void setProductImageWidth(final String productImageWidth) {
        this.productImageWidth = productImageWidth;
    }

    @XmlElement(name = "product-image-height")
    public String getProductImageHeight() {
        return productImageHeight;
    }

    public void setProductImageHeight(final String productImageHeight) {
        this.productImageHeight = productImageHeight;
    }

    @XmlElementWrapper(name = "page-available-sizes")
    @XmlElement(name = "page-available-size")
    public List<String> getPageAvailableSize() {
        return pageAvailableSize;
    }

    public void setPageAvailableSize(final List<String> pageAvailableSize) {
        this.pageAvailableSize = pageAvailableSize;
    }

    public List<ProductSearchResultRO> getProducts() {
        return products;
    }

    public void setProducts(final List<ProductSearchResultRO> products) {
        this.products = products;
    }

    @XmlElement(name = "filtered-navigation")
    public FilteredNavigationRO getFilteredNavigation() {
        return filteredNavigation;
    }

    public void setFilteredNavigation(final FilteredNavigationRO filteredNavigation) {
        this.filteredNavigation = filteredNavigation;
    }
}
