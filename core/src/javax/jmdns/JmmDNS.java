/**
 *
 */
package javax.jmdns;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.jmdns.impl.JmmDNSImpl;
import javax.jmdns.impl.NetworkTopologyDiscoveryImpl;

/**
 * Java Multihomed Multicast DNS<br/>
 * Uses an underlying {@link JmDNS} instance for each {@link InetAddress} found on this computer.<br/>
 * This class will monitor network topology changes, and will create or destroy JmDNS instances as required. It is your responsibility to maintain services registration (hint: use a {@link NetworkTopologyListener}).<br/>
 * Most of this class methods have no notion of transaction: if an Exception is raised in the middle of execution, you may be in an incoherent state.
 * <p>
 * <b>Note:</b> This API is experimental and may change in the future please let us know what work and what does not work in you application.
 * </p>
 *
 * @author C&eacute;drik Lime, Pierre Frisch
 */
public interface JmmDNS extends Closeable
{

    public static class Factory
    {
        private static volatile JmmDNS _instance;

        /**
         * This interface defines a delegate to the EOClassDescriptionRegister class to enable subclassing.
         */
        public static interface ClassDelegate
        {

            /**
             * Allows the delegate the opportunity to construct and return a different JmmDNS.
             *
             * @return Should return a new JmmDNS.
             * @see #classDelegate()
             * @see #setClassDelegate(ClassDelegate anObject)
             */
            public JmmDNS newJmmDNS();
        }

        private static final AtomicReference<ClassDelegate> _databaseClassDelegate = new AtomicReference<ClassDelegate>();

        /**
         * Assigns <code>delegate</code> as JmmDNS's class delegate. The class delegate is optional.
         *
         * @param delegate
         *            The object to set as JmmDNS's class delegate.
         * @see #classDelegate()
         * @see JmmDNS.Factory.ClassDelegate
         */
        public static void setClassDelegate(ClassDelegate delegate)
        {
            _databaseClassDelegate.set(delegate);
        }

        /**
         * Returns JmmDNS's class delegate.
         *
         * @return JmmDNS's class delegate.
         * @see #setClassDelegate(ClassDelegate anObject)
         * @see JmmDNS.Factory.ClassDelegate
         */
        public static ClassDelegate classDelegate()
        {
            return _databaseClassDelegate.get();
        }

        /**
         * Returns a new instance of JmmDNS using the class delegate if it exists.
         *
         * @return new instance of JmmDNS
         */
        protected static JmmDNS newJmmDNS()
        {
            JmmDNS dns = null;
            ClassDelegate delegate = _databaseClassDelegate.get();
            if (delegate != null)
            {
                dns = delegate.newJmmDNS();
            }
            return (dns != null ? dns : new JmmDNSImpl());
        }

        /**
         * Return the instance of the Multihommed Multicast DNS.
         *
         * @return the JmmDNS
         */
        public static JmmDNS getInstance()
        {
            if (_instance == null)
            {
                synchronized (Factory.class)
                {
                    if (_instance == null)
                    {
                        _instance = JmmDNS.Factory.newJmmDNS();
                    }
                }
            }
            return _instance;
        }
    }

    /**
     * This class is used to resolve the list of Internet address to use when attaching JmDNS to the network.
     * <p>
     * To create you own filtering class for Internet Addresses you will need to implement the class and the factory delegate. These must be called before any other call to JmDNS.
     *
     * <pre>
     * public static class MyNetworkTopologyDiscovery implements NetworkTopologyDiscovery
     * {
     *
     *     &#064;Override
     *     public InetAddress[] getInetAddresses()
     *     {
     *         // TODO Auto-generated method stub
     *         return null;
     *     }
     *
     *     &#064;Override
     *     public boolean useInetAddress(NetworkInterface networkInterface, InetAddress interfaceAddress)
     *     {
     *         // TODO Auto-generated method stub
     *         return false;
     *     }
     *
     * }
     *
     * public static class MyClass implements NetworkTopologyDiscovery.Factory.ClassDelegate
     * {
     *     public MyClass()
     *     {
     *         super();
     *         NetworkTopologyDiscovery.Factory.setClassDelegate(this);
     *
     *         // Access JmDNS or JmmDNS
     *     }
     *
     *     &#064;Override
     *     public NetworkTopologyDiscovery newNetworkTopologyDiscovery()
     *     {
     *         return new MyNetworkTopologyDiscovery();
     *     }
     *
     * }
     * </pre>
     *
     * </p>
     */
    public static interface NetworkTopologyDiscovery
    {
        public static class Factory
        {
            private static volatile NetworkTopologyDiscovery _instance;

