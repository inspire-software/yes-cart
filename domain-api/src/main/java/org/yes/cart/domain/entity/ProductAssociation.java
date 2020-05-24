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

package org.yes.cart.domain.entity;

/**
 * Product associations.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductAssociation extends Auditable, Rankable {

    /**
     * Pk Value.
     *
     * @return pk value.
     */
    long getProductassociationId();

    /**
     * Set pk value.
     *
     * @param productassociationId pk value.
     */
    void setProductassociationId(long productassociationId);

    /**
     * Rank.
     *
     * @return rank.
     */
    @Override
    int getRank();

    /**
     * Set rank of association.
     *
     * @param rank rank of association.
     */
    @Override
    void setRank(int rank);

    /**
     * Association type.
     *
     * @return association type.
     */
    Association getAssociation();

    /**
     * Set association type.
     *
     * @param association association type.
     */
    void setAssociation(Association association);

    /**
     * Get the main(source) product.
     *
     * @return main product.
     */
    Product getProduct();

    /**
     * Set main product.
     *
     * @param product main product.
     */
    void setProduct(Product product);

    /**
     * Get associated (destination) product.
     *
     * @return associated product.
     */
    String getAssociatedSku();

    /**
     * Set associated product SKU.
     *
     * @param associatedSku associated product.
     */
    void setAssociatedSku(String associatedSku);

}


