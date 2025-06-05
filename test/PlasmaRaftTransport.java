
package com.example.raftplasma;

import com.hazelcast.microraft.Transport;
import com.oblong.jelly.Pool;
import com.oblong.jelly.Hose;
import java.io.*;

public class PlasmaRaftTransport implements Transport {

    private final Map<RaftEndpoint, Pool> endpointPools;

    public PlasmaRaftTransport(Map<RaftEndpoint, Pool> endpointPools) {
        this.endpointPools = endpointPools;
    }

    @Override
    public void send(RaftEndpoint target, Object message) {
        try {
            Pool targetPool = endpointPools.get(target);
            if (targetPool == null) {
                throw new IllegalStateException("No pool for endpoint: " + target);
            }

            byte[] serialized = serialize(message);
            targetPool.deposit(serialized); // Deposit into the target's pool
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        out.flush();
        return bos.toByteArray();
    }
}
