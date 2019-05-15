package pl.pregiel.dice_app.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.lang.ref.WeakReference;
import java.util.List;

import pl.pregiel.dice_app.R;
import pl.pregiel.dice_app.utils.TextValidator;
import pl.pregiel.dice_app.pojos.User;
import pl.pregiel.dice_app.UserInfo;
import pl.pregiel.dice_app.utils.Utils;
import pl.pregiel.dice_app.web.WebController;

public class RegistrationActivity extends AppCompatActivity {

    private EditText username, password, confirmPassword;

    private TextView errorUsernameSize, errorUsernameDuplicate, errorPasswordSize,
            errorConfirmPasswordDiff;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        errorUsernameSize = findViewById(R.id.textView_registration_usernameError_size);
        errorUsernameDuplicate = findViewById(R.id.textView_registration_usernameError_duplicate);
        errorPasswordSize = findViewById(R.id.textView_registration_passwordError_size);
        errorConfirmPasswordDiff = findViewById(R.id.textView_registration_confirmPasswordError_diff);

        username = findViewById(R.id.editText_registration_username);
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateUsername(username.getText().toString());
                }
            }
        });
        username.addTextChangedListener(new TextValidator() {
            public void textChanged() {
                errorUsernameSize.setVisibility(View.GONE);
                errorUsernameDuplicate.setVisibility(View.GONE);
                Utils.setEditTextBorderColor(username, R.color.colorBorder);

            }
        });

        password = findViewById(R.id.editText_registration_password);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validatePassword(password.getText().toString());
                }
            }
        });
        password.addTextChangedListener(new TextValidator() {
            public void textChanged() {
                errorPasswordSize.setVisibility(View.GONE);
                errorConfirmPasswordDiff.setVisibility(View.GONE);
                Utils.setEditTextBorderColor(password, R.color.colorBorder);

            }
        });

        confirmPassword = findViewById(R.id.editText_registration_confirmPassword);
        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateConfirmPassword(confirmPassword.getText().toString(),
                            password.getText().toString());
                }
            }
        });
        confirmPassword.addTextChangedListener(new TextValidator() {
            public void textChanged() {
                errorConfirmPasswordDiff.setVisibility(View.GONE);
                Utils.setEditTextBorderColor(confirmPassword, R.color.colorBorder);
            }
        });
    }

    public void submit(View view) {
        if (validateRegistration()) {
            new RegistrationTask(this).execute();
        }
    }

    private static class RegistrationTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Activity> reference;

        private RegistrationTask(Activity context) {
            reference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Activity activity = reference.get();

            final EditText username = activity.findViewById(R.id.editText_registration_username);
            final EditText password = activity.findViewById(R.id.editText_registration_password);
            final EditText confirmPassword = activity.findViewById(R.id.editText_registration_confirmPassword);

            User user = new User(username.getText().toString(),
                    password.getText().toString(),
                    confirmPassword.getText().toString());

            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<User> entity = new HttpEntity<>(user,
                    WebController.getHttpEntityWithoutAuth().getHeaders());

            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        WebController.USER_LIST_URL, HttpMethod.POST, entity, String.class);

                JSONObject jsonObject = new JSONObject(response.getBody());

                UserInfo.getInstance().setUsername(username.getText().toString());

                WebController.setupHttpEntity(jsonObject.getString("token"));

                activity.runOnUiThread(() -> Toast.makeText(activity,
                        String.format(activity.getString(R.string.login_welcome),
                                UserInfo.getInstance().getUsername()), Toast.LENGTH_LONG).show());

                Intent view = new Intent(activity, RoomListActivity.class);
                view.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(view);

            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    try {
                        JSONObject jsonObject = new JSONObject(e.getResponseBodyAsString());
                        JSONArray errorJSONArray = (JSONArray) jsonObject.get("errors");

                        handleErrorList(Utils.listFromJSONString(
                                errorJSONArray.toString(), ",", true));
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, R.string.login_errors_unknown, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, R.string.login_errors_unknown, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, R.string.login_errors_unknown, Toast.LENGTH_LONG).show();
                });
            } catch (ResourceAccessException e) {
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity, R.string.all_noServer, Toast.LENGTH_LONG).show();
                });
            }
            return null;
        }

        private void handleErrorList(final List<String> errorList) {
            final Activity activity = reference.get();

            final EditText username = activity.findViewById(R.id.editText_registration_username);
            final EditText password = activity.findViewById(R.id.editText_registration_password);
            final EditText confirmPassword = activity.findViewById(R.id.editText_registration_confirmPassword);


            final TextView errorUsernameSize = activity.findViewById(
                    R.id.textView_registration_usernameError_size);

            final TextView errorUsernameDuplicate = activity.findViewById(
                    R.id.textView_registration_usernameError_duplicate);

            final TextView errorPasswordSize = activity.findViewById(
                    R.id.textView_registration_passwordError_size);

            final TextView errorConfirmPasswordDiff = activity.findViewById(
                    R.id.textView_registration_confirmPasswordError_diff);

            activity.runOnUiThread(() -> {
                for (String s : errorList) {
                    switch (s) {
                        case "username.notFound":
                        case "username.size":
                            errorUsernameSize.setVisibility(View.VISIBLE);
                            Utils.setEditTextBorderColor(username, R.color.colorAlert);
                            break;

                        case "username.duplicate":
                            errorUsernameDuplicate.setVisibility(View.VISIBLE);
                            Utils.setEditTextBorderColor(username, R.color.colorAlert);
                            break;

                        case "password.notFound":
                        case "password.size":
                            errorPasswordSize.setVisibility(View.VISIBLE);
                            Utils.setEditTextBorderColor(password, R.color.colorAlert);
                            break;

                        case "confirmPassword.notFound":
                        case "confirmPassword.diff":
                            errorConfirmPasswordDiff.setVisibility(View.VISIBLE);
                            Utils.setEditTextBorderColor(confirmPassword, R.color.colorAlert);
                            break;
                    }
                }
            });
        }
    }

    private boolean validateRegistration() {
        int usernameError = validateUsername(username.getText().toString());
        int passwordError = validatePassword(password.getText().toString());
        int confirmPasswordError = validateConfirmPassword(confirmPassword.getText().toString(),
                password.getText().toString());


        return usernameError == 0 && passwordError == 0 && confirmPasswordError == 0;
    }

    /**
     * @return 0 - no errors
     * 1 - too short or too long (6 - 32 characters)
     */
    private int validateUsername(String username) {
        if (username.length() < 6 || username.length() > 32) {
            Utils.setEditTextBorderColor(this.username, R.color.colorAlert);
            errorUsernameSize.setVisibility(View.VISIBLE);
            return 1;
        }
        Utils.setEditTextBorderColor(this.username, R.color.colorBorder);
        return 0;
    }

    /**
     * @return 0 - no errors
     * 1 - too short or too long (8 - 32 characters)
     */
    private int validatePassword(String password) {
        Utils.setEditTextBorderColor(this.password, R.color.colorAlert);
        if (password.length() < 6 || password.length() > 32) {
            errorPasswordSize.setVisibility(View.VISIBLE);
            return 1;
        }
        Utils.setEditTextBorderColor(this.password, R.color.colorBorder);
        return 0;
    }

    /**
     * @return 0 - no errors
     * 1 - password and confirm password is different
     */
    private int validateConfirmPassword(String confirmPassword, String password) {
        Utils.setEditTextBorderColor(this.confirmPassword, R.color.colorAlert);
        if (!confirmPassword.equals(password)) {
            errorConfirmPasswordDiff.setVisibility(View.VISIBLE);
            return 1;
        }
        Utils.setEditTextBorderColor(this.confirmPassword, R.color.colorBorder);
        return 0;
    }

}
