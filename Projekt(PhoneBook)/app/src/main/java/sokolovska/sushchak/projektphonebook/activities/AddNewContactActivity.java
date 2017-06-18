package sokolovska.sushchak.projektphonebook.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import sokolovska.sushchak.projektphonebook.Contact;
import sokolovska.sushchak.projektphonebook.fragmets.DataPickFragment;
import sokolovska.sushchak.projektphonebook.R;
import sokolovska.sushchak.projektphonebook.DBHelper;

public class AddNewContactActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, View.OnFocusChangeListener{

    private final long GROUP_NOTHING = -1;
    private final int REQUEST_PICK_IMAGE = 1;

    private String imagePath = null;

    private ImageView ivPhoto;
    private EditText etName;
    private EditText etSecondName;
    private EditText etMiddleName;
    private EditText etMobile;
    private EditText etWorkMobile;
    private EditText etMail;
    private EditText etWorkMail;
    private EditText etAddress;
    private EditText etWorkAddress;
    private EditText etHappyBirthday;
    private EditText etNote;
    private Spinner spGroup;

    private DBHelper database;
    private Long groupId;

    private ImageButton ib_mobileClear;
    private ImageButton ib_workMobileClear;
    private ImageButton ib_mailClear;
    private ImageButton ib_workMailClear;
    private ImageButton ib_addressClear;
    private ImageButton ib_workAddressClear;
    private ImageButton ib_happyBirthdayClear;
    private ImageButton ib_noteClear;

    private long contactId;
    private boolean edit = false;
    private Contact contact;
    private DialogFragment dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
        database = new DBHelper(this);

        initViews();

        Intent intent = getIntent();
        if(intent != null){
            contactId = intent.getLongExtra("ContactId", -1);
            edit = intent.getBooleanExtra("Edit", false);
        }

        if(edit) getData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = database.getGroups();
        cursor.moveToFirst();

        Contact.Group group = new Contact.Group(cursor);

        final ArrayList<String> groups = group.getNames();
        final ArrayList<Long> idGroups = group.getIds();

        groups.add(getString(R.string.choose_group));
        idGroups.add(GROUP_NOTHING);

