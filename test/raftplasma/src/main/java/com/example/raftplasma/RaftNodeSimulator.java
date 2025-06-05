
package com.example.raftplasma;

import io.microraft.RaftNode;
import io.microraft.RaftNodeBuilder;
import com.oblong.jelly.Pool;
import com.oblong.jelly.Hose;
import java.util.*;

public class RaftNodeSimulator {

    public static void main(String[] args) {
        // Create pools for each node
        Pool pool1 = new Pool("node1");
        Pool pool2 = new Pool("node2");
        Pool pool3 = new Pool("node3");

        // Create Raft nodes
        RaftNode node1 = new RaftNodeBuilder().setLocalEndpoint("node1").build();
        RaftNode node2 = new RaftNodeBuilder().setLocalEndpoint("node2").build();
        RaftNode node3 = new RaftNodeBuilder().setLocalEndpoint("node3").build();

        // Map endpoints to pools
        Map<RaftEndpoint, Pool> endpointPools = new HashMap<>();
        endpointPools.put(node1.getLocalEndpoint(), pool1);
        endpointPools.put(node2.getLocalEndpoint(), pool2);
        endpointPools.put(node3.getLocalEndpoint(), pool3);

        // Create transport
        PlasmaRaftTransport transport = new PlasmaRaftTransport(endpointPools);

        // Set transport for each node
        node1.setTransport(transport);
        node2.setTransport(transport);
        node3.setTransport(transport);

        // Start receivers
        new Thread(new PlasmaRaftReceiver(pool1, node1)).start();
        new Thread(new PlasmaRaftReceiver(pool2, node2)).start();
        new Thread(new PlasmaRaftReceiver(pool3, node3)).start();

        // Manually nominate node1 as leader for testing
        node1.becomeLeader();
    }
}
