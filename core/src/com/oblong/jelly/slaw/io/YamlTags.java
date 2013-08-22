// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.oblong.jelly.NumericIlk;
import static com.oblong.jelly.NumericIlk.*;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;
import com.oblong.jelly.util.ExceptionHandler;

import static com.oblong.jelly.SlawIlk.*;

final class YamlTags {

    static final String YAML_NS = "!!";
    static final String OB_NS = "!";

    static final String NADA_YT = "nada";
    static final String BINARY_YT = "binary";
    static final String NIL_YT = "null";
    static final String BOOL_YT = "bool";
    static final String STRING_YT = "string";
    static final String YSTRING_YT = "str";
    static final String PROTEIN_YT = "protein";
    static final String CONS_YT = "cons";
    static final String MAP_YT = "omap";
    static final String UMAP_YT = "map";
    static final String LIST_YT = "seq";
    static final String NUMBER_YT = "";
    static final String COMPLEX_YT = "complex";
    static final String VECTOR_YT = "vector";
    static final String MVECTOR_YT = "multivector";
    static final String ARRAY_YT = "array";
    static final String I8_NYT = "i8";
    static final String I16_NYT = "i16";
    static final String I32_NYT = "i32";
    static final String I64_NYT = "i64";
    static final String U8_NYT = "u8";
    static final String U16_NYT = "u16";
    static final String U32_NYT = "u32";
    static final String U64_NYT = "u64";
    static final String F32_NYT = "f32";
    static final String F64_NYT = "f64";

    static final String INT_YT = "int";
    static final String FLOAT_YT = "float";

    static final String INGESTS_KEY = "ingests";
    static final String DESCRIPS_KEY = "descrips";
    static final String DATA_KEY = "rude_data";

    static final String EOED = "--- " + OB_NS + NADA_YT + "\n";

    static boolean isNil(String tag) { return NIL_YT.equals(tag); }
    static boolean isBool(String tag) { return BOOL_YT.equals(tag); }
    static boolean isString(String tag) {
        return STRING_YT.equals(tag) || BINARY_YT.equals(tag);
    }
    static boolean isYamlString(String tag) { return YSTRING_YT.equals(tag); }

    static boolean isComplex(String tag) { return COMPLEX_YT.equals(tag); }
    static boolean isVector(String tag) { return VECTOR_YT.equals(tag); }
    static boolean isMVector(String tag) { return MVECTOR_YT.equals(tag); }
    static boolean isArray(String tag) { return ARRAY_YT.equals(tag); }
    static boolean isList(String tag) { return LIST_YT.equals(tag); }
    static boolean isCons(String tag) { return CONS_YT.equals(tag); }
    static boolean isOMap(String tag) { return MAP_YT.equals(tag); }
    static boolean isUMap(String tag) { return UMAP_YT.equals(tag); }
    static boolean isMap(String tag) { return isUMap(tag) || isOMap(tag); }
    static boolean isProtein(String tag) { return PROTEIN_YT.equals(tag); }

    static String tag(Slaw s) {
        if (s.count() == 0 && s.isArray()) return OB_NS + EMPTY_TAGS.get(s);
        return tag(s.ilk(), s.numericIlk());
    }

    static String tag(SlawIlk ilk, NumericIlk ni) {
        return (ilk == NUMBER)
            ? OB_NS + NUMERIC_TAGS.get(ni)
            : (IN_YAML_NS.contains(ilk) ? YAML_NS : OB_NS) + TAGS.get(ilk);
    }

    static String rawTag(String tag) {
        if (tag == null) return "";
        int i = tag.indexOf(":slaw/");
        if (i < 0) i = tag.lastIndexOf(':'); else i += 5;
        if (i < 0) i = tag.lastIndexOf('!');
        return i < 0 ? tag : tag.substring(i + 1);
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
        if (ilk != NUMBER_ARRAY) num += "/";
        String nstr;
        if (ni.isIntegral()) nstr = ni.isSigned() ? "i" : "u";
        else nstr = "f";
        return "empty/" + num + nstr + ni.bytes();
    }

    static NumericIlk numericIlk(String tag) {
        return NUMERIC_ILKS.get(tag);
    }

    static Slaw getEmptyArray(String tag) {
        return EMPTY_ARRAYS.get(tag);
    }

    static final Map<SlawIlk, String> TAGS;
    static final Map<NumericIlk, String> NUMERIC_TAGS;
    static final Set<SlawIlk> IN_YAML_NS;

    static final Map<String, Slaw> EMPTY_ARRAYS;
    static final Map<Slaw, String> EMPTY_TAGS;

    static final Map<String, NumericIlk> NUMERIC_ILKS;

    static {
        IN_YAML_NS = EnumSet.of(NIL, BOOL, STRING, MAP, LIST);

        TAGS = new EnumMap<SlawIlk, String>(SlawIlk.class);
        TAGS.put(NIL, NIL_YT);
        TAGS.put(BOOL, BOOL_YT);
        TAGS.put(STRING, STRING_YT);
        TAGS.put(PROTEIN, PROTEIN_YT);
        TAGS.put(CONS, CONS_YT);
        TAGS.put(MAP, MAP_YT);
        TAGS.put(LIST, LIST_YT);
        TAGS.put(NUMBER, NUMBER_YT);
        TAGS.put(COMPLEX, COMPLEX_YT);
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

        NUMERIC_ILKS = new HashMap<String, NumericIlk>();
        for (NumericIlk ni : NUMERIC_TAGS.keySet())
            NUMERIC_ILKS.put(NUMERIC_TAGS.get(ni), ni);
        NUMERIC_ILKS.put(INT_YT, INT64);
        NUMERIC_ILKS.put(FLOAT_YT, FLOAT64);

        EMPTY_ARRAYS = new HashMap<String, Slaw>();
        EMPTY_TAGS = new HashMap<Slaw, String>();
        for (SlawIlk i : SlawIlk.arrayIlks()) {
            final int maxDim = i.maxDimension();
            for (int d = i.minDimension(); d <= maxDim; ++d) {
                for (NumericIlk ni : NumericIlk.values()) {
                    final String tag = emptyArrayTag(i, ni, d);
                    try {
                        final Slaw s = Slaw.array(i, ni, d);
                        EMPTY_ARRAYS.put(tag, s);
                        EMPTY_TAGS.put(s, tag);
                    } catch (Throwable e) {
                        ExceptionHandler.handleException(e);
                        System.out.println("Auch: " + e + "i: " + i +
                                           ", ni: " + ni + ", d: " + d);
                    }
                }
            }
        }
    }

    private YamlTags() {}

}
