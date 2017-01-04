package org.yes.cart.service.order.impl;

import org.junit.Test;
import org.yes.cart.service.order.DeliveryBucket;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 18/02/2016
 * Time: 16:01
 */
public class DeliveryBucketImplTest {

    @Test
    public void testConstructor() throws Exception {

        try {
            new DeliveryBucketImpl(null, "001");
            fail("Must not accept nulls");
        } catch (IllegalArgumentException iae) {
            // ok
        }

        try {
            new DeliveryBucketImpl("D1", null);
            fail("Must not accept nulls");
        } catch (IllegalArgumentException iae) {
            // ok
        }

    }

    @Test
    public void testCompareTo() throws Exception {

        assertEquals(0, new DeliveryBucketImpl("D1", "S001").compareTo(new DeliveryBucketImpl("D1", "S001")));
        assertTrue(0 > new DeliveryBucketImpl("D1", "S001").compareTo(new DeliveryBucketImpl("D2", "S001")));
        assertTrue(0 < new DeliveryBucketImpl("D2", "S001").compareTo(new DeliveryBucketImpl("D1", "S001")));

        assertFalse(0 == new DeliveryBucketImpl("D1", "S002").compareTo(new DeliveryBucketImpl("D1", "S001")));
        assertFalse(0 == new DeliveryBucketImpl("D1", "S001").compareTo(new DeliveryBucketImpl("D1", "S002")));
        assertTrue(0 > new DeliveryBucketImpl("D1", "S001").compareTo(new DeliveryBucketImpl("D2", "S002")));
        assertTrue(0 < new DeliveryBucketImpl("D1", "S002").compareTo(new DeliveryBucketImpl("D2", "S001")));
        assertTrue(0 > new DeliveryBucketImpl("D2", "S001").compareTo(new DeliveryBucketImpl("D1", "S002")));
        assertTrue(0 < new DeliveryBucketImpl("D2", "S002").compareTo(new DeliveryBucketImpl("D1", "S001")));

        assertTrue(0 == new DeliveryBucketImpl("D1", "S001", "Q1").compareTo(new DeliveryBucketImpl("D1", "S001", "Q1")));
        assertTrue(0 > new DeliveryBucketImpl("D1", "S001").compareTo(new DeliveryBucketImpl("D1", "S001", "Q1")));
        assertTrue(0 < new DeliveryBucketImpl("D1", "S001", "Q1").compareTo(new DeliveryBucketImpl("D1", "S001")));


        final SortedMap<DeliveryBucket, Object> buckets = new TreeMap<DeliveryBucket, Object>();
        buckets.put(new DeliveryBucketImpl("D1", "S001"), new Object());
        buckets.put(new DeliveryBucketImpl("D2", "S001"), new Object());
        buckets.put(new DeliveryBucketImpl("D3", "S001"), new Object());
        buckets.put(new DeliveryBucketImpl("D3", "S001", "Q1"), new Object());
        buckets.put(new DeliveryBucketImpl("D4", "S001"), new Object());
        buckets.put(new DeliveryBucketImpl("D5", "S001"), new Object());
        buckets.put(new DeliveryBucketImpl("D1", "S002"), new Object());
        buckets.put(new DeliveryBucketImpl("D2", "S002"), new Object());
        buckets.put(new DeliveryBucketImpl("D3", "S002", "Q1"), new Object());
        buckets.put(new DeliveryBucketImpl("D3", "S002", "Q2"), new Object());
        buckets.put(new DeliveryBucketImpl("D4", "S002"), new Object());
        buckets.put(new DeliveryBucketImpl("D5", "S002"), new Object());

        final List<String> order = new ArrayList<String>(Arrays.asList(
                "S001_D1_", "S001_D2_", "S001_D3_",   "S001_D3_Q1", "S001_D4_", "S001_D5_",
                "S002_D1_", "S002_D2_", "S002_D3_Q1", "S002_D3_Q2", "S002_D4_", "S002_D5_"
        ));

        for (final DeliveryBucket bucket : buckets.keySet()) {
            assertEquals(bucket.toString(), order.remove(0)); // check that the ordering is correct
        }

        assertTrue(order.isEmpty()); // make sure all entries are accounted for


    }

    @Test
    public void testEquals() throws Exception {

        assertEquals(new DeliveryBucketImpl("D1", ""), new DeliveryBucketImpl("D1", ""));
        assertEquals(new DeliveryBucketImpl("D1", "S001"), new DeliveryBucketImpl("D1", "S001"));
        assertEquals(new DeliveryBucketImpl("D1", "S002"), new DeliveryBucketImpl("D1", "S002"));
        assertEquals(new DeliveryBucketImpl("D1", "S002", "Q1"), new DeliveryBucketImpl("D1", "S002", "Q1"));
        assertFalse(new DeliveryBucketImpl("D1", "").equals(new DeliveryBucketImpl("D2", "")));
        assertFalse(new DeliveryBucketImpl("D1", "S001").equals(new DeliveryBucketImpl("D1", "S002")));
        assertFalse(new DeliveryBucketImpl("D2", "S001").equals(new DeliveryBucketImpl("D2", "S002")));
        assertFalse(new DeliveryBucketImpl("D2", "S001").equals(new DeliveryBucketImpl("D1", "S001")));
        assertFalse(new DeliveryBucketImpl("D1", "S001").equals(new DeliveryBucketImpl("D2", "S001")));
        assertFalse(new DeliveryBucketImpl("D1", "S001", "Q1").equals(new DeliveryBucketImpl("D2", "S001")));
        assertFalse(new DeliveryBucketImpl("D1", "S001").equals(new DeliveryBucketImpl("D2", "S001", "Q1")));

    }
}