package pl.pregiel.dice_app.web;


import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import org.json.JSONObject;

import io.reactivex.Single;
import pl.pregiel.dice_app.UserInfo;
import pl.pregiel.dice_app.dtos.RoomDetailsDto;
import pl.pregiel.dice_app.utils.ParametrizedRunnable;

public class RoomHub {
    private HubConnection hubConnection;

    public RoomHub(Activity activity) {
        hubConnection = HubConnectionBuilder.create("http://192.168.1.20:45455/hub").
                withAccessTokenProvider(Single.defer(() ->
                        Single.just(UserInfo.getInstance().getToken()))).build();

        hubConnection.on("Send", message -> {
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "Message: " + message, Toast.LENGTH_LONG).show();
            });
        }, String.class);

        hubConnection.on("UserJoined", message -> {
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "Welcome: " + message, Toast.LENGTH_LONG).show();
            });
        }, String.class);

        hubConnection.on("UserLeaved", message -> {
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "Goodbye: " + message, Toast.LENGTH_LONG).show();
            });
        }, String.class);

        hubConnection.on("InvalidRoom", message -> {
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "Invalid room: " + message, Toast.LENGTH_LONG).show();
            });
        }, String.class);
    }

    public void setOnTarget(String target, ParametrizedRunnable runnable) {
        hubConnection.on(target, () -> {
            runnable.run();
        });
    }

    public <T> void setOnTarget(String target, ParametrizedRunnable runnable,
                                Class<T> class1) {
        hubConnection.on(target, param1 -> {
            runnable.getParameters().clear();
            runnable.addParameters(param1);
            runnable.run();
        }, class1);
    }

    public <T1, T2> void setOnTarget(String target, ParametrizedRunnable runnable,
                                     Class<T1> class1, Class<T2> class2) {
        hubConnection.on(target, (param1, param2) -> {
            runnable.getParameters().clear();
            runnable.addParameters(param1, param2);
            runnable.run();
        }, class1, class2);
    }

    public <T1, T2, T3> void setOnTarget(String target, ParametrizedRunnable runnable,
                                         Class<T1> class1, Class<T2> class2, Class<T3> class3) {
        hubConnection.on(target, (param1, param2, param3) -> {
            runnable.getParameters().clear();
            runnable.addParameters(param1, param2, param3);
            runnable.run();
        }, class1, class2, class3);
    }


    private static class HubConnectionStartTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... objects) {
            HubConnection hubConnection = (HubConnection) objects[0];
            int roomId = (int) objects[1];

            if (hubConnection != null) {
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
                    hubConnection.stop().blockingAwait();

                hubConnection.start().blockingAwait();
                hubConnection.send("JoinRoom", roomId);
            }
            return null;
        }
    }

    private static class HubConnectionStopTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Object... objects) {
            HubConnection hubConnection = (HubConnection) objects[0];
            int roomId = (int) objects[1];

            if (hubConnection != null)
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    hubConnection.send("LeaveRoom", roomId);
                    hubConnection.stop().blockingAwait();
                }
            return null;
        }
    }

    public void send(String method, Object... args) {
        hubConnection.send(method, args);
    }

    public void joinRoom(int roomId) {
        new HubConnectionStartTask().execute(hubConnection, roomId);
    }

    public void leaveRoom(int roomId) {
        new HubConnectionStopTask().execute(hubConnection, roomId);
    }
}
