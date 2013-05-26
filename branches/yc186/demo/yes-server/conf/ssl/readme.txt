This sslkey is a selfsigned key for purpose of running Demo and not to be used in any other way.
The password is: ycselfsigned
Details: Yes Cart Demo, GB

If you need to regenerate a self-signed sslkey the steps are:
1. Create a new certificate file (using commandline)

yc$ $JAVA_HOME/bin/keytool -genkey -alias tomcat -keyalg RSA -keystore $YC_HOME/demo/yes-server/conf/ssl/sslkey
# NOTE: remember that there may be issues with files ownership on UNIX so remember to chown the file correctly

2. Follow the steps in key tool and put "Yes Cart Demo" in all fields relating to organisation, "N/A" in city
and state, and "GB" into country. Please use password "ycselfsigned" for ALL steps that require password input
(There are two but due to tomcat implementation details this has to be the same password).

3. If you followed all the steps above exactly that should be all. However if you have not you will need to
modify the server.xml connector tag and change keystoreFile and keystorePass attributes on the SSL connector
(i.e. Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true").
This should be something like this after the changes:

    <Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"
               maxThreads="150" scheme="https" secure="true"
               keystoreFile="conf/ssl/sslkey" keystorePass="ycselfsigned"
               clientAuth="false" sslProtocol="TLS" />

IMPORTANT: if you are running tomcat behind Apache HTTP or use native tomcat libraries the above approach will not
work as you need to configure SSL on the AJP Connector. Here is the link to full article on HTTPS for tomcat 6:
http://tomcat.apache.org/tomcat-6.0-doc/ssl-howto.html