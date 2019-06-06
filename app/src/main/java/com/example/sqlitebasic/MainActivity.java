package com.example.sqlitebasic;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.adapter.ContactAdapter;
import com.example.adapter.ContactAdapterAutoComplete;
import com.example.interfacelisterner.MainActivityListener;
import com.example.model.Contact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityListener {
    public static final int REQUEST_CODE_MAIN = 13;
    public static final int REQUEST_CODE_DEL = 17;
    public static final int SENT_MESSAGE = 10;
    AutoCompleteTextView txtSeach;
    ListView lvContact;
    FloatingActionButton btnAdd;

    public static List listContact;
    public static ContactAdapter contactAdapter;
    public static ContactAdapterAutoComplete contactAdapterAutoComplete;

    public static String DB_NAME = "dbContact.sqlite";
    public static final String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database = null;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Ghi File SQLite vào Hệ thống
        copyDBtoSys();

        // Khởi tạo các component
        addControls();
        // Khởi tạo sự kiện
        addEvents();


        // Trả dữ liệu từ SQLite to View
        showAllContactOnListView();
    }

    private void showAllContactOnListView() {
        // Mở file SQLite
        database = openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        // Truy vấn CSDL
        Cursor cursor = database.query("contact", null, null, null, null, null,null);
        listContact.clear();
        while (cursor.moveToNext()){
            Contact contact = new Contact();
            contact.setId(cursor.getInt(0));
            contact.setName(cursor.getString(1));
            contact.setPhone(cursor.getString(2));
            contact.setAvatar(cursor.getBlob(3));
            listContact.add(contact);
        }
        cursor.close();
        contactAdapter.notifyDataSetChanged();
        contactAdapterAutoComplete.updateListContact(listContact);
    }

    private void copyDBtoSys() {
        File dbFile = getDatabasePath(DB_NAME);
        if (!dbFile.exists()){
            try {
                copyDBfromAsset();
            }
            catch (Exception ex){
                Log.e("Error_copyDBtoSys", ex.toString());
            }
        }
    }

    private void copyDBfromAsset() {
        try {
            // File input
            InputStream input = getAssets().open(DB_NAME);
            // path file output
            String outFileName = getPathStore();

            // Create file output
            File file = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            // Check exist
            if (!file.exists()){
                file.mkdir();
            }
            OutputStream output = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int lenght;
            while ((lenght = input.read(buffer)) > 0) {
                output.write(buffer, 0, lenght);
            }
            output.flush();
            output.close();
            input.close();
        }
        catch (Exception ex){
            Log.e("Error_copyDBfromAsset", ex.toString());
        }

    }

    private String getPathStore(){
        // Trả về thư mục gốc getApplicationInfo().dataDir
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DB_NAME;
    }

    private void addEvents() {
        // Button add
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, AddContactActivity.class);
                Contact contact = null;
                intent.putExtra("contact", contact);
                startActivityForResult(intent, REQUEST_CODE_MAIN);
            }
        });

        // Click view detail
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(MainActivity.this, AddContactActivity.class);
                Contact contact = (Contact) listContact.get(position);
                intent.putExtra("contact", contact);
                startActivityForResult(intent, REQUEST_CODE_MAIN);
            }
        });

        // Click on autotextview
        txtSeach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.parse("tel:" + txtSeach.getText().toString());
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(uri);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 101);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    startActivity(intent);
                }
            }
        });



    }

    private void addControls() {
        txtSeach = findViewById(R.id.txtSeach);
        lvContact = findViewById(R.id.lvContact);
        btnAdd = findViewById(R.id.btnAdd);
        listContact = new ArrayList<Contact>();
        contactAdapter =  new ContactAdapter(MainActivity.this,R.layout.item_layout,listContact);
        lvContact.setAdapter(contactAdapter);
        contactAdapterAutoComplete = new ContactAdapterAutoComplete(MainActivity.this, listContact);
        txtSeach.setAdapter(contactAdapterAutoComplete);
        contactAdapter.setListener(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Nếu là thêm mới
        if (requestCode == REQUEST_CODE_MAIN && resultCode == AddContactActivity.REQUEST_CODE_ADD_CONTACT){
            Contact contact = (Contact) data.getSerializableExtra("contact");
            addContact(contact);
        }
        //Nếu là update
        else if (requestCode == REQUEST_CODE_MAIN && resultCode == AddContactActivity.REQUEST_CODE_EDIT_CONTACT){
            Contact contact = (Contact) data.getSerializableExtra("contact");
            updateContact(contact);
        }
    }

    private void updateContact(Contact contact) {
        ContentValues row = new ContentValues();
        row.put("name", contact.getName());
        row.put("phone", contact.getPhone());
        row.put("avatar",contact.getAvatar());

        long r = database.update("contact", row, "id = ?", new String[]{contact.getId() +""});
//        showAllContactOnListView();
    }

    private void addContact(Contact contact) {
        ContentValues row = new ContentValues();
        row.put("name", contact.getName());
        row.put("phone", contact.getPhone());
        row.put("avatar",contact.getAvatar());

        long r = database.insert("contact",null, row);
        showAllContactOnListView();
    }

    @Override
    public void sentSmsContact(Contact contact) {
        Intent intent = new Intent(MainActivity.this, SentMessageActivity.class);
        intent.putExtra("contact", contact);
        MainActivity.this.startActivityForResult(intent, SENT_MESSAGE);
    }

    @Override
    public void callContact(Contact contact) {
        Uri uri = Uri.parse("tel:" + contact.getPhone());
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(uri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && MainActivity.this.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            MainActivity.this.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 100);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            MainActivity.this.startActivity(intent);
        }
    }

    @Override
    public void delContact(Contact contact) {
        database.delete("contact"," id = ?", new String[]{contact.getId() + ""});

        Cursor cursor =  MainActivity.database.query("contact", null, null, null, null, null,null);
        listContact.clear();
        while (cursor.moveToNext()){
            Contact contactResult = new Contact();
            contactResult.setId(cursor.getInt(0));
            contactResult.setName(cursor.getString(1));
            contactResult.setPhone(cursor.getString(2));
            contactResult.setAvatar(cursor.getBlob(3));
            listContact.add(contactResult);
        }
        cursor.close();
        contactAdapterAutoComplete.updateListContact(MainActivity.listContact);
        contactAdapter.notifyDataSetChanged();
    }
}
