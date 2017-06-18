package sokolovska.sushchak.projektphonebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper{

    private static final String DB_NAME = "phonebook";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase sqLiteDatabase;

    private Context context;

    public DBHelper(final Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context cannot be null");

        this.context = context;

        sqLiteDatabase = new SQLiteOpenHelper(DBHelper.this.context, DB_NAME, null, DB_VERSION){

            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                sqLiteDatabase.execSQL(DBContact.CREATE_TABLE);

                sqLiteDatabase.execSQL(Group.CREATE_TABLE);

                ContentValues cv = new ContentValues();
                String[] groups = {context.getString(R.string.group_vip),
                        context.getString(R.string.group_kolege),
                        context.getString(R.string.group_rodzina),
                        context.getString(R.string.group_neighbors),
                        context.getString(R.string.group_iidp)};

                for (String str : groups) {
                    cv.put(Group.KEY_NAME, str);
                    sqLiteDatabase.insert(Group.TABLE_NAME, null, cv);
                    cv.clear();
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}
        }.getWritableDatabase();

    }

    /**
     * metoda dla odczytu wszystkich kontaktów z bazy
     * @return cursor
     */
    public Cursor getAllContacts(){
        Cursor cursor = sqLiteDatabase.query(DBContact.TABLE_NAME, null, null, null, null, null, DBContact.KEY_NAME);
        if(cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    /**
     * dla pobrania jednego kontaktu z bazy
     * @param contactId id kontaktu w bazie
     * @return
     */
    public Cursor getContact(long contactId){

        String selection = DBContact.KEY_ID + "=?";
        String args[] = new String[]{String.valueOf(contactId)};

        Cursor cursor = sqLiteDatabase.query(DBContact.TABLE_NAME, null, selection, args , null, null, null);
        if(cursor != null && cursor.getCount() > 0 ){
            return cursor;
        }
        else return null;
    }

    /**
     * funkcja pobrania grupy z bazy
     * @param contactId id grupy w bazie
     * @return
     */
    public Contact.Group getGroup(long contactId){

        Contact.Group group = new Contact.Group();

        String selection = Group.KEY_ID + "=?";
        String args[] = new String[]{String.valueOf(contactId)};

        Cursor cursor = sqLiteDatabase.query(Group.TABLE_NAME, null, selection, args , null, null, null);
        cursor.moveToFirst();

        group.setName(cursor.getString(cursor.getColumnIndex(Group.KEY_NAME)));
        group.setId(cursor.getLong(cursor.getColumnIndex(Group.KEY_ID)));
        return group;
    }


    /**
     * metoda do wpisania w bazę nowego kontaktu
     * @param cv obiekt klasy ContentValues
     *           gdzie zapisania wszystka informacja kontaktu
     */
    public void AddContact(ContentValues cv){
        sqLiteDatabase.insert(DBContact.TABLE_NAME, null, cv);
    }

    /**
     * metoda do zmieniania informacji kontaktu
     * @param cv obiekt klasy ContentValues
     * @param id id kontaktu w bazie
     */
    public void updateContact(ContentValues cv, long id){

        String args[] = new String[]{String.valueOf(id)};
        String whereClause = DBContact.KEY_ID + "=?";

        sqLiteDatabase.update(DBContact.TABLE_NAME, cv, whereClause, args);

    }

    /**
     * metoda do usunięcia z bazy kontaktu
     * @param id id kontaktu
     * @return
     */
    public boolean deleteContact(long id){

        String selection = DBContact.KEY_ID + "=?";
        String args[] = new String[]{String.valueOf(id)};

        int allow = sqLiteDatabase.delete(DBContact.TABLE_NAME, selection, args);

        if (allow == 1) return true;
        else return false;
    }

    /**
     * metoda do pobrania wszystkich grup z bazy
     * @return
     */
    public Cursor getGroups(){
        Cursor cursor = sqLiteDatabase.query(Group.TABLE_NAME, null, null, null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    /**
     * odczytuje wszystkich kontaktów które są w jednej grupię
     * @param idGroup id grupy w bazie
     * @return
     */
    public Cursor getContactsByGroup(long idGroup){
        String selection = DBContact.KEY_GROUP + "=?";
        String args[] = new String[]{String.valueOf(idGroup)};

        Cursor cursor = sqLiteDatabase.query(DBContact.TABLE_NAME, null, selection, args , null, null, null);
        if(cursor != null && cursor.getCount() > 0 ){
            return cursor;
        }
        else return null;
    }

    public void addGroup(ContentValues cv){
        sqLiteDatabase.insert(Group.TABLE_NAME, null, cv);
    }

    public void removeGroup(long idGroup){
        String selection = Group.KEY_ID + "=?";
        String args[] = new String[]{String.valueOf(idGroup)};

        sqLiteDatabase.delete(Group.TABLE_NAME, selection, args);
    }


    public void close(){
        sqLiteDatabase.close();
    }


    public class DBContact {

        public static final String TABLE_NAME = "contacts";

        public static final String KEY_ID = "_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_SECOND_NAME = "second_name";
        public static final String KEY_MIDDLE_NAME = "middle_name";
        public static final String KEY_MOBILE = "phone";
        public static final String KEY_WORK_MOBILE = "work_phone";
        public static final String KEY_MAIL = "mail";
        public static final String KEY_WORK_MAIL = "work_mail";
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_WORK_ADDRESS = "work_address";
        public static final String KEY_PHOTO = "photo";
        public static final String KEY_NOTE = "note";
        public static final String KEY_HAPPY_BIRTHDAY = "happybirthday";
        public static final String KEY_GROUP = "groupa";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "( "
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_NAME + " text, "
                + KEY_SECOND_NAME + " text,"
                + KEY_MIDDLE_NAME + " text,"
                + KEY_MOBILE + " text,"
                + KEY_WORK_MOBILE + " text,"
                + KEY_MAIL + " text,"
                + KEY_WORK_MAIL + " text,"
                + KEY_ADDRESS + " text,"
                + KEY_WORK_ADDRESS + " text,"
                + KEY_PHOTO + " text,"
                + KEY_HAPPY_BIRTHDAY + " numeric,"
                + KEY_NOTE + " text,"
                + KEY_GROUP + " integer)";
    }

    public class Group{

        public static final String TABLE_NAME = "groups";

        public static final String KEY_ID = "_id";
        public static final String KEY_NAME = "name";

        public static final String CREATE_TABLE = "create table " + TABLE_NAME + "( "
                + KEY_ID + " integer primary key autoincrement,"
                + KEY_NAME + " text not null);";

    }
}
