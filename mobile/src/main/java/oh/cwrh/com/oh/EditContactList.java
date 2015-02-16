package oh.cwrh.com.oh;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import oh.cwrh.com.oh.adapters.RemoveContactAdapter;
import oh.cwrh.com.oh.database.DataSource;


public class EditContactList extends Activity {

    private DataSource dataSource;

    private RemoveContactAdapter removeContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contacts);
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(removeContactAdapter.isDataSetChanged()) {
            dataSource.commitArrayChanges(removeContactAdapter.getList());
            Toast.makeText(this,getResources().getString(R.string.saving_feedback),Toast.LENGTH_SHORT).show();
        }

        if (dataSource.isOpen()) {
            dataSource.close();
        }


    }

    @Override
    protected void onResume(){
        super.onResume();

        ListView list =  (ListView)findViewById(R.id.remove_list);

        dataSource = new DataSource(this);
        dataSource.open();

        removeContactAdapter = new RemoveContactAdapter(this, dataSource.getAllContacts());

        list.setAdapter(removeContactAdapter);
        removeContactAdapter.setIsInMovableMode(false);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                removeContactAdapter.setIsInMovableMode(true);
                invalidateOptionsMenu();
                return true;
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_mode_menu, menu);

        if(removeContactAdapter.isInMovableMode()){

            menu.findItem(R.id.done).setVisible(true);
        }else{
            menu.findItem(R.id.done).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.done) {
            removeContactAdapter.setIsInMovableMode(false);
            invalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
