package oh.cwrh.com.oh.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import oh.cwrh.com.oh.R;
import oh.cwrh.com.oh.adapters.ContactItemAdapter;

/**
 * Created by Nic on 2/11/2015.
 */
public class ContactsListFragment extends Fragment {


    public ContactsListFragment(){}
    private ContactItemAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);


        View rootView = inflater.inflate(R.layout.list_fragment, container, false);

        adapter = new ContactItemAdapter(getActivity());
        //the rootView is the listView because it has no children
        ListView list = (ListView)rootView;
        list.setAdapter( adapter );

        return rootView;
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }





}
