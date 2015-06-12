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

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 16:19
 */
@Dto
@XmlRootElement(name = "carrier")
public class CarrierRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(value = "carrierId", readOnly = true)
    private long carrierId;

    @DtoField(value = "name", readOnly = true)
    private String name;

    @DtoField(value = "description", readOnly = true)
    private String description;

    @DtoField(value = "worldwide", readOnly = true)
    private boolean worldwide;

    @DtoField(value = "country", readOnly = true)
    private boolean country;

    @DtoField(value = "state", readOnly = true)
    private boolean state;

    @DtoField(value = "local", readOnly = true)
    private boolean local;

    @DtoField(value = "displayName", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> displayNames;

    @DtoField(value = "displayDescription", converter = "i18nStringConverter", readOnly = true)
    private Map<String, String> displayDescriptions;

    @DtoCollection(
            value = "carrierSla",
            dtoBeanKey = "org.yes.cart.domain.ro.CarrierSlaRO",
            entityGenericType = CarrierSla.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<CarrierSlaRO> carrierSla = new ArrayList<CarrierSlaRO>();


    @XmlAttribute(name = "carrier-id")
    public long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(final long carrierId) {
        this.carrierId = carrierId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @XmlAttribute(name = "worldwide")
    public boolean isWorldwide() {
        return worldwide;
    }

    public void setWorldwide(final boolean worldwide) {
        this.worldwide = worldwide;
    }

    @XmlAttribute(name = "country")
    public boolean isCountry() {
        return country;
    }

    public void setCountry(final boolean country) {
        this.country = country;
    }

    @XmlAttribute(name = "state")
    public boolean isState() {
        return state;
    }

    public void setState(final boolean state) {
        this.state = state;
    }

    @XmlAttribute(name = "local")
    public boolean isLocal() {
        return local;
    }

    public void setLocal(final boolean local) {
        this.local = local;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-names")
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-descriptions")
    public Map<String, String> getDisplayDescriptions() {
        return displayDescriptions;
    }

    public void setDisplayDescriptions(final Map<String, String> displayDescriptions) {
        this.displayDescriptions = displayDescriptions;
    }

    @XmlElementWrapper(name = "available-sla")
    @XmlElement(name = "sla")
    public List<CarrierSlaRO> getCarrierSla() {
        return carrierSla;
    }

    public void setCarrierSla(final List<CarrierSlaRO> carrierSla) {
        this.carrierSla = carrierSla;
    }
}
