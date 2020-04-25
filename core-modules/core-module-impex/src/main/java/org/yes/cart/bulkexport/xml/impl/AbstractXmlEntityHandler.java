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

package org.yes.cart.bulkexport.xml.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.bulkexport.xml.XmlEntityExportHandler;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.utils.DateUtils;

import java.io.OutputStreamWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:28
 */
public abstract class AbstractXmlEntityHandler<T> implements XmlEntityExportHandler<T> {

    protected static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected static final String DATE_FORMAT = "yyyy-MM-dd";

    private final String rootTag;
    private boolean prettyPrint = false;

    protected AbstractXmlEntityHandler(final String rootTag) {
        this.rootTag = rootTag;
    }


    @Override
    public void startXml(final OutputStreamWriter writer) throws Exception {
        write(writer, startXmlInternal());
    }

    String startXmlInternal() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<" + rootTag + ">\n";
    }

    @Override
    public void endXml(final OutputStreamWriter writer) throws Exception {
        write(writer, endXmlInternal(), true);
    }

    String endXmlInternal() {
        return "</" + rootTag + ">";
    }

    protected Tag tag(final String name) {
        return new Tag(name, this.prettyPrint);
    }

    protected Tag tag(final Tag parent, final String name) {
        return new Tag(parent, name, this.prettyPrint);
    }

    protected static class Tag {

        private static final String INDENT = "    ";

        private final boolean prettyPrint;
        private final int nested;
        private final Tag parent;
        private final String name;
        private final StringBuilder xml = new StringBuilder();
        private boolean hasTextOrTag = false;

        public Tag(final String name) {
            this(name, false);
        }

        public Tag(final String name, final boolean prettyPrint) {
            this(null, name, prettyPrint);
        }

        private Tag(final Tag parent, final String name) {
            this(parent, name, parent != null && parent.prettyPrint);
        }

        private Tag(final Tag parent, final String name, final boolean prettyPrint) {
            this.name = name;
            this.parent = parent;
            this.nested = parent != null ? parent.nested + 1 : 0;
            this.prettyPrint = prettyPrint;
            this.xml.append('<').append(name);
        }

        public Tag attr(final String name, final String value) {
            if (this.hasTextOrTag) {
                throw new IllegalStateException("This tag already has characters or inner tags:\n" + this.toXml());
            }
            if (StringUtils.isNotBlank(value)) {
                this.xml.append(' ').append(name).append("=\"");
                appendEscapedXmlUtf8(this.xml, value);
                this.xml.append('"');
            }
            return this;
        }

        public Tag attr(final String name, final Boolean value) {
            if (this.hasTextOrTag) {
                throw new IllegalStateException("This tag already has characters or inner tags:\n" + this.toXml());
            }
            if (value != null) {
                this.xml.append(' ').append(name).append("=\"").append(value).append('"');
            }
            return this;
        }

        public Tag attr(final String name, final Number value) {
            if (this.hasTextOrTag) {
                throw new IllegalStateException("This tag already has characters or inner tags:\n" + this.toXml());
            }
            if (value != null) {
                this.xml.append(' ').append(name).append("=\"").append(value).append('"');
            }
            return this;
        }

        private Tag bool(final Boolean value) {
            if (value != null) {
                if (!this.hasTextOrTag && this.xml.charAt(this.xml.length() - 1) != '>') {
                    this.xml.append('>');
                }
                this.xml.append(value);
                this.hasTextOrTag = true;
            }
            return this;
        }

        private Tag num(final Number value) {
            if (value != null) {
                if (!this.hasTextOrTag && this.xml.charAt(this.xml.length() - 1) != '>') {
                    this.xml.append('>');
                }
                this.xml.append(value);
                this.hasTextOrTag = true;
            }
            return this;
        }

        private Tag i18n(final I18NModel value) {
            if (value != null) {
                if (!value.getAllValues().isEmpty()) {
                    for (final Map.Entry<String, String> i18n : value.getAllValues().entrySet()) {
                        tag("i18n").attr("lang", i18n.getKey()).cdata(i18n.getValue()).end();
                    }
                } else if (value.getValue(I18NModel.DEFAULT) != null) {
                    tag("i18n").attr("lang", I18NModel.DEFAULT).cdata(value.getValue(I18NModel.DEFAULT)).end();
                }
            }
            return this;
        }

        private Tag ext(final Attributable attributable) {
            if (!attributable.getAllAttributes().isEmpty()) {
                final Tag custom = tag("custom-attributes");
                for (final AttrValue attrValue : attributable.getAllAttributes()) {
                    custom.tag("custom-attribute")
                            .attr("id", attrValue.getAttrvalueId())
                            .attr("guid", attrValue.getGuid())
                            .attr("attribute", attrValue.getAttributeCode())
                                .tagCdata("custom-value", attrValue.getVal())
                                .tagI18n("custom-display-value", attrValue.getDisplayVal())
                            .end();

                }
                return custom.end();
            }
            return this;
        }

        private Tag ext(final Map<String, Pair<String, I18NModel>> attributes) {
            if (!attributes.isEmpty()) {
                final Tag custom = tag("custom-attributes");
                for (final Map.Entry<String, Pair<String, I18NModel>> attrValue : attributes.entrySet()) {
                    custom.tag("custom-attribute")
                            .attr("attribute", attrValue.getKey())
                                .tagCdata("custom-value", attrValue.getValue().getFirst())
                                .tagI18n("custom-display-value", attrValue.getValue().getSecond())
                            .end();

                }
                return custom.end();
            }
            return this;
        }

        private Tag seo(final Seoable seoable) {
            if (seoable != null && seoable.getSeo() != null) {
                final Seo seo = seoable.getSeo();
                return tag("seo")
                        .tagCdata("uri", seo.getUri())
                        .tagCdata("meta-title", seo.getTitle())
                        .tagI18n("meta-title-display", seo.getDisplayTitle())
                        .tagCdata("meta-keywords", seo.getMetakeywords())
                        .tagI18n("meta-keywords-display", seo.getDisplayMetakeywords())
                        .tagCdata("meta-description", seo.getMetadescription())
                        .tagI18n("meta-description-display", seo.getDisplayMetadescription())
                    .end();
            }
            return this;
        }

        public Tag chars(final String value) {
            if (StringUtils.isNotBlank(value)) {
                if (!this.hasTextOrTag && this.xml.charAt(this.xml.length() - 1) != '>') {
                    this.xml.append('>');
                }
                appendEscapedXmlUtf8(this.xml, value);

                this.hasTextOrTag = true;
            }
            return this;
        }

        public Tag cdata(final String value) {
            if (StringUtils.isNotBlank(value)) {
                if (!this.hasTextOrTag && this.xml.charAt(this.xml.length() - 1) != '>') {
                    this.xml.append('>');
                }
                this.xml.append("<![CDATA[").append(value).append("]]>");
                this.hasTextOrTag = true;
            }
            return this;
        }

        public Tag tag(final String name) {
            this.hasTextOrTag = true;
            return new Tag(this, name);
        }

        public Tag tagChars(final String name, final String value) {
            if (value != null) {
                this.hasTextOrTag = true;
                return new Tag(this, name).chars(value).end();
            }
            return this;
        }

        public Tag tagCdata(final String name, final String value) {
            if (value != null) {
                this.hasTextOrTag = true;
                return new Tag(this, name).cdata(value).end();
            }
            return this;
        }

        public Tag tagBool(final String name, final Boolean value) {
            if (value != null) {
                this.hasTextOrTag = true;
                return new Tag(this, name).bool(value).end();
            }
            return this;
        }

        public Tag tagNum(final String name, final Number value) {
            if (value != null) {
                this.hasTextOrTag = true;
                return new Tag(this, name).num(value).end();
            }
            return this;
        }

        public Tag tagList(final String name, final String itemName, final String value, final char delimiter) {
            if (StringUtils.isNotBlank(value)) {
                this.hasTextOrTag = true;

                final String[] values = StringUtils.split(value, delimiter);
                final Tag list = new Tag(this, name);
                for (final String item : values) {
                    list.tagChars(itemName, item.trim());
                }
                return list.end();
            }
            return this;
        }

        public Tag tagI18n(final String name, final I18NModel value) {
            if (value != null) {
                this.hasTextOrTag = true;
                return new Tag(this, name).i18n(value).end();
            }
            return this;
        }

        public Tag tagExt(final Attributable value) {
            if (value != null) {
                this.hasTextOrTag = true;
                return ext(value);
            }
            return this;
        }

        public Tag tagExt(final Map<String, Pair<String, I18NModel>> value) {
            if (value != null) {
                this.hasTextOrTag = true;
                return ext(value);
            }
            return this;
        }

        public Tag tagSeo(final Seoable value) {
            if (value != null) {
                this.hasTextOrTag = true;
                return seo(value);
            }
            return this;
        }

        public Tag tagTime(final Auditable auditable) {
            if (auditable.getCreatedTimestamp() != null) {
                tagChars("created-timestamp", DateUtils.format(auditable.getCreatedTimestamp(), TIMESTAMP_FORMAT));
                this.hasTextOrTag = true;
            }
            if (auditable.getCreatedBy() != null) {
                tagChars("created-by", auditable.getCreatedBy());
                this.hasTextOrTag = true;
            }
            if (auditable.getUpdatedTimestamp() != null) {
                tagChars("updated-timestamp", DateUtils.format(auditable.getUpdatedTimestamp(), TIMESTAMP_FORMAT));
                this.hasTextOrTag = true;
            }
            if (auditable.getUpdatedBy() != null) {
                tagChars("updated-by", auditable.getUpdatedBy());
                this.hasTextOrTag = true;
            }
            return this;
        }

        public Tag tagTime(final org.yes.cart.payment.persistence.entity.Auditable auditable) {
            if (auditable.getCreatedTimestamp() != null) {
                tagChars("created-timestamp", DateUtils.format(auditable.getCreatedTimestamp(), TIMESTAMP_FORMAT));
                this.hasTextOrTag = true;
            }
            if (auditable.getCreatedBy() != null) {
                tagChars("created-by", auditable.getCreatedBy());
                this.hasTextOrTag = true;
            }
            if (auditable.getUpdatedTimestamp() != null) {
                tagChars("updated-timestamp", DateUtils.format(auditable.getUpdatedTimestamp(), TIMESTAMP_FORMAT));
                this.hasTextOrTag = true;
            }
            if (auditable.getUpdatedBy() != null) {
                tagChars("updated-by", auditable.getUpdatedBy());
                this.hasTextOrTag = true;
            }
            return this;
        }

        public Tag tagTime(final String name, final Instant instant) {
            if (instant != null) {
                tagChars(name, DateUtils.format(instant, TIMESTAMP_FORMAT));
                this.hasTextOrTag = true;
            }
            return this;
        }

        public Tag tagTime(final String name, final LocalDateTime instant) {
            if (instant != null) {
                tagChars(name, DateUtils.format(instant, TIMESTAMP_FORMAT));
                this.hasTextOrTag = true;
            }
            return this;
        }

        public Tag tagTime(final String name, final LocalDate instant) {
            if (instant != null) {
                tagChars(name, DateUtils.format(instant, DATE_FORMAT));
                this.hasTextOrTag = true;
            }
            return this;
        }

        public Tag end() {
            if (this.hasTextOrTag) {
                if (prettyPrint && this.xml.charAt(this.xml.length() - 1) == '\n') {
                    for (int i = 0; i < this.nested; i++) {
                        this.xml.append(INDENT);
                    }
                }
                this.xml.append("</").append(name).append('>');
            } else {
                this.xml.append("/>");
            }
            if (prettyPrint) {
                for (int i = 0; i < this.nested; i++) {
                    this.xml.insert(0, INDENT);
                }
                this.xml.append('\n');
            }
            if (this.parent != null) {
                if (this.parent.xml.charAt(this.parent.xml.length() - 1) != '>' &&
                        this.parent.xml.charAt(this.parent.xml.length() - 1) != '\n') {
                    this.parent.xml.append('>');
                    if (prettyPrint) {
                        this.parent.xml.append('\n');
                    }
                }
                this.parent.xml.append(this.toXml());
                this.parent.hasTextOrTag = true;
                return this.parent;
            }
            return this;
        }

        public String toXml() {
            return this.xml.toString();
        }


    }

    protected static void appendEscapedXmlUtf8(final StringBuilder toAppendTo, final String text){
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                switch (c) {
                    case '<':
                        toAppendTo.append("&lt;");
                        break;
                    case '>':
                        toAppendTo.append("&gt;");
                        break;
                    case '"':
                        toAppendTo.append("&quot;");
                        break;
                    case '\'':
                        toAppendTo.append("&#039;");
                        break;
                    case '&':
                        toAppendTo.append("&amp;");
                        break;
                    default:
                        toAppendTo.append(c);
                }
            }
        }
    }

    /**
     * Convenience method to write a non-empty tag and count it.
     *
     * @param tag         tag to output
     * @param writer      writer to output tag to
     * @param listener    listener
     *
     * @throws Exception in case of write errors
     */
    protected void handleInternal(final Tag tag,
                                  final OutputStreamWriter writer,
                                  final JobStatusListener listener) throws Exception {
        final String xmlChunk = tag.toXml();
        if (xmlChunk != null) {
            write(writer, xmlChunk);
            listener.count(tag.name);
        }
    }

    /**
     * Common writer method for writing.
     *
     * @param writer      writer to output tag to
     * @param out         string out
     *
     * @throws Exception in case of write errors
     */
    protected void write(final OutputStreamWriter writer,
                         final String out) throws Exception {
        write(writer, out, false);
    }

    /**
     * Common writer method for writing.
     *
     * @param writer      writer to output tag to
     * @param out         string out
     * @param forceFlush  force calling write.flush()
     *
     * @throws Exception in case of write errors
     */
    protected void write(final OutputStreamWriter writer,
                         final String out,
                         final boolean forceFlush) throws Exception {
        if (out != null) {
            writer.write(out);
        }
        if (forceFlush) {
            writer.flush();
        }
    }

    /**
     * Spring IoC.
     *
     * @param prettyPrint set pretty print mode (new lines and indents)
     */
    public void setPrettyPrint(final boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }
}
