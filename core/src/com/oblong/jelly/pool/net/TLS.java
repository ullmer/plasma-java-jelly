
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
 * you what this class does, too.  It is non-instantiable, and has a single
 * public method, startTLS().  It wraps functionality in the separate
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
     * /etc/oblong/certificate-authorities.jks
     */
    public static Socket startTLS (Socket sock, String host, int port)
        throws IOException,
               GeneralSecurityException {
        TLSFactory fact = getFactory ();
        return fact . startTLS (sock, host, port);
    }

    private static synchronized TLSFactory getFactory ()
        throws IOException,
               GeneralSecurityException {
        if (factory == null) {
            InputStream trustStream = new BufferedInputStream (new FileInputStream ("/etc/oblong/certificate-authorities.jks"));
            InputStream keyStream = null;
            factory = new TLSFactory (makeFactory ("JKS", trustStream,
                                                   "JKS", keyStream,
                                                   null, null));
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
                    Util . getInstance ());

        return ctx . getSocketFactory ();
    }

    private static TLSFactory factory = null;
}
