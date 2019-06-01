package com.nghiatv.filemanager.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.nghiatv.filemanager.R;

public abstract class InputDialog extends AlertDialog.Builder {
    private EditText edtInput;

    protected InputDialog(Context context, String positive, String title) {
        super(context);

        View view = View.inflate(context, R.layout.dialog_input, null);
        edtInput = view.findViewById(R.id.edtInput);

        setView(view);
        setCancelable(false);
        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        setTitle(title);
    }

    public abstract void onActionClick(String text);

    public void setDefault(String text) {
        edtInput.setText(text);
        edtInput.setSelection(edtInput.getText().length());
    }
}
