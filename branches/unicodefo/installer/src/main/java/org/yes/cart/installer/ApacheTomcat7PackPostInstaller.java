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
