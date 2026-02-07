package com.example.digitalacademy.Common.Helpers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.function.Function;

public class SpinnerAdapter<T> extends ArrayAdapter<T> {

    private final List<T> items;
    private final Function<T, String> displayMapper;

    public SpinnerAdapter(@NonNull Context context,
                          int resource,
                          @NonNull List<T> items,
                          @NonNull Function<T, String> displayMapper) {
        super(context, resource, items);
        this.items = items;
        this.displayMapper = displayMapper;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setText(displayMapper.apply(items.get(position)));
        return textView;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setText(displayMapper.apply(items.get(position)));
        return textView;
    }
}
