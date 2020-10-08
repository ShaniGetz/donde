package com.example.donde.utils.map_utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.donde.R;

public class StatusDialog extends AppCompatDialogFragment {
    private EditText editStatus;
    private StatusDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_status_dialog, null);
        builder.setView(view)
                .setTitle("Status")
                .setIcon(R.drawable.ic_baseline_mode_comment_24)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String status = editStatus.getText().toString();
                        listener.applyText(status);
                    }
                });
        editStatus = view.findViewById(R.id.edit_status);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (StatusDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement StatusDialogListener");
        }
    }

    public interface StatusDialogListener {
        void applyText(String status);
    }
}