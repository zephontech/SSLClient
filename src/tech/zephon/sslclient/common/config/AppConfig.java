package tech.zephon.sslclient.common.config;

import java.util.Properties;

public class AppConfig {

    private String KEYSTOREPATH = null;
    private String TRUSTSTOREPATH = null;
    private String KEYSTOREPW = null;
    private String TRUSTSTOREPW = null;
    private String KEYPASS = null;
    private String HTTPS_SERV_URL = null;
    private String trustAllCertificate = "false";// DEFAULT VALUE
    private String keystoreType = "JKS";// DEFAULT VALUE
    private String regex = null;
    private String keymanageralgorithm = null;
    private int mqreadinterval = 1;
    private int httpsfialureinterval = 5;
    private int prodissueinterval = 1;

    public AppConfig(Properties config) {
        
        this.KEYSTOREPATH = config.getProperty("KEYSTOREPATH");
        this.TRUSTSTOREPATH = config.getProperty("TRUSTSTOREPATH");
        this.KEYSTOREPW = config.getProperty("KEYSTOREPW");
        this.TRUSTSTOREPW = config.getProperty("TRUSTSTOREPW");
        this.KEYPASS = config.getProperty("KEYPASS");
        this.keystoreType = config.getProperty("keystoreType");
        this.trustAllCertificate = config.getProperty("trustAllCertificate");
        this.keymanageralgorithm = config.getProperty("keymanageralgorithm");
        
    }

    public String getKEYSTOREPATH() {
        return KEYSTOREPATH;
    }

    public void setKEYSTOREPATH(String KEYSTOREPATH) {
        this.KEYSTOREPATH = KEYSTOREPATH;
    }

    public String getTRUSTSTOREPATH() {
        return TRUSTSTOREPATH;
    }

    public void setTRUSTSTOREPATH(String TRUSTSTOREPATH) {
        this.TRUSTSTOREPATH = TRUSTSTOREPATH;
    }

    public String getKEYSTOREPW() {
        return KEYSTOREPW;
    }

    public void setKEYSTOREPW(String KEYSTOREPW) {
        this.KEYSTOREPW = KEYSTOREPW;
    }

    public String getTRUSTSTOREPW() {
        return TRUSTSTOREPW;
    }

    public void setTRUSTSTOREPW(String TRUSTSTOREPW) {
        this.TRUSTSTOREPW = TRUSTSTOREPW;
    }

    public String getKEYPASS() {
        return KEYPASS;
    }

    public void setKEYPASS(String KEYPASS) {
        this.KEYPASS = KEYPASS;
    }

    public String getHTTPS_SERV_URL() {
        return HTTPS_SERV_URL;
    }

    public void setHTTPS_SERV_URL(String HTTPS_SERV_URL) {
        this.HTTPS_SERV_URL = HTTPS_SERV_URL;
    }

    public String getTrustAllCertificate() {
        return trustAllCertificate;
    }

    public void setTrustAllCertificate(String trustAllCertificate) {
        this.trustAllCertificate = trustAllCertificate;
    }

    public String getKeystoreType() {
        return keystoreType;
    }

    public void setKeystoreType(String keystoreType) {
        this.keystoreType = keystoreType;
    }

    public String getKeymanageralgorithm() {
        return keymanageralgorithm;
    }

    public void setKeymanageralgorithm(String keymanageralgorithm) {
        this.keymanageralgorithm = keymanageralgorithm;
    }
    
    

}
