// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.List;

import com.oblong.jelly.Hose;
import com.oblong.jelly.MetadataRequest;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.ProteinMetadata;

/**
 *
 * Created: Thu Dec  9 20:05:23 2010
 *
 * @author jao
 */
final class MetadataFetcher {

    MetadataFetcher(Hose hose) throws PoolException {
        this.hose = hose;
        for (int i = 0; i < pages.length; ++i) pages[i] = new Page();
        requery();
    }

    void close() {
        if (hose.isConnected()) hose.withdraw();
    }

    boolean isClosed() {
        return hose.isConnected();
    }

    int count() {
        return (int)(1 + lastIndex - firstIndex);
    }

    void requery() throws PoolException {
        firstIndex = hose.oldestIndex();
        lastIndex = hose.newestIndex();
        for (int i = 0; i < pages.length; ++i) pages[i].pageNumber = -1;
        prefetchPage(0);
    }

    ProteinMetadata get(int pos) {
        final Page p = getPage(pos);
        if (p == null) return null;
        final int off = pageOffset(pos);
        if (off > PREFETCH_THRESHOLD) prefetchPage(pos + 1);
        synchronized (p) {
            return p.data[off];
        }
    }

    private static class Page {
        public int pageNumber = -1;
        public ProteinMetadata[] data = null;
    }

    private static int pos2page(int pos) {
        return pos / PAGE_SIZE;
    }

    private static int pageOffset(int pos) {
        return pos % PAGE_SIZE;
    }

    private static final int PAGE_SIZE = 50;
    private static final int PREFETCH_THRESHOLD = 32;
    private static final int PAGE_NO = 3;

    private Page findPage(int pn) {
        for (int i = 0; i < pages.length; ++i)
            if (pages[i].pageNumber == pn) return pages[i];
        return null;
    }

    private Page getPage(int pn) {
        final Page p = findPage(pn);
        return p == null ? fetchPage(selectPage(pn)) : p;
    }

    private Page fetchPage(final Page p) {
        try {
            final MetadataRequest[] r =
                MetadataRequest.range(firstIndex(p), PAGE_SIZE);
            final List<ProteinMetadata> md = hose.metadata(r);
            final int no = md.size();
            for (int i = 0; i < no; ++i) p.data[i] = md.get(i);
            for (int i = no; i < p.data.length; ++i) p.data[i] = null;
            return p;
        } catch (PoolException e) {
            p.pageNumber = -1;
            return null;
        }
    }

    private void prefetchPage(int pn) {
        if (null == findPage(pn)) {
            final Page p = selectPage(pn);
            new Thread(new Runnable () {
                    public void run() {
                        synchronized (p) { fetchPage(p); }
                    }
                }).start();
        }
    }

    private Page selectPage(int pn) {
        Page farthest = null;
        int delta = -1;
        for (int i = 0; i < pages.length; ++i) {
            if (pages[i].pageNumber < 0) {
                farthest = pages[i];
                break;
            } else {
                final int d = Math.abs(pn - pages[i].pageNumber);
                if (delta < d) {
                    farthest = pages[i];
                    delta = d;
                }
            }
        }
        assert farthest != null;
        farthest.pageNumber = pn;
        return farthest;
    }

    private long firstIndex(Page p) {
        if (p.pageNumber <= 0) return firstIndex;
        return firstIndex + (p.pageNumber * PAGE_SIZE);
    }

    private final Hose hose;
    private long firstIndex;
    private long lastIndex;
    private final Page[] pages = new Page[PAGE_NO];
}
