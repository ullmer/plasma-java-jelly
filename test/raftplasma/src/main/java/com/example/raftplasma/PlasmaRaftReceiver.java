
package com.example.raftplasma;

import com.oblong.jelly.Pool;
import io.microraft.microraft.RaftNode;
import java.io.*;

public class PlasmaRaftReceiver implements Runnable {

    private final Pool localPool;
    private final RaftNode raftNode;

    public PlasmaRaftReceiver(Pool localPool, RaftNode raftNode) {
        this.localPool = localPool;
        this.raftNode = raftNode;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] data = localPool.await(); // Blocking call
                Object message = deserialize(data);
                raftNode.handle(message); // Dispatch to MicroRaft
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(bis);
        return in.readObject();
    }
}
