// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Base64;
//import org.yaml.snakeyaml.util.Base64Coder; //deprecated

import com.oblong.jelly.Slaw;
import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.SlawIO.YamlOptions;
import com.oblong.jelly.Protein;

import static com.oblong.jelly.SlawIlk.*;

import com.oblong.jelly.slaw.SlawExternalizer;


public final class YamlExternalizer implements SlawExternalizer {

    private class Base64Mapper { 
      public String encode(String inputStr) {
        String encodedString = Base64.getEncoder().encodeToString(inputStr.getBytes());
        return encodedString;
      }
    }
    public static SlawExternalizer rawExternalizer() {
        return new YamlExternalizer(new YamlOptions(true, false));
    }

    public YamlExternalizer(YamlOptions options) {
        this.options = options;
        linearList = false;
    }

    @Override public final void extern(Slaw s, OutputStream os)
        throws IOException {
        extern(s, os, "", "");
    }

    void extern(Slaw s, OutputStream os, String prefix, String suffix)
        throws IOException {
        maybeTag(s, os);
        switch (s.ilk()) {
        case BOOL: externBool(s, os); break;
        case STRING: externString(s, os); break;
        case NUMBER: externNumber(s, os); break;

        case COMPLEX: case NUMBER_VECTOR: case COMPLEX_VECTOR:
        case MULTI_VECTOR: case NUMBER_ARRAY: case COMPLEX_ARRAY:
            externShortList(s, os); break;

        case VECTOR_ARRAY: case COMPLEX_VECTOR_ARRAY:
        case MULTI_VECTOR_ARRAY:
            externList(s, os, prefix); break;

        case CONS: externCons(s, os, prefix); break;
        case LIST: externList(s, os, prefix); break;
        case MAP: externMap(s, os, prefix); break;
        case PROTEIN: externProtein(s.toProtein(), os, prefix); break;
        default: assert s.ilk() == NIL : "Unexpected ilk: " + s.ilk(); break;
        }
        write(os, suffix);
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
            if (!options.emitTags()) n = Slaw.int64(n.emitLong());
            if (ni.bytes() > 4 && ni.isSigned()) {
                write(os, n.emitBigInteger().toString());
            } else {
                write(os, Long.toString(n.emitLong(), 10));
            }
        } else {
            if (!options.emitTags()) n = Slaw.float64(n.emitDouble());
            write(os, Double.toString(n.emitDouble()));
        }
    }

    void externCons(Slaw s, OutputStream os, String pr) throws IOException {
        write(os, "{");
        extern(s.car(), os, pr + "   ", " : ");
        extern(s.cdr(), os, pr + "       ", "}");
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
        if (linearList) {
            externShortList(s, os);
        } else if (s.count() > 0) {
            for (Slaw ss : s) {
                write(os, "\n" + pr + "- ");
                extern(ss, os, pr + "       ", "");
            }
        } else {
            write(os, "[]");
        }
    }

    void externShortMap(Slaw s, OutputStream os) throws IOException {
        final int c = s.count();
        write(os, "[");
        for (int i = 0; i < c; ++i) {
            externCons(s.nth(i), os, "");
            if (i < c - 1) write(os, ", ");
        }
        write(os, "]");
    }

    void externMap(Slaw s, OutputStream os, String pr) throws IOException {
        final int c = s.count();
        if (c > 0) {
            if (linearList) {
                externShortMap(s, os);
            } else {
                final boolean old = linearList;
                linearList = true;
                for (Slaw ss : s) {
                    write(os, "\n  " + pr + "- ");
                    extern(ss.car(), os, pr + "     ", " : ");
                    extern(ss.cdr(), os, pr + "         ", "");
                }
                linearList = old;
            }
        } else {
            write(os, "{}");
        }
    }

    void externProtein(Protein p, OutputStream os, String pr)
        throws IOException {
        final Slaw ingests = p.ingests();
        final Slaw descrips = p.descrips();
        if (ingests == null && descrips == null && p.dataLength() == 0) {
            write(os, "{}");
        } else {
            if (ingests != null) {
                write(os, "\n" + pr + YamlTags.INGESTS_KEY + ": ");
                extern(ingests, os, pr + "  ", "");
            }
            if (descrips != null){
                write(os, "\n" + pr + YamlTags.DESCRIPS_KEY + ": ");
                extern(descrips, os, pr + "  ", "");
            }
            if (p.dataLength() > 0) {
                write(os, "\n" + pr + YamlTags.DATA_KEY
                          + ": !!binary |-\n" + pr + "    ");
                write(os, pr);

		Base64Mapper b64m;
		System.out.println("YamlExternalizer externProtein: further porting required");
                //for (char c : b64m.encode(p.copyData()))
                //    os.write((byte)c);
            }
        }
    }

    private static OutputStream write(OutputStream os, String s)
        throws IOException {
        os.write(s.getBytes("UTF-8"));
        return os;
    }

    private void maybeTag(Slaw s, OutputStream os) throws IOException {
        final boolean emitTags = options.emitTags();
        if (!s.isNumber() || emitTags) {
            write(os, YamlTags.tag(s) + " ");
        }
    }

    private final YamlOptions options;
    private boolean linearList;
}
