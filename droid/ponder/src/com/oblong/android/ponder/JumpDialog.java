// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.util.ExceptionHandler;

/**
 *
 * Created: Tue Dec 14 16:33:45 2010
 *
 * @author jao
 */
final class JumpDialog implements TextWatcher,
                                  DialogInterface.OnClickListener,
                                  View.OnClickListener {

    JumpDialog(PoolDetails p) {
        parent = p;
        createDialog();
    }

    void show(PoolAddress address) {
        PoolInfo info = PoolInfo.tryGet(address);
        if (info != null && info.cursor().getCount() > 0) {
            firstIndex = info.cursor().firstIndex();
            lastIndex = info.cursor().lastIndex();
            limitsLabel.setText(String.format("First: %d  Last: %d",
                                              firstIndex, lastIndex));
            final int c = (int)(lastIndex - info.cursor().getPosition());
            indexEntry.setText(String.format("%d", c));
            dialog.show();
        }
    }

     public void onClick(DialogInterface dialog, int id) {
        switch (id) {
        case DialogInterface.BUTTON_POSITIVE:
            final long p = Long.parseLong(indexEntry.getText().toString());
            parent.showProtein(p, (int) (p - lastIndex));
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            break;
        }
        dialog.dismiss();
    }

     public void onClick(View button) {
        long idx = -1;
        switch (button.getId()) {
        case R.id.first_button: idx = firstIndex; break;
        case R.id.last_button: idx = lastIndex; break;
        }
        if (idx > -1) indexEntry.setText(String.format("%d", idx));
    }

     public void afterTextChanged(Editable e) {
        long idx;
        try {
            idx = Long.parseLong(indexEntry.getText().toString());
        } catch (Throwable x) {
            ExceptionHandler.handleException(x);
            idx = -1;
        }
        final boolean valid = idx <= lastIndex && idx >= firstIndex;
        errorLabel.setText(valid ? "" : "Invalid index");
        final Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) b.setEnabled(valid);
    }

    
    public void beforeTextChanged(CharSequence e, int s, int c, int a) {
    }

     public void onTextChanged(CharSequence e, int s, int b, int c) {
    }

    private void createDialog() {
        final LayoutInflater inflater = (LayoutInflater)
            parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewGroup group =
            (ViewGroup) parent.findViewById(R.id.jump_dialog);
        final View layout = inflater.inflate(R.layout.jump_dialog, group);

        limitsLabel = (TextView) layout.findViewById(R.id.limits_label);
        indexEntry = (EditText) layout.findViewById(R.id.index_entry);
        indexEntry.addTextChangedListener(this);
        errorLabel = (TextView) layout.findViewById(R.id.error_label);

        final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setView(layout);
        builder.setTitle("Jump to protein");
        builder.setPositiveButton("Jump", this);
        builder.setNegativeButton("Cancel", this);

        dialog = builder.create();
        dialog.setOwnerActivity(parent);

        final Button lastButton =
            (Button)layout.findViewById(R.id.last_button);
        lastButton.setOnClickListener(this);
        final Button firstButton =
            (Button)layout.findViewById(R.id.first_button);
        firstButton.setOnClickListener(this);
    }

    private final PoolDetails parent;
    private AlertDialog dialog;
    private TextView limitsLabel;
    private EditText indexEntry;
    private TextView errorLabel;
    private long firstIndex;
    private long lastIndex;
}