        cursor.close();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groups);

        spGroup.setAdapter(arrayAdapter);

        if(edit) {
            int i = 0;

            if (contact.getIdGroup() != 0)
                for (i = 0; i < idGroups.size(); i++)
                    if (idGroups.get(i) != null && contact.getIdGroup() != 0)
                        if (idGroups.get(i) == contact.getIdGroup())
                            break;

            spGroup.setSelection(i);
        }else
            spGroup.setSelection(groups.size()-1);

        spGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position != (groups.size() - 1)) {
                groupId = idGroups.get(position);
                }
                else groupId = GROUP_NOTHING;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

    }

    /**
     * Metoda służy do wyświetlania lub schowania dodatkowych pól
     * Nazwiska oraz imię rodzinego
     * @param view
     */

    public void clickDown(View view) {

        ImageButton imageButton = (ImageButton) findViewById(R.id.iBtn_down);

        if(etSecondName.getVisibility() == View.VISIBLE){
            etName.setHint(R.string.add_name_and_surname);
            etSecondName.setVisibility(View.GONE);
            etMiddleName.setVisibility(View.GONE);
            imageButton.setBackground(getDrawable(R.drawable.ic_arrow_down));
        }
        else {
            etName.setHint(R.string.add_name);
            etSecondName.setVisibility(View.VISIBLE);
            etMiddleName.setVisibility(View.VISIBLE);
            imageButton.setBackground(getDrawable(R.drawable.ic_arrow_up));
        }
    }

    /**
     * funkcja dla inicializacji wszystkich
     * zmienych
     */
    private void initViews(){
        etName = (EditText) findViewById(R.id.et_add_name);
        etSecondName = (EditText) findViewById(R.id.et_add_second_name);
        etMiddleName = (EditText) findViewById(R.id.et_add_middle_name);
        etMobile = (EditText) findViewById(R.id.et_mobile);
        etWorkMobile = (EditText) findViewById(R.id.et_work_mobile);
        etMail = (EditText) findViewById(R.id.et_mail);
        etWorkMail = (EditText) findViewById(R.id.et_work_mail);
        etAddress = (EditText) findViewById(R.id.et_address);
        etWorkAddress = (EditText) findViewById(R.id.et_work_address);
        etHappyBirthday = (EditText) findViewById(R.id.et_happy_birthday);
        etNote = (EditText) findViewById(R.id.et_note);

        spGroup = (Spinner) findViewById(R.id.sp_add_group);

        ivPhoto = (ImageView) findViewById(R.id.iv_add_photo);

        ib_mobileClear = (ImageButton) findViewById(R.id.ib_mobile_clear);
        ib_workMobileClear = (ImageButton) findViewById(R.id.ib_work_mobile_clear);
        ib_mailClear = (ImageButton) findViewById(R.id.ib_mail_clear);
        ib_workMailClear = (ImageButton) findViewById(R.id.ib_work_mail_clear);
        ib_addressClear = (ImageButton) findViewById(R.id.ib_address_clear);
        ib_workAddressClear = (ImageButton) findViewById(R.id.ib_work_address_clear);
        ib_happyBirthdayClear = (ImageButton) findViewById(R.id.ib_happy_birthday_clear);
        ib_noteClear = (ImageButton) findViewById(R.id.ib_note_clear);

        etMobile.addTextChangedListener(this);
        etWorkMobile.addTextChangedListener(this);
        etMail.addTextChangedListener(this);
        etWorkMail.addTextChangedListener(this);
        etAddress.addTextChangedListener(this);
        etWorkAddress.addTextChangedListener(this);
        etHappyBirthday.addTextChangedListener(this);
        etNote.addTextChangedListener(this);

        ivPhoto.setOnClickListener(this);

        ib_mobileClear.setOnClickListener(this);
        ib_workMobileClear.setOnClickListener(this);
        ib_mailClear.setOnClickListener(this);
        ib_workMailClear.setOnClickListener(this);
        ib_addressClear.setOnClickListener(this);
        ib_workAddressClear.setOnClickListener(this);
        ib_happyBirthdayClear.setOnClickListener(this);
        ib_noteClear.setOnClickListener(this);

        etHappyBirthday.setOnFocusChangeListener(this);
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


        if(etMobile.length() > 0) ib_mobileClear.setVisibility(View.VISIBLE);
        else ib_mobileClear.setVisibility(View.INVISIBLE);

        if(etWorkMobile.length() > 0) ib_workMobileClear.setVisibility(View.VISIBLE);
        else ib_workMobileClear.setVisibility(View.INVISIBLE);

        if(etMail.length() > 0) ib_mailClear.setVisibility(View.VISIBLE);
        else ib_mailClear.setVisibility(View.INVISIBLE);

        if(etWorkMail.length() > 0) ib_workMailClear.setVisibility(View.VISIBLE);
        else ib_workMailClear.setVisibility(View.INVISIBLE);

        if(etAddress.length() > 0) ib_addressClear.setVisibility(View.VISIBLE);
        else ib_addressClear.setVisibility(View.INVISIBLE);

        if(etWorkAddress.length() > 0) ib_workAddressClear.setVisibility(View.VISIBLE);
        else ib_workAddressClear.setVisibility(View.INVISIBLE);

        if(etHappyBirthday.length() > 0) ib_happyBirthdayClear.setVisibility(View.VISIBLE);
        else ib_happyBirthdayClear.setVisibility(View.INVISIBLE);

        if(etNote.length() > 0) ib_noteClear.setVisibility(View.VISIBLE);
        else ib_noteClear.setVisibility(View.INVISIBLE);

    }

    @Override
    public void afterTextChanged(Editable editable) {}

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_mobile_clear:
                etMobile.setText("");
                ib_mobileClear.setVisibility(View.INVISIBLE);
                break;

            case R.id.ib_work_mobile_clear:
                etWorkMobile.setText("");
                ib_workMobileClear.setVisibility(View.INVISIBLE);
                break;

            case R.id.ib_mail_clear:
                etMail.setText("");
                ib_mailClear.setVisibility(View.INVISIBLE);
                break;

            case R.id.ib_work_mail_clear:
                etWorkMail.setText("");
                ib_workMailClear.setVisibility(View.INVISIBLE);
                break;

            case R.id.ib_address_clear:
                etAddress.setText("");
                ib_addressClear.setVisibility(View.INVISIBLE);
                break;

            case R.id.ib_work_address_clear:
                etWorkAddress.setText("");
                ib_workAddressClear.setVisibility(View.INVISIBLE);
                break;

            case R.id.ib_happy_birthday_clear:
                etHappyBirthday.setText("");
                ib_happyBirthdayClear.setVisibility(View.INVISIBLE);
                break;

            case R.id.ib_note_clear:
                etNote.setText("");
                ib_noteClear.setVisibility(View.INVISIBLE);
                break;

            case R.id.btn_cancel:
                Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;

            case R.id.iv_add_photo:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
               startActivityForResult(intent, REQUEST_PICK_IMAGE);
        }

    }

    /**
     * metoda dla pobrania z bazy wszystkiej informacji kontaktu
     */

    private void getData(){
        Cursor cursor = new DBHelper(this).getContact(contactId);

        contact = new Contact(this, cursor);

        etName.setText(contact.getName());
        etSecondName.setText(contact.getSecondName());
        etMiddleName.setText(contact.getMiddleName());
        etMobile.setText(contact.getMobile());
        etWorkMobile.setText(contact.getWorkMobile());
        etMail.setText(contact.getMail());
        etWorkMail.setText(contact.getWorkMail());
        etAddress.setText(contact.getAddress());
        etWorkAddress.setText(contact.getWorkAddress());
        etHappyBirthday.setText(contact.getHappyBirthday());
        etNote.setText(contact.getNote());

        if(contact.getPhoto() != null)
            if(new File(contact.getPhoto()).exists())
                ivPhoto.setImageURI(Uri.parse(contact.getPhoto()));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }


    /**
     * funkcja dodania kontaktu do bazy
     * @param view
     */
    public void save(View view) {

        ContentValues contentValues = readDataFromEditText();
        if(contentValues != null )
            if (edit) {
              database.updateContact(contentValues, contactId);
              onBackPressed();

            } else {
              database.AddContact(contentValues);
              onBackPressed();

            }
            else Toast.makeText(this, getString(R.string.all_pols_null), Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK)
            if(data != null) {
                Uri imageUri = data.getData();
                imagePath =  getRealPathFromUri(this, imageUri);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ivPhoto.setImageBitmap(rotateBitmap(bitmap, imagePath));
            }
    }

    /**
     *metoda dla pobrania rzeczywistego miejsca zachowania obrazu
     * @param context
     * dla udostępniania funkcyj na poziomie Activity
     * @param contentUri
     * @return
     */
    private String getRealPathFromUri(Context context, Uri contentUri){
        Cursor cursor = null;
        try{
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * metoda dla ustawienia obrótu obrazu
     * @param bitmap obrazek w postaci Bitmap
     * @param path miejsce zachowania obrazku
     * @return Bitmap
     */
    private Bitmap rotateBitmap(Bitmap bitmap, String path){

           Bitmap myBitmap = bitmap;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("...", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            switch (orientation){
                case 6:
                    matrix.postRotate(90);
                    break;
                case 3:
                    matrix.postRotate(180);
                    break;
                case 8:
                    matrix.postRotate(270);
                    break;
        }
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myBitmap;
    }

    /**
     * metoda która zwraca true kiedy wszystki kontrolki(EditText) są puste
     * false obrotnie
     * @return
     */
    private boolean isAllClear(){
        return !(etName.length() != 0 ||
                etSecondName.length() != 0 ||
                etMiddleName.length() != 0 ||
                etNote.length() != 0);
    }

    public void clickAddDate(View view) {
        dialog = new DataPickFragment();
        dialog.show(getSupportFragmentManager(), getString(R.string.choose_date));
    }

    @Override
    public void onFocusChange(View view, boolean b) {

        if(view.equals(etHappyBirthday) && b){
            dialog = new DataPickFragment();
            dialog.show(getSupportFragmentManager(), getString(R.string.choose_date));
        }

    }

    /**
     * metoda która odczytuje zapisane informacje o kontakcie
     * oraz zwraca objekt klasy ContentValues który potrzebny dla zapisania w baze
     * @return
     */
    private ContentValues readDataFromEditText() {

        if (!isAllClear()) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(DBHelper.DBContact.KEY_NAME, String.valueOf(etName.length() != 0 ? etName.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_SECOND_NAME, String.valueOf(etSecondName.length() != 0 ? etSecondName.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_MIDDLE_NAME, String.valueOf(etMiddleName.length() != 0 ? etMiddleName.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_MOBILE, String.valueOf(etMobile.length() != 0 ? etMobile.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_WORK_MOBILE, String.valueOf(etWorkMobile.length() != 0 ? etWorkMobile.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_MAIL, String.valueOf(etMail.length() != 0 ? etMail.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_WORK_MAIL, String.valueOf(etWorkMail.length() != 0 ? etWorkMail.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_ADDRESS, String.valueOf(etAddress.length() != 0 ? etAddress.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_WORK_ADDRESS, String.valueOf(etWorkAddress.length() != 0 ? etWorkAddress.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_NOTE, String.valueOf(etNote.length() != 0 ? etNote.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_HAPPY_BIRTHDAY, String.valueOf(etHappyBirthday.length() != 0 ? etHappyBirthday.getText() : ""));
            contentValues.put(DBHelper.DBContact.KEY_GROUP, groupId);

            if (imagePath != null)
                contentValues.put(DBHelper.DBContact.KEY_PHOTO, imagePath);

            return contentValues;
        }

        return null;
    }

}
