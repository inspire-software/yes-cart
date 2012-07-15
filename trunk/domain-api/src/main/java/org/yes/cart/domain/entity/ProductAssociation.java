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

package org.yes.cart.domain.entity;

/**
 * Product associations.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductAssociation extends Auditable {

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
     * Get accosiated(destiation) product.
     *
     * @return accosiated product.
     */
    Product getProductAssociated();

    /**
     * Set accosiated product.
     *
     * @param productAssociated accosiated product.
     */
    void setProductAssociated(Product productAssociated);

}


