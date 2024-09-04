

package tech.zephon.sslclient.ssl.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import tech.zephon.sslclient.common.logger.Logger;


public class SSLClient2 {
    
    private static final Logger LOGGER = Logger.getLogger(SSLClient.class.getName());
    
    private HttpsURLConnection connection_ = null;
    private SSLContext sslContext = null;
    private int _responseCode = -1;
    
    public SSLClient2(SSLContext sslContex) {
        
        this.sslContext = sslContex;
        LOGGER.debug("sslContext:" + sslContext);
    }

    
    /**
     *
     * @param url
     * @param method
     * @param message
     * @param msgtype json or xml
     * @return
     */
    public String sendRequest(URL url, String method, String message, String msgtype) {

        String response = "ERROR Invalid Method";

        if ("POST".equalsIgnoreCase(method))
        {
            boolean connected = setPostSSLConnection(url, method, msgtype);
            if (connected)
            {
                try 
                {
                    //Sending the request to Remote server
                    OutputStreamWriter writer = new OutputStreamWriter(connection_.getOutputStream());
                    writer.write(message);
                    writer.flush();
                    writer.close();
                    _responseCode = connection_.getResponseCode();
                    LOGGER.info("Response Code :" + _responseCode);
                    // reading the response
                    if (_responseCode == HttpURLConnection.HTTP_OK) 
                    { // success
                        InputStreamReader reader = new InputStreamReader(connection_.getInputStream());
                        StringBuilder buf = new StringBuilder();
                        char[] cbuf = new char[2048];
                        int num;
                        while (-1 != (num = reader.read(cbuf))) {
                            buf.append(cbuf, 0, num);
                        }
                        response = buf.toString();
                    }
                    

                } catch (Exception e) {
                    LOGGER.error("Exception:" + e.getMessage(),e);
                    response = "Exception:" + e.getMessage();
                }
                finally
                {
                    releaseConnection();
                }
                return response;
            }
        }
        
        if ("GET".equalsIgnoreCase(method))
        {
            boolean connected = this.setGetSSLConnection(url, method, message, msgtype);
            if (connected)
            {
                try
                {
                    _responseCode = connection_.getResponseCode();
                    LOGGER.info("Response Code :" + _responseCode);
                    if (_responseCode == HttpURLConnection.HTTP_OK) { // success
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection_.getInputStream()));
                        String inputLine;
                        StringBuffer resp = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            resp.append(inputLine);
                        }
                        in.close();
                        return resp.toString();
                    }
                }
                catch(Exception e)
                {
                    LOGGER.error("Exception:" + e.getMessage(),e);
                    response = "Exception:" + e.getMessage();
                }
                finally
                {
                    releaseConnection();
                }
                return response;
            }
        }
        
        return response;
    }
    
    private boolean setPostSSLConnection(URL url, String method, String msgtype) {
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        try {
            connection_ = (HttpsURLConnection) url.openConnection();
            connection_.setSSLSocketFactory(sslContext.getSocketFactory());
            connection_.setDoOutput(true);
            connection_.setRequestMethod(method);
            connection_.setRequestProperty("Content-Type", msgtype /*"text/xml" */);
            connection_.connect();
            return true;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while establishing connection to SSL server. Error :" + e.getMessage(),e);
            if (connection_ != null)
            {
                connection_.disconnect();
                connection_ = null;
            }
            return false;
        }
    }
    
    public boolean setGetSSLConnection(URL url, String method, String message, String msgtype) {
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        
        try
        {
            connection_ = (HttpsURLConnection) url.openConnection();
            connection_.setSSLSocketFactory(sslContext.getSocketFactory());
            connection_.setRequestMethod(method);
            connection_.setRequestProperty("Content-Type", msgtype /*"text/xml" */);
            connection_.connect();
        }
        catch(Exception e)
        {
            LOGGER.error("Exception occurred while establishing connection to SSL server. Error :" + e.getMessage(),e);
            if (connection_ != null)
            {
                connection_.disconnect();
                connection_ = null;
            }
            return false;
        }
        
        return true;
    }

    public void releaseConnection() {
        if (connection_ != null) {
            try
            {
                connection_.disconnect();
                connection_ = null;
            }
            catch(Exception e)
            {
                
            }
        }

    }
}
