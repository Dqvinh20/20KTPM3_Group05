package com.example.tripshare.ui.dialog;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;

import com.example.tripshare.R;

public class ImagePreviewDialog extends DialogFragment {
    ImageView imgPreview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        if (getArguments() != null) {
            Bitmap imgSrc = getArguments().getParcelable("imgSrc");
            if (imgSrc != null) {
                this.imgPreview.setImageBitmap(imgSrc);
            }
            else {
                Drawable brokenImage = AppCompatResources.getDrawable(this.getActivity(), R.drawable.ic_baseline_broken_image_24);
                this.imgPreview.setImageDrawable(brokenImage);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_img_preview, container, false);
        imgPreview = v.findViewById(R.id.imgPreview);
        return v;
    }
}
