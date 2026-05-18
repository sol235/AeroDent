package com.diploma.aerodent.util;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import com.diploma.aerodent.R;

public class DialogUtils {

    public interface OnAnnulConfirmListener {
        void onConfirm();
    }

    // Show annul dialog

    public static void showAnnulDialog(Context context, OnAnnulConfirmListener listener) {
        new AlertDialog.Builder(context).setTitle(R.string.annul_record_title).setMessage(R.string.annul_record_message)
                .setPositiveButton(R.string.annul_record, (dialog, which) -> {
                    if (listener != null) {
                        listener.onConfirm();
                    }
                }).setNegativeButton(R.string.cancel, null).show();
    }
}
