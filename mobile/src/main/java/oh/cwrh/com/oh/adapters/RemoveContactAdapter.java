package oh.cwrh.com.oh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import oh.cwrh.com.oh.R;
import oh.cwrh.com.oh.database.Contact;

/**
 * Created by Nic on 2/14/2015.
 */
public class RemoveContactAdapter extends BaseAdapter {


    private boolean dataSetChanged = false;

    private boolean isInMovableMode = false;

    private ArrayList<Contact> list;

    private Context context;

    public RemoveContactAdapter(Context c, ArrayList<Contact> items){
        context = c;
        list = items;
    }

    public ArrayList<Contact> getList(){
        return list;
    }

    public boolean isDataSetChanged() {
        return dataSetChanged;
    }

    //TODO: test for IndexOutOfBounds exceptions
    private void moveItemUpOne(int currentPosition){
        if(currentPosition > 0 && getCount() > 0) {

            //need to save this, we will overWrite list.get(cp-1) and need to assign it later
            Contact temp = list.get(currentPosition-1);

            //current
            Contact current = list.get(currentPosition);

            //move the current up one
            updateItem(current,currentPosition-1);
            //set the one above to be one lower
            updateItem(temp, currentPosition);
            //update UI
            notifyDataSetChanged();
            dataSetChanged = true;
        }
    }

    //TODO: test for IndexOutOfBounds exceptions
    private void moveItemDownOne(int currentPosition){
        if(currentPosition < getCount()-1 && getCount() > 0) {

            //need to save this, we will overWrite list.get(cp+1) and need to assign it later
            Contact temp = list.get(currentPosition+1);

            //current
            Contact current = list.get(currentPosition);

            //move the current up one
            updateItem(current,currentPosition+1);
            //set the one above to be one lower
            updateItem(temp, currentPosition);
            //update UI
            notifyDataSetChanged();
            dataSetChanged = true;
        }
    }

    private void updateItem(Contact c, int position){
        list.remove(position);
        list.add(position, c);
    }

    public void setIsInMovableMode(boolean itemsAreMovable){
        isInMovableMode = itemsAreMovable;
        notifyDataSetChanged();
    }

    public boolean isInMovableMode(){
        return isInMovableMode;
    }
    @Override
    public Contact getItem(int position){return list.get(position);}

    @Override
    public long getItemId(int position){
        return list.get(position).getId();
    }

    @Override
    public int getCount(){
        return list.size();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if(convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.edit_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.edit_name);
            viewHolder.phone = (TextView)convertView.findViewById(R.id.edit_phone);
            viewHolder.remove = (ImageButton)convertView.findViewById(R.id.remove_btn);

            viewHolder.up = (ImageButton)convertView.findViewById(R.id.move_up_one);
            viewHolder.down = (ImageButton)convertView.findViewById(R.id.move_down_one);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        final Contact contact = list.get(position);

        viewHolder.name.setText( contact.getName() );
        viewHolder.phone.setText( contact.getPhone() );

        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
                dataSetChanged = true;
            }
        });


        if(isInMovableMode) {

            viewHolder.up.setVisibility(View.VISIBLE);
            viewHolder.down.setVisibility(View.VISIBLE);

            viewHolder.up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveItemUpOne(position);
                }
            });

            viewHolder.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveItemDownOne(position);
                }
            });

        }else{
            viewHolder.up.setVisibility(View.GONE);
            viewHolder.down.setVisibility(View.GONE);
        }




        return convertView;
    }



    static class ViewHolder{
        public TextView name, phone;
        public ImageButton remove, up, down;
    }

}
