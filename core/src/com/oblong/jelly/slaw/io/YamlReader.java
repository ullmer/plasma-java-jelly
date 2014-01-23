// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.oblong.util.ExceptionHandler;
import net.jcip.annotations.NotThreadSafe;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.util.Base64Coder;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIO;
import com.oblong.jelly.SlawReader;
import com.oblong.jelly.slaw.SlawFactory;

@NotThreadSafe
final class YamlReader implements SlawReader {

    public YamlReader(UnicodeReader reader, SlawFactory f) {
        Iterable<Node> it = new Yaml().composeAll(reader);
        nodes = it == null ? null : it.iterator();
        next = null;
        factory = f;
    }

    @Override public boolean hasNext() {
        if (next == null) next = fetchNext();
        return next != null;
    }

    @Override public Slaw next() {
        final Slaw result = hasNext() ? next : null;
        next = null;
        return result;
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override public boolean close() {
        nodes = null;
        next = null;
        return true;
    }

    @Override public SlawIO.Format format() { return SlawIO.Format.YAML; }

    private Slaw fetchNext() {
        try {
            if (nodes == null || !nodes.hasNext()) return null;
            Slaw s = parse(nodes.next());
            while (s == null && nodes.hasNext()) s = parse(nodes.next());
            return s;
        } catch (Throwable e) { // hasNext() may throw
            ExceptionHandler.handleException(e);
            reportError(e.getMessage());
            return null;
        }
    }

    private Slaw parse(Node node) {
        if (node == null) return null;
        final String tag = YamlTags.rawTag(node.getTag().getValue());
        Slaw s = null;
        if (node instanceof ScalarNode)
            s = parseScalar(tag, ((ScalarNode)node).getValue());
        else if (node instanceof SequenceNode)
            s = parseSeq(tag, ((SequenceNode)node).getValue());
        else if (node instanceof MappingNode)
            s = parseMap(tag, ((MappingNode)node).getValue());
        if (s == null && !YamlTags.NADA_YT.equals(tag))
            reportError("Unrecognised node: " + node + "(tag: " + tag + ")");
        return s;
    }

    private Slaw parseScalar(String tag, String nv) {
        final NumericIlk ni = YamlTags.numericIlk(tag);
        if (ni != null) return parseNumber(ni, nv);
        if (YamlTags.isNil(tag)) return factory.nil();
        if (YamlTags.isYamlString(tag)) return parseYamlString(nv);
        if (YamlTags.isString(tag)) return factory.string(nv);
        if (YamlTags.isBool(tag)) return factory.bool(Boolean.valueOf(nv));
        return null;
    }

    private Slaw parseSeq(String tag, List<Node> nodes) {
        final Slaw s = YamlTags.getEmptyArray(tag);
        if (s != null) return s;
        final Slaw[] children = toSlawx(nodes);
        if (YamlTags.isComplex(tag)) return readComplex(children);
        if (YamlTags.isList(tag)) return readList(children);
        if (YamlTags.isVector(tag)) return readVector(children);
        if (YamlTags.isMVector(tag)) return readMVector(children);
        if (YamlTags.isArray(tag)) return readArray(children);
        if (YamlTags.isOMap(tag)) return readOMap(children);
        return null;
    }

    private Slaw parseMap(String tag, List<NodeTuple> pairs) {
        if (YamlTags.isCons(tag)) return parseCons(pairs);
        if (YamlTags.isUMap(tag)) return parseUMap(pairs);
        if (YamlTags.isProtein(tag)) return parseProtein(pairs);
        return null;
    }

    private Slaw parseNumber(NumericIlk ni, String val) {
        if (ni.isIntegral())
            return ni == NumericIlk.UNT64
                ? factory.number(new BigInteger(val))
                : factory.number(ni, new Long(val));
        return factory.number(ni, new Double(val));
    }

    private Slaw parseYamlString(String val) {
        try {
            return parseNumber(NumericIlk.FLOAT64, val);
        } catch (NumberFormatException e) {
            return factory.string(val);
        }
    }

    private Slaw parseCons(List<NodeTuple> pairs) {
        if (pairs.size() != 1) return null;
        final Slaw car = parse(pairs.get(0).getKeyNode());
        final Slaw cdr = parse(pairs.get(0).getValueNode());
        return (car == null || cdr == null) ? null : factory.cons(car, cdr);
    }

    private Slaw parseUMap(List<NodeTuple> pairs) {
        final List<Slaw> cmps = new ArrayList<Slaw>(pairs.size() * 2);
        for (NodeTuple kv : pairs) {
            cmps.add(parse(kv.getKeyNode()));
            cmps.add(parse(kv.getValueNode()));
        }
        return factory.map(cmps);
    }

    private Slaw parseProtein(List<NodeTuple> pairs) {
        final Slaw INGK = Slaw.string(YamlTags.INGESTS_KEY);
        final Slaw DESK = Slaw.string(YamlTags.DESCRIPS_KEY);
        final Slaw DATK = Slaw.string(YamlTags.DATA_KEY);
        final Slaw s = parseUMap(pairs);
        if (s == null) return null;
        byte[] data = null;
        final Slaw ds = s.find(DATK);
        if (ds != null && ds.isString())
            data = Base64Coder.decode(ds.emitString().toCharArray());
        return factory.protein(s.find(DESK), s.find(INGK), data);
    }

    private Slaw readComplex(Slaw[] slawx) {
        try {
            return factory.complex(slawx[0], slawx[1]);
        } catch (Throwable e) {
            ExceptionHandler.handleException(e);
            return null;
        }
    }

    private Slaw readList(Slaw[] slawx) {
        return factory.list(slawx);
    }

    private Slaw readVector(Slaw[] slawx) {
        try {
            return factory.vector(slawx);
        } catch (Throwable e) {
            ExceptionHandler.handleException(e);
            return null;
        }
    }

    private Slaw readMVector(Slaw[] slawx) {
        try {
            return factory.multivector(slawx);
        } catch (Throwable e) {
            ExceptionHandler.handleException(e);
            return null;
        }
    }

    private Slaw readArray(Slaw[] slawx) {
        return factory.array(slawx);
    }

    private Slaw readOMap(Slaw[] maps) {
        List<Slaw> slawx = new ArrayList<Slaw>(maps.length * 2);
        for (Slaw m : maps) {
            if (m.count() != 1) return null;
            for (Slaw kv : m) {
                slawx.add(kv.car());
                slawx.add(kv.cdr());
            }
        }
        return factory.map(slawx);
    }

    private Slaw[] toSlawx(List<Node> nodes) {
        final Slaw[] NO_SLAWX = new Slaw[0];
        if (nodes == null) return NO_SLAWX;
        final List<Slaw> result = new ArrayList<Slaw>(nodes.size());
        for (Node n : nodes) {
            final Slaw s = parse(n);
            if (s != null) result.add(s);
        }
        return result.toArray(NO_SLAWX);
    }

    private void reportError(String msg) {
        final Logger logger = Logger.getLogger(getClass().getName());
        logger.warning(msg);
    }

    private Iterator<Node> nodes;
    private Slaw next;
    private final SlawFactory factory;
}
