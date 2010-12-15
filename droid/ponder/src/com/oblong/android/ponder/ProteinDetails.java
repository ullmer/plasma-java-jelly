// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.oblong.jelly.ProteinMetadata;

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

        final String d =
            Utils.formatNumber(proteinMetadata.descripsNumber(), "descrip");
        final String ds = Utils.formatSize(proteinMetadata.descripsSize());
        descTitle.setText(String.format("%s (%s)", d, ds));
        if (proteinMetadata.descripsNumber() > 0)
            descDetail.setText(proteinMetadata.descrips().toString());

        final String i =
            Utils.formatNumber(proteinMetadata.ingestsNumber(), "ingest");
        final String is = Utils.formatSize(proteinMetadata.ingestsSize());
        ingTitle.setText(String.format("%s (%s)", i, is));
        if (proteinMetadata.ingestsNumber() > 0)
            ingDetail.setText(proteinMetadata.ingests().toString());

        final long n = proteinMetadata.dataSize();
        final String s = n > 0 ? Utils.formatSize(n) + " of" : "No";
        dataTitle.setText(String.format("%s rude data", s));
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
