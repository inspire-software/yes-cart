package org.yes.cart.utils.impl;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.utils.DateUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * User: denispavlov
 * Date: 18/02/2018
 * Time: 15:18
 */
public final class JsonAdapterUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JsonAdapterUtils.class);

    // 4.0.0 keep this marker for easier version changes
    public static final Version VERSION = new Version(4, 0, 0, null, "org.yes", "json");

    public static final JsonSerializer<Instant> JS_FRIENDLY_INSTANT_SERIALIZER = new JSInstantSerializer();
    public static final JsonSerializer<LocalDate> JS_FRIENDLY_LOCALDATE_SERIALIZER = new JSLocalDateSerializer();
    public static final JsonSerializer<LocalDateTime> JS_FRIENDLY_LOCALDATETIME_SERIALIZER = new JSLocalDateTimeSerializer();

    public static final JsonDeserializer<Instant> JS_FRIENDLY_INSTANT_DESERIALIZER = new JSInstantDeserializer();
    public static final JsonDeserializer<LocalDate> JS_FRIENDLY_LOCALDATE_DESERIALIZER = new JSLocalDateDeserializer();
    public static final JsonDeserializer<LocalDateTime> JS_FRIENDLY_LOCALDATETIME_DESERIALIZER = new JSLocalDateTimeDeserializer();

    private JsonAdapterUtils() {
        // no instance
    }

    /**
     * Basic set of serializers/deserializers for java.time.* pckage which are JS friendly.
     *
     * The serialization is done to long millis so that JS can just do "new Date(x)".
     *
     * Deserializers use the following fallback:
     * String values: ISO, SDT
     * Integer: millis
     *
     * @param module custom module
     */
    public static void configureModuleWithJSFriendlyJavaTime(final SimpleModule module) {
        // java.time.* support

        module.addSerializer(Instant.class, JS_FRIENDLY_INSTANT_SERIALIZER);
        module.addSerializer(LocalDate.class, JS_FRIENDLY_LOCALDATE_SERIALIZER);
        module.addSerializer(LocalDateTime.class, JS_FRIENDLY_LOCALDATETIME_SERIALIZER);

        module.addDeserializer(Instant.class, JS_FRIENDLY_INSTANT_DESERIALIZER);
        module.addDeserializer(LocalDate.class, JS_FRIENDLY_LOCALDATE_DESERIALIZER);
        module.addDeserializer(LocalDateTime.class, JS_FRIENDLY_LOCALDATETIME_DESERIALIZER);
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
            if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
                final String str = p.getValueAsString();
                try {
                    // try ISO first
                    return ZonedDateTime.parse(str).toInstant();
                } catch (Exception zdt) {
                    final Instant instant = DateUtils.iParseSDT(p.getValueAsString());
                    if (instant == null) {
                        LOG.error("Unable to process instant " + p.getValueAsString(), zdt);
                    } else {
                        return instant;
                    }
                }
            } else if (p.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
                return DateUtils.iFrom(p.getLongValue());
            }
            throw new InvalidFormatException(p, "Unable to format instant: " + p.getValueAsString(), p.getCurrentValue(), Instant.class);
        }
    }

    private static class JSLocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
                final String str = p.getValueAsString();
                try {
                    // try ISO first
                    return ZonedDateTime.parse(str).toLocalDateTime().toLocalDate();
                } catch (Exception zdt) {
                    final LocalDate ld = DateUtils.ldParseSDT(p.getValueAsString());
                    if (ld == null) {
                        LOG.error("Unable to process date " + p.getValueAsString(), zdt);
                    } else {
                        return ld;
                    }
                }
            } else if (p.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
                return DateUtils.ldFrom(p.getLongValue());
            }
            throw new InvalidFormatException(p, "Unable to format date: " + p.getValueAsString(), p.getCurrentValue(), LocalDate.class);
        }
    }

    private static class JSLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
                final String str = p.getValueAsString();
                try {
                    // try ISO first
                    return ZonedDateTime.parse(str).toLocalDateTime();
                } catch (Exception zdt) {
                    final LocalDateTime ldt = DateUtils.ldtParseSDT(p.getValueAsString());
                    if (ldt == null) {
                        LOG.error("Unable to process datetime " + p.getValueAsString(), zdt);
                    } else {
                        return ldt;
                    }
                }
            } else if (p.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
                return DateUtils.ldtFrom(p.getLongValue());
            }
            throw new InvalidFormatException(p, "Unable to format datetime: " + p.getValueAsString(), p.getCurrentValue(), LocalDateTime.class);
        }
    }


}
