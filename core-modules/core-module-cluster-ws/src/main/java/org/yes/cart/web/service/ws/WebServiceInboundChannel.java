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

package org.yes.cart.web.service.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

/**
 * User: denispavlov
 * Date: 17/06/2015
 * Time: 13:23
 */
@WebService
@BindingType(value=javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING)
public interface WebServiceInboundChannel {

    /**
     * Ping call to test if WS is accessible
     */
    @WebMethod
    @WebResult(name = "ping")
    void ping();

    /**
     * Receive incoming message via WS.
     *
     * @param inbound incoming message
     *
     * @return response to this message
     */
    @WebMethod
    @WebResult(name = "response")
    WsMessage accept(@WebParam(name = "inbound") WsMessage inbound);


}
