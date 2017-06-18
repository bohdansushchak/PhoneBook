package sokolovska.sushchak.projektphonebook.activities;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import sokolovska.sushchak.projektphonebook.Contact;
import sokolovska.sushchak.projektphonebook.DBHelper;
import sokolovska.sushchak.projektphonebook.R;

public class RemoveCheckedContactsActivity extends AppCompatActivity {


    private ListView listView;
    private DBHelper dbHelper;

    private String[] names;
    private ArrayList<Contact> contacts;
    private long [] idContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_checked_contacts);
        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        contacts = getDataDB();
        if(contacts.size() != 0) {
            listView = (ListView) findViewById(R.id.lv_contacts_remove);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


            names = getNames(contacts);

            ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, names);

            listView.setAdapter(adapter);
        }
        else {
            Toast.makeText(this, "Nie ma kontaktów do usunięcia", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    public void cancel(View view) {
        onBackPressed();
    }

    public void remove(View view) {
        SparseBooleanArray sbArray = listView.getCheckedItemPositions();
        if (sbArray.size() != 0){
            for (int i = 0; i < sbArray.size(); i++) {
                int key = sbArray.keyAt(i);
                if (sbArray.get(key))
                    dbHelper.deleteContact(idContacts[key]);
            }
        onBackPressed();
    }
        else Toast.makeText(this, "Nie ma kontaktów do usunięcia", Toast.LENGTH_LONG).show();
    }

    private ArrayList<Contact> getDataDB(){
        Cursor cursor = dbHelper.getAllContacts();
        ArrayList<Contact> list = new ArrayList<>();

        int indexContact = cursor.getColumnIndex(DBHelper.DBContact.KEY_ID);
        int indexName = cursor.getColumnIndex(DBHelper.DBContact.KEY_NAME);
        int indexSecondName = cursor.getColumnIndex(DBHelper.DBContact.KEY_SECOND_NAME);
        int indexPhoto = cursor.getColumnIndex(DBHelper.DBContact.KEY_PHOTO);
        if(cursor.getCount() != 0) {

            cursor.moveToFirst();
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getLong(indexContact));
                contact.setName(cursor.getString(indexName));
                contact.setSecondName(cursor.getString(indexSecondName));
                contact.setPhoto(cursor.getString(indexPhoto));
                list.add(contact);

            } while (cursor.moveToNext());
        }
        return list;
    }

    private String[] getNames(ArrayList<Contact> contacts){
        String[] names = new String[contacts.size()];
        idContacts = new long[contacts.size()];
        for(int i = 0 ; i < contacts.size(); i++){

            StringBuilder name = new StringBuilder();

            if(contacts.get(i) != null) {
                name.append(contacts.get(i).getName());
                name.append(" ");
            }
            if(contacts.get(i).getSecondName() != null)
                name.append(contacts.get(i).getSecondName());

            names[i] = name.toString();
            idContacts[i] = contacts.get(i).getId();
        }
        return names;
    }
}
