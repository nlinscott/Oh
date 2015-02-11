package oh.cwrh.com.oh;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import oh.cwrh.com.oh.database.Contact;
import oh.cwrh.com.oh.database.DataSource;


public class RemoveFavorites extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_favorites);

    }


    @Override
    protected void onPause(){
        super.onPause();
        finish();

    }
    @Override
    protected void onResume(){
        super.onResume();
        LinearLayout layout = (LinearLayout)findViewById(R.id.listView);
        if(layout.getChildCount() > 0){
            layout.removeAllViews();
        }
        initialize();
    }


    public void initialize(){
        final DataSource ds = new DataSource(this);
        ds.open();
        final LinearLayout layout = (LinearLayout)findViewById(R.id.listView);
        for(final Contact c : ds.getAllContacts()){
            final View item = LayoutInflater.from(this).inflate(R.layout.remove_list_item, null);
            layout.addView(item);
            TextView name = (TextView)item.findViewById(R.id.remove_name);
            TextView phone = (TextView)item.findViewById(R.id.remove_phone);
            ImageButton remove = (ImageButton)item.findViewById(R.id.remove_btn);

            name.setText(c.getName());
            phone.setText(c.getPhone());
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ds.deleteContact(c);
                    layout.removeView(item);
                }
            });
        }
    }

}
