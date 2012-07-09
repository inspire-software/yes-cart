package org.yes.cart.installer;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Class responsible for Apache Tomcat 7 post-installation configuration.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">shyiko</a>
 */
public class ApacheTomcat7Configurer {

  private String tomcatHome;
  private String jdkHome;
  private int httpPort;
  private int httpsPort;
  private String sslKeyStoreFile;
  private String sslKeyStorePassword;
  private String catalinaOptions = "-Xms256m -Xmx1024m -XX:MaxPermSize=256m";

  public ApacheTomcat7Configurer(String tomcatHome) {
    this.tomcatHome = tomcatHome;
  }

  public void setJdkHome(String jdkHome) {
    this.jdkHome = jdkHome;
  }

  public void setHttpPort(int httpPort) {
    this.httpPort = httpPort;
  }

  public void setHttpsPort(int httpsPort) {
    this.httpsPort = httpsPort;
  }

  public void setSslKeyStoreFile(String sslKeyStoreFile) {
    this.sslKeyStoreFile = sslKeyStoreFile;
  }

  public void setSslKeyStorePassword(String sslKeyStorePassword) {
    this.sslKeyStorePassword = sslKeyStorePassword;
  }

  public void setCatalinaOptions(String catalinaOptions) {
    this.catalinaOptions = catalinaOptions;
  }

  public void configure() throws IOException {
    generateSetEnv();
    configureHTTP();
    configureSSL();
  }

  private void configureHTTP() throws IOException {
    File serverXMLFile = new File(tomcatHome, "conf" + File.separator + "server.xml");
    Files.write(
            Files.toString(serverXMLFile, Charset.forName("UTF-8")).
                    replace("8080", String.valueOf(httpPort)),
            serverXMLFile, Charset.forName("UTF-8")
    );
  }

  private void configureSSL() throws IOException {
    if (sslKeyStoreFile == null) {
      return;
    }
    File serverXMLFile = new File(tomcatHome, "conf" + File.separator + "server.xml");
    Files.write(
            Files.toString(serverXMLFile, Charset.forName("UTF-8")).
                    replace("        <Connector port=\"8009\" protocol=\"AJP/1.3\" redirectPort=\"8443\" />",
                            "        <Connector port=\"8009\" protocol=\"AJP/1.3\" redirectPort=\"8443\" />\n" +
                                    "        <Connector port=\"" + httpsPort + "\" protocol=\"HTTP/1.1\" SSLEnabled=\"true\"\n" +
                                    "               maxThreads=\"150\" scheme=\"https\" secure=\"true\"\n" +
                                    "               clientAuth=\"false\" sslProtocol=\"TLS\"\n" +
                                    "               keystoreFile=\"" + sslKeyStoreFile + "\" " +
                                    "keystorePass=\"" + Strings.nullToEmpty(sslKeyStorePassword) + "\" />"
                    ).replace("8443", Integer.toString(httpsPort)),
            serverXMLFile, Charset.forName("UTF-8")
    );
  }

  private void generateSetEnv() throws IOException {
    boolean isWindows = isWindows();
    File setEnvFile = new File(tomcatHome, "bin" + File.separator + (isWindows ? "setenv.bat" : "setenv.sh"));
    Files.write(generateSetEnvContent(
            ImmutableMap.<String, String>builder().
                    put("CATALINA_OPTS", catalinaOptions).
                    put("JAVA_HOME", jdkHome).build(),
            isWindows
    ), setEnvFile, Charset.forName("UTF-8"));
  }

  private String generateSetEnvContent(Map<String, String> envVariables, boolean isWindows) {
    String prefix = isWindows ? "set " : "export ";
    String lineSeparator = System.getProperty("line.separator");
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> entry : envVariables.entrySet()) {
      sb.append(prefix).append(entry.getKey()).append("=").append(entry.getValue()).append(lineSeparator);
    }
    return sb.toString();
  }

  private boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }
}
