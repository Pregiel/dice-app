package pl.pregiel.dice_app.dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;

import pl.pregiel.dice_app.HttpResultMessage;
import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.WebController;
import pl.pregiel.dice_app.dtos.RoomDto;

public class PasswordDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_room_password, null);

        final EditText passwordText = view.findViewById(R.id.editText_passwordDialog_password);

        builder.setView(view)
                .setTitle("Password")
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    RoomDto room = new RoomDto();
                    room.setId(getArguments().getInt("id"));
                    room.setPassword(passwordText.getText().toString());

                    new GetIntoRoomTask(getActivity(), room).execute();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                });

        return builder.create();
    }


    private static class GetIntoRoomTask extends AsyncTask<Void, Void, Void> {
        private RoomDto room;
        private WeakReference<Activity> reference;

        private GetIntoRoomTask(Activity context, RoomDto room) {
            reference = new WeakReference<>(context);
            this.room = room;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Activity activity = reference.get();

            RestTemplate restTemplate = new RestTemplate();

            try {
                HttpEntity<RoomDto> entity = new HttpEntity<>(room,
                        WebController.getHttpEntity().getHeaders());

                ResponseEntity<String> response = restTemplate.exchange(
                        String.format(WebController.ROOM_URL, room.getId()), HttpMethod.POST,
                        entity, String.class);

                String body = response.getBody();
                System.out.println(body);
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, R.string.login_welcome, Toast.LENGTH_LONG).show();
                });
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    if (WebController.checkResponseBody(e.getResponseBodyAsString(),
                            HttpResultMessage.CredentialsInvalid)) {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, R.string.room_errors_invalidPassword, Toast.LENGTH_LONG).show();
                        });

                    } else if (WebController.checkResponseBody(e.getResponseBodyAsString(),
                            HttpResultMessage.RoomNotFound)) {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, R.string.room_errors_notFound, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, R.string.room_errors_unknown, Toast.LENGTH_LONG).show();
                    });
                }
            }
            return null;
        }
    }
}
