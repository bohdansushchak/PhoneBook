package sokolovska.sushchak.projektphonebook;

import android.content.Context;
import android.database.Cursor;
import java.util.ArrayList;


public class Contact {

    private long id;
    private String name;
    private String secondName;
    private String middleName;
    private String mobile;
    private String workMobile;
    private String mail;
    private String workMail;
    private String address;
    private String workAddress;
    private String photo;
    private String happyBirthday;
    private String group;
    private String note;

    private ArrayList<String> data;
    private ArrayList<String> title;
    private int[] idIcons;
    private Context context;
    private long idGroup = 0;

    public Contact (){
    }

    public Contact(Context context, Cursor cursor){

        if (context == null)
            throw new IllegalArgumentException("Context cannot be null");
        this.context = context;

        int iId = cursor.getColumnIndex(DBHelper.DBContact.KEY_ID);
        int iName = cursor.getColumnIndex(DBHelper.DBContact.KEY_NAME);
        int iSecondName = cursor.getColumnIndex(DBHelper.DBContact.KEY_SECOND_NAME);
        int iMiddleName = cursor.getColumnIndex(DBHelper.DBContact.KEY_MIDDLE_NAME);
        int iMobile = cursor.getColumnIndex(DBHelper.DBContact.KEY_MOBILE);
        int iWorkMobile = cursor.getColumnIndex(DBHelper.DBContact.KEY_WORK_MOBILE);
        int iMail = cursor.getColumnIndex(DBHelper.DBContact.KEY_MAIL);
        int iWorkMail = cursor.getColumnIndex(DBHelper.DBContact.KEY_WORK_MAIL);
        int iAddress = cursor.getColumnIndex(DBHelper.DBContact.KEY_ADDRESS);
        int iWorkAddress = cursor.getColumnIndex(DBHelper.DBContact.KEY_WORK_ADDRESS);
        int iPhoto = cursor.getColumnIndex(DBHelper.DBContact.KEY_PHOTO);
        int iNote = cursor.getColumnIndex(DBHelper.DBContact.KEY_NOTE);
        int iHappyBirthday = cursor.getColumnIndex(DBHelper.DBContact.KEY_HAPPY_BIRTHDAY);
        int iGroup = cursor.getColumnIndex(DBHelper.DBContact.KEY_GROUP);

        cursor.moveToFirst();

        this.setId(cursor.getLong(iId));
        this.setName(cursor.getString(iName));
        this.setSecondName(cursor.getString(iSecondName));
        this.setMiddleName(cursor.getString(iMiddleName));
        this.setMobile(cursor.getString(iMobile));
        this.setWorkMobile(cursor.getString(iWorkMobile));
        this.setMail(cursor.getString(iMail));
        this.setWorkMail(cursor.getString(iWorkMail));
        this.setAddress(cursor.getString(iAddress));
        this.setWorkAddress(cursor.getString(iWorkAddress));
        this.setPhoto(cursor.getString(iPhoto));
        this.setNote(cursor.getString(iNote));
        this.setHappyBirthday(cursor.getString(iHappyBirthday));
        this.setIdGroup(cursor.getLong(iGroup));

        data = new ArrayList<>();
        title = new ArrayList<>();
        int i = 0;
        idIcons = new int[8];

        if(mobile != null){
            data.add(mobile);
            title.add(this.context.getString(R.string.add_mobile));
            idIcons[i] = R.drawable.ic_phone;
            ++i;
        }
        if(workMobile != null) {
            data.add(workMobile);
            title.add(this.context.getString(R.string.add_workMobile));
            idIcons[i] = R.drawable.ic_phone;
            ++i;
        }
        if(mail != null) {
            data.add(mail);
            title.add(this.context.getString(R.string.add_mail));
            idIcons[i] = R.drawable.ic_email;
            ++i;
        }
        if(workMail != null) {
            data.add(workMail);
            title.add(this.context.getString(R.string.add_workMail));
            idIcons[i] = R.drawable.ic_email;
            ++i;
        }
        if(address != null) {
            data.add(address);
            title.add(this.context.getString(R.string.add_address));
            idIcons[i] = R.drawable.ic_home;
            ++i;
        }
        if(workAddress != null) {
            data.add(workAddress);
            title.add(this.context.getString(R.string.add_workAddress));
            idIcons[i] = R.drawable.ic_business;
            ++i;
        }
        if(happyBirthday != null) {
            data.add(happyBirthday);
            title.add(this.context.getString(R.string.add_happyBirthday));
            idIcons[i] = R.drawable.ic_cake;
            ++i;
        }
        if(idGroup != -1)
        {
            Group group = new DBHelper(context).getGroup(idGroup);
            data.add(group.getName());
            title.add(this.context.getString(R.string.add_group));
            idIcons[i] = R.drawable.ic_group;
            ++i;
        }
        if(note != null) {
            data.add(note);
            title.add(this.context.getString(R.string.add_note));
            idIcons[i] = R.drawable.ic_note;
        }

    }

