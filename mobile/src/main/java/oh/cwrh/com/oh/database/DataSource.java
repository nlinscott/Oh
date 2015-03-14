package oh.cwrh.com.oh.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Nic on 1/31/2015.
 */
public class DataSource {

    private static DataSource dbInstance = null;

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NAME,
            MySQLiteHelper.COLUMN_PHONE,
            MySQLiteHelper.COLUMN_PHOTO
    };
    private boolean isOpen;

    public static DataSource getInstance(Context c){
        if(dbInstance == null) {
            dbInstance = new DataSource(c);
        }
        return dbInstance;
    }

    private DataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        isOpen = false;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        isOpen = true;
    }

    public boolean isOpen(){return isOpen;}

    public void close(){
        dbHelper.close();
        isOpen = false;
    }

    private void addContact(Contact c){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, c.getName());
        values.put(MySQLiteHelper.COLUMN_PHONE, c.getPhone());
        if(c.getPhotoUri()!=null){
            values.put(MySQLiteHelper.COLUMN_PHOTO, c.getPhotoUri().toString());
        }
       database.insert(MySQLiteHelper.TABLE_CONTACTS, null,
                values);

    }

    /**
     * Checks the database to see if the contact exists
     * @param contact
     * @return
     */
    public boolean contactExists(Contact contact){

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
                allColumns,
                MySQLiteHelper.COLUMN_NAME + " = ? AND " +
                        MySQLiteHelper.COLUMN_PHONE + " = ?",
                new String[]{contact.getName(), contact.getPhone()},
                null,null, null, null);

        boolean result = cursor.moveToFirst();
        cursor.close();

        return result;
    }

    public Contact addContactToUI(Contact c){

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, c.getName());
        values.put(MySQLiteHelper.COLUMN_PHONE, c.getPhone());
        if(c.getPhotoUri()!=null){
            values.put(MySQLiteHelper.COLUMN_PHOTO, c.getPhotoUri().toString());
        }
        long insertId = database.insert(MySQLiteHelper.TABLE_CONTACTS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId,null,
                null, null, null);

        cursor.moveToFirst();
        Contact contact = cursorToContact(cursor);
        cursor.close();
        return contact;
    }

    public void deleteContact(Contact c) {
        long id = c.getId();
        database.delete(MySQLiteHelper.TABLE_CONTACTS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Contact c = cursorToContact(cursor);
            contacts.add(c);

            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return contacts;
    }

    private Contact cursorToContact(Cursor cursor) {
        if(cursor.getString(3)!= null){
            return new Contact(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    Uri.parse(cursor.getString(3)));
        }

        return new Contact(cursor.getLong(0),
                                cursor.getString(1),
                                cursor.getString(2));


    }


    private int getCount(){
        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
                allColumns, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void commitArrayChanges(ArrayList<Contact> changes){
        if(changes != getAllContacts()) {

            database.delete(MySQLiteHelper.TABLE_CONTACTS, null, null);

            for (Contact c : changes) {

                addContact(c);

            }
        }

    }


}
