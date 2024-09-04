package tech.zephon.sslclient.ssl.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import tech.zephon.sslclient.common.config.ApplicationConfig;
import tech.zephon.sslclient.common.logger.Logger;
import javax.net.ssl.KeyManager;

public class SSLContextConfig {

    private static final Logger LOGGER = Logger.getLogger(SSLContextConfig.class.getName());
    private ApplicationConfig config_ = ApplicationConfig.getInstance();
    private TrustManager[] trustAllCerts = null;
    private String keymanageralgorithm = null;

    public SSLContext setupSslContext() {

        SSLContext sslContext = null;
        boolean trustall = false;
        try {

            String keyStorePath = config_.getKEYSTOREPATH();
            String trustStorePath = config_.getTRUSTSTOREPATH();
            String keyStorePw = config_.getKEYSTOREPW();
            String trustStorePw = config_.getTRUSTSTOREPW();
            String keyPass = config_.getKEYPASS();
            String trustAllCertificate = config_.getTrustAllCertificate();
            String keystoreType = config_.getKeystoreType();
            keymanageralgorithm = config_.getKeymanageralgorithm();
            LOGGER.debug("Check trust client");
            trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                }
            };
            LOGGER.debug("Check trust all certs");
            if (trustAllCertificate.equalsIgnoreCase("True")) {
                trustall = true;
            }
            
