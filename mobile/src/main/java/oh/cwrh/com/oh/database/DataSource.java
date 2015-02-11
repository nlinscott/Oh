package oh.cwrh.com.oh.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Nic on 1/31/2015.
 */
public class DataSource {


    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_PHONE };
    private boolean isOpen;

    public DataSource(Context context) {
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

    /**
     * This creates a note, then returns the newly created note in order to update
     * the UI.
     *
     */
    public Contact addContact(String name, String phone){
        if(name.length() > 10) {
            name = name.substring(0, 10) + "...";
        }

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        values.put(MySQLiteHelper.COLUMN_PHONE, phone);
        long insertId = database.insert(MySQLiteHelper.TABLE_CONTACTS, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId,null,
                null, null, null);
        cursor.moveToFirst();
        Contact c = cursorToContact(cursor);
        cursor.close();
        return c;
    }


    public void deleteContact(Contact c) {
        long id = c.getId();
        database.delete(MySQLiteHelper.TABLE_CONTACTS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
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
        Contact c = new Contact(cursor.getLong(0),
                                cursor.getString(1),
                                cursor.getString(2));

        return c;
    }

}
