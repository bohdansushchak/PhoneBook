package sokolovska.sushchak.projektphonebook.activities;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import sokolovska.sushchak.projektphonebook.Contact;
import sokolovska.sushchak.projektphonebook.DBHelper;
import sokolovska.sushchak.projektphonebook.R;
import sokolovska.sushchak.projektphonebook.adapters.SimpleAllDataAdapter;


public class FullDataContactActivity extends AppCompatActivity {

    private ImageView ivFull;
    private ImageView ivSmall;
    private TextView tvContactName;

    private RecyclerView rvData;

    private Handler handlerSetImages;
    private Contact contact;
    private long contactId= -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_data_contact);

        rvData = (RecyclerView) findViewById(R.id.rv_all_data_contact);
        ivFull = (ImageView) findViewById(R.id.iv_mage_full);
        ivSmall = (ImageView) findViewById(R.id.iv_image_small);

        tvContactName = (TextView) findViewById(R.id.tv_name);

        handlerSetImages = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent != null)
            contactId = intent.getLongExtra("ContactId", -1);

        getData(contactId);
    }

    private void getData(long contactPosition){
        Cursor cursor = new DBHelper(FullDataContactActivity.this).getContact(contactPosition);
        contact = new Contact(FullDataContactActivity.this, cursor);

        ArrayList<String> data = contact.getAllData();
        ArrayList<String> title = contact.getTitles();
        int[] idIcons = contact.getIdIcons();


        StringBuilder contactName = new StringBuilder();
        if(contact.getName() != null) {
            contactName.append(contact.getName());
            contactName.append(" ");
        }
        if(contact.getMiddleName() != null) {
            contactName.append(contact.getMiddleName());
            contactName.append(" ");
        }
        if(contact.getSecondName() != null) {

            contactName.append(contact.getSecondName());
        }

        tvContactName.setText(contactName);

        SimpleAllDataAdapter allDataAdapter = new SimpleAllDataAdapter(FullDataContactActivity.this, idIcons, data, title, contact);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        rvData.setLayoutManager(layoutManager);
        rvData.setAdapter(allDataAdapter);

        if(contact.getPhoto() != null) {
            Handler handler = new Handler();
            handler.post(runnable);  // start wątku do wczytania zdjęcz
        }

    }
       Runnable runnable = new Runnable() {
        @Override
        public void run() {
                File photo = new File(contact.getPhoto());
                if(photo.exists()){
                    ivFull.setImageURI(Uri.parse(photo.getAbsolutePath()));
                    ivSmall.setImageURI(Uri.parse(photo.getAbsolutePath()));
            }
        }
    };


    @Override
    protected void onStop() {
        handlerSetImages.removeCallbacks(runnable); // zamknięcie wątku
        super.onStop();
    }

    public void clickBack(View view) {
        onBackPressed();
    }

    public void clickEdit(View view) {
        Intent intentEdit = new Intent(this, AddNewContactActivity.class);
        intentEdit.putExtra("Edit", true);
        intentEdit.putExtra("ContactId", contactId);
        startActivity(intentEdit);
    }
}
