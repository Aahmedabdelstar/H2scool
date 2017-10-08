package com.schoolh2.h2scool.chatcomponents;

/**
 * Created by MMenem on 10/8/2017.
 */


        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.graphics.Color;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.annotation.RequiresApi;
        import android.support.v4.view.GravityCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.RecyclerView.ViewHolder;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.View.OnLayoutChangeListener;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.LinearLayout.LayoutParams;
        import android.widget.ProgressBar;
        import android.widget.TextView;


        import com.firebase.ui.database.FirebaseRecyclerAdapter;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.google.firebase.database.ValueEventListener;
        import com.schoolh2.h2scool.MainActivity;
        import com.schoolh2.h2scool.R;

        import java.util.Arrays;
        import java.util.Comparator;

public class ChatConversationActivity extends AppCompatActivity {
    private static final int GALLERY_INTENT = 2;
    public static final int MULTIPLE_PERMISSIONS = 10;
    public static final int READ_EXTERNAL_STORAGE = 0;
    static String receivedName;
    static String senderName;
    private DatabaseReference chatDatabase;
    private EditText etMessageArea;
    private FirebaseDatabase firebaseDatabase;
    private ImageView ivAttachIcon;
    private FirebaseAuth mAuth;
    private AuthStateListener mAuthListener;
    Uri mImageUri = Uri.EMPTY;
    public LinearLayoutManager mLinearLayoutManager;
    private ProgressDialog mProgressDialog;
    private String messageId;
    private String messageUniqueId;
    private ImageView no_data_available_image;
    final CharSequence[] options = new CharSequence[]{"Camera", "Gallery"};
    String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"};
    private String pictureImagePath = "";
    private ProgressBar progressBar;
    private Query queryOnCurrentMessage;
    private String recUserId;
    public RecyclerView recyclerView;
    private ImageView send_icon;
    private TextView tvNoChat;
    private TextView tvRecieverName;
    private FirebaseUser user;
    private String userID;
    private DatabaseReference usersDatabase;

    public static class ChatConversationViewHolder extends ViewHolder {

        LinearLayout layout = ((LinearLayout) this.mView.findViewById(R.id.chat_linear_layout));
        View mView;
        private final TextView message = ((TextView) this.mView.findViewById(R.id.fetch_chat_messgae));
        final LayoutParams params = new LayoutParams(-1, -2);
        private final TextView sender = ((TextView) this.mView.findViewById(R.id.fetch_chat_sender));
        final LayoutParams text_params = new LayoutParams(-1, -2);

        public ChatConversationViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        private void setSender(String title) {
            if (title.equals(ChatConversationActivity.senderName)) {
                this.params.setMargins(5, 5, 10, 10);
                this.text_params.setMargins(15, 10, 0, 5);
                this.sender.setLayoutParams(this.text_params);
                this.mView.setLayoutParams(this.params);
                this.mView.setBackgroundResource(R.drawable.shape_out_message);
                this.sender.setText("YOU");
                return;
            }
            this.params.setMargins(10, 0, 5, 10);
            this.sender.setGravity(GravityCompat.START);
            this.text_params.setMargins(60, 10, 0, 5);
            this.sender.setLayoutParams(this.text_params);
            this.mView.setLayoutParams(this.params);
            this.mView.setBackgroundResource(R.drawable.shap_in_message);
            this.sender.setText(ChatConversationActivity.senderName);

        }

