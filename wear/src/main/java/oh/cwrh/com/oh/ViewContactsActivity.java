package oh.cwrh.com.oh;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import oh.cwrh.com.oh.database.Contact;
import oh.cwrh.com.oh.database.DataSource;
import oh.cwrh.com.oh.fragment.ContactInfoFragment;
import oh.cwrh.com.oh.tools.ZoomTransformer;

public class ViewContactsActivity extends FragmentActivity{

    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

            initialize();

            }
        });
    }


    private void initialize(){
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        pager.setPageTransformer(true, new ZoomTransformer());

        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                return gestureDetector.onTouchEvent(ev);
            }
        });

        DataSource ds = DataSource.getInstance(getApplicationContext());
        ds.open();

        ArrayList<Contact> list = ds.getAllContacts();
        if(list.size() > 0) {
            pager.setAdapter(new ContactSliderAdapter(getSupportFragmentManager(), list));
        }else{
            /*
            View empty = findViewById(R.id.no_contacts_view);
            empty.setVisibility(View.VISIBLE);
            pager.setVisibility(View.GONE);
            */
        }
        ds.close();

        final DismissOverlayView mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.gesture_text);
        mDismissOverlay.showIntroIfNecessary();

        // Configure a gesture detector
        gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent ev) {
                mDismissOverlay.show();
            }
        });
    }

    private class ContactSliderAdapter extends FragmentStatePagerAdapter {

        private ArrayList<Contact> contactList;

        public ContactSliderAdapter(FragmentManager fm, ArrayList<Contact> list) {
            super(fm);
            contactList = list;
        }


        @Override
        public Fragment getItem(int position) {
            ContactInfoFragment frag = new ContactInfoFragment();
            Contact c = contactList.get(position);
            frag.setContact(c);
            return frag;
        }

        @Override
        public int getCount() {
            return contactList.size();
        }

    }



}


