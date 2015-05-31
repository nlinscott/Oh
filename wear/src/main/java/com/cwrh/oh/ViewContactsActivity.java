package com.cwrh.oh;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.WatchViewStub;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.cwrh.oh.database.Contact;
import com.cwrh.oh.database.DataSource;
import com.cwrh.oh.fragment.ContactInfoFragment;
import com.cwrh.oh.interfaces.ExitButtonClickCallback;
import com.cwrh.oh.tools.Debug;
import com.cwrh.oh.tools.ExitOverlayView;
import com.cwrh.oh.tools.ZoomTransformer;

import java.util.ArrayList;


public class ViewContactsActivity extends FragmentActivity{

    private GestureDetector gestureDetector;

    private ExitOverlayView exitOverlayView;

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

        View.OnTouchListener touchListner = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setOnTouchListener(touchListner);
        pager.setOffscreenPageLimit(2);
        pager.setPageTransformer(true, new ZoomTransformer());

        DataSource ds = DataSource.getInstance(getApplicationContext());
        ds.open();
        ArrayList<Contact> list = ds.getAllContacts();
        ds.close();

        View empty = findViewById(R.id.no_contacts_view);
        empty.setOnTouchListener(touchListner);
        if(list.size() > 0) {
            pager.setAdapter(new ContactSliderAdapter(getSupportFragmentManager(), list));
            empty.setVisibility(View.GONE);
        }else{

            empty.setVisibility(View.VISIBLE);
            pager.setVisibility(View.GONE);

        }

        exitOverlayView = (ExitOverlayView) findViewById(R.id.dismiss_overlay);
        exitOverlayView.setText(R.string.gesture_text);
        exitOverlayView.showOnFirstRun();
        exitOverlayView.setButtonClickCallback(new ExitButtonClickCallback() {
            @Override
            public void buttonClicked() {
                finish();
            }
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent ev) {
                Debug.log("long press");
                exitOverlayView.show();
                super.onLongPress(ev);
            }

            /**
             * Debugging only
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                exitOverlayView.debugMode();
                Debug.log("double tap");
                return super.onDoubleTap(e);
            }
            */
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