            /**
             * This interface defines a delegate to the EOClassDescriptionRegister class to enable subclassing.
             */
            public static interface ClassDelegate
            {

                /**
                 * Allows the delegate the opportunity to construct and return a different NetworkTopologyDiscovery.
                 *
                 * @return Should return a new NetworkTopologyDiscovery Object.
                 * @see #classDelegate()
                 * @see #setClassDelegate(ClassDelegate anObject)
                 */
                public NetworkTopologyDiscovery newNetworkTopologyDiscovery();
            }

            private static final AtomicReference<ClassDelegate> _databaseClassDelegate = new AtomicReference<ClassDelegate>();

            /**
             * Assigns <code>delegate</code> as NetworkTopologyDiscovery's class delegate. The class delegate is optional.
             *
             * @param delegate
             *            The object to set as NetworkTopologyDiscovery's class delegate.
             * @see #classDelegate()
             * @see JmmDNS.Factory.ClassDelegate
             */
            public static void setClassDelegate(ClassDelegate delegate)
            {
                _databaseClassDelegate.set(delegate);
            }

            /**
             * Returns NetworkTopologyDiscovery's class delegate.
             *
             * @return NetworkTopologyDiscovery's class delegate.
             * @see #setClassDelegate(ClassDelegate anObject)
             * @see JmmDNS.Factory.ClassDelegate
             */
            public static ClassDelegate classDelegate()
            {
                return _databaseClassDelegate.get();
            }

            /**
             * Returns a new instance of NetworkTopologyDiscovery using the class delegate if it exists.
             *
             * @return new instance of NetworkTopologyDiscovery
             */
            protected static NetworkTopologyDiscovery newNetworkTopologyDiscovery()
            {
                NetworkTopologyDiscovery instance = null;
                ClassDelegate delegate = _databaseClassDelegate.get();
                if (delegate != null)
                {
                    instance = delegate.newNetworkTopologyDiscovery();
                }
                return (instance != null ? instance : new NetworkTopologyDiscoveryImpl());
            }

            /**
             * Return the instance of the Multihommed Multicast DNS.
             *
             * @return the JmmDNS
             */
            public static NetworkTopologyDiscovery getInstance()
            {
                if (_instance == null)
                {
                    synchronized (Factory.class)
                    {
                        if (_instance == null)
                        {
                            _instance = JmmDNS.NetworkTopologyDiscovery.Factory.newNetworkTopologyDiscovery();
                        }
                    }
                }
                return _instance;
            }
        }

        /**
         * Get all local Internet Addresses for the machine.
         *
         * @return Set of InetAddress
         */
        public abstract InetAddress[] getInetAddresses();

        /**
         * Check if a given InetAddress should be used for mDNS
         *
         * @param networkInterface
         * @param interfaceAddress
         * @return <code>true</code> is the address is to be used, <code>false</code> otherwise.
         */
        public boolean useInetAddress(NetworkInterface networkInterface, InetAddress interfaceAddress);

    }

    /**
     * Return the names of the JmDNS instances.
     *
     * @return list of name of the JmDNS
     * @see javax.jmdns.JmDNS#getName()
     */
    public abstract String[] getNames();

    /**
     * Return the list HostName associated with this JmmDNS instance.
     *
     * @return list of host names
     * @see javax.jmdns.JmDNS#getHostName()
     */
    public abstract String[] getHostNames();

    /**
     * Return the list of addresses of the interface to which this instance of JmmDNS is bound.
     *
     * @return list of Internet Address
     * @throws IOException
     * @see javax.jmdns.JmDNS#getInterface()
     */
    public abstract InetAddress[] getInterfaces() throws IOException;

