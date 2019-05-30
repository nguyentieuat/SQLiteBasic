package com.example.sqlitebasic;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class SentMessageActivity extends AppCompatActivity {

    AutoCompleteTextView txtPhoneReceiver;
    EditText txtMessage;
    ImageButton btnCallReceiver;
    Button btnSentMessage;
    ListView lvMessage;
    Intent intent;
    Contact contact;
    BroadcastReceiver broadcastReceiver;

    List listSMS;
    ArrayAdapter arrayAdapter;

    public static int REQUEST_CODE_SMS = 2211;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sent_message);

        addControls();
        addEvents();
    }

    private void addControls() {
        txtPhoneReceiver = findViewById(R.id.txtPhoneReciever);
        btnCallReceiver = findViewById(R.id.btnCallReceiver);
        lvMessage = findViewById(R.id.lvMessage);
        txtMessage = findViewById(R.id.txtMessage);
        txtMessage.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        btnSentMessage = findViewById(R.id.btnSentMessage);

        intent = getIntent();
        contact = (Contact) intent.getSerializableExtra("contact");

        txtPhoneReceiver.setText(contact.getPhone());
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int result = getResultCode();
                String msg = "Da gui thanh cong";
                if (result != Activity.RESULT_OK) {
                    msg = "Gui that bai";
                }
                Toast.makeText(SentMessageActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        };

        listSMS = new ArrayList();
        listSMS = getAllSMS();

        arrayAdapter = new ArrayAdapter(SentMessageActivity.this, android.R.layout.simple_list_item_1, listSMS);
        lvMessage.setAdapter(arrayAdapter);


    }

    private List getAllSMS() {
        List listSMS = new ArrayList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, 100);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            Uri uri = Uri.parse("content://sms/inbox");
            Cursor cursor = getContentResolver().query(uri,null, " address = " + txtPhoneReceiver.getText().toString(), null, null);
            int indexTimeStamp = cursor.getColumnIndex("date");
            int indexBody = cursor.getColumnIndex("body");

            if (indexBody < 0 || !cursor.moveToFirst())
                return listSMS;

            while (cursor.moveToNext()){
                String timeStamp = cursor.getString(indexTimeStamp);
                String body = cursor.getString(indexBody);

                listSMS.add(timeStamp + "\n" + body);
            }
        }
        return  listSMS;
    }

    private void addEvents() {

        btnCallReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + contact.getPhone());
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(uri);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 100);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    startActivity(intent);
                }
            }
        });

        btnSentMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = txtPhoneReceiver.getText().toString();
                String sms = txtMessage.getText().toString();
                if (phone.isEmpty() || sms.isEmpty()){
                    Toast.makeText(SentMessageActivity.this, "Nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
                } else {
                    sentSMS(phone, sms);
                }

            }
        });
    }

    private void sentSMS(String phone, String sms) {
        final SmsManager smsManager = SmsManager.getDefault();
        Intent msgSent = new Intent("ACTION_MSG_SENT");
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,msgSent,0);
        registerReceiver( broadcastReceiver , new IntentFilter("ACTION_MSG_SENT"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SentMessageActivity.REQUEST_CODE_SMS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            smsManager.sendTextMessage(phone, null, sms, pendingIntent, null);
        }
        finish();
    }


    @Override
    protected void onResume() {
        registerReceiver(broadcastReceiver, new IntentFilter("ACTION_MSG_SENT"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException ex) {
            // If Receiver not registered
        }
        super.onPause();
    }
}
