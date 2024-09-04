

package tech.zephon.sslclient.testers;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import tech.zephon.sslclient.common.config.AppConfig;
import tech.zephon.sslclient.common.logger.Logger;
import tech.zephon.sslclient.ssl.client.SSLClient2;
import tech.zephon.sslclient.ssl.config.SSLContextConfig2;


public class ClientApp2 {
    
    private static final Logger LOGGER = Logger.getLogger(ClientApp2.class.getName());

    public static void main(String[] args) {
        
        LOGGER.debug("In main");
        System.setProperty("javax.net.debug", "ssl:handshake");
        Properties config = new Properties();
        //Load (String)configuration
        try {
            //String currentPath = System.getProperty("user.dir");
            config.load(new FileInputStream(new File("system.properties")));
            LOGGER.debug("Config:" + config);
        } catch (Exception e) {
            LOGGER.error("Exception in reading properties file : system.properties:" + e.getMessage(),e);
            System.exit(-1);
        }
        
        AppConfig appConfig = new AppConfig(config);
        
        SSLContextConfig2 sslConfig = new SSLContextConfig2();
        SSLContext sslContext = null;
        
        try
        {
            sslContext = sslConfig.createSslContext(appConfig);
        }
        catch(Exception e)
        {
            LOGGER.error("Exception creating SSLContextConfig:" + e.getMessage(),e);
            System.exit(-1);
        }
        
        SSLClient2 sslClient = new SSLClient2(sslContext);
        try
        {
            String strurl = "https://spring.testserver.org:8443/server/test";
            URL url = new URL(strurl);
            String method = "GET";
            String message = "";// can be json or xml
            String msgtype = "text/xml";
            String response = sslClient.sendRequest(url, method, message, msgtype);
            LOGGER.debug("GET Resp:" + response);
        }
        catch(Exception e)
        {
            LOGGER.error("Exception running get request:" + e.getMessage(),e);
            System.exit(-1);
        }
        
        try
        {
            String strurl = "https://spring.testserver.org:8443/server/test";
            URL url = new URL(strurl);
            String method = "POST";
            String message = "your json message body";//can be a json or xml message
            String msgtype = "text/xml";
            String response = sslClient.sendRequest(url, method, message, msgtype);
            LOGGER.debug("POST Resp:" + response);
        }
        catch(Exception e)
        {
            LOGGER.error("Exception running post request:" + e.getMessage(),e);
            System.exit(-1);
        }
        
    }
}
