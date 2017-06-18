package sokolovska.sushchak.projektphonebook.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sokolovska.sushchak.projektphonebook.Contact;
import sokolovska.sushchak.projektphonebook.DBHelper;
import sokolovska.sushchak.projektphonebook.R;

public class ContactByGroupActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ExpandableListView expandableListView;

    private ArrayList<Map<String, String>> groupData;

    // коллекция для элементов одной группы
    private ArrayList<Map<String, String>> childDataItem;

    // общая коллекция для коллекций элементов
    private ArrayList<ArrayList<Map<String, String>>> childData;

    private Map<String, String> stringMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_by_group);
        init();
    }


    /**
     * funkcja inicializacji zmienych
     */
    void init(){

        expandableListView = (ExpandableListView) findViewById(R.id.expand_view);

        dbHelper = new DBHelper(this);

        Contact.Group gr = new Contact.Group(dbHelper.getGroups());
        ArrayList<String> groups = gr.getNames();
        ArrayList<Long> idGroups = gr.getIds();

        groups.add("Bez grupy");
        idGroups.add(-1l);

        groupData = new ArrayList<Map<String, String>>();
        for (String group : groups) {
            stringMap = new HashMap<String, String>();
            stringMap.put("groupName", group);
            groupData.add(stringMap);
        }

        String groupFrom[] = new String[] {"groupName"};
        int groupTo[] = new int[] {R.id.textView};

        childData = new ArrayList<ArrayList<Map<String, String>>>();
        childDataItem = new ArrayList<Map<String, String>>();

        for (int i = 0; i < idGroups.size(); i++){
            ArrayList<Contact> contacts = getContacts(idGroups.get(i));
            if(contacts == null){
                groupData.remove(i);
                continue;
            }
            for (Contact contact: getContacts(idGroups.get(i))) {
                if(contact == null) {
                    //idGroups.remove(i);
                   //groups.remove(i);
                }else {

                    stringMap = new HashMap<String, String>();
                    stringMap.put("id", String.valueOf(contact.getId()));
                    stringMap.put("contactName", contact.getName());
                    stringMap.put("ContactSecondName", contact.getSecondName());
                    childDataItem.add(stringMap);
                }
            }
            childData.add(childDataItem);
            childDataItem = new ArrayList<Map<String, String>>();
        }

        String childFrom[] = new String[] {"contactName","ContactSecondName"};
        // список ID view-элементов, в которые будет помещены атрибуты элементов
        int childTo[] = new int[] {R.id.tv_list_name, R.id.tv_list_second_name};

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                R.layout.simple_expandable_list_item,
                groupFrom,
                groupTo,
                childData,
                R.layout.item_expandable,
                childFrom,
                childTo);

        expandableListView = (ExpandableListView) findViewById(R.id.expand_view);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                String idStr =  childData.get(groupPosition).get(childPosition).get("id");
                Long idContact = Long.decode(idStr);

                Intent intentView = new Intent(getBaseContext(), FullDataContactActivity.class);
                intentView.putExtra("ContactId", idContact);
                startActivity(intentView);
                return false;
            }
        });
    }

    /**
     * metoda dla zapisania kontaktów w liste
     * @param idGroup id grupy
     * @return
     */
    private ArrayList<Contact> getContacts(long idGroup){
        ArrayList<Contact> list = new ArrayList<>();
        Cursor cursor = dbHelper.getContactsByGroup(idGroup);
        if (cursor == null)
            return null;
        int indexContact = cursor.getColumnIndex(DBHelper.DBContact.KEY_ID);
        int indexName = cursor.getColumnIndex(DBHelper.DBContact.KEY_NAME);
        int indexSecondName = cursor.getColumnIndex(DBHelper.DBContact.KEY_SECOND_NAME);
        int indexPhoto = cursor.getColumnIndex(DBHelper.DBContact.KEY_PHOTO);
        cursor.moveToFirst();
        do {
            Contact contact = new Contact();
            contact.setId(cursor.getLong(indexContact));
            contact.setName(cursor.getString(indexName));
            contact.setSecondName(cursor.getString(indexSecondName));
            contact.setPhoto(cursor.getString(indexPhoto));
            list.add(contact);

        } while (cursor.moveToNext());
        return list;
    }

}
