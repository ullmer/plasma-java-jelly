
/* (c)  oblong industries */

package com.oblong.jelly.pool.net;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.CertificateException;
import javax.net.ssl.*;

import com.oblong.tls.random.*;
import com.oblong.tls.util.*;

/**
 * Created with Emacs, since we seem to insist on filling doc comments with
 * what editor we use, instead of what the class does.  However, I'll tell
 * you what this class does, too.  It is non-instantiable, and has a public
 * method startTLS() which is called by Jelly, and a couple of public
 * setCredentials() methods which can be called by the application to
 * specify the trust store and the client certificate.
 *
 * This class wraps functionality in the separate
 * ob-tls-helpers library, which is available here:
 *
 * git.oblong.com:~ppelletier/pub/ob-tls-helpers.git
 *
 * (Which I separated because I thought its functionality might be more
 * generally useful, since it seems like stuff which belongs in Java
 * but isn't.  We might even want to post the library on Oblong's
 * Github page.)
 */
final class TLS {
    /**
     * Given a socket which is already connected to the specified
     * host and port (which we need to know for verification
     * purposes), initiates TLS on the socket, and returns a new
     * socket, which should now be used in place of the original
     * socket.
     *
     * The remote server's certificate is validated against
     * the credentials supplied with setCredentials(), or
     * "/etc/oblong/certificate-authorities.jks" if no other
     * credentials have been provided.
     */
    public static Socket startTLS (Socket sock, String host, int port)
        throws IOException,
               GeneralSecurityException {
        TLSFactory fact = getFactory ();
        return fact . startTLS (sock, host, port);
    }

    /**
     * Set the trusted root certificates used by Jelly.
     * trustStoreType is the name of a type of keystore (i. e. a
     * file format) as accepted by java.security.KeyStore.getInstance().
     * trustStoreStream is (the contents of) a file, in the format
     * named by trustStoreType, which contains all the root certificates
     * we should trust, which will then be used to validate the
     * pool server's certificate.
     *
     * Though I abhor global state, this is necessary because there
     * isn't really any way to pass this information down through
     * the Jelly pool API.  So just call this method at the beginning
     * of your program, before you use any pools.
     */
    public static void setCredentials (String trustStoreType,
                                       InputStream trustStoreStream,
                                       char[] trustStorePassword)
        throws IOException,
               GeneralSecurityException {
        setCredentials (trustStoreType, trustStoreStream, trustStorePassword,
                        KeyStore . getDefaultType (), null, null, null);
    }

    /**
     * This probably isn't needed until we start supporting
     * client certificates.
     */
    public static synchronized
        void setCredentials (String trustStoreType,
                             InputStream trustStoreStream,
                             char[] trustStorePassword,
                             String keyStoreType,
                             InputStream keyStoreStream,
                             char[] keyStorePassword,
                             char[] privateKeyPassword)
            throws IOException,
                   GeneralSecurityException {
            factory = new TLSFactory (makeFactory (trustStoreType,
                                                   trustStoreStream,
                                                   trustStorePassword,
                                                   keyStoreType,
                                                   keyStoreStream,
                                                   keyStorePassword,
                                                   privateKeyPassword));
        }

    /**
     * This is lower-level than setCredentials(), and allows
     * supplying your own SSLSocketFactory, instead of letting us
     * make one for you.
     */
    public static synchronized void setFactory (SSLSocketFactory sslSockFact) {
        factory = new TLSFactory (sslSockFact);
    }

    private static synchronized TLSFactory getFactory ()
        throws IOException,
               GeneralSecurityException {
        if (factory == null) {
            InputStream trustStream = new BufferedInputStream (new FileInputStream ("/etc/oblong/certificate-authorities.jks"));
            setCredentials ("JKS", trustStream, null);
        }
        return factory;
    }

    private static SSLSocketFactory makeFactory (String trustStoreType,
                                                 InputStream trustStoreStream,
                                                 char[] trustStorePassword,
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
        trustStore . load (trustStoreStream, trustStorePassword);
        TrustManagerFactory tmf = TrustManagerFactory . getInstance ("PKIX");
        tmf . init (trustStore);

        KeyStore keyStore = KeyStore . getInstance (keyStoreType);
        keyStore . load (keyStoreStream, keyStorePassword);
        KeyManagerFactory kmf = KeyManagerFactory . getInstance ("SunX509");
        kmf . init (keyStore, privateKeyPassword);

        SSLContext ctx = SSLContext . getInstance ("TLS");
        ctx . init (kmf . getKeyManagers (),
                    tmf . getTrustManagers (),
                    Util . getInstance ());

        return ctx . getSocketFactory ();
    }

    private static TLSFactory factory = null;
}
