package org.yes.cart.remote.service.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yes.cart.domain.dto.DtoPaymentGatewayInfo;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayDescriptorImpl;
import org.yes.cart.service.payment.PaymentModulesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

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
        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGatewaysDescriptors(false);
            will(
                    returnValue(
                            new ArrayList<PaymentGatewayDescriptor>() {{
                                add(new PaymentGatewayDescriptorImpl("one", "one", "one", "one"));
                            }}
                    )
            );
        }});
        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGatewaysDescriptors(true); //all
            will(
                    returnValue(
                            new ArrayList<PaymentGatewayDescriptor>() {{
                                add(new PaymentGatewayDescriptorImpl("one", "one", "one", "one"));
                                add(new PaymentGatewayDescriptorImpl("two", "two", "two", "two"));
                            }}
                    )
            );
        }});
        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGateway("one");
            will( returnValue(  null     )       );
        }});

        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGateway("two");
            will( returnValue(  null     )       );
        }});

        RemotePaymentModulesManagementServiceImpl service = new RemotePaymentModulesManagementServiceImpl(
                paymentModulesManager,
                null);
        List<DtoPaymentGatewayInfo> rez = service.getPaymentGateways("ru");
        assertEquals(2, rez.size());
        
        int cntActive = 0;
        int cntPassive = 0;
        for (DtoPaymentGatewayInfo dt : rez) {
            if (dt.isActive()) {
                cntActive++;
            } else {
                cntPassive++;
            }
        }
        assertEquals(1, cntActive);
        assertEquals(1, cntPassive);

    }

    @Test
    public void testGetPaymentGateways1() throws Exception {

        paymentModulesManager = mockery.mock(PaymentModulesManager.class);
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
                                add(new PaymentGatewayDescriptorImpl("one", "one", "one", "one"));
                                add(new PaymentGatewayDescriptorImpl("two", "two", "two", "two"));
                            }}
                    )
            );
        }});
        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGateway("one");
            will( returnValue(  null     )       );
        }});

        mockery.checking(new Expectations() {{
            allowing(paymentModulesManager).getPaymentGateway("two");
            will( returnValue(  null     )       );
        }});

        RemotePaymentModulesManagementServiceImpl service = new RemotePaymentModulesManagementServiceImpl(
                paymentModulesManager,
                null);
        List<DtoPaymentGatewayInfo> rez = service.getPaymentGateways("ru");
        assertEquals(2, rez.size());
        
        int cntActive = 0;
        int cntPassive = 0;
        for (DtoPaymentGatewayInfo dt : rez) {
            if (dt.isActive()) {
                cntActive++;
            } else {
                cntPassive++;
            }
        }

        assertEquals(0, cntActive);
        assertEquals(2, cntPassive);

    }





}
