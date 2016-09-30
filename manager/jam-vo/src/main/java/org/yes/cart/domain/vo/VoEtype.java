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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;

/**
 * User: denispavlov
 */
@Dto
public class VoEtype {

    @DtoField(value = "etypeId", readOnly = true)
    private long etypeId;

    @DtoField(value = "javatype")
    private String javatype;

    @DtoField(value = "businesstype")
    private String businesstype;

    public long getEtypeId() {
        return etypeId;
    }

    public void setEtypeId(final long etypeId) {
        this.etypeId = etypeId;
    }

    public String getJavatype() {
        return javatype;
    }

    public void setJavatype(final String javatype) {
        this.javatype = javatype;
    }

    public String getBusinesstype() {
        return businesstype;
    }

    public void setBusinesstype(final String businesstype) {
        this.businesstype = businesstype;
    }

}
