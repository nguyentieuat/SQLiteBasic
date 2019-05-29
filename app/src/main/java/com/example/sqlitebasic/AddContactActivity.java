package com.example.sqlitebasic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.model.Contact;

import java.io.ByteArrayOutputStream;

public class AddContactActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_ADD_IMG = 1;
    public static final int REQUEST_CODE_ADD_CONTACT = 33;
    public static final int REQUEST_CODE_EDIT_CONTACT = 34;
    ImageView imgInputAvatar;
    EditText txtInputName, txtInputPhone;
    ImageButton btnSave, btnCancle;
    Intent intent;
    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        addControls();
        addEvents();
    }

    private void addControls() {
        // khai báo các component
        imgInputAvatar = findViewById(R.id.imgInputAvatar);
        txtInputName = findViewById(R.id.txtInputName);
        txtInputPhone = findViewById(R.id.txtInputPhone);
        btnSave = findViewById(R.id.btnSave);
        btnCancle = findViewById(R.id.btnCancle);

        intent = getIntent();
        contact = (Contact) intent.getSerializableExtra("contact");
        if (contact != null) {
            txtInputName.setText(contact.getName());
            txtInputPhone.setText(contact.getPhone());
            if (contact.getAvatar() != null) {
                // Đọc dữ liệu từ DB, convertToBitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(contact.getAvatar(),0,contact.getAvatar().length);
                // Set ảnh theo bitmap
                imgInputAvatar.setImageBitmap(bitmap);
            }
        }

    }

    private void addEvents() {
        // Thêm ảnh
        imgInputAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImg();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void saveData() {
        // Chuyển ảnh thành mảng byte
        byte[] avatar = convertImgToArrayByte(imgInputAvatar);
        String name = txtInputName.getText().toString();
        String phone = txtInputPhone.getText().toString();
        if (phone == "" || phone.isEmpty()) {
            Toast.makeText(this, "Nhập thông tin", Toast.LENGTH_LONG).show();
            return;
        }

        if (contact != null) {
            contact.setAvatar(avatar);
            contact.setName(name);
            contact.setPhone(phone);

            intent.putExtra("contact", contact);
            setResult(REQUEST_CODE_EDIT_CONTACT, intent);
        }
        else {
            Contact contact = new Contact(name, phone, avatar);
            intent.putExtra("contact", contact);
            setResult(REQUEST_CODE_ADD_CONTACT, intent);
        }
        finish();
    }

    private byte[] convertImgToArrayByte(ImageView imgInputAvatar) {
        // chuyển ảnh thành dangj bitmap
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imgInputAvatar.getDrawable();

        // Nếu có ảnh
        if (bitmapDrawable != null) {
            // Chuyển dạng bitmap
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //Chuyển ảnh sang bitmap với định dạng PNG, chất lượng 100
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            // Trả về mảng byte
            return stream.toByteArray();
        }
        // Không có ảnh trả về null
        return null;
    }

    private void addImg() {
        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_ADD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (requestCode == REQUEST_CODE_ADD_IMG && resultCode == RESULT_OK)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgInputAvatar.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