    /**
     * funkcja dla pobrania wszystkiej informacji kontaktu w postaci listy
     * @return
     */
    public ArrayList<String> getAllData(){ return data; }

    public ArrayList<String> getTitles(){
        return title;
    }
    public int[] getIdIcons(){ return idIcons; }

    public String allData(){
        StringBuilder contact = new StringBuilder();

        contact.append(name);
        if(secondName != null)
            contact.append(" " + secondName);

        contact.append("\n");

        for(int i = 0 ; i < data.size(); i++){
            contact.append(title.get(i) + ": " + data.get(i) + "\n");
        }

        return contact.toString();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(long idGroup) {
        if(idGroup != 0)
        this.idGroup = idGroup;
    }

    public void setNote(String note) {

        if(note.length() > 0)
        this.note = note;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
            this.photo = photo;
    }

    public String getHappyBirthday() {
        return happyBirthday;
    }

    public void setHappyBirthday(String happyBirthday) {

        if(happyBirthday.length() > 0)
            this.happyBirthday = happyBirthday;
        else this.happyBirthday = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        if(name.length() > 0)
            this.name = name;
        else this.name = null;
    }

    public String getSecondName() {return secondName;}

    public void setSecondName(String secondName) {

        if(secondName.length() > 0)
            this.secondName = secondName;
        else this.secondName = null;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {

        if(middleName.length() > 0)
            this.middleName = middleName;
        else this.middleName = null;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {

        if(mobile.length() > 0)
            this.mobile = mobile;
        else this.mobile = null;
    }

    public String getWorkMobile() {
        return workMobile;
    }

    public void setWorkMobile(String workMobile) {

        if(workMobile.length() > 0)
            this.workMobile = workMobile;
        else this.workMobile = null;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        if(mail.length() > 0)
            this.mail = mail;
        else this.mail = null;
    }

    public String getWorkMail() {
        return workMail;
    }

    public void setWorkMail(String workMail) {
        if(workMail != null && workMail.length() > 0)
        this.workMail = workMail;
        else this.workMail = null;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {

        if(address.length() > 0)
            this.address = address;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {

        if(workAddress.length() > 0)
            this.workAddress = workAddress;
    }
    public static class Group{
        private long id;
        private String name;
        private ArrayList<String> names;
        private ArrayList<Long> ids;

        Group(){}

        public Group(Cursor cursor){
            names = new ArrayList<>();
            ids = new ArrayList<>();
            int id = cursor.getColumnIndex(DBHelper.Group.KEY_ID);
            int name = cursor.getColumnIndex(DBHelper.Group.KEY_NAME);

            cursor.moveToFirst();
            do{
                names.add(cursor.getString(name));
                ids.add(cursor.getLong(id));

            }while (cursor.moveToNext());
        }

        public ArrayList<String> getNames() {
            return names;
        }

        public ArrayList<Long> getIds() {
            return ids;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
