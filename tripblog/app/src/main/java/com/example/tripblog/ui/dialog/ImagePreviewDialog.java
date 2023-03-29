package com.example.tripblog.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.tripblog.R;

public class ImagePreviewDialog extends Dialog {

    ImageView imgPreview;
    Bitmap imgSrc;
    Context context;
    public ImagePreviewDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_img_preview);
        setCancelable(true);
        imgPreview = findViewById(R.id.imgPreview);
    }

    @Override
    protected void onStart() {
        imgPreview.setImageBitmap(imgSrc);
        super.onStart();
    }

    public void setImgPreview(Bitmap imagePreviewSrc) {
        this.imgSrc = imagePreviewSrc;
    }

}
