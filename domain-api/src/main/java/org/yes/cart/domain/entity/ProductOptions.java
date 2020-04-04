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

package org.yes.cart.domain.entity;

import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 17/02/2020
 * Time: 08:18
 */
public interface ProductOptions {

    /**
     * Is this product type configurable.
     *
     * @return true if configurable
     */
    boolean isConfigurable();

    /**
     * Set product type to configurable
     *
     * @param configurable true if configurable
     */
    void setConfigurable(boolean configurable);

    /**
     * Get options specific to a SKU.
     *
     * @param sku SKU
     *
     * @return list of SKU specific options
     */
    List<ProductOption> getConfigurationOptionForSKU(String sku);

    /**
     * Get options specific to a SKU.
     *
     * @param sku SKU
     *
     * @return list of SKU specific options
     */
    List<ProductOption> getConfigurationOptionForSKU(ProductSku sku);

    /**
     * Get {@link ProductOption} for product if it has configurable flag
     *
     * @return Set of {@link ProductOption} for product.
     */
    Set<ProductOption> getConfigurationOption();

    /**
     * Create configuration option
     *
     * @param attribute attribute code
     * @param sku       optional SKU
     *
     * @return new or existing option
     */
    ProductOption createOrGetConfigurationOption(String attribute, String sku);

    /**
     * Remove option.
     *
     * @param attribute attribute code
     * @param sku       optional SKU
     *
     * @return true if removed (false if did not match)
     */
    boolean removeConfigurationOption(String attribute, String sku);


}
