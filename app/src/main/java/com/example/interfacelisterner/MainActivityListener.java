package com.example.interfacelisterner;

import com.example.model.Contact;

public interface MainActivityListener {


    void sentSmsContact(Contact contact);

    void callContact(Contact contact);

    void delContact(Contact contact);
}
