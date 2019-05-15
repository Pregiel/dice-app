package pl.pregiel.dice_app.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import pl.pregiel.dice_app.R;

public class PasswordDialogFragment extends DialogFragment {
    private Runnable positiveRunnable, negativeRunnable;
    private EditText passwordText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_room_password, null);

        passwordText = view.findViewById(R.id.editText_passwordDialog_password);

        builder.setView(view)
                .setTitle("Password")
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    if (positiveRunnable != null) {
                        positiveRunnable.run();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    if (negativeRunnable != null) {
                        negativeRunnable.run();
                    }
                });

        return builder.create();
    }

    public void setPositiveRunnable(Runnable positiveRunnable) {
        this.positiveRunnable = positiveRunnable;
    }

    public void setNegativeRunnable(Runnable negativeRunnable) {
        this.negativeRunnable = negativeRunnable;
    }

    public EditText getPasswordText() {
        return passwordText;
    }
}