            if (keystoreType.equalsIgnoreCase("JKS")) {
                LOGGER.debug("JKS Store");
                sslContext = initializeSSLContext(keyStorePath, keyStorePw, trustStorePath, trustStorePw, keyPass, trustall);
                LOGGER.debug("JKS Initialized");
            } else {
                LOGGER.debug("Other Store");
                sslContext = initializeSSLContextP12Cert(keyStorePath, keyStorePw, trustStorePath, trustStorePw, keyPass, trustall);
            }

        } catch (Exception exp) {
            LOGGER.error("ConfigException exception occurred while reading the config file : " + exp.getMessage());
            exp.printStackTrace();
        }
        return sslContext;
    }

    /**
     *
     * @param keyStorePath
     * @param pwKeyStore
     * @param trustStorePath
     * @param pwTrustStore
     * @param keyPass
     * @return
     * @throws Exception
     */
    private SSLContext initializeSSLContext(final String keyStorePath, final String pwKeyStore, final String trustStorePath, final String pwTrustStore, final String keyPass, final boolean trustall) {
        LOGGER.info(" In initializeSSLContext");
        char[] keyStorePw = pwKeyStore.toCharArray();
        char[] trustStorePw = pwTrustStore.toCharArray();
        char[] keyPw = keyPass.toCharArray();
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextInt();

        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException exp) {
            LOGGER.error("KeyStoreException exception occurred while reading the config file : " + exp.getMessage());
        }
        FileInputStream fis = null;
        try {
            try {
                fis = new FileInputStream(keyStorePath);
            } catch (FileNotFoundException exp) {
                LOGGER.error("FileNotFoundException exception occurred " + exp.getMessage());
            }
            try {
                ks.load(fis, keyStorePw);
            } catch (NoSuchAlgorithmException exp) {
                LOGGER.error("NoSuchAlgorithmException exception occurred " + exp.getMessage());
            } catch (CertificateException exp) {
                LOGGER.error("CertificateException exception occurred " + exp.getMessage());
            } catch (IOException exp) {
                LOGGER.error("CertificateException exception occurred " + exp.getMessage());
            }
        } finally {
            if (fis != null)
					try {
                fis.close();
            } catch (IOException exp) {
                LOGGER.error("IOException exception occurred " + exp.getMessage());
            }
        }

        LOGGER.info("[initializeSSLContext] KMF keystorepw loaded.");

        KeyManagerFactory kmf = null;
        try {
            String algorithm = KeyManagerFactory.getDefaultAlgorithm();
            LOGGER.debug("Dflt kmf algo :" + algorithm);
            kmf = KeyManagerFactory.getInstance(algorithm);
        } catch (NoSuchAlgorithmException exp) {
            LOGGER.error("IOException exception occurred " + exp.getMessage());
        }
        try {
            kmf.init(ks, keyPw);
        } catch (UnrecoverableKeyException exp) {
            LOGGER.error("UnrecoverableKeyException exception occurred " + exp.getMessage());
        } catch (KeyStoreException exp) {
            LOGGER.error("KeyStoreException exception occurred " + exp.getMessage());
        } catch (NoSuchAlgorithmException exp) {
            LOGGER.error("NoSuchAlgorithmException exception occurred " + exp.getMessage());
        }

        LOGGER.info("[initializeSSLContext] KMF init done.");

        KeyStore ts = null;
        try {
            ts = KeyStore.getInstance("JKS");
        } catch (KeyStoreException exp) {
            LOGGER.error("NoSuchAlgorithmException exception occurred " + exp.getMessage());
        }
        FileInputStream tfis = null;
        SSLContext sslContext = null;
        try {
            tfis = new FileInputStream(trustStorePath);
            ts.load(tfis, trustStorePw);
            String algorithm = KeyManagerFactory.getDefaultAlgorithm();
            LOGGER.debug("Dflt trust algo :" + algorithm);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
            tmf.init(ts);
            LOGGER.info("[initializeSSLContext] Truststore initialized");
            sslContext = SSLContext.getInstance("TLS");

            if (trustall) {
                sslContext.init(kmf.getKeyManagers(), trustAllCerts, secureRandom);
            } else {
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
            }

        } catch (NoSuchAlgorithmException exp) {
            LOGGER.error("NoSuchAlgorithmException exception occurred " + exp.getMessage());
        } catch (CertificateException exp) {
            LOGGER.error("NoSuchAlgorithmException exception occurred " + exp.getMessage());
        } catch (IOException exp) {
            LOGGER.error("NoSuchAlgorithmException exception occurred " + exp.getMessage());
        } catch (KeyStoreException exp) {
            LOGGER.error("NoSuchAlgorithmException exception occurred " + exp.getMessage());
        } catch (KeyManagementException exp) {
            LOGGER.error("NoSuchAlgorithmException exception occurred " + exp.getMessage());
        } finally {
            if (tfis != null)
					try {
                tfis.close();
            } catch (IOException exp) {
                LOGGER.error("NoSuchAlgorithmException exception occurred " + exp.getMessage());
            }
        }

        if ((sslContext == null)) {
            LOGGER.error("[initializeSSLContext] sslContext is null");
            System.exit(-1);
        }
        return sslContext;
    }

    /**
     *
     * @param keyStorePath
     * @param pwKeyStore
     * @param trustStorePath
     * @param pwTrustStore
     * @param keyPass
     * @return
     * @throws Exception
     */
    private SSLContext initializeSSLContextP12Cert(final String keyStorePath, final String pwKeyStore, final String trustStorePath, final String pwTrustStore, final String keyPass, final boolean trustall) {
        LOGGER.info("In initializeSSLContextP12Cert");
        SSLContext sslContext = null;
        String keystore = keyStorePath;
        String keystorepass = pwKeyStore;
        String truststore = trustStorePath;
        String truststorepass = pwTrustStore;

        try {
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(new FileInputStream(keystore), keystorepass.toCharArray());
            String algorithm = KeyManagerFactory.getDefaultAlgorithm();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(clientStore, keystorepass.toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(truststore), truststorepass.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
            tmf.init(trustStore);
            TrustManager[] tms = tmf.getTrustManagers();
            sslContext = SSLContext.getInstance("TLS");

            if (trustall) {
                sslContext.init(kms, trustAllCerts, new SecureRandom());
            } else {
                sslContext.init(kms, tms, new SecureRandom());
            }

        } catch (NoSuchAlgorithmException exp) {
            LOGGER.error("NoSuchAlgorithmException exception occurred " + exp.getMessage());
        } catch (CertificateException exp) {
            LOGGER.error("CertificateException exception occurred " + exp.getMessage());
        } catch (IOException exp) {
            LOGGER.error("IOException occurred while reading the key file  " + exp.getMessage());
        } catch (KeyStoreException exp) {
            LOGGER.error("KeyStoreException exception occurred " + exp.getMessage());
        } catch (KeyManagementException exp) {
            LOGGER.error("KeyManagementException exception occurred " + exp.getMessage());
        } catch (UnrecoverableKeyException exp) {
            LOGGER.error("UnrecoverableKeyException exception occurred " + exp.getMessage());
        }

        if ((sslContext == null)) {
            LOGGER.error("[initializeSSLContext] sslContext is null");
            LOGGER.error("[initializeSSLContext] verify ssl config");
            LOGGER.error("MyREST application exit with status code -1");
            //System.exit(-1);
        }
        LOGGER.info("[initializeSSLContextP12Cert] Truststore and KeyStore initialized");
        return sslContext;
    }

}
