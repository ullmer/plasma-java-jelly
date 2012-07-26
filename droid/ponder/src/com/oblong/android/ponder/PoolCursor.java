// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import com.oblong.jelly.Hose;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.ProteinMetadata;

import static com.oblong.jelly.Pool.participate;

/**
 *
 * Created: Thu Dec  9 13:45:00 2010
 *
 * @author jao
 */
final class PoolCursor implements Cursor {

    PoolCursor(Hose hose) throws PoolException {
        fetcher = new MetadataFetcher(hose);
        current = -1;
    }

     public short getShort(int n) {
        return (short)getLong(n);
    }

     public int getInt(int n) {
        return (int)getLong(n);
    }

     public long getLong(int n) {
        final ProteinMetadata md = currentMeta();
        if (md == null) return 0;
        switch (n) {
        case 0: return md.index();
        case 1: return (long)md.timestamp();
        case 2: return md.size();
        case 3: return md.descripsNumber();
        case 4: return md.descripsSize();
        case 5: return md.ingestsNumber();
        case 6: return md.ingestsSize();
        case 7: return md.dataSize();
        case 8: return 0;
        default: assert isNull(n);
        }
        return 0;
    }

     public float getFloat(int n) {
        return (float)getDouble(n);
    }

     public double getDouble(int n) {
        return (n == 1) ? getTimestamp() : (double)getLong(n);
    }

     public String getString(int n) {
        final ProteinMetadata md = currentMeta();
        if (md == null) return null;
        switch (n) {
        case 0: return String.format("%d", md.index());
        case 1: return Double.toString(md.timestamp());
        case 2: return Utils.formatSize(md.size());
        case 3: return String.format("%3d d", md.descripsNumber());
        case 4: return Utils.formatSize(md.descripsSize());
        case 5: return String.format("%3d i", md.ingestsNumber());
        case 6: return Utils.formatSize(md.ingestsSize());
        case 7: return Utils.formatSize(md.dataSize());
        case 8: return String.format("%3d (%s) %3d (%s)  %s",
                                     md.descripsNumber(),
                                     Utils.formatSize(md.descripsSize()),
                                     md.ingestsNumber(),
                                     Utils.formatSize(md.ingestsSize()),
                                     Utils.formatSize(md.size()));
        default: assert isNull(n);
        }
        return null;
    }

     public byte[] getBlob(int n) {
        throw new UnsupportedOperationException("getBlob is not supported");
    }

     public void copyStringToBuffer(int n, CharArrayBuffer buffer) {
        String result = getString(n);
        if (result != null) {
            final char[] data = buffer.data;
            if (data == null || data.length < result.length()) {
                buffer.data = result.toCharArray();
            } else {
                result.getChars(0, result.length(), data, 0);
            }
            buffer.sizeCopied = result.length();
        }
    }

     public void deactivate() {
        close();
        for (DataSetObserver o : dataObservers) o.onInvalidated();
    }

     public boolean isNull(int n) {
        return n < 0 || n >= COLUMNS.length;
    }

     public boolean requery() {
        try {
            fetcher.requery();
        } catch (PoolException e) {
            Ponder.logger().severe("Cursor query failed: " + e.getMessage());
            return false;
        }
        for (DataSetObserver o : dataObservers) o.onChanged();
        for (ContentObserver o : contentObservers) o.onChange(false);
        return true;
    }

     public void close() {
        fetcher.close();
    }

     public boolean isClosed() {
        return fetcher.isClosed();
    }

     public int getCount() {
        return fetcher.count();
    }

     public int getPosition() {
        return current;
    }

     public boolean isFirst() {
        return current == 0;
    }

     public boolean isLast() {
        return current == getCount() - 1;
    }

     public boolean isBeforeFirst() {
        return current < 0;
    }

     public boolean isAfterLast() {
        return current >= fetcher.count();
    }

     public boolean move(int offset) {
        int n = current + offset;
        final boolean result = n > -1 && n < getCount();
        if (!result) {
            if (n < 0) n = -1; else n = getCount();
        }
        current = n;
        return result;
    }

     public boolean moveToPosition(int n) {
        if (n < -1 || n > fetcher.count()) return false;
        current = n;
        return true;
    }

     public boolean moveToFirst() {
        current = 0;
        return fetcher.count() > 0;
    }

     public boolean moveToLast() {
        current = fetcher.count() - 1;
        return fetcher.count() > 0;
    }

     public boolean moveToNext() {
        if (current == fetcher.count()) return false;
        ++current;
        return true;
    }

     public boolean moveToPrevious() {
        if (current < 0) return false;
        --current;
        return true;
    }

     public int getColumnIndex(String name) {
        for (int i = 0; i < COLUMNS.length; ++i)
            if (COLUMNS[i].equals(name)) return i;
        return -1;
    }

     public int getColumnIndexOrThrow(String name)
        throws IllegalArgumentException {
        final int col = getColumnIndex(name);
        if (col < 0) throw new IllegalArgumentException();
        return col;
    }

     public String getColumnName(int n) {
        return (n < 0 || n >= COLUMNS.length) ? null : COLUMNS[n];
    }

     public String[] getColumnNames() {
        return COLUMNS;
    }

     public int getColumnCount() {
        return COLUMNS.length;
    }

     public void registerContentObserver(ContentObserver o) {
        contentObservers.add(o);
    }

     public void unregisterContentObserver(ContentObserver o) {
        contentObservers.remove(o);
    }

     public void registerDataSetObserver(DataSetObserver o) {
        dataObservers.add(o);
    }

     public void unregisterDataSetObserver(DataSetObserver o) {
        dataObservers.remove(o);
    }

     public void setNotificationUri(ContentResolver cr, Uri uri) {

    }

     public boolean getWantsAllOnMoveCalls() {
        return false;
    }

     public Bundle getExtras() {
        return Bundle.EMPTY;
    }

     public Bundle respond(Bundle bundle) {
        return Bundle.EMPTY;
    }

    static final String[] COLUMNS = {
        "_id", "timestamp", "size",
        "descrip_no", "descrip_size",
        "ingest_no", "ingests_size",
        "data_size", "info"
    };

    long firstIndex() {
        return fetcher.firstIndex();
    }

    long lastIndex() {
        return fetcher.lastIndex();
    }

    void prepareForAdapter() {
        fetcher.prepareForAdapter();
    }

    long getProteinIndex() {
        return getLong(0);
    }

    private double getTimestamp() {
        final ProteinMetadata md = fetcher.get(current);
        return (md == null) ? 0 : md.timestamp();
    }

    private ProteinMetadata currentMeta() {
        return fetcher.get(fetcher.count() - current - 1);
    }

    private final Set<ContentObserver> contentObservers =
        new HashSet<ContentObserver>();
    private final Set<DataSetObserver> dataObservers =
        new HashSet<DataSetObserver>();
    private final MetadataFetcher fetcher;
    private int current;

}
