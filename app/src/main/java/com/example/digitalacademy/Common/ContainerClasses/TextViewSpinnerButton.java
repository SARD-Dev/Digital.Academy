package com.example.digitalacademy.Common.ContainerClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.digitalacademy.R;

import java.util.List;

public class TextViewSpinnerButton {

    /**
     * Show the dialog.
     *
     * @param context    any Activity or Context
     * @param title      dialog title string
     * @param options    list of spinner entries
     * @param initialPos zero-based initial selection index
     * @param cb         callback when OK is tapped
     */
    public static void show(
            Context context,
            String title,
            List<String> options,
            int initialPos,
            Callback cb
    ) {
        // Inflate custom view
        View view = LayoutInflater.from(context)
                .inflate(R.layout.text_view_spinner_button, null, false);

        TextView tvTitle = view.findViewById(R.id.dialogTitle);
        Spinner spinner = view.findViewById(R.id.dialogSpinner);
        Button btnOk = view.findViewById(R.id.dialogButton);

        // Set title
        tvTitle.setText(title);

        // Populate spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                options
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinner.setAdapter(adapter);
        spinner.setSelection(Math.max(0, Math.min(initialPos, options.size() - 1)));

        // Build and show AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        btnOk.setOnClickListener(v -> {
            String selected = (String) spinner.getSelectedItem();
            cb.onSelected(selected);
            dialog.dismiss();
        });

        dialog.show();
    }

    public interface Callback {
        void onSelected(String selectedItem);
    }
}