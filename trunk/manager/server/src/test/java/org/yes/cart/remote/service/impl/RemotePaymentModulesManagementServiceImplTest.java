package org.yes.cart.remote.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yes.cart.domain.dto.DtoPaymentGatewayInfo;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayDescriptorImpl;
import org.yes.cart.service.payment.PaymentModulesManager;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/29/12
 * Time: 12:34 PM
 */
@RunWith(org.jmock.integration.junit4.JMock.class)
public class RemotePaymentModulesManagementServiceImplTest {

    private Mockery mockery = new JUnit4Mockery();

    private PaymentModulesManager paymentModulesManager;



    @Test
    public void testGetPaymentGateways0() throws Exception {

        paymentModulesManager = mockery.mock(PaymentModulesManager.class);
        final PaymentGateway pg1 = mockery.mock(PaymentGateway.class, "pg1");
        final PaymentGateway pg2 = mockery.mock(PaymentGateway.class, "pg2");

        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGatewaysDescriptors(false);
            will(
                    returnValue(
                            new ArrayList<PaymentGatewayDescriptor>() {{
                                add(new PaymentGatewayDescriptorImpl("one", "one", "one"));
                            }}
                    )
            );
        }});
        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGatewaysDescriptors(true); //all
            will(
                    returnValue(
                            new ArrayList<PaymentGatewayDescriptor>() {{
                                add(new PaymentGatewayDescriptorImpl("one", "one", "one"));
                                add(new PaymentGatewayDescriptorImpl("two", "two", "two"));
                            }}
                    )
            );
        }});
        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGateway("one");
            will( returnValue(  pg1     )       );
            allowing(pg1).getName("ru");
            will(returnValue("pg1 name"));
        }});

        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGateway("two");
            will( returnValue(  pg2     )       );
            allowing(pg2).getName("ru");
            will(returnValue("pg2 name"));
        }});

        RemotePaymentModulesManagementServiceImpl service = new RemotePaymentModulesManagementServiceImpl(
                paymentModulesManager,
                null, null);
        List<DtoPaymentGatewayInfo> rez = service.getPaymentGateways("ru");
        assertEquals(2, rez.size());
        
        int cntActive = 0;
        int cntPassive = 0;
        Map<String, DtoPaymentGatewayInfo> dts = new HashMap<String, DtoPaymentGatewayInfo>();
        for (DtoPaymentGatewayInfo dt : rez) {
            if (dt.isActive()) {
                cntActive++;
            } else {
                cntPassive++;
            }
            dts.put(dt.getLabel(), dt);
        }
        assertEquals(1, cntActive);
        assertEquals(1, cntPassive);
        assertTrue(dts.containsKey("one"));
        assertEquals("pg1 name", dts.get("one").getName());
        assertTrue(dts.get("one").isActive());
        assertTrue(dts.containsKey("two"));
        assertEquals("pg2 name", dts.get("two").getName());
        assertFalse(dts.get("two").isActive());

    }

    @Test
    public void testGetPaymentGateways1() throws Exception {

        paymentModulesManager = mockery.mock(PaymentModulesManager.class);
        final PaymentGateway pg1 = mockery.mock(PaymentGateway.class, "pg1");
        final PaymentGateway pg2 = mockery.mock(PaymentGateway.class, "pg2");

        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGatewaysDescriptors(false);
            will(
                    returnValue(
                            Collections.EMPTY_LIST
                    )
            );
        }});
        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGatewaysDescriptors(true); //all
            will(
                    returnValue(
                            new ArrayList<PaymentGatewayDescriptor>() {{
                                add(new PaymentGatewayDescriptorImpl("one", "one", "one"));
                                add(new PaymentGatewayDescriptorImpl("two", "two", "two"));
                            }}
                    )
            );
        }});
        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGateway("one");
            will( returnValue(  pg1     )       );
            allowing(pg1).getName("ru");
            will(returnValue("pg1 name"));
        }});

        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGateway("two");
            will( returnValue(  pg2     )       );
            allowing(pg2).getName("ru");
            will(returnValue("pg2 name"));
        }});

        RemotePaymentModulesManagementServiceImpl service = new RemotePaymentModulesManagementServiceImpl(
                paymentModulesManager,
                null, null);
        List<DtoPaymentGatewayInfo> rez = service.getPaymentGateways("ru");
        assertEquals(2, rez.size());
        
        int cntActive = 0;
        int cntPassive = 0;
        Map<String, DtoPaymentGatewayInfo> dts = new HashMap<String, DtoPaymentGatewayInfo>();
        for (DtoPaymentGatewayInfo dt : rez) {
            if (dt.isActive()) {
                cntActive++;
            } else {
                cntPassive++;
            }
            dts.put(dt.getLabel(), dt);
        }

        assertEquals(0, cntActive);
        assertEquals(2, cntPassive);
        assertTrue(dts.containsKey("one"));
        assertEquals("pg1 name", dts.get("one").getName());
        assertFalse(dts.get("one").isActive());
        assertTrue(dts.containsKey("two"));
        assertEquals("pg2 name", dts.get("two").getName());
        assertFalse(dts.get("two").isActive());

    }





}
