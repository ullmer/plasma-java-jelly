/**
 * Jelly's public API to interact with remote pools is contained, in
 * its totallity, in this package.
 *
 * <p> As a matter of fact, you can build a fully functional jelly
 * application by using {@link com.oblong.jelly.Pool}'s methods to
 * connect to pools and obtain {@link com.oblong.jelly.Hose} instances
 * allowing you to exchange {@link com.oblong.jelly.Protein} objects
 * with remote servers. Proteins can be deconstructed into their
 * constituent {@link com.oblong.jelly.Slaw}, from which you can
 * obtain native java objects. Besides the classes above, you'll
 * probably need to take a look at a couple of enumerations, {@link
 * com.oblong.jelly.SlawIlk SlawIlk} and {@link
 * com.oblong.jelly.NumericIlk}, to properly handle the many Slaw
 * kinds, and to {@link com.oblong.jelly.PoolException} to deal with
 * errors.
 *
 * <p> In some cases, you might find useful a more structured approach
 * to pool addressing (using {@link com.oblong.jelly.PoolAddress} and
 * {@link com.oblong.jelly.PoolServerAddress}), and the reification of
 * pool servers, which can be accomplished (via the creation of
 * objects implementing {@link com.oblong.jelly.PoolServer} interface)
 * using the {@link com.oblong.jelly.Pool#server} factory method.
 *
 * <p> It's also possible to treat errors in a finer-grained way by
 * means of the host of exception classes defined in the library, all
 * of them deriving from {@link com.oblong.jelly.PoolException}.
 *
 * <h3>Addressing, and connecting to, pools</h3>
 *
 * A pool location if fully given by its URI, a string formed by a
 * scheme, an optional host name, an optional port, and a pool name.
 * When no host name is provided (as in, e.g., "tcp:///a-pool"),
 * "localhost" is used, while 65456 is the default port (thus, the
 * previous URI is equivalent to "tcp://localhost:65456/a-pool").
 * Note, however, that the scheme part (e.g. "tcp://" or "mem://") is
 * always mandatory, and that you need to specify a host name if you
 * want to include a port number.
 *
 * <p> Once you know its URI, you can connect to a pool using {@link
 * com.oblong.jelly.Pool#participate(String) Pool's participate}
 * method, which will give you back a {@link com.oblong.jelly.Hose
 * Hose instance}, equipped with all that's needed to exchange
 * proteins with a pool.
 *
 * <p> {@link com.oblong.jelly.Pool} also contains methods for {@link
 * com.oblong.jelly.Pool#create(String,PoolOptions) creating} and
 * {@link com.oblong.jelly.Pool#dispose(String) deleting} pools.
 *
 * <p> If you prefer to to treat pool URIs in a safer way, {@link
 * com.oblong.jelly.PoolAddress PoolAddress} objects encapsulate and
 * parse them, and allow you to separate the server part of the
 * address as a {@link com.oblong.jelly.PoolServerAddress} instance.
 * The latter can be used, in turn, to obtain instances of {@link
 * com.oblong.jelly.PoolServer}, a class reifying pool servers. As
 * noticed before, usage of these classes is optional: you can perform
 * all required operations on pools via string URIs and the static
 * methods in {@link com.oblong.jelly.Pool}.
 *
 * <h3>Reading and writing proteins</h3>
 *
 * Once you get a {@link com.oblong.jelly.Hose} instance from either
 * {@link com.oblong.jelly.Pool} or a {@link
 * com.oblong.jelly.PoolServer}, exchanging proteins with a pool is
 * just a matter of using Hose's interface.
 *
 * <pre>
 *   Hose h = null;
 *   try {
 *       h = Pool.participate("tcp://imladris/brandywine");
 *       Protein p = h.next();
 *       if (p.timestamp() < THE_END_OF_THE_3RD_AGE) {
 *           Slaw s = p.ingests();
 *           String elrondsRing = s.find(Slaw.string("Elrond")).emitString();
 *           doSomeMagic(elrondsRing);
 *       } else {
 *           Slaw descrips = Slaw.list(Slaw.int8(0),
 *                                     Slaw.string("too late"));
 *           h.deposit(Slaw.protein(descrips, nil));
 *       }
 *   } catch (PoolException e) {
 *       tellMithrandir(e);
 *   } finally {
 *       if (h != null) h.withdraw();
 *   }
 * </pre>
 *
 * <p> The above code snippet, despite being silly, illustrates an
 * important point to keep in mind when using jelly: Hose instances,
 * when created, allocate resources, such as network connections, that
 * you want to release, explicitly, as soon as you know that you won't
 * need them anymore. To that end, one calls {@link
 * com.oblong.jelly.Hose#withdraw} on the hose; note how the code
 * above makes sure that this method is called even in the presence of
 * errors.
 *
 * <p> A second important thing to note is that Hose is the only
 * <b>not</b> thread-safe class in the library, since it has mutable
 * state but uses no synchronization primitives internally.
 *
 * <h3>Deconstructing proteins</h3>
 *
 * {@link com.oblong.jelly.Protein} constitutes the exchange coin with
 * pools. Its a kind of {@link com.oblong.jelly.Slaw} with attached
 * metadata (an index and timestamp, for instance) and two methods,
 * <code>ingests()</code> and <code>descrips()</code> giving you
 * access to its constituent slawx. Proteins can be either obtained
 * from hoses (using, for instance, {@link
 * com.oblong.jelly.Hose#next()} or {@link
 * com.oblong.jelly.Hose#nth(long)}) or explicitly constructed from
 * other slawx using the factory method {@link
 * com.oblong.jelly.Slaw#protein(Slaw,Slaw)}.
 *
 * <p> {@link com.oblong.jelly.Slaw} encompasses all the data types a
 * Slaw can represent (with {@link com.oblong.jelly.SlawIlk} and
 * {@link com.oblong.jelly.NumericIlk} playing the part of
 * discriminators), and provides methods to extract that data as
 * native java objects and values, as well as accessing sub-slaw for
 * composite Slaw ilks (such as map or list).
 *
 * <p> The fact that Slaw is actually a union type means that some of
 * their methods will not be applicable to concrete instances (e.g.,
 * one cannot call {@link com.oblong.jelly.Slaw#emitString()} on a
 * slaw with ilk {@link com.oblong.jelly.SlawIlk#MAP}).

 * <p> We have chosen to flag errors caused by calling unsupported
 * operations on Slaw instances by means of an <i>unchecked</i>
 * exception, <code>UnsupportedOperationException</code>. The
 * rationale is that you can always avoid those errors by checking for
 * the Slaw's actual ilk, and there will be many places in your
 * programs where you know the ilk of a given Slaw instance (e.g., by
 * construction), and forcing a try/catch block in such cases seemed
 * unnecessary.
 *
 * <p> The price of that convenience is of course that you must use
 * Slaw's methods carefully to avoid run-time errors.
 *
 * <p> Slaw instances are either obtained from proteins or created
 * using the host of static factory methods in {@link
 * com.oblong.jelly.Slaw}.
 *
 * <h3>Serializing Slaw</h3>
 *
 * Although pools are very useful for communicating proteins from one
 * process to another, and can also be used for storage of proteins,
 * it's also sometimes useful to store proteins (or other slawx) in a
 * traditional file (or perhaps a String or a byte array).
 *
 * <p>The class {@link com.oblong.jelly.SlawIO} provides a collection
 * of static methods allowing serialization of <code>Slaw</code> and
 * <code>Protein</code> instances to and from files, strings and byte
 * arrays, in binary and text formats. The first is designed for speed
 * and compactness, while the second is a human-readable rendering of
 * slawx using YAML syntax.
 *
 * <p>Serialization can be performed in one shot for a list of Slaw,
 * or stepwise, via the {@link com.oblong.jelly.SlawReader} and {@link
 * com.oblong.jelly.SlawWriter} interfaces.
 *
 * <p>The documentation for {@link com.oblong.jelly.SlawIO} provides
 * more details on the serialization formats and utilities.
 *
 *
 * <h3>Error handling</h3>
 *
 * Errors in pool operations are reported by means of exceptions of a
 * type always deriving from {@link com.oblong.jelly.PoolException}.
 * That allows to write simple try/catch clauses with a single catcher
 * for <code>PoolException</code>, using {@link
 * com.oblong.jelly.PoolException#kind()} to discover the error's
 * nature. But you can also catch the different exception types
 * individually, if that's the (perhaps more idiomatic) style that you
 * favour. The {@link com.oblong.jelly.PoolException documentation of
 * PoolException} explains how to work with our exception system.
 *
 * <h3>Other kinds of pool: in-memory pools</h3>
 *
 * <p> TCP is not the only supported scheme. You can also use a
 * pseudo-pool server local to the running JVM that store their
 * proteins in memory. Their URI scheme is "mem". The following code
 * creates a 'mem-pool', opens a connection and writes some proteins
 * to it:
 *
 * <pre>
 *    Hose h = Pool.participate("mem:///a-mem-pool", null);
 *    Proteins[] proteins = gimmeProts();
 *    for (Protein p : proteins) h.deposit(p);
 *    h.withdraw();
 * </pre>
 *
 * where we have used the two-arguments version of
 * <code>participate</code>, which creates the pool if it doesn't
 * already exist. After the code above is executed, the proteins in
 * the array will be stored in memory and accessable by any other hose
 * using the standard Hose interface. Note that we use an empty
 * hostname (the library will then use "localhost" as a default); you
 * can use any other hostname you want, as long as you use the same
 * URI later on to access the pool. Also, in the current version of
 * jelly, in-memory pools accept ignore any creation options (that's
 * why the second argument in the call to participate is null).
 *
 * <p> In-memory pools come in handy for testing your applications
 * without needing to establish network connections, but can also help
 * providing local sources of proteins to protein sinks that can be
 * coded abstracting away from the real nature of the server providing
 * the proteins (i.e., taking either a Hose or a URI as inputs).
 *
 */
package com.oblong.jelly;
