package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.slaw.java.SlawMap;
import com.oblong.util.logging.ObLog;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/12/13
 * Time: 2:29 PM
 *
 */
public class RawSlawMapField extends AbstractField<SlawMap> {

    private final ObLog log = ObLog.get(this);

    public RawSlawMapField(String name) {
        super(name);
    }

    public RawSlawMapField(SlawSchema schema, boolean isOptional, String name) {
        super(schema, isOptional, name);
    }

    @Override
    protected SlawMap fromSlaw_Custom(Slaw slaw) {
        return (SlawMap) slaw;
    }

    @Override
    protected Slaw toSlaw_Custom(SlawMap value) {
        return value;
    }

}
