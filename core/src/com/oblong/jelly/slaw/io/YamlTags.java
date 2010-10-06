// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

import static com.oblong.jelly.NumericIlk.*;
import static com.oblong.jelly.SlawIlk.*;

final class YamlTags {

    static final String NIL_YT = "!!null";
    static final String BOOL_YT = "!!bool";
    static final String STRING_YT = "!!string";
    static final String PROTEIN_YT = "!protein";
    static final String CONS_YT = "!cons";
    static final String MAP_YT = "!!omap";
    static final String UMAP_YT = "!!map";
    static final String LIST_YT = "!list";
    static final String NUMBER_YT = "";
    static final String COMPLEX_YT = "!complex";
    static final String VECTOR_YT = "!vector";
    static final String MVECTOR_YT = "!multivector";
    static final String ARRAY_YT = "!array";
    static final String I8_NYT = "!i8";
    static final String I16_NYT = "!i16";
    static final String I32_NYT = "!i32";
    static final String I64_NYT = "!i64";
    static final String U8_NYT = "!u8";
    static final String U16_NYT = "!u16";
    static final String U32_NYT = "!u32";
    static final String U64_NYT = "!u64";
    static final String F32_NYT = "!f32";
    static final String F64_NYT = "!f64";

    static String tag(Slaw s) {
        if (s.count() == 0 && s.isArray())
            return EMPTY_TAGS.get(s);
        return tag(s.ilk(), s.numericIlk());
    }

    static String tag(SlawIlk ilk, NumericIlk ni) {
        return (ilk == NUMBER) ? NUMERIC_TAGS.get(ni) : TAGS.get(ilk);
    }

    static String emptyArrayTag(SlawIlk ilk, NumericIlk ni, int dim) {
        assert ilk.isArray();
        String num = "";
        switch (ilk) {
        case NUMBER_ARRAY: break;
        case COMPLEX_ARRAY: num = "complex"; break;
        case VECTOR_ARRAY: num = "vector/" + dim; break;
        case COMPLEX_VECTOR_ARRAY: num = "vector/" + dim + "/complex"; break;
        case MULTI_VECTOR_ARRAY: num = "multivector/" + dim; break;
        default: assert false : "Non-array ilk " + ilk; break;
        }
        String nstr;
        if (ni.isIntegral()) nstr = ni.isSigned() ? "i" : "u";
        else nstr = "f";
        return "!empty/" + num + "/" + nstr + ni.bytes();
    }

    static SlawIlk ilk(String tag) {
        return null;
    }

    static NumericIlk numericIlk(String tag) {
        return null;
    }

    static final Map<SlawIlk, String> TAGS;
    static final Map<NumericIlk, String> NUMERIC_TAGS;

    static final Map<String, Slaw> EMPTY_ARRAYS;
    static final Map<Slaw, String> EMPTY_TAGS;

    // static final Map<String, SlawIlk> ILKS;
    // static final Map<String, NumericIlk> NUMERIC_ILKS;

    static {
        TAGS = new EnumMap<SlawIlk, String>(SlawIlk.class);
        TAGS.put(NIL, NIL_YT);
        TAGS.put(BOOL, BOOL_YT);
        TAGS.put(STRING, STRING_YT);
        TAGS.put(PROTEIN, PROTEIN_YT);
        TAGS.put(CONS, CONS_YT);
        TAGS.put(MAP, MAP_YT);
        TAGS.put(LIST, LIST_YT);
        TAGS.put(NUMBER, NUMBER_YT);
        TAGS.put(NUMBER_VECTOR, VECTOR_YT);
        TAGS.put(COMPLEX_VECTOR, VECTOR_YT);
        for (SlawIlk i : SlawIlk.arrayIlks()) TAGS.put(i, ARRAY_YT);

        NUMERIC_TAGS = new EnumMap<NumericIlk, String>(NumericIlk.class);
        NUMERIC_TAGS.put(INT8, I8_NYT);
        NUMERIC_TAGS.put(INT16, I16_NYT);
        NUMERIC_TAGS.put(INT32, I32_NYT);
        NUMERIC_TAGS.put(INT64, I64_NYT);
        NUMERIC_TAGS.put(UNT8, U8_NYT);
        NUMERIC_TAGS.put(UNT16, U16_NYT);
        NUMERIC_TAGS.put(UNT32, U32_NYT);
        NUMERIC_TAGS.put(UNT64, U64_NYT);
        NUMERIC_TAGS.put(FLOAT32, F32_NYT);
        NUMERIC_TAGS.put(FLOAT64, F64_NYT);

        EMPTY_ARRAYS = new HashMap<String, Slaw>();
        EMPTY_TAGS = new HashMap<Slaw, String>();
        for (SlawIlk i : SlawIlk.arrayIlks()) {
            final int maxDim = i.maxDimension();
            for (int d = i.minDimension(); d <= maxDim; ++d) {
                for (NumericIlk ni : NumericIlk.values()) {
                    final String tag = emptyArrayTag(i, ni, d);
                    final Slaw s = Slaw.array(i, ni, d);
                    EMPTY_ARRAYS.put(tag, s);
                    EMPTY_TAGS.put(s, tag);
                }
            }
        }
    }

    private YamlTags() {}

}
