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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Attributable;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.domain.entity.Seoable;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 21:00
 */
public class AbstractXmlEntityHandlerTest {

    private final Mockery context = new JUnit4Mockery();

    private final AbstractXmlEntityHandler<Object> handler = new AbstractXmlEntityHandler<Object>("root") {
        @Override
        public void handle(final JobStatusListener statusListener,
                           final XmlExportDescriptor xmlExportDescriptor,
                           final ImpExTuple<String, Object> tuple,
                           final XmlValueAdapter xmlValueAdapter,
                           final String fileToExport,
                           final OutputStreamWriter writer) {
            // do nothing
        }
    };

    private final AbstractXmlEntityHandler<Object> handlerPretty = new AbstractXmlEntityHandler<Object>("root") {
        @Override
        public void handle(final JobStatusListener statusListener,
                           final XmlExportDescriptor xmlExportDescriptor,
                           final ImpExTuple<String, Object> tuple,
                           final XmlValueAdapter xmlValueAdapter,
                           final String fileToExport,
                           final OutputStreamWriter writer) {
            // do nothing
        }
    };

    @Before
    public void setUp() throws Exception {
        handlerPretty.setPrettyPrint(true);
    }

    @Test
    public void testStart() throws Exception {

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<root>\n", handler.startXmlInternal());


    }

    @Test
    public void testEnd() throws Exception {

        assertEquals("</root>", handler.endXmlInternal());


    }

    @Test
    public void testXmlBuilderStandard() throws Exception {

        assertEquals("<root attr1=\"av1+&amp;&amp;\" attr2=\"av2zzz\">&amp;testValue</root>",
            this.handler
                .tag("root").attr("attr1", "av1+&&").attr("attr2", "av2zzz")
                    .chars("&testValue")
                .end()
            .toXml());

        assertEquals("<root attr1=\"av1+&amp;&amp;\" attr2=\"av2zzz\"><![CDATA[&testValue]]></root>",
            this.handler
                .tag("root").attr("attr1", "av1+&&").attr("attr2", "av2zzz")
                    .cdata("&testValue")
                .end()
            .toXml());

        assertEquals("<root><sub1><![CDATA[&testValue]]></sub1></root>",
            this.handler
                .tag("root")
                    .tag("sub1")
                        .cdata("&testValue")
                    .end()
                .end()
            .toXml());

        assertEquals("<root><sub1><![CDATA[&testValue]]></sub1><sub2>&amp;testValue 2\n" +
                        "another line</sub2><bool>true</bool><chars>&amp;chars</chars><cdata><![CDATA[&chars]]></cdata><num-bd>30</num-bd><num-int>31</num-int><num-long>32</num-long></root>",
            this.handler
                .tag("root")
                    .tag("sub1")
                        .cdata("&testValue")
                    .end()
                    .tag("sub2")
                        .chars("&testValue 2")
                        .chars("\nanother line")
                    .end()
                    .tagBool("bool", true)
                    .tagChars("chars", "&chars")
                    .tagCdata("cdata", "&chars")
                    .tagNum("num-bd", new BigDecimal("30"))
                    .tagNum("num-int", 31)
                    .tagNum("num-long", 32L)
                .end()
            .toXml());

        final Map<String, String> names = new HashMap<>();
        names.put("en", "english");
        names.put("de", "deutch");
        final I18NModel displayName = new StringI18NModel(names);

        assertEquals("<root><display-name><i18n lang=\"de\"><![CDATA[deutch]]></i18n><i18n lang=\"en\"><![CDATA[english]]></i18n></display-name></root>",
                this.handler
                        .tag("root")
                            .tagI18n("display-name", displayName)
                        .end()
                    .toXml());

        final Attributable attr = this.context.mock(Attributable.class);
        final AttrValue av1 = this.context.mock(AttrValue.class, "av1");
        final AttrValue av2 = this.context.mock(AttrValue.class, "av2");

        this.context.checking(new Expectations() {{
            allowing(attr).getAllAttributes(); will(returnValue(Arrays.asList(av1, av2)));
            allowing(av1).getAttrvalueId(); will(returnValue(1L));
            allowing(av1).getAttributeCode(); will(returnValue("CODE1"));
            allowing(av1).getGuid(); will(returnValue("GUID1"));
            allowing(av1).getVal(); will(returnValue("val1"));
            allowing(av1).getDisplayVal(); will(returnValue(displayName));
            allowing(av2).getAttrvalueId(); will(returnValue(2L));
            allowing(av2).getAttributeCode(); will(returnValue("CODE2"));
            allowing(av2).getGuid(); will(returnValue("GUID2"));
            allowing(av2).getVal(); will(returnValue("val2"));
            allowing(av2).getDisplayVal(); will(returnValue(displayName));
        }});

        assertEquals("<root><custom-attributes><custom-attribute id=\"1\" guid=\"GUID1\" attribute=\"CODE1\"><custom-value><![CDATA[val1]]></custom-value><custom-display-value><i18n lang=\"de\"><![CDATA[deutch]]></i18n><i18n lang=\"en\"><![CDATA[english]]></i18n></custom-display-value></custom-attribute><custom-attribute id=\"2\" guid=\"GUID2\" attribute=\"CODE2\"><custom-value><![CDATA[val2]]></custom-value><custom-display-value><i18n lang=\"de\"><![CDATA[deutch]]></i18n><i18n lang=\"en\"><![CDATA[english]]></i18n></custom-display-value></custom-attribute></custom-attributes></root>",
                this.handler
                        .tag("root")
                            .tagExt(attr)
                        .end()
                    .toXml());

        final Seoable seoable = this.context.mock(Seoable.class);
        final Seo seo = this.context.mock(Seo.class);

        this.context.checking(new Expectations() {{
            allowing(seoable).getSeo(); will(returnValue(seo));
            allowing(seo).getUri(); will(returnValue("abc"));
            allowing(seo).getTitle(); will(returnValue("title"));
            allowing(seo).getDisplayTitle(); will(returnValue(displayName));
            allowing(seo).getMetakeywords(); will(returnValue("keyword"));
            allowing(seo).getDisplayMetakeywords(); will(returnValue(displayName));
            allowing(seo).getMetadescription(); will(returnValue("description"));
            allowing(seo).getDisplayMetadescription(); will(returnValue(displayName));
        }});

        assertEquals("<root><seo><uri><![CDATA[abc]]></uri><meta-title><![CDATA[title]]></meta-title><meta-title-display><i18n lang=\"de\"><![CDATA[deutch]]></i18n><i18n lang=\"en\"><![CDATA[english]]></i18n></meta-title-display><meta-keywords><![CDATA[keyword]]></meta-keywords><meta-keywords-display><i18n lang=\"de\"><![CDATA[deutch]]></i18n><i18n lang=\"en\"><![CDATA[english]]></i18n></meta-keywords-display><meta-description><![CDATA[description]]></meta-description><meta-description-display><i18n lang=\"de\"><![CDATA[deutch]]></i18n><i18n lang=\"en\"><![CDATA[english]]></i18n></meta-description-display></seo></root>",
                this.handler
                        .tag("root")
                            .tagSeo(seoable)
                        .end()
                    .toXml());

    }

