package pl.pregiel.dice_app.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;
import java.util.List;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.Room;
import pl.pregiel.dice_app.UserInfo;
import pl.pregiel.dice_app.WebController;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        username = findViewById(R.id.editText_login_username);
        password = findViewById(R.id.editText_login_password);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((GradientDrawable) username.getBackground()).setStroke(2,
                        ContextCompat.getColor(getApplicationContext(), R.color.colorBorder));

                ((GradientDrawable) password.getBackground()).setStroke(2,
                        ContextCompat.getColor(getApplicationContext(), R.color.colorBorder));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
    }

    public void login(View view) {
        new LoginTask(this).execute();
    }

    public void registration(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    private static class LoginTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Activity> reference;

        private LoginTask(Activity context) {
            reference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Activity activity = reference.get();

            final EditText username = activity.findViewById(R.id.editText_login_username);
            final EditText password = activity.findViewById(R.id.editText_login_password);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    username.setEnabled(false);
                    password.setEnabled(false);
                }
            });

            try {
                RestTemplate restTemplate = new RestTemplate();

                UserInfo.getInstance().setUsername(username.getText().toString());

                WebController.setupRequest(username.getText().toString(), password.getText().toString());

                ResponseEntity<List<Room>> response = restTemplate.exchange(
                        String.format(WebController.LOGIN_URL, UserInfo.getInstance().getUsername()), HttpMethod.GET,
                        WebController.getRequest(), new ParameterizedTypeReference<List<Room>>() {
                        });

                UserInfo.getInstance().setRoomList(response.getBody());

//                final JsonNode name = root.path("title");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, String.format(activity.getString(R.string.login_welcome), UserInfo.getInstance().getUsername()), Toast.LENGTH_LONG).show();
                    }
                });

                Intent view = new Intent(activity, RoomListActivity.class);
                activity.startActivity(view);
                activity.finish();

            } catch (HttpStatusCodeException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        username.setEnabled(true);
                        password.setEnabled(true);
                    }
                });

                int statusCode = e.getStatusCode().value();

                if (statusCode == 401) {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, R.string.login_invalid, Toast.LENGTH_LONG).show();

                            ((GradientDrawable) username.getBackground()).setStroke(3,
                                    ContextCompat.getColor(activity, R.color.colorAlert));

                            ((GradientDrawable) password.getBackground()).setStroke(3,
                                    ContextCompat.getColor(activity, R.color.colorAlert));

                        }
                    });
                }
            }

//
//            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//
////            final String result = restTemplate.getForObject(BASE_URL+"jackson", String.class, "title=ja");
//
//            ResponseEntity<String> result = restTemplate.getForEntity(BASE_URL + "jackson?title=hakuna", String.class);
//

            return null;
        }
    }
}
