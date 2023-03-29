package com.example.tripblog.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.tripblog.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SimpleLoadingDialog extends MaterialAlertDialogBuilder {
    AlertDialog dialog;
    public SimpleLoadingDialog(@NonNull Context context) {
        super(context);
        setView(R.layout.loading);
        setBackground(context.getDrawable(android.R.color.transparent));
        setCancelable(false);
    }

    @Override
    public AlertDialog show() {
        dialog = super.show();
        return dialog;
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