        private void setMessage(String title) {
            if (!title.startsWith("https")) {
                if (this.sender.getText().equals(ChatConversationActivity.senderName)) {
                    this.text_params.setMargins(65, 10, 22, 15);
                } else {
                    this.text_params.setMargins(15, 10, 22, 15);
                }
                this.message.setLayoutParams(this.text_params);
                this.message.setText(title);
                this.message.setTextColor(Color.parseColor("#FFFFFF"));
                this.message.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_chat_conversation);
        this.recUserId = getIntent().getExtras().getString("userRecMessage");
        this.mAuth = FirebaseAuth.getInstance();
        this.userID = this.mAuth.getCurrentUser().getUid();
        this.mAuthListener = new AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                ChatConversationActivity.this.user = firebaseAuth.getCurrentUser();
                if (ChatConversationActivity.this.user != null) {
                    ChatConversationActivity.this.userID = ChatConversationActivity.this.user.getUid();
                    return;
                }
                ChatConversationActivity.this.startActivity(new Intent(ChatConversationActivity.this, MainActivity.class));
                ChatConversationActivity.this.finish();
            }
        };
        this.tvRecieverName = (TextView) findViewById(R.id.chat_textView);
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.chatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat");
        this.messageId = this.userID + this.recUserId;
        Character[] chars = new Character[this.messageId.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = Character.valueOf(this.messageId.charAt(i));
        }
        Arrays.sort(chars, new Comparator<Character>() {
            @RequiresApi(api = 19)
            public int compare(Character c1, Character c2) {
                int cmp = Character.compare(Character.toLowerCase(c1.charValue()), Character.toLowerCase(c2.charValue()));
                return cmp != 0 ? cmp : Character.compare(c1.charValue(), c2.charValue());
            }
        });
        StringBuilder sb = new StringBuilder(chars.length);
        for (Character charValue : chars) {
            sb.append(charValue.charValue());
        }
        this.messageUniqueId = sb.toString();
        this.queryOnCurrentMessage = this.chatDatabase.orderByChild("messageId").equalTo(this.messageUniqueId);
        this.usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        this.usersDatabase.child(this.userID).addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChatConversationActivity.senderName = ((UserInformation) dataSnapshot.getValue(UserInformation.class)).getUserName();
                } else {
                }
            }

            public void onCancelled(DatabaseError error) {
            }
        });
        this.usersDatabase.child(this.recUserId).addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserInformation userInformation = (UserInformation) dataSnapshot.getValue(UserInformation.class);
                    ChatConversationActivity.receivedName = userInformation.getUserName();
                    ChatConversationActivity.this.tvRecieverName.setText(userInformation.getUserName());
                    return;
                }
            }

            public void onCancelled(DatabaseError error) {
            }
        });
        this.recyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);
        this.send_icon = (ImageView) findViewById(R.id.sendButton);
        this.no_data_available_image = (ImageView) findViewById(R.id.no_data_available_image);
        this.etMessageArea = (EditText) findViewById(R.id.messageArea);
        this.mProgressDialog = new ProgressDialog(this);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        this.tvNoChat = (TextView) findViewById(R.id.no_chat_text);
        this.mLinearLayoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.mLinearLayoutManager);
        this.mLinearLayoutManager.setStackFromEnd(true);
        this.send_icon.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DatabaseReference newMessage = ChatConversationActivity.this.chatDatabase.push();
                String messageText = ChatConversationActivity.this.etMessageArea.getText().toString().trim();
                if (messageText.equals("")) {
                    return;
                }
                newMessage.child("message").setValue(messageText);
                newMessage.child("sender").setValue(ChatConversationActivity.senderName);
                newMessage.child("receiverID").setValue(ChatConversationActivity.this.recUserId);
                newMessage.child("receiverName").setValue(ChatConversationActivity.receivedName);
                newMessage.child("messageId").setValue(ChatConversationActivity.this.messageUniqueId);
                newMessage.child("senderUserid").setValue(ChatConversationActivity.this.userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        ChatConversationActivity.this.etMessageArea.setText("");
                    }
                });
            }
        });
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onStart() {
        super.onStart();
        checkMessageExistOrNot();
        getAllChatbetweenTwoUsers();
    }

    private void checkMessageExistOrNot() {
        this.queryOnCurrentMessage.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    ChatConversationActivity.this.progressBar.setVisibility(View.GONE);
                    ChatConversationActivity.this.recyclerView.setVisibility(View.VISIBLE);
                    ChatConversationActivity.this.no_data_available_image.setVisibility(View.GONE);
                    ChatConversationActivity.this.tvNoChat.setVisibility(View.GONE);
                    ChatConversationActivity.this.recyclerView.postDelayed(new Runnable() {
                        public void run() {
                            ChatConversationActivity.this.recyclerView.smoothScrollToPosition(ChatConversationActivity.this.recyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 500);
                    ChatConversationActivity.this.recyclerView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            if (bottom < oldBottom) {
                                ChatConversationActivity.this.recyclerView.postDelayed(new Runnable() {
                                    public void run() {
                                        ChatConversationActivity.this.recyclerView.smoothScrollToPosition(ChatConversationActivity.this.recyclerView.getAdapter().getItemCount() - 1);
                                    }
                                }, 100);
                            }
                        }
                    });
                    return;
                }
                ChatConversationActivity.this.progressBar.setVisibility(View.GONE);
                ChatConversationActivity.this.recyclerView.setVisibility(View.GONE);
                ChatConversationActivity.this.no_data_available_image.setVisibility(View.VISIBLE);
                ChatConversationActivity.this.tvNoChat.setVisibility(View.VISIBLE);
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getAllChatbetweenTwoUsers() {
        this.recyclerView.setAdapter(new FirebaseRecyclerAdapter<MessageModel, ChatConversationViewHolder>(MessageModel.class, R.layout.show_chat_conversation_single_item, ChatConversationViewHolder.class, this.queryOnCurrentMessage) {
            protected void populateViewHolder(ChatConversationViewHolder chatViewHolder, MessageModel model, int position) {
                chatViewHolder.setSender(model.getSender());
                chatViewHolder.setMessage(model.getMessage());
            }
        });
    }
}
