package sokolovska.sushchak.projektphonebook.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sokolovska.sushchak.projektphonebook.Contact;
import sokolovska.sushchak.projektphonebook.DBHelper;
import sokolovska.sushchak.projektphonebook.R;
import sokolovska.sushchak.projektphonebook.adapters.SimpleAdapter;


public class AllContactsActivity extends AppCompatActivity{


    private SearchView searchView;

    private DBHelper dataBase;

    private RecyclerView recyclerView;

    private List<Contact> contacts;
    private SimpleAdapter listAdapter;

    private SimpleAdapter gridAdapter;

    private SharedPreferences sharedPreferences;
    private final String LIST = "List";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_contact);
        sharedPreferences = getPreferences(MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_item_add_contact:
                startActivity(new Intent(this, AddNewContactActivity.class));
                break;

            case R.id.menu_item_delete_ch_contacts:
                if(dataBase.getAllContacts().getCount() != 0) {
                    Intent intent = new Intent(this, RemoveCheckedContactsActivity.class);
                    startActivity(intent);
                }
                else Toast.makeText(this, "Niema kontaktów", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_item_filter:
                if (dataBase.getAllContacts().getCount() != 0)
                    startActivity(new Intent(this, ContactByGroupActivity.class));
                else Toast.makeText(this, "Nie ma kontaktów", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_item_list: {
                item.setChecked(true);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(LIST, item.isChecked());
                editor.commit();
                initList();
                break;
            }

            case R.id.menu_item_grid: {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(LIST, false);
                editor.commit();
                item.setChecked(true);
                initList();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * metoda dla stworzenia meniu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem list = menu.findItem(R.id.menu_item_list);
        MenuItem grid = menu.findItem(R.id.menu_item_grid);
        if(sharedPreferences.getBoolean(LIST, true))
            list.setChecked(true);
        else grid.setChecked(true);
        return true;
    }

    /**
     * funkcja inicializacji zmienych
     */
    void initList() {
        dataBase = new DBHelper(this);
        recyclerView = (RecyclerView) findViewById(R.id.rv_all_contacts);
        searchView = (SearchView) findViewById(R.id.SearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(sharedPreferences.getBoolean(LIST, true)) {
                    if (listAdapter != null)
                        listAdapter.filterList(s);
                }else if(gridAdapter != null)
                    gridAdapter.filterList(s);

                return true;
            }
        });

        if(sharedPreferences.getBoolean(LIST, true)) { // dla grid lub listy

            Handler handlerForList = new Handler();  //Wątek dla wczytania kontaktów z bazy dannych
            handlerForList.post(new Runnable() {
                @Override
                public void run() {
                    Cursor cursor = dataBase.getAllContacts();
                    contacts = new ArrayList<>();

                    if (cursor != null && cursor.getCount() != 0) {
                        contacts = loadDataFromDB(cursor);

                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);

                        recyclerView.setHasFixedSize(false);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);


                        listAdapter = new SimpleAdapter(AllContactsActivity.this, contacts,
                                R.layout.list_item,
                                R.id.tv_list_name_ch,
                                R.id.tv_list_second_name_ch,
                                R.id.iv_image_item_ch);
                        recyclerView.setAdapter(listAdapter);
                        searchView.setQueryHint("Kontaktów : " + contacts.size());
                    }

                }
            });
        } else{
            Handler handlerForGrid = new Handler();  //Wątek dla wczytania kontaktów z bazy dannych
            handlerForGrid.post(new Runnable() {
                @Override
                public void run() {
                    Cursor cursor = dataBase.getAllContacts();
                    contacts = new ArrayList<>();

                    if (cursor != null && cursor.getCount() != 0) {
                        contacts = loadDataFromDB(cursor);

                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);

                        recyclerView.setHasFixedSize(false);

                        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
                        recyclerView.setLayoutManager(layoutManager);

                        gridAdapter = new SimpleAdapter(AllContactsActivity.this, contacts,
                                R.layout.grid_item,
                                R.id.tv_grid_name,
                                R.id.tv_grid_second_name,
                                R.id.iv_image_grid_item);
                        recyclerView.setAdapter(gridAdapter);
                        searchView.setQueryHint("Kontaktów : " + contacts.size());
                    }
                }
            });
        }

    }

    private ArrayList<Contact> loadDataFromDB(Cursor cursor){
        ArrayList<Contact> list = new ArrayList<>();

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
