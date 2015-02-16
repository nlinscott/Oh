package oh.cwrh.com.oh.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import oh.cwrh.com.oh.R;
import oh.cwrh.com.oh.SendSMS;
import oh.cwrh.com.oh.database.Contact;
import oh.cwrh.com.oh.database.DataSource;
import oh.cwrh.com.oh.tools.OnSwipeListener;

/**
 * Created by Nic on 2/11/2015.
 */
public class ContactItemAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Contact> list;
    private static final long WAIT_MS = 2500;
    public ContactItemAdapter(Context c){
        super();
        context = c;
        DataSource ds = new DataSource(c);
        ds.open();
        list = ds.getAllContacts();
        ds.close();
    }

    @Override
    public void notifyDataSetChanged(){
        DataSource ds = new DataSource(context);
        ds.open();
        list = ds.getAllContacts();
        ds.close();
        super.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position){
        return list.get(position).getId();
    }

    @Override

    public Contact getItem(int position){
        return list.get(position);
    }
    @Override
    public int getCount(){
        return list.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.contact_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.name);
            viewHolder.phone = (TextView)convertView.findViewById(R.id.phone_number);
            viewHolder.omw = (LinearLayout)convertView.findViewById(R.id.omw);
            viewHolder.here = (LinearLayout)convertView.findViewById(R.id.here);
            viewHolder.container = convertView.findViewById(R.id.data);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        final Contact contact = list.get(position);
        viewHolder.name.setText(contact.getName());
        viewHolder.phone.setText( contact.getPhone() );

        final Intent intent = new Intent(context, SendSMS.class);

        intent.putExtra(SendSMS.NAME, contact.getName());
        intent.putExtra(SendSMS.PHONE_NUMBER, contact.getPhone());

        //on swipe, check to make sure that they are all gone before creating a new handler.
        //this prevents people from getting text-bombed.. for WAIT_MS /1000 seconds
        viewHolder.container.setOnTouchListener( new OnSwipeListener(context){
            @Override
            public void onSwipeLeft(){
                if(viewHolder.here.getVisibility() == View.GONE
                        && viewHolder.omw.getVisibility() == View.GONE) {

                    viewHolder.here.setVisibility(View.VISIBLE);
                    viewHolder.omw.setVisibility(View.GONE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewHolder.here.setVisibility(View.GONE);
                            intent.setAction(SendSMS.ACTION_HERE);
                            context.startService(intent);

                        }
                    }, WAIT_MS);
                }
            }
            @Override
            public void onSwipeRight() {

                if(viewHolder.here.getVisibility() == View.GONE
                        && viewHolder.omw.getVisibility() == View.GONE) {

                    viewHolder.omw.setVisibility(View.VISIBLE);
                    viewHolder.here.setVisibility(View.GONE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewHolder.omw.setVisibility(View.GONE);
                            intent.setAction(SendSMS.ACTION_LEAVING);
                            context.startService(intent);

                        }
                    }, WAIT_MS);

                }
            }
        });


        return convertView;
    }



    static class ViewHolder{
        public TextView name, phone;
        public LinearLayout omw, here;
        public View container;
    }
}