    /**
     * Get service information. If the information is not cached, the method will block until updated information is received on all DNS.
     * <p/>
     * Usage note: Do not call this method from the AWT event dispatcher thread. You will make the user interface unresponsive.
     *
     * @param type
     *            fully qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @return list of service info. If no service info is found the list is empty.
     * @see javax.jmdns.JmDNS#getServiceInfo(java.lang.String, java.lang.String)
     */
    public abstract ServiceInfo[] getServiceInfos(String type, String name);

    /**
     * Get service information. If the information is not cached, the method will block until updated information is received on all DNS.
     * <p/>
     * Usage note: If you call this method from the AWT event dispatcher thread, use a small timeout, or you will make the user interface unresponsive.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param timeout
     *            timeout in milliseconds. Typical timeout should be 5s.
     * @return list of service info. If no service info is found the list is empty.
     * @see javax.jmdns.JmDNS#getServiceInfo(java.lang.String, java.lang.String, long)
     */
    public abstract ServiceInfo[] getServiceInfos(String type, String name, long timeout);

    /**
     * Get service information. If the information is not cached, the method will block until updated information is received on all DNS.
     * <p/>
     * Usage note: If you call this method from the AWT event dispatcher thread, use a small timeout, or you will make the user interface unresponsive.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param persistent
     *            if <code>true</code> ServiceListener.resolveService will be called whenever new new information is received.
     * @return list of service info. If no service info is found the list is empty.
     * @see javax.jmdns.JmDNS#getServiceInfo(java.lang.String, java.lang.String, boolean)
     */
    public abstract ServiceInfo[] getServiceInfos(String type, String name, boolean persistent);

    /**
     * Get service information. If the information is not cached, the method will block until updated information is received on all DNS.
     * <p/>
     * Usage note: If you call this method from the AWT event dispatcher thread, use a small timeout, or you will make the user interface unresponsive.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param timeout
     *            timeout in milliseconds. Typical timeout should be 5s.
     * @param persistent
     *            if <code>true</code> ServiceListener.resolveService will be called whenever new new information is received.
     * @return list of service info. If no service info is found the list is empty.
     * @see javax.jmdns.JmDNS#getServiceInfo(java.lang.String, java.lang.String, boolean, long)
     */
    public abstract ServiceInfo[] getServiceInfos(String type, String name, boolean persistent, long timeout);

    /**
     * Request service information. The information about the service is requested and the ServiceListener.resolveService method is called as soon as it is available.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @see javax.jmdns.JmDNS#requestServiceInfo(java.lang.String, java.lang.String)
     */
    public abstract void requestServiceInfo(String type, String name);

    /**
     * Request service information. The information about the service is requested and the ServiceListener.resolveService method is called as soon as it is available.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param persistent
     *            if <code>true</code> ServiceListener.resolveService will be called whenever new new information is received.
     * @see javax.jmdns.JmDNS#requestServiceInfo(java.lang.String, java.lang.String, boolean)
     */
    public abstract void requestServiceInfo(String type, String name, boolean persistent);

    /**
     * Request service information. The information about the service is requested and the ServiceListener.resolveService method is called as soon as it is available.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param timeout
     *            timeout in milliseconds
     * @see javax.jmdns.JmDNS#requestServiceInfo(java.lang.String, java.lang.String, long)
     */
    public abstract void requestServiceInfo(String type, String name, long timeout);

    /**
     * Request service information. The information about the service is requested and the ServiceListener.resolveService method is called as soon as it is available.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code> .
     * @param name
     *            unqualified service name, such as <code>foobar</code> .
     * @param persistent
     *            if <code>true</code> ServiceListener.resolveService will be called whenever new new information is received.
     * @param timeout
     *            timeout in milliseconds
     * @see javax.jmdns.JmDNS#requestServiceInfo(java.lang.String, java.lang.String, boolean, long)
     */
    public abstract void requestServiceInfo(String type, String name, boolean persistent, long timeout);

    /**
     * Listen for service types.
     *
     * @param listener
     *            listener for service types
     * @throws IOException
     * @see javax.jmdns.JmDNS#addServiceTypeListener(javax.jmdns.ServiceTypeListener)
     */
    public abstract void addServiceTypeListener(ServiceTypeListener listener) throws IOException;

