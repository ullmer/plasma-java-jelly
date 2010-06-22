// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;


import java.util.HashSet;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.Hose;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolOptions;
import com.oblong.jelly.PoolServer;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.SlawFactory;

/**
 *
 * Created: Mon Jun 14 16:22:00 2010
 *
 * @author jao
 */
@ThreadSafe
public final class Server implements PoolServer {

    public Server(ServerConnectionFactory cf, PoolServerAddress addr) 
        throws PoolException {
        address = addr;
        connectionFactory = cf;
    }

    @Override public void create(String name, PoolOptions opts)
        throws PoolException {
        final ServerConnection connection = connectionFactory.get(address);
        final SlawFactory factory = connection.factory();
        checkRetort(connection.send(Request.CREATE,
                                    factory.string(name),
                                    factory.string("mmap"),
                                    opts.toSlaw()),
                    0);
        connection.close();
    }

    @Override public void dispose(String name) throws PoolException {
        final ServerConnection connection = connectionFactory.get(address);
        final SlawFactory factory = connection.factory();
        checkRetort(connection.send(Request.DISPOSE,
                                    factory.string(name)),
                    0);
        connection.close();
    }

    @Override public Set<String> pools() throws PoolException {
        final ServerConnection connection = connectionFactory.get(address);
        final Slaw list = connection.send(Request.LIST);
        connection.close();
        Set<String> result = new HashSet<String>(list.count());
        for (Slaw s : list.emitList()) {
            if (s.isString()) result.add(s.emitString());
        }
        return result;
    }

    @Override public Hose participate(String name) throws PoolException {
        final ServerConnection connection = connectionFactory.get(address);
        final SlawFactory factory = connection.factory();
        checkRetort(connection.send(Request.PARTICIPATE,
                                    factory.string(name),
                                    factory.protein(null, null, null)),
                    0);
        return new PoolHose(connection, name);
    }

    @Override public Hose participate(String name, PoolOptions opts)
        throws PoolException {
        final ServerConnection connection = connectionFactory.get(address);
        final SlawFactory factory = connection.factory();
        checkRetort(connection.send(Request.PARTICIPATE_C,
                                    factory.string(name),
                                    factory.string("mmap"),
                                    opts.toSlaw(),
                                    factory.protein(null, null, null)),
                    0);
        return new PoolHose(connection, name);
     }

    private static void checkRetort(Slaw ret, int pos)
        throws PoolException {
    }

    private final PoolServerAddress address;
    private final ServerConnectionFactory connectionFactory;
}
