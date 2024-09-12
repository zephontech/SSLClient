

package tech.zephon.sslclient.testers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyStore;
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import tech.zephon.sslclient.common.config.AppConfig;
import tech.zephon.sslclient.common.logger.Logger;


public class ClientApp3 {
    
    private static final Logger LOGGER = Logger.getLogger(ClientApp2.class.getName());
    
    private static String enableXml
        = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:fpma=\"http://dla.mil/FPMA.ws:amps\"> "
        + "<soapenv:Header/> " + "<soapenv:Body> " + "<fpma:enable> " + "<UserName>JOEBLOW</UserName> "
        + " </fpma:enable> " + "</soapenv:Body> " + "</soapenv:Envelope>";
    
    private static String strurl = "https://someurl/ws/Services/Services_Port";

    public static void main(String[] args) throws java.lang.Exception {
        //System.setProperty("javax.net.debug", "ssl:handshake");
        
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
        
        
        String serverAddress = "fpisstg.logisticsinformationservice.dla.mil";
        int serverPort = 443;

        // load client cert from store
        KeyStore client = KeyStore.getInstance("JKS");
        FileInputStream clientIS = new FileInputStream(appConfig.getKEYSTOREPATH());
        client.load(clientIS, appConfig.getKEYSTOREPW().toCharArray());

        // initialize KeyManagerFactory
        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(client, appConfig.getKEYSTOREPW().toCharArray());

        // load trust store
        KeyStore trust = KeyStore.getInstance("JKS");
        FileInputStream trustIS = new FileInputStream(appConfig.getTRUSTSTOREPATH());
        trust.load(trustIS, appConfig.getTRUSTSTOREPW().toCharArray());

        // initialize TrustManagerFactory
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(trust);

        // initialize SSLContext
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(keyFactory.getKeyManagers(), trustFactory.getTrustManagers(), null);

        // create socket factory
        SSLSocketFactory socketFactory = context.getSocketFactory();
        
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        URL url = new URL(strurl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(context.getSocketFactory());
        connection.setDoOutput(true);
        String msgtype = "text/xml";
        connection.setRequestProperty("Content-Type", msgtype /*"text/xml" */);
        connection.connect();
        // connect to server
        SSLSocket socket = (SSLSocket) socketFactory.createSocket(serverAddress, serverPort);
        
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(enableXml);
        writer.flush();
        writer.close();
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        StringBuilder buf = new StringBuilder();
        char[] cbuf = new char[2048];
        int num;
        while (-1 != (num = reader.read(cbuf))) {
            buf.append(cbuf, 0, num);
        }
        String response = buf.toString();
        System.out.println("resdp:" + response);
        
    }

}
