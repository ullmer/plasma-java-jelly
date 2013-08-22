
/* (c)  oblong industries */

package com.oblong.jelly.pool.net;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.CertificateException;
import javax.net.ssl.*;

final class TLS {
    public static Socket startTLS (Socket sock, String host, int port)
        throws IOException,
               GeneralSecurityException {
        SSLSocketFactory fact = getFactory ();
        return fact . createSocket (sock, host, port, true);
    }

    private static synchronized SSLSocketFactory getFactory ()
        throws IOException,
               GeneralSecurityException {
        if (factory == null) {
            InputStream trustStream = new BufferedInputStream (new FileInputStream ("/etc/oblong/certificate-authorities.jks"));
            InputStream keyStream = null;
            factory = makeFactory ("JKS", trustStream,
                                   "JKS", keyStream, null, null);
        }
        return factory;
    }

    private static SSLSocketFactory makeFactory (String trustStoreType,
                                                 InputStream trustStoreStream,
                                                 String keyStoreType,
                                                 InputStream keyStoreStream,
                                                 char[] keyStorePassword,
                                                 char[] privateKeyPassword)
        throws KeyStoreException,
               IOException,
               NoSuchAlgorithmException,
               CertificateException,
               KeyManagementException,
               UnrecoverableKeyException {
        KeyStore trustStore = KeyStore . getInstance (trustStoreType);
        trustStore . load (trustStoreStream, null);
        TrustManagerFactory tmf = TrustManagerFactory . getInstance ("PKIX");
        tmf . init (trustStore);

        KeyStore keyStore = KeyStore . getInstance (keyStoreType);
        keyStore . load (keyStoreStream, keyStorePassword);
        KeyManagerFactory kmf = KeyManagerFactory . getInstance ("SunX509");
        kmf . init (keyStore, privateKeyPassword);

        SSLContext ctx = SSLContext . getInstance ("TLS");
        ctx . init (kmf . getKeyManagers (),
                    tmf . getTrustManagers (),
                    new SecureRandom ());

        return ctx . getSocketFactory ();
    }

    private static SSLSocketFactory factory = null;
}
