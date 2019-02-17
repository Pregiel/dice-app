package pl.pregiel.dice_app;


import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;

import pl.pregiel.dice_app.activities.RegistrationActivity;

public class RegistrationActivityTest extends ActivityInstrumentationTestCase2<RegistrationActivity> {
    private Activity activity;
    private EditText username, password, confirmPassword;
    private Button submit;

    public RegistrationActivityTest(Class<RegistrationActivity> activityClass) {
        super(activityClass);
    }

    public RegistrationActivityTest() {
        super(RegistrationActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);

        activity = getActivity();

        username = activity.findViewById(R.id.editText_registration_username);
        password = activity.findViewById(R.id.editText_registration_password);
        confirmPassword = activity.findViewById(R.id.editText_registration_confirmPassword);

        submit = activity.findViewById(R.id.button_registration_submit);
    }

    @UiThreadTest
    public void testInvalidUsername() throws InterruptedException {
        username.requestFocus();
        username.setText("123");
        password.requestFocus();
        password.setText("11111111");
        confirmPassword.requestFocus();
        confirmPassword.setText("11111111");

        Thread.sleep(1000);
        submit.callOnClick();

    }
}
