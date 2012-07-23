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

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">shyiko</a>
 */
public class ApacheTomcat7ConfigurerTest {

  private File resourcesDirectory = new File("src/test/resources/tomcat7");

  @Test
  public void testConfigureOnLinux() throws Exception {
    String os = System.setProperty("os.name", "linux");
    try {
      testConfigure(80, 443, null, null, "http-only.server.xml");
    } finally {
      System.setProperty("os.name", os);
    }
  }

  @Test
  public void testConfigureOnWindows() throws Exception {
    String os = System.setProperty("os.name", "windows");
    try {
      testConfigure(80, 443, null, null, "http-only.server.xml");
    } finally {
      System.setProperty("os.name", os);
    }
  }

  @Test
  public void testConfigureSSL() throws Exception {
    testConfigure(80, 443, "${user.home}/.keystore", "keypass", "http+ssl.server.xml");
  }

  private void testConfigure(int httpPort, int httpsPort, String keyStoreFile, String keyStorePassword,
                             String expectedServerXMLFileName) throws Exception {
    File tempDir = prepareInstallationDirectory();
    ApacheTomcat7Configurer configurer = new ApacheTomcat7Configurer(tempDir.getAbsolutePath());
    configurer.setJdkHome("/usr/lib/jvm/java-6-sun");
    configurer.setCatalinaOptions("-Xms256m -Xmx1024m -XX:MaxPermSize=256m");
    configurer.setHttpPort(httpPort);
    configurer.setHttpsPort(httpsPort);
    configurer.setSslKeyStoreFile(keyStoreFile);
    configurer.setSslKeyStorePassword(keyStorePassword);
    configurer.configure();
    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    String setEnvFileName = "bin" + File.separator + (isWindows ? "setenv.bat" : "setenv.sh");
    assertFilesAreEqualIgnoringWhitespaces(
            new File(resourcesDirectory, setEnvFileName),
            new File(tempDir, setEnvFileName)
    );
    assertFilesAreEqualIgnoringWhitespaces(
            new File(resourcesDirectory, "conf" + File.separator + expectedServerXMLFileName),
            new File(tempDir, "conf" + File.separator + "server.xml")
    );
  }

  private File prepareInstallationDirectory() throws IOException {
    File tempDir = Files.createTempDir();
    assertTrue(new File(tempDir, "bin").mkdir());
    assertTrue(new File(tempDir, "conf").mkdir());
    Files.copy(
            new File(resourcesDirectory, "conf" + File.separator + "server.xml"),
            new File(tempDir, "conf" + File.separator + "server.xml")
    );
    return tempDir;
  }

  private void assertFilesAreEqualIgnoringWhitespaces(File expected, File actual) throws Exception {
    String assertionMessage = "Expected content in " + expected + ", Actual content in " + actual;
    List<String> expectedLines = Files.readLines(expected, Charset.forName("UTF-8"));
    List<String> actualLines = Files.readLines(actual, Charset.forName("UTF-8"));
    assertEquals(assertionMessage, expectedLines.size(), actualLines.size());
    for (int i = 0, end = expectedLines.size(); i < end; i++) {
      String expectedLine = expectedLines.get(i);
      String actualLine = actualLines.get(i);
      assertEquals(assertionMessage, expectedLine.trim(), actualLine.trim());
    }
  }
}
