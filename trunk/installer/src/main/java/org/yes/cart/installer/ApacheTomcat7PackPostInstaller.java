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

package org.yes.cart.installer;

import com.google.common.base.Strings;
import com.izforge.izpack.installer.AutomatedInstallData;

import java.io.File;

/**
 * "Apache Tomcat 7" pack post installer responsible for web server configuration based on user input.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">shyiko</a>
 */
public class ApacheTomcat7PackPostInstaller extends PackPostInstaller {

  public ApacheTomcat7PackPostInstaller() {
    super("Apache Tomcat 7");
  }

  @Override
  protected void postInstall(AutomatedInstallData data) throws Exception {
    ApacheTomcat7Configurer configurer = new ApacheTomcat7Configurer(new File(data.getInstallPath()).getCanonicalPath());
    configurer.setJdkHome(new File(data.getVariable("JDKPath")).getCanonicalPath());
    configurer.setHttpPort(Integer.parseInt(data.getVariable("http.port")));
    String httpsPort = data.getVariable("https.port");
    String sslKeyStoreFile = data.getVariable("ssl.keystore.file");
    if (!Strings.isNullOrEmpty(httpsPort) && !Strings.isNullOrEmpty(sslKeyStoreFile)) {
      configurer.setHttpsPort(Integer.parseInt(httpsPort));
      configurer.setSslKeyStoreFile(sslKeyStoreFile);
      configurer.setSslKeyStorePassword(data.getVariable("ssl.keystore.password"));
    }
    configurer.configure();
  }
}
