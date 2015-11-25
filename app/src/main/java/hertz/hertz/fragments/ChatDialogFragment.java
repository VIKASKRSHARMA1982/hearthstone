package hertz.hertz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hertz.hertz.R;
import hertz.hertz.activities.AvailableDriversActivity;
import hertz.hertz.activities.BaseActivity;
import hertz.hertz.adapters.ChatAdapter;
import hertz.hertz.helpers.AppConstants;
import hertz.hertz.model.Chat;

/**
 * Created by rsbulanon on 11/17/15.
 */
public class ChatDialogFragment extends DialogFragment {

    @Bind(R.id.btnSend) Button tvHeader;
    @Bind(R.id.etMessage) EditText etMessage;
    @Bind(R.id.lvChat) ListView lvChat;
    private View view;
    private BaseActivity activity;
    private String recipient;
    private ArrayList<Chat> chats = new ArrayList<>();

    public static ChatDialogFragment newInstance(String recipient) {
        ChatDialogFragment frag = new ChatDialogFragment();
        frag.recipient = recipient;
        Log.d("push","RECIPIENT --> " + recipient);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_chat, null);
        ButterKnife.bind(this, view);
        activity = (BaseActivity)getActivity();
        lvChat.setAdapter(new ChatAdapter(getActivity(), chats));
        listenToRoom(recipient);
        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        return mDialog;
    }

    @OnClick(R.id.btnSend)
    public void sendChat() {
        if (activity.isNetworkAvailable()) {
            if (!etMessage.getText().toString().isEmpty()) {
                Log.d("push", "push sent to --> " + recipient);
                ParsePush push = new ParsePush();
                push.setChannel((recipient.split("-")[1]).toString());
                JSONObject data = new JSONObject();
                JSONObject conv = new JSONObject();
                try {
                    /** notification message */
                    data.put("alert","You received message from " + ParseUser.getCurrentUser().getString("firstName")
                    + " " + ParseUser.getCurrentUser().getString("lastName"));
                    /** chat data */
                    conv.put("room",recipient);
                    conv.put("message",etMessage.getText().toString());
                    conv.put("sender",ParseUser.getCurrentUser().getObjectId());
                    conv.put("timestamp", Calendar.getInstance().getTime().toString());
                    data.put("json", conv);
                    Chat chat = new Chat(ParseUser.getCurrentUser().getObjectId(),
                            etMessage.getText().toString(),Calendar.getInstance().getTime().toString());
                    AppConstants.FIREBASE.child("Chat").child(recipient).push().setValue(chat, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError == null) {
                                Log.d("push", "chat successfully created!");
                            } else {
                                Log.d("push", "error in sending chat --> " + firebaseError.getMessage());
                            }
                        }
                    });
                    push.setData(data);
                    push.sendInBackground(new SendCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("push", "push successful!");
                            } else {
                                Log.d("push", "push failed --> " + e.toString());
                            }
                        }
                    });
                    etMessage.setText("");
                } catch (JSONException e) {
                    Log.d("push","error in creating json --> " + e.toString());
                    e.printStackTrace();
                }
            } else {
                activity.setError(etMessage,AppConstants.WARN_PLEASE_ENTER_YOUR_MESSAGE);
            }
        } else {
            activity.showSweetDialog(AppConstants.ERR_CONNECTION, "error");
        }
    }

    public void addChat(Chat chat) {
        chats.add(chat);
        ((BaseAdapter)lvChat.getAdapter()).notifyDataSetChanged();
    }

    public void listenToRoom(String room) {
        AppConstants.FIREBASE.child("Chat").child(room).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chats.add(dataSnapshot.getValue(Chat.class));
                ((BaseAdapter)lvChat.getAdapter()).notifyDataSetChanged();
                lvChat.post(new Runnable() {
                    @Override
                    public void run() {
                        lvChat.setSelection(lvChat.getAdapter().getCount() - 1);
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
