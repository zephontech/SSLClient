package tech.zephon.sslclient.testers;

import tech.zephon.sslclient.common.config.ApplicationConfig;
import tech.zephon.sslclient.ssl.client.SSLClient;
import java.net.URL;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import tech.zephon.sslclient.common.logger.Logger;

public class ClientApp {

    private static final Logger LOGGER = Logger.getLogger(ClientApp.class.getName());

    public static void main(String[] args) {
        LOGGER.debug("In main");
        //System.setProperty("javax.net.debug", "ssl:handshake");
        Properties config = new Properties();
        //Load (String)configuration
        try {
            //String currentPath = System.getProperty("user.dir");
            config.load(new FileInputStream(new File("system.properties")));
            LOGGER.debug("Config:" + config);
        } catch (Exception e) {
            LOGGER.error("Exception in reading properties file : system.properties",e);
            System.exit(-1);
        }

        ApplicationConfig ac = ApplicationConfig.getInstance();
        ac.setKEYSTOREPATH((String) config.getProperty("KEYSTOREPATH"));
        ac.setTRUSTSTOREPATH((String) config.getProperty("TRUSTSTOREPATH"));
        ac.setKEYSTOREPW((String) config.getProperty("KEYSTOREPW"));
        ac.setTRUSTSTOREPW((String) config.getProperty("TRUSTSTOREPW"));
        ac.setKEYPASS((String) config.getProperty("KEYPASS"));
        ac.setKeystoreType((String) config.getProperty("keystoreType"));
        ac.setTrustAllCertificate((String) config.getProperty("trustAllCertificate"));
        ac.setKeymanageralgorithm((String) config.getProperty("keymanageralgorithm"));

        try {
            //A SOAP web service call
            SSLClient sslClient = SSLClient.getSSLClient();

            /*
            String strurl ="https://192.168.56.102:7001/AmpsSoapService/FPMA_ws_amps_Port";//you can add all the urls in config file
            URL url = new URL(strurl);
            String method = "POST";
            String message = "your soap message body";//This must be a complete xml message
            String msgtype = "text/xml";
            String response = sslClient.sendRequest(url, method, message, msgtype);
            System.out.println("Resp:" + response);
            if (1 ==1 )
                return;
             */
            //A RESTFul GET web service call	
            String strurl = "https://spring.testserver.org:8443/server/test";
            URL url = new URL(strurl);
            String method = "GET";
            String message = "";// can be json or xml
            String msgtype = "text/xml";
            String response = sslClient.sendRequest(url, method, message, msgtype);
            LOGGER.debug("GET Resp:" + response);

            //A RESTFul POST web service call			
            strurl = "https://spring.testserver.org:8443/server/test";
            url = new URL(strurl);
            method = "POST";
            message = "your json message body";//can be a json or xml message
            msgtype = "text/xml";
            response = sslClient.sendRequest(url, method, message, msgtype);
            LOGGER.debug("POST Resp:" + response);
        } catch (Exception e) {
            LOGGER.error("Error doing post:" + e.getMessage(),e);
        }
    }

}
