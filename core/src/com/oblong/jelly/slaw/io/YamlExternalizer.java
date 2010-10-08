// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.IOException;
import java.io.OutputStream;

import org.yaml.snakeyaml.util.Base64Coder;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.SlawIO.YamlOptions;
import com.oblong.jelly.Protein;

import static com.oblong.jelly.SlawIlk.*;

import com.oblong.jelly.slaw.SlawExternalizer;

public final class YamlExternalizer implements SlawExternalizer {

    public static SlawExternalizer rawExternalizer() {
        return new YamlExternalizer(new YamlOptions(true, false));
    }

    public YamlExternalizer(YamlOptions options) {
        this.options = options;
    }

    @Override public final void extern(Slaw s, OutputStream os)
        throws IOException {
        if (options.useDirectives()) write(os, "--- ");
        extern(s, os, "", "\n");
    }

    void extern(Slaw s, OutputStream os, String prefix, String suffix)
        throws IOException {
        write(os, prefix);
        maybeTag(s, os);
        switch (s.ilk()) {
        case NIL: externNil(s, os); break;
        case BOOL: externBool(s, os); break;
        case STRING: externString(s, os); break;
        case NUMBER: externNumber(s, os); break;

        case COMPLEX: case NUMBER_VECTOR: case COMPLEX_VECTOR:
        case MULTI_VECTOR: case NUMBER_ARRAY: case COMPLEX_ARRAY:
        case VECTOR_ARRAY: case COMPLEX_VECTOR_ARRAY: case MULTI_VECTOR_ARRAY:
            externShortList(s, os); break;

        case CONS: externCons(s, os, prefix); break;
        case LIST: externList(s, os, prefix); break;
        case MAP: externMap(s, os, prefix); break;
        case PROTEIN: externProtein(s.toProtein(), os, prefix); break;
        default: assert s.ilk() == NIL : "Unexpected ilk: " + s.ilk();
        }
        write(os, suffix);
    }

    void externNil(Slaw s, OutputStream os) throws IOException {
    }

    void externBool(Slaw s, OutputStream os) throws IOException {
        write(os, s.emitBoolean() ? "true" : "false");
    }

    void externString(Slaw s, OutputStream os) throws IOException {
        write(os, "\"");
        write(os, s.emitString().replace("\"", "\\\""));
        write(os, "\"");
    }

    void externNumber(Slaw n, OutputStream os) throws IOException {
        final NumericIlk ni = n.numericIlk();
        if (ni.isIntegral()) {
            if (ni.bytes() > 4 && ni.isSigned()) {
                write(os, n.emitBigInteger().toString());
            } else {
                write(os, Long.toString(n.emitLong(), 10));
            }
        } else {
            write(os, Double.toString(n.emitDouble()));
        }
    }

    void externCons(Slaw s, OutputStream os, String pr) throws IOException {
        extern(s.car(), os, pr + "{ ", " : ");
        extern(s.cdr(), os, "", "}");
    }

    void externShortList(Slaw s, OutputStream os) throws IOException {
        final int c = s.count();
        write(os, "[");
        for (int i = 0; i < c; ++i) {
            extern(s.nth(i), os, "", "");
            if (i < c - 1) write(os, ", ");
        }
        write(os, "]");
    }

    void externList(Slaw s, OutputStream os, String pr) throws IOException {
        if (s.count() > 0) {
            for (Slaw ss : s) extern(ss, os, "\n" + pr + "- ", "");
        } else {
            write(os, pr + "[]");
        }
    }

    void externMap(Slaw s, OutputStream os, String pr) throws IOException {
        if (s.count() > 0) {
            for (Slaw ss : s) {
                extern(ss.car(), os, "\n" + pr + "- ", " : ");
                extern(ss.cdr(), os, "", "");
            }
        } else {
            write(os, pr + "{}");
        }
    }

    void externProtein(Protein p, OutputStream os, String pr)
        throws IOException {
        final Slaw ingests = p.ingests();
        final Slaw descrips = p.descrips();
        if (ingests == null && descrips == null && p.dataLength() == 0) {
            write(os, pr + " {}");
        } else {
            if (ingests != null) {
                write(os, "\n" + pr + YamlTags.INGESTS_KEY + ":");
                extern(ingests, os, pr + "  ", "");
            }
            if (descrips != null){
                write(os, "\n" + pr + YamlTags.DESCRIPS_KEY + ":");
                extern(descrips, os, pr + "  ", "");
            }
            if (p.dataLength() > 0) {
                write(os, "\n" + pr + YamlTags.DATA_KEY + ": |\n" + pr);
                write(os, pr);
                for (char c : Base64Coder.encode(p.copyData()))
                    os.write((byte)c);
            }
        }
    }

    private static OutputStream write(OutputStream os, String s)
        throws IOException {
        try {
            os.write(s.getBytes("UTF-8"));
        } catch (Exception e) {
            // this should hardly happen, if at all
            os.write(s.getBytes());
        }
        return os;
    }

    private void maybeTag(Slaw s, OutputStream os) throws IOException {
        final boolean emitTags = options.emitTags();
        if (!s.isNumber() || emitTags) { write(os, YamlTags.tag(s) + " "); }
    }

    final YamlOptions options;
}
