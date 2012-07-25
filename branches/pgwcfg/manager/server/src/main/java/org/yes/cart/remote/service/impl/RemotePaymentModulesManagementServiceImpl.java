/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.remote.service.RemotePaymentModulesManagementService;
import org.yes.cart.service.payment.PaymentModulesManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * Remote service to manage payment gateways and his parameters.
 * Delete and add parameters operation are prohibited for security reason. This two operations are very rare
 * and can not be performed without tech personal support.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/22/12
 * Time: 8:59 PM
 */
public class RemotePaymentModulesManagementServiceImpl implements RemotePaymentModulesManagementService {


    private PaymentModulesManager paymentModulesManager;
    
    
    /** {@inheritDoc}*/
    public List<Pair<String, String>> getAllowedPaymentGateways(final String lang) {
        
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true);
        final List<Pair<String, String>> rez = new ArrayList<Pair<String, String>>(descriptors.size());
        for (PaymentGatewayDescriptor descr :  descriptors) {
            final PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway(descr.getLabel());
            rez.add(new Pair<String, String>(
                    paymentGateway.getLabel(),
                    descr.getName()
            ));
        }
        return rez;

    }

    /** {@inheritDoc}*/
    public List<Pair<String, String>> getAvailablePaymentGateways(final String lang) {

        final List<Pair<String, String>> allowed = getAllowedPaymentGateways(lang);
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false);

        
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters(String gatewayLabel, String lang) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateConfigurationParameter(String gatewayLabel, String paramaterLabel, String parameterValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void allowPaymentGateway(String label) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void disallowPaymentGateway(String label) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* 

  public List<PaymentGateway> getAllowedPaymentGateways() {
      final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true);
      final List<PaymentGateway> rez = new ArrayList<PaymentGateway>(descriptors.size());
      for (PaymentGatewayDescriptor descr :  descriptors) {
          //rez.add();
      }
      return rez;
  }

  public List<PaymentGateway> getAvailablePaymentGateways() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void updateConfigurationParameter(String gatewayLabel, String paramaterLabel, String parameterValue) {
      //To change body of implemented methods use File | Settings | File Templates.
  }

  public void allowPaymentGateway(String label) {
      //To change body of implemented methods use File | Settings | File Templates.
  }

  public void disallowPaymentGateway(String label) {
      //To change body of implemented methods use File | Settings | File Templates.
  }  */
}