    /**
     * Remove listener for service types.
     *
     * @param listener
     *            listener for service types
     * @see javax.jmdns.JmDNS#removeServiceTypeListener(javax.jmdns.ServiceTypeListener)
     */
    public abstract void removeServiceTypeListener(ServiceTypeListener listener);

    /**
     * Listen for services of a given type. The type has to be a fully qualified type name such as <code>_http._tcp.local.</code>.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code>.
     * @param listener
     *            listener for service updates
     * @see javax.jmdns.JmDNS#addServiceListener(java.lang.String, javax.jmdns.ServiceListener)
     */
    public abstract void addServiceListener(String type, ServiceListener listener);

    /**
     * Remove listener for services of a given type.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code>.
     * @param listener
     *            listener for service updates
     * @see javax.jmdns.JmDNS#removeServiceListener(java.lang.String, javax.jmdns.ServiceListener)
     */
    public abstract void removeServiceListener(String type, ServiceListener listener);

    /**
     * Register a service. The service is registered for access by other jmdns clients. The name of the service may be changed to make it unique.<br>
     * <b>Note</b> the Service info is cloned for each network interface.
     *
     * @param info
     *            service info to register
     * @throws IOException
     * @see javax.jmdns.JmDNS#registerService(javax.jmdns.ServiceInfo)
     */
    public abstract void registerService(ServiceInfo info) throws IOException;

    /**
     * Unregister a service. The service should have been registered.
     *
     * @param info
     *            service info to remove
     * @see javax.jmdns.JmDNS#unregisterService(javax.jmdns.ServiceInfo)
     */
    public abstract void unregisterService(ServiceInfo info);

    /**
     * Unregister all services.
     *
     * @see javax.jmdns.JmDNS#unregisterAllServices()
     */
    public abstract void unregisterAllServices();

    /**
     * Register a service type. If this service type was not already known, all service listeners will be notified of the new service type. Service types are automatically registered as they are discovered.
     *
     * @param type
     *            full qualified service type, such as <code>_http._tcp.local.</code>.
     * @see javax.jmdns.JmDNS#registerServiceType(java.lang.String)
     */
    public abstract void registerServiceType(String type);

    /**
     * Returns a list of service infos of the specified type.
     *
     * @param type
     *            Service type name, such as <code>_http._tcp.local.</code>.
     * @return An array of service instance.
     * @see javax.jmdns.JmDNS#list(java.lang.String)
     */
    public abstract ServiceInfo[] list(String type);

    /**
     * Returns a list of service infos of the specified type.
     *
     * @param type
     *            Service type name, such as <code>_http._tcp.local.</code>.
     * @param timeout
     *            timeout in milliseconds. Typical timeout should be 6s.
     * @return An array of service instance.
     * @see javax.jmdns.JmDNS#list(java.lang.String, long)
     */
    public abstract ServiceInfo[] list(String type, long timeout);

    /**
     * Returns a list of service infos of the specified type sorted by subtype. Any service that do not register a subtype is listed in the empty subtype section.
     *
     * @param type
     *            Service type name, such as <code>_http._tcp.local.</code>.
     * @return A dictionary of service info by subtypes.
     * @see javax.jmdns.JmDNS#listBySubtype(java.lang.String)
     */
    public abstract Map<String, ServiceInfo[]> listBySubtype(String type);

    /**
     * Returns a list of service infos of the specified type sorted by subtype. Any service that do not register a subtype is listed in the empty subtype section.
     *
     * @param type
     *            Service type name, such as <code>_http._tcp.local.</code>.
     * @param timeout
     *            timeout in milliseconds. Typical timeout should be 6s.
     * @return A dictionary of service info by subtypes.
     * @see javax.jmdns.JmDNS#listBySubtype(java.lang.String, long)
     */
    public abstract Map<String, ServiceInfo[]> listBySubtype(String type, long timeout);

    /**
     * Listen to network changes.
     *
     * @param listener
     *            listener for network changes
     */
    public abstract void addNetworkTopologyListener(NetworkTopologyListener listener);

    /**
     * Remove listener for network changes.
     *
     * @param listener
     *            listener for network changes
     */
    public abstract void removeNetworkTopologyListener(NetworkTopologyListener listener);

    /**
     * Returns list of network change listeners
     *
     * @return list of network change listeners
     */
    public abstract NetworkTopologyListener[] networkListeners();

}
