package com.mohammadmawed.ebayclonemvvvm.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadmawed.ebayclonemvvvm.R;

import java.text.BreakIterator;

public class OffersAdapter extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public TextView priceTextview, titleTextView, timeTextView, locationTextView;
    public View itemViewView;
    public OffersAdapter(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image_single_view);
        titleTextView = itemView.findViewById(R.id.title_single_View);
        priceTextview = itemView.findViewById(R.id.price_single_view);
        locationTextView = itemView.findViewById(R.id.location_single_view);
        timeTextView = itemView.findViewById(R.id.time_single_view);
        itemViewView = itemView;
    }
}