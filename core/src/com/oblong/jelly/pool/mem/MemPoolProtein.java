package com.oblong.jelly.pool.mem;

import com.oblong.jelly.Hose;
import com.oblong.jelly.Protein;
import com.oblong.jelly.pool.PoolProtein;

/**
 * @author Karol, 2014-05-06
 */
public class MemPoolProtein extends PoolProtein {


	public long getSequentialProteinIndex() {
		return sequentialProteinIndex;
	}

	private final long sequentialProteinIndex;

	public MemPoolProtein(Protein p, long idx, double ts, Hose h, long sequentialProteinIndex) {
		super(p, idx, ts, h);
		this.sequentialProteinIndex = sequentialProteinIndex;
	}

	public MemPoolProtein(PoolProtein p, Hose h, long sequentialProteinIndex) {
		super(p, h);
		this.sequentialProteinIndex = sequentialProteinIndex;
	}
}
