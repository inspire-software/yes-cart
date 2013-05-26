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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * Associated products.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductAssociationDTO extends Identifiable {

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
    int getRank();

    /**
     * Set rank of asscosiation.
     *
     * @param rank rank of asscosiation.
     */
    void setRank(int rank);

    /**
     * Association type.
     *
     * @return association type.
     */
    long getAssociationId();

    /**
     * Set association type.
     *
     * @param associationId association type.
     */
    void setAssociationId(long associationId);


    /**
     * Get the main(source) product id.
     *
     * @return main product id.
     */
    long getProductId();

    /**
     * Set main product id.
     *
     * @param productId main product id.
     */
    void setProductId(long productId);

    /**
     * Get product pk.
     *
     * @return product pk.
     */
    long getAssociatedProductId();

    /**
     * Set product pk.
     *
     * @param associatedProductId product pk.
     */
    void setAssociatedProductId(long associatedProductId);

    /**
     * Get the product code.
     *
     * @return product code.
     */
    String getAssociatedCode();

    /**
     * Product code.
     *
     * @param associatedCode
     */
    void setAssociatedCode(String associatedCode);

    /**
     * Get the product name.
     *
     * @return product name.
     */
    String getAssociatedName();

    /**
     * Product name.
     *
     * @param associatedName
     */
    void setAssociatedName(String associatedName);

    /**
     * Get the product description.
     *
     * @return product description.
     */
    String getAssociatedDescription();

    /**
     * Product description.
     *
     * @param associatedDescription description
     */
    void setAssociatedDescription(String associatedDescription);

}
