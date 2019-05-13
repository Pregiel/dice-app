package pl.pregiel.dice_app.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.TextValidator;
import pl.pregiel.dice_app.UserInfo;
import pl.pregiel.dice_app.WebController;
import pl.pregiel.dice_app.pojos.User;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        username = findViewById(R.id.editText_login_username);
        password = findViewById(R.id.editText_login_password);

        TextValidator textValidator = new TextValidator() {
            @Override
            public void textChanged() {
                ((GradientDrawable) username.getBackground()).setStroke(2,
                        ContextCompat.getColor(getApplicationContext(), R.color.colorBorder));

                ((GradientDrawable) password.getBackground()).setStroke(2,
                        ContextCompat.getColor(getApplicationContext(), R.color.colorBorder));

                TextView error = findViewById(R.id.textView_login_error_invalid);
                error.setVisibility(View.GONE);
            }
        };
        username.addTextChangedListener(textValidator);
        password.addTextChangedListener(textValidator);
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

            User user = new User(username.getText().toString(),
                    password.getText().toString(), password.getText().toString());

            activity.runOnUiThread(() -> {
                username.setEnabled(false);
                password.setEnabled(false);
            });
            RestTemplate restTemplate = new RestTemplate();

            UserInfo.getInstance().setUsername(username.getText().toString());

            HttpEntity<User> entity = new HttpEntity<>(user,
                    WebController.getHttpEntityWithoutAuth().getHeaders());

            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        WebController.AUTHENTICATE_URL, HttpMethod.POST,
                        entity, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {

                    JSONObject jsonObject = new JSONObject(response.getBody());

                    WebController.setupHttpEntity(jsonObject.getString("token"));

                    activity.runOnUiThread(() -> Toast.makeText(activity,
                            String.format(activity.getString(R.string.login_welcome),
                                    UserInfo.getInstance().getUsername()), Toast.LENGTH_LONG).show());

                    Intent view = new Intent(activity, RoomListActivity.class);
                    activity.startActivity(view);
                    activity.finish();
                } else {
                    activity.runOnUiThread(() -> {
                        username.setEnabled(true);
                        password.setEnabled(true);
                    });

                    if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, R.string.login_errors_invalid, Toast.LENGTH_LONG).show();

                            ((GradientDrawable) username.getBackground()).setStroke(3,
                                    ContextCompat.getColor(activity, R.color.colorAlert));

                            ((GradientDrawable) password.getBackground()).setStroke(3,
                                    ContextCompat.getColor(activity, R.color.colorAlert));

                            TextView error = activity.findViewById(R.id.textView_login_error_invalid);
                            error.setVisibility(View.VISIBLE);
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
