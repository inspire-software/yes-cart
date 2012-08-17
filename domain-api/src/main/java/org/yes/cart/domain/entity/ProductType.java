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

import java.util.Collection;


/**
 * Type of product.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */

public interface ProductType extends Auditable {

    /**
     * Primary key.
     *
     * @return primary key.
     */
    long getProducttypeId();

    /**
     * Set primary key
     *
     * @param producttypeId primary key to set.
     */
    void setProducttypeId(long producttypeId);

    /**
     * Name of product type.
     *
     * @return name of product type
     */
    String getName();

    /**
     * Set product type name
     *
     * @param name name to set
     */
    void setName(String name);

    /**
     * Description of product type.
     *
     * @return description
     */
    String getDescription();

    /**
     * Set description.
     *
     * @param description to set
     */
    void setDescription(String description);

    /**
     */
    //Map<String,AttrValue> getAttribute();

    //void setAttribute(Map<String,AttrValue> attribute);

    /**
     * Extended attributes definition.
     *
     * @return map of attribute code, and extended attribute definition
     * @see org.yes.cart.domain.entity.ProductTypeAttr
     */
    //Map<String, ProductTypeAttr> getAttributeExt();

    //void setAttributeExt(Map<String, ProductTypeAttr> attributeExt);

    Collection<ProductTypeAttr> getAttributes();

    void setAttributes(Collection<ProductTypeAttr> attribute);


    Collection<ProdTypeAttributeViewGroup> getAttributeViewGroup();

    void setAttributeViewGroup(Collection<ProdTypeAttributeViewGroup> attributeViewGroup);


    /**
     */
    String getUitemplate();

    void setUitemplate(String uitemplate);

    /**
     */
    String getUisearchtemplate();

    void setUisearchtemplate(String uisearchtemplate);

    /**
     * Is this product type service.
     *
     * @return true is this product service
     */
    boolean isService();

    /**
     * Set this product type as service. For example - Gift wrap
     *
     * @param service service flag to set.
     */
    void setService(boolean service);

    /**
     * Is this product type ensemble.
     *
     * @return true if ensemble
     */
    boolean isEnsemble();

    /**
     * Set product type to ensemble
     *
     * @param ensemble true is ensemble
     */
    void setEnsemble(boolean ensemble);

    /**
     * Is this product type can be shipped
     *
     * @return true if product shippable
     */
    boolean isShipable();


    /**
     * Set product type to shippable.
     *
     * @param shipable true if shippable
     */
    void setShipable(boolean shipable);

    /**
     * Is product digital.
     *
     * @return true if product digital.
     */
    boolean isDigital();

    /**
     * Set digital flag to product.
     *
     * @param digital flag to set
     */
    void setDigital(boolean digital);

    /**
     * Is digital product downloadable ?
     *
     * @return true in case if digital product can be downloaded.
     */
    boolean isDownloadable();

    /**
     * Set downloadable flag.
     *
     * @param downloadable flag to set.
     */
    void setDownloadable(boolean downloadable);

}


