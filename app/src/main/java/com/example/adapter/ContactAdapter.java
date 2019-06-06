package com.example.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.interfacelisterner.MainActivityListener;
import com.example.model.Contact;
import com.example.sqlitebasic.MainActivity;
import com.example.sqlitebasic.R;
import com.example.sqlitebasic.SentMessageActivity;

import java.util.List;

public class ContactAdapter extends ArrayAdapter {
    Activity context;
    int resource;
    List objects;
    ImageButton btnCall ;
    ImageButton btnSms ;
    ImageButton btnDel;
    MainActivityListener mainActivityListener;
    public ContactAdapter(Activity context, int resource, List objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(this.resource, null);


        TextView txtName = row.findViewById(R.id.txtName);
        TextView txtPhone = row.findViewById(R.id.txtPhone);
        ImageView imgAvatar = row.findViewById(R.id.imgAvatar);
         btnCall = row.findViewById(R.id.btnCall);
         btnSms = row.findViewById(R.id.btnSms);
         btnDel = row.findViewById(R.id.btnDel);
        btnCall.setFocusable(false);
        btnSms.setFocusable(false);
        btnDel.setFocusable(false);

        Contact contact = (Contact) this.objects.get(position);
        txtName.setText(contact.getId() + " " +contact.getName());
        txtPhone.setText(contact.getPhone());



        if (contact.getAvatar() != null) {
            // Đọc dữ liệu từ DB, convertToBitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(contact.getAvatar(),0,contact.getAvatar().length);
            // Set ảnh theo bitmap
            imgAvatar.setImageBitmap(bitmap);
        }
        events(contact);
        return row;
    }

    private void events(final Contact contact) {
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityListener.delContact(contact);
                            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityListener.callContact(contact);
            }
        });

        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivityListener.sentSmsContact(contact);
            }
        });

    }

    public void setListener(MainActivityListener mainActivityListener){
        this.mainActivityListener = mainActivityListener;
    }

}
