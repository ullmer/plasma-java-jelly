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

    static PoolCursor get(PoolAddress address) {
        PoolCursor cs = cursors.get(address);
        if (cs == null)
            try {
                cs = new PoolCursor(participate(address));
                cursors.put(address, cs);
            } catch (PoolException e) {
                Ponder.logger().severe("Couldn't connect to " + address
                                       + ": " + e.getMessage());
            }
        return cs;
    }

    PoolCursor(Hose hose) throws PoolException {
        fetcher = new MetadataFetcher(hose);
        current = -1;
    }

    static final String[] COLUMNS = {
        "_id", "timestamp", "size",
        "descrip_no", "descrip_size",
        "ingest_no", "ingests_size",
        "data_size"
    };

    @Override public short getShort(int n) {
        return (short)getLong(n);
    }

    @Override public int getInt(int n) {
        return (int)getLong(n);
    }

    @Override public long getLong(int n) {
        final ProteinMetadata md = fetcher.get(current);
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
        default: assert isNull(n);
        }
        return 0;
    }

    @Override public float getFloat(int n) {
        return (float)getDouble(n);
    }

    @Override public double getDouble(int n) {
        return (n == 1) ? getTimestamp() : (double)getLong(n);
    }

    @Override public String getString(int n) {
        final ProteinMetadata md = fetcher.get(current);
        if (md == null) return "";
        switch (n) {
        case 0: return String.format("%-10d", md.index());
        case 1: return Double.toString(md.timestamp());
        case 2: return formatSize(md.size());
        case 3: return String.format("%3d d", md.descripsNumber());
        case 4: return formatSize(md.descripsSize());
        case 5: return String.format("%3d i", md.ingestsNumber());
        case 6: return formatSize(md.ingestsSize());
        case 7: return formatSize(md.dataSize());
        default: assert isNull(n);
        }
        return "";
    }

    @Override public byte[] getBlob(int n) {
        throw new UnsupportedOperationException("getBlob is not supported");
    }

    @Override public void copyStringToBuffer(int n, CharArrayBuffer buffer) {
        buffer = new CharArrayBuffer(getString(n).toCharArray());
    }

    private double getTimestamp() {
        final ProteinMetadata md = fetcher.get(current);
        return (md == null) ? 0 : md.timestamp();
    }

    @Override public void deactivate() {
        close();
        for (DataSetObserver o : dataObservers) o.onInvalidated();
    }

    @Override public boolean isNull(int n) {
        return n < 0 || n >= COLUMNS.length;
    }

    @Override public boolean requery() {
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

    @Override public void close() {
        fetcher.close();
    }

    @Override public boolean isClosed() {
        return fetcher.isClosed();
    }

    @Override public int getCount() {
        return fetcher.count();
    }

    @Override public int getPosition() {
        return current;
    }

    @Override public boolean isFirst() {
        return current == 0;
    }

    @Override public boolean isLast() {
        return current == getCount() - 1;
    }

    @Override public boolean isBeforeFirst() {
        return current < 0;
    }

    @Override public boolean isAfterLast() {
        return current >= fetcher.count();
    }

    @Override public boolean move(int offset) {
        int n = current + offset;
        final boolean result = n > -1 && n < getCount();
        if (!result) {
            if (n < 0) n = -1; else n = getCount();
        }
        current = n;
        return result;
    }

    @Override public boolean moveToPosition(int n) {
        if (n < -1 || n > fetcher.count()) return false;
        current = n;
        return true;
    }

    @Override public boolean moveToFirst() {
        current = 0;
        return fetcher.count() > 0;
    }

    @Override public boolean moveToLast() {
        current = fetcher.count() - 1;
        return fetcher.count() > 0;
    }

    @Override public boolean moveToNext() {
        if (current == fetcher.count()) return false;
        ++current;
        return true;
    }

    @Override public boolean moveToPrevious() {
        if (current < 0) return false;
        --current;
        return true;
    }

    @Override public int getColumnIndex(String name) {
        for (int i = 0; i < COLUMNS.length; ++i)
            if (COLUMNS[i].equals(name)) return i;
        return -1;
    }

    @Override public int getColumnIndexOrThrow(String name)
        throws IllegalArgumentException {
        final int col = getColumnIndex(name);
        if (col < 0) throw new IllegalArgumentException();
        return col;
    }

    @Override public String getColumnName(int n) {
        return (n < 0 || n >= COLUMNS.length) ? null : COLUMNS[n];
    }

    @Override public String[] getColumnNames() {
        return COLUMNS;
    }

    @Override public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override public void registerContentObserver(ContentObserver o) {
        contentObservers.add(o);
    }

    @Override public void unregisterContentObserver(ContentObserver o) {
        contentObservers.remove(o);
    }

    @Override public void registerDataSetObserver(DataSetObserver o) {
        dataObservers.add(o);
    }

    @Override public void unregisterDataSetObserver(DataSetObserver o) {
        dataObservers.remove(o);
    }

    @Override public void setNotificationUri(ContentResolver cr, Uri uri) {

    }

    @Override public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override public Bundle getExtras() {
        return Bundle.EMPTY;
    }

    @Override public Bundle respond(Bundle bundle) {
        return Bundle.EMPTY;
    }

    private static String formatSize(long number) {
        float result = number;
        String suffix = "b";
        if (result > 900) {
            suffix = "Kb";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "Mb";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "Gb";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "Tb";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "Pb";
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f %s", result, suffix);
        } else if (result < 10) {
            value = String.format("%.1f %s", result, suffix);
        } else if (result < 100) {
            value = String.format("%.0f %s", result, suffix);
        } else {
            value = String.format("%.0f %s", result, suffix);
        }
        return value;
    }


    private final Set<ContentObserver> contentObservers =
        new HashSet<ContentObserver>();
    private final Set<DataSetObserver> dataObservers =
        new HashSet<DataSetObserver>();
    private final MetadataFetcher fetcher;
    private int current;

    private static final HashMap<PoolAddress, PoolCursor> cursors =
        new HashMap<PoolAddress, PoolCursor>();

}
