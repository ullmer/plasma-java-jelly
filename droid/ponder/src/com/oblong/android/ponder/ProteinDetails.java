// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.oblong.jelly.ProteinMetadata;
import com.oblong.jelly.Slaw;

/**
 *
 * Created: Wed Dec 15 15:04:37 2010
 *
 * @author jao
 */
public final class ProteinDetails extends Activity {

    static void launch(Activity launcher,
                       ProteinMetadata meta,
                       String pool) {
        final Intent intent = new Intent(launcher, ProteinDetails.class);
        proteinMetadata = meta;
        poolName = pool;
        launcher.startActivity(intent);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.protein_details);
    }

    @Override public void onStart() {
        super.onStart();
        title = (TextView) findViewById(R.id.protein_idx);
        origin = (TextView) findViewById(R.id.protein_org);
        descTitle = (TextView) findViewById(R.id.protein_descrips);
        descDetail = (TextView) findViewById(R.id.protein_descrips_x);
        ingTitle = (TextView) findViewById(R.id.protein_ingests);
        ingDetail = (TextView) findViewById(R.id.protein_ingests_x);
        dataTitle = (TextView) findViewById(R.id.protein_data);

        title.setText(String.format("Protein %d", proteinMetadata.index()));
        origin.setText(String.format("from %s", poolName));

        showSlaw("descrip", descTitle, descDetail,
                 proteinMetadata.descrips(), proteinMetadata.descripsSize());

        showSlaw("ingest", ingTitle, ingDetail,
                 proteinMetadata.ingests(), proteinMetadata.ingestsSize());

        final long n = proteinMetadata.dataSize();
        final String s = n > 0 ? Utils.formatSize(n) + " of" : "No";
        dataTitle.setText(String.format("%s rude data", s));
    }

    private static void showSlaw(String kind, TextView title, TextView dets,
                                 Slaw s, long size) {
        if (size > 0 && s != null) {
            final String cstr = Utils.formatNumber(s.count(), kind);
            final String sstr = Utils.formatSize(size);
            title.setText(String.format("%s (%s)", cstr, sstr));
            dets.setText(s.toString());
        } else {
            title.setText(String.format("No %ss", kind));
            dets.setText("");
        }
    }

    private static ProteinMetadata proteinMetadata;
    private static String poolName;

    private TextView title;
    private TextView origin;
    private TextView descTitle;
    private TextView descDetail;
    private TextView ingTitle;
    private TextView ingDetail;
    private TextView dataTitle;
}
