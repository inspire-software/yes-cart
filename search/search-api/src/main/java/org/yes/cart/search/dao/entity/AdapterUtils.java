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

package org.yes.cart.search.dao.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.dto.StoredAttributesDTO;
import org.yes.cart.domain.dto.impl.ProductSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.ProductSkuSearchResultDTOImpl;
import org.yes.cart.domain.dto.impl.StoredAttributesDTOImpl;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.utils.DateUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User: denispavlov
 * Date: 20/03/2018
 * Time: 09:41
 */
public class AdapterUtils {

    private static final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    public static final String FIELD_PK = "_PK";
    public static final String FIELD_CLASS = "_CLASS";
    public static final String FIELD_OBJECT = "_OBJECT";
    public static final String FIELD_INDEXTIME = "_IDXTIME";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {

        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 4.0.0 keep this marker for easier version changes
        final SimpleModule module = new SimpleModule("search", new Version(4, 0, 0, null, "org.yes", "search"));
        module.addAbstractTypeMapping(ProductSearchResultDTO.class, ProductSearchResultDTOImpl.class);
        module.addAbstractTypeMapping(ProductSkuSearchResultDTO.class, ProductSkuSearchResultDTOImpl.class);
        module.addAbstractTypeMapping(StoredAttributesDTO.class, StoredAttributesDTOImpl.class);
        module.addAbstractTypeMapping(I18NModel.class, StringI18NModel.class);

        // java.time.* support
        module.addSerializer(Instant.class, new JSInstantSerializer());
        module.addSerializer(LocalDate.class, new JSLocalDateSerializer());
        module.addSerializer(LocalDateTime.class, new JSLocalDateTimeSerializer());
        module.addDeserializer(Instant.class, new JSInstantDeserializer());
        module.addDeserializer(LocalDate.class, new JSLocalDateDeserializer());
        module.addDeserializer(LocalDateTime.class, new JSLocalDateTimeDeserializer());

        MAPPER.registerModule(module);

    }

    private AdapterUtils() {
        // no instance
    }

    /**
     * Reads object from stored field.
     *
     * @param serialized field value
     * @param clazz      object type
     */
    public static <T> T readObjectFieldValue(final String serialized, final Class<T> clazz) {

        if (StringUtils.isNotBlank(serialized)) {
            try {
                return MAPPER.readValue(serialized, clazz);
            } catch (Exception exp) {
                LOGFTQ.error("Unable to de-serialise the object in field: " + FIELD_OBJECT + ", object: " + serialized, exp);
            }
        }
        return null;
    }


    /**
     * Write object to string.
     *
     * @param object     object type
     */
    static String writeObjectFieldValue(final Object object) {

        if (object != null) {
            try {
                return MAPPER.writeValueAsString(object);
            } catch (Exception exp) {
                LOGFTQ.error("Unable to serialise the object into field: xxxx, object: " + object, exp);
            }
        }
        return null;
    }

    private static class JSInstantSerializer extends JsonSerializer<Instant> {
        @Override
        public void serialize(final Instant value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeNumber(value.toEpochMilli());
        }
    }

    private static class JSLocalDateSerializer extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(final LocalDate value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeNumber(DateUtils.millis(value));
        }
    }

    private static class JSLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(final LocalDateTime value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeNumber(DateUtils.millis(value));
        }
    }

    private static class JSInstantDeserializer extends JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return DateUtils.iFrom(p.getLongValue());
        }
    }

    private static class JSLocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return DateUtils.ldFrom(p.getLongValue());
        }
    }

    private static class JSLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return DateUtils.ldtFrom(p.getLongValue());
        }
    }

}
