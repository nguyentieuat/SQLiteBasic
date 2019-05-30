package com.example.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.Contact;
import com.example.sqlitebasic.MainActivity;
import com.example.sqlitebasic.R;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapterAutoComplete extends ArrayAdapter<Contact> {

    List<Contact> listContact;

    public ContactAdapterAutoComplete(MainActivity context, List<Contact> contactList) {
        super(context, 0, contactList);
        listContact = new ArrayList<>(contactList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.layout_item_contact, parent, false
            );
        }

        TextView  txtNameContact= convertView.findViewById(R.id.txtNameContact) ;
        TextView txtNumPhone = convertView.findViewById(R.id.txtNumPhone);

        Contact contact = getItem(position);
        if (contact != null) {
            txtNameContact.setText(contact.getName());
            txtNumPhone.setText(contact.getPhone());
        }
        return convertView;
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults results = new FilterResults();
//                List<Contact> suggestions = new ArrayList<>();
//
//                if (constraint == null || constraint.length() == 0) {
//                    suggestions.addAll(listContact);
//
//                } else {
//                    String filterPattern = constraint.toString().trim();
//
//                    for (Contact item : listContact) {
//                        if (filterPattern.matches("\\d")){
//                            if (item.getPhone().contains(filterPattern)) {
//                                suggestions.add(item);
//                            }
//                        } else {
//                            if (item.getName().contains(filterPattern)) {
//                                suggestions.add(item);
//                            }
//                        }
//                    }
//                }
//
//                results.values = suggestions;
//                results.count = suggestions.size();
//
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                clear();
//                addAll((List) results.values);
//                notifyDataSetChanged();
//            }
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults results = new FilterResults();
//                List<Contact> suggestions = new ArrayList<>();
//
//                if (constraint == null || constraint.length() == 0) {
//                    suggestions.addAll(listContact);
//
//                } else {
//                    String filterPattern = constraint.toString().trim();
//
//                    for (Contact item : listContact) {
//                        if (item.getPhone().contains(filterPattern)) {
//                            suggestions.add(item);
//                        }
//                    }
//                }
//
//                results.values = suggestions;
//                results.count = suggestions.size();
//
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                clear();
//                addAll((List) results.values);
//                notifyDataSetChanged();
//            }
//
//
//        };
//    }
}
