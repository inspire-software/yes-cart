/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.remote.service.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.yes.cart.util.DateUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Customized object mapper to support creation of particular dto classes from json.
 * Created by igor on 30.12.2015.
 */
public class DtoObjectMapper extends ObjectMapper {

    public DtoObjectMapper() {
        super();

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        final SimpleModule module = new SimpleModule("dto", new Version( 3, 4, 0, null, "org.yes", "jam"));
        //module.addAbstractTypeMapping(ShopDTO.class, ShopDTOImpl.class);

        // java.time.* support
        module.addSerializer(Instant.class, new JSInstantSerializer());
        module.addSerializer(LocalDate.class, new JSLocalDateSerializer());
        module.addSerializer(LocalDateTime.class, new JSLocalDateTimeSerializer());
        module.addDeserializer(Instant.class, new JSInstantDeserializer());
        module.addDeserializer(LocalDate.class, new JSLocalDateDeserializer());
        module.addDeserializer(LocalDateTime.class, new JSLocalDateTimeDeserializer());

        this.registerModule(module);

    }

    private class JSInstantSerializer extends JsonSerializer<Instant> {
        @Override
        public void serialize(final Instant value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeNumber(value.toEpochMilli());
        }
    }

    private class JSLocalDateSerializer extends JsonSerializer<LocalDate> {
        @Override
        public void serialize(final LocalDate value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeNumber(DateUtils.millis(value));
        }
    }

    private class JSLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(final LocalDateTime value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            gen.writeNumber(DateUtils.millis(value));
        }
    }

    private class JSInstantDeserializer extends JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return DateUtils.iFrom(p.getLongValue());
        }
    }

    private class JSLocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return DateUtils.ldFrom(p.getLongValue());
        }
    }

    private class JSLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return DateUtils.ldtFrom(p.getLongValue());
        }
    }
}
