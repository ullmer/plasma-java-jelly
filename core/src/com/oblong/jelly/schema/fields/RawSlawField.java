package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.util.logging.ObLog;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/12/13
 * Time: 2:29 PM
 *
 */
public class RawSlawField extends AbstractField<Slaw> {

    private final ObLog log = ObLog.get(this);

    public RawSlawField(String name) {
        super(name);
    }

    public RawSlawField(SlawSchema schema, boolean isOptional, String name) {
        super(schema, isOptional, name);
    }

    @Override
    protected Slaw fromSlaw_Custom(Slaw slaw) {
        return slaw;
    }

    @Override
    protected Slaw toSlaw_Custom(Slaw value) {
        return value;
    }

}
