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

package org.yes.cart.utils.impl;

import org.hibernate.collection.spi.PersistentCollection;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.Auditable;
import org.yes.cart.domain.misc.Pair;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 *
 * User: iazarny@yahoo.com
 * Date: 29-Jan-2012
 * Time: 12:24 PM
 */
public class ObjectUtilTest {

    private final Mockery context = new JUnit4Mockery();

    @Test
    public void testToObjectArrayNull() throws Exception {

        Object [] objnull = ObjectUtil.toObjectArray(null);
        assertNull(objnull[0]);

    }

    @Test
    public void testToObjectArrayObjects() throws Exception {

        Object []
        objarr = ObjectUtil.toObjectArray(new String[] { "one", "two" });
        assertEquals("one", objarr[0]);
        assertEquals("two", objarr[1]);

        objarr = ObjectUtil.toObjectArray(new int[] { 1, 2 });
        assertEquals("1", objarr[0]);
        assertEquals("2", objarr[1]);

    }

    @Test
    public void testToObjectArrayBasic() throws Exception {

        // Object transformation
        Object [] objarr = ObjectUtil.toObjectArray(new Pair<String, Integer>("first", 10));
        assertEquals("Static: serialVersionUID", objarr[0]);
        assertEquals("first", objarr[1]);
        assertEquals("10", objarr[2]);

        // Object transformation with XML escaping (we are sending this over WebServices!!!)
        Object [] objarrXML = ObjectUtil.toObjectArray(new Pair<String, Integer>("<first>", 10));
        assertEquals("Static: serialVersionUID", objarr[0]);
        assertEquals("&lt;first&gt;", objarrXML[1]);
        assertEquals("10", objarrXML[2]);

    }

    @Test
    public void testToObjectArrayPrimitivesAndString() throws Exception {

        // Single entry transformation
        Object []
        objprim = ObjectUtil.toObjectArray(12);
        assertEquals("12", objprim[0]);
        objprim = ObjectUtil.toObjectArray(11L);
        assertEquals("11", objprim[0]);
        objprim = ObjectUtil.toObjectArray(10f);
        assertEquals("10.0", objprim[0]);
        objprim = ObjectUtil.toObjectArray(9d);
        assertEquals("9.0", objprim[0]);
        objprim = ObjectUtil.toObjectArray("text");
        assertEquals("text", objprim[0]);
        objprim = ObjectUtil.toObjectArray("text>");
        assertEquals("text&gt;", objprim[0]);

    }

    @Test
    public void testToObjectArrayBasicPropertyCollection() throws Exception {

        // Object transformation
        Object [] objarr = ObjectUtil.toObjectArray(new Pair<String, List<String>>("first", Arrays.asList("text")));
        assertEquals("Static: serialVersionUID", objarr[0]);
        assertEquals("first", objarr[1]);
        assertEquals("Collection: second", objarr[2]);

    }

    @Test
    public void testToObjectArrayBasicPropertyMap() throws Exception {

        // Object transformation
        Object [] objarr = ObjectUtil.toObjectArray(new Pair<String, Map<String, String>>("first", Collections.singletonMap("key", "value")));
        assertEquals("Static: serialVersionUID", objarr[0]);
        assertEquals("first", objarr[1]);
        assertEquals("Map: second", objarr[2]);

    }

    @Test
    public void testToObjectArrayAuditable() throws Exception {

        final Auditable auditable = context.mock(Auditable.class, "auditable");

        context.checking(new Expectations() {{
            one(auditable).getId(); will(returnValue(123L));
        }});

        // Object transformation
        Object [] objarr = ObjectUtil.toObjectArray(new Pair<String, Auditable>("first", auditable));
        assertEquals("Static: serialVersionUID", objarr[0]);
        assertEquals("first", objarr[1]);
        assertEquals("123: second", objarr[2]);

        context.assertIsSatisfied();

    }

    @Test
    public void testToObjectArrayAuditablePrime() throws Exception {

        final Auditable auditable = new Auditable() {

            private long addressId = 123L;
            private long version = 3;
            private Date createdTimestamp = null;
            private Date updatedTimestamp = null;
            private String createdBy = "admin1";
            private String updatedBy = "admin2";
            private String guid = "GUID-123";

            public long getId() {
                return addressId;
            }

            public long getVersion() {
                return version;
            }

            public Date getCreatedTimestamp() {
                return createdTimestamp;
            }

            public Date getUpdatedTimestamp() {
                return updatedTimestamp;
            }

            public String getCreatedBy() {
                return createdBy;
            }

            public String getUpdatedBy() {
                return updatedBy;
            }

            public String getGuid() {
                return guid;
            }

            public void setCreatedTimestamp(final Date createdTimestamp) {
                this.createdTimestamp = createdTimestamp;
            }

            public void setUpdatedTimestamp(final Date updatedTimestamp) {
                this.updatedTimestamp = updatedTimestamp;
            }

            public void setCreatedBy(final String createdBy) {
                this.createdBy = createdBy;
            }

            public void setUpdatedBy(final String updatedBy) {
                this.updatedBy = updatedBy;
            }

            public void setGuid(final String guid) {
                this.guid = guid;
            }
        };

        // Object transformation
        Object [] objarr = ObjectUtil.toObjectArray(auditable);
        assertEquals("123", objarr[0]);
        assertEquals("3", objarr[1]);
        assertNull(objarr[2]);
        assertNull(objarr[3]);
        assertEquals("admin1", objarr[4]);
        assertEquals("admin2", objarr[5]);
        assertEquals("GUID-123", objarr[6]);

    }

    @Test
    public void testToObjectArrayAbstractPersistentCollection() throws Exception {

        final PersistentCollection collection = context.mock(PersistentCollection.class, "auditable");

        // Object transformation
        Object [] objarr = ObjectUtil.toObjectArray(new Pair<String, PersistentCollection>("first", collection));
        assertEquals("Static: serialVersionUID", objarr[0]);
        assertEquals("first", objarr[1]);
        assertEquals("Collection: second (H)", objarr[2]);

        context.assertIsSatisfied();

    }


}
