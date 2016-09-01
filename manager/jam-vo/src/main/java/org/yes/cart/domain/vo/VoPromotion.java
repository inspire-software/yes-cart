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
import org.yes.cart.domain.misc.MutablePair;

import java.util.Date;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13-10-22
 * Time: 5:37 PM
 */
@Dto
public class VoPromotion {

    @DtoField(value = "promotionId", readOnly = true)
    private long promotionId;

    @DtoField(value = "shopCode")
    private String shopCode;
    @DtoField(value = "currency")
    private String currency;
    @DtoField(value = "code")
    private String code;
    @DtoField(value = "promoType")
    private String promoType;
    @DtoField(value = "promoAction")
    private String promoAction;

    @DtoField(value = "eligibilityCondition")
    private String eligibilityCondition;

    @DtoField(value = "promoActionContext")
    private String promoActionContext;

    @DtoField(value = "tag")
    private String tag;

    @DtoField(value = "rank")
    private int rank;

    @DtoField(value = "name")
    private String name;
    @DtoField(value = "description")
    private String description;

    @DtoField(value = "displayNames", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayNames;

    @DtoField(value = "displayDescriptions", converter = "DisplayValues")
    private List<MutablePair<String, String>> displayDescriptions;

    @DtoField(value = "couponTriggered")
    private boolean couponTriggered;
    @DtoField(value = "canBeCombined")
    private boolean canBeCombined;
    @DtoField(value = "enabled")
    private boolean enabled;
    @DtoField(value = "enabledFrom")
    private Date enabledFrom;
    @DtoField(value = "enabledTo")
    private Date enabledTo;

}