    @Test
    public void testXmlBuilderPretty() throws Exception {

        assertEquals("<root attr1=\"av1+&amp;&amp;\" attr2=\"av2zzz\">&amp;testValue</root>\n",
            this.handlerPretty
                .tag("root").attr("attr1", "av1+&&").attr("attr2", "av2zzz")
                    .chars("&testValue")
                .end()
            .toXml());

        assertEquals("<root attr1=\"av1+&amp;&amp;\" attr2=\"av2zzz\"><![CDATA[&testValue]]></root>\n",
            this.handlerPretty
                .tag("root").attr("attr1", "av1+&&").attr("attr2", "av2zzz")
                    .cdata("&testValue")
                .end()
            .toXml());

        assertEquals("<root>\n    <sub1><![CDATA[&testValue]]></sub1>\n</root>\n",
            this.handlerPretty
                .tag("root")
                    .tag("sub1")
                        .cdata("&testValue")
                    .end()
                .end()
            .toXml());

        assertEquals("<root>\n" +
                        "    <sub1><![CDATA[&testValue]]></sub1>\n" +
                        "    <sub2>&amp;testValue 2\n" +
                        "another line</sub2>\n" +
                        "    <bool>true</bool>\n" +
                        "    <chars>&amp;chars</chars>\n" +
                        "    <cdata><![CDATA[&chars]]></cdata>\n" +
                        "    <num-bd>30</num-bd>\n" +
                        "    <num-int>31</num-int>\n" +
                        "    <num-long>32</num-long>\n" +
                        "</root>\n",
            this.handlerPretty
                .tag("root")
                    .tag("sub1")
                        .cdata("&testValue")
                    .end()
                    .tag("sub2")
                        .chars("&testValue 2")
                        .chars("\nanother line")
                    .end()
                    .tagBool("bool", true)
                    .tagChars("chars", "&chars")
                    .tagCdata("cdata", "&chars")
                    .tagNum("num-bd", new BigDecimal("30"))
                    .tagNum("num-int", 31)
                    .tagNum("num-long", 32L)
                .end()
            .toXml());


        final Map<String, String> names = new HashMap<>();
        names.put("en", "english");
        names.put("de", "deutch");
        final I18NModel displayName = new StringI18NModel(names);

        assertEquals("<root>\n" +
                        "    <display-name>\n" +
                        "        <i18n lang=\"de\"><![CDATA[deutch]]></i18n>\n" +
                        "        <i18n lang=\"en\"><![CDATA[english]]></i18n>\n" +
                        "    </display-name>\n" +
                        "</root>\n",
                this.handlerPretty
                        .tag("root")
                            .tagI18n("display-name", displayName)
                        .end()
                    .toXml());

        final Attributable attr = this.context.mock(Attributable.class);
        final AttrValue av1 = this.context.mock(AttrValue.class, "av1");
        final AttrValue av2 = this.context.mock(AttrValue.class, "av2");

        this.context.checking(new Expectations() {{
            allowing(attr).getAllAttributes(); will(returnValue(Arrays.asList(av1, av2)));
            allowing(av1).getAttrvalueId(); will(returnValue(1L));
            allowing(av1).getAttributeCode(); will(returnValue("CODE1"));
            allowing(av1).getGuid(); will(returnValue("GUID1"));
            allowing(av1).getVal(); will(returnValue("val1"));
            allowing(av1).getDisplayVal(); will(returnValue(displayName));
            allowing(av2).getAttrvalueId(); will(returnValue(2L));
            allowing(av2).getAttributeCode(); will(returnValue("CODE2"));
            allowing(av2).getGuid(); will(returnValue("GUID2"));
            allowing(av2).getVal(); will(returnValue("val2"));
            allowing(av2).getDisplayVal(); will(returnValue(displayName));
        }});

        assertEquals("<root>\n" +
                        "    <custom-attributes>\n" +
                        "        <custom-attribute id=\"1\" guid=\"GUID1\" attribute=\"CODE1\">\n" +
                        "            <custom-value><![CDATA[val1]]></custom-value>\n" +
                        "            <custom-display-value>\n" +
                        "                <i18n lang=\"de\"><![CDATA[deutch]]></i18n>\n" +
                        "                <i18n lang=\"en\"><![CDATA[english]]></i18n>\n" +
                        "            </custom-display-value>\n" +
                        "        </custom-attribute>\n" +
                        "        <custom-attribute id=\"2\" guid=\"GUID2\" attribute=\"CODE2\">\n" +
                        "            <custom-value><![CDATA[val2]]></custom-value>\n" +
                        "            <custom-display-value>\n" +
                        "                <i18n lang=\"de\"><![CDATA[deutch]]></i18n>\n" +
                        "                <i18n lang=\"en\"><![CDATA[english]]></i18n>\n" +
                        "            </custom-display-value>\n" +
                        "        </custom-attribute>\n" +
                        "    </custom-attributes>\n" +
                        "</root>\n",
                this.handlerPretty
                        .tag("root")
                            .tagExt(attr)
                        .end()
                    .toXml());


        final Seoable seoable = this.context.mock(Seoable.class);
        final Seo seo = this.context.mock(Seo.class);

        this.context.checking(new Expectations() {{
            allowing(seoable).getSeo(); will(returnValue(seo));
            allowing(seo).getUri(); will(returnValue("abc"));
            allowing(seo).getTitle(); will(returnValue("title"));
            allowing(seo).getDisplayTitle(); will(returnValue(displayName));
            allowing(seo).getMetakeywords(); will(returnValue("keyword"));
            allowing(seo).getDisplayMetakeywords(); will(returnValue(displayName));
            allowing(seo).getMetadescription(); will(returnValue("description"));
            allowing(seo).getDisplayMetadescription(); will(returnValue(displayName));
        }});

        assertEquals("<root>\n" +
                        "    <seo>\n" +
                        "        <uri><![CDATA[abc]]></uri>\n" +
                        "        <meta-title><![CDATA[title]]></meta-title>\n" +
                        "        <meta-title-display>\n" +
                        "            <i18n lang=\"de\"><![CDATA[deutch]]></i18n>\n" +
                        "            <i18n lang=\"en\"><![CDATA[english]]></i18n>\n" +
                        "        </meta-title-display>\n" +
                        "        <meta-keywords><![CDATA[keyword]]></meta-keywords>\n" +
                        "        <meta-keywords-display>\n" +
                        "            <i18n lang=\"de\"><![CDATA[deutch]]></i18n>\n" +
                        "            <i18n lang=\"en\"><![CDATA[english]]></i18n>\n" +
                        "        </meta-keywords-display>\n" +
                        "        <meta-description><![CDATA[description]]></meta-description>\n" +
                        "        <meta-description-display>\n" +
                        "            <i18n lang=\"de\"><![CDATA[deutch]]></i18n>\n" +
                        "            <i18n lang=\"en\"><![CDATA[english]]></i18n>\n" +
                        "        </meta-description-display>\n" +
                        "    </seo>\n" +
                        "</root>\n",
                this.handlerPretty
                        .tag("root")
                            .tagSeo(seoable)
                        .end()
                    .toXml());


    }



    @Test
    public void escapeUtf8() throws Exception {

        final String test = "Some string with <tag>tag</tag> & \"funky\" 'chars'\\text";
        final StringBuilder xml = new StringBuilder();

        AttributeXmlEntityHandler.appendEscapedXmlUtf8(xml, test);

        assertEquals("Some string with &lt;tag&gt;tag&lt;/tag&gt; &amp; &quot;funky&quot; &#039;chars&#039;\\text", xml.toString());

    }

}