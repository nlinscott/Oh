package oh.cwrh.com.oh.custom;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import oh.cwrh.com.oh.R;


/**
 * Created by Nic on 2/6/2015.
 */
public class SwipeView extends RelativeLayout {
    Context context;
    FrameLayout center;
    private String name;
    private String number;
    private Vibrate vibrator;

    public SwipeView(Context c, AttributeSet attrs){
        super(c, attrs);
        context = c;
        vibrator = new Vibrate();

    }

    public SwipeView(Context c){
        super(c);
        context = c;
        vibrator = new Vibrate();
    }

    public void setNameAndNumber(String fullname, String num){

        //get inflater service
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //add the background layout and retrieve that view
        View background = inflater.inflate(R.layout.canvas_background_layout, this, true);

        //find that layout within the background
        center = (FrameLayout)background.findViewById(R.id.center);
        View data =  inflater.inflate(R.layout.layout_contact_info,center,false);

        //get and set text
        TextView txtName = (TextView)data.findViewById(R.id.name);
        TextView txtNumber = (TextView)data.findViewById(R.id.number);
        name = fullname;
        number = num;
        txtName.setText(name);
        txtNumber.setText(number);
        center.addView( data );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       // Debug.log("touched");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
               break;
            case MotionEvent.ACTION_MOVE:
                setPosition( event.getX() );
                break;
            case MotionEvent.ACTION_UP:
                checkPosition( event.getX() );
                break;
            default:
                return false;
        }

        return true;

    }

    public void setPosition(float x){
        float middle = x-(center.getWidth()/2);
        if(middle > 0 && middle + center.getWidth() < this.getWidth()) {
            center.setX(x - (center.getWidth() / 2));
        }
    }

    public void checkPosition(float x){
        float middle = x-(center.getWidth()/2);

        //point on Up must be between 0 and the width of the center FrameLayout
       if(x >= 0 && x <= center.getWidth() && center.getX() >= 0 && center.getX() <= center.getWidth()) {

           vibrator.vibrate();
           /*
           Intent intent = new Intent(context, SendSMS.class);

           intent.putExtra(SendSMS.NAME, name);
           intent.putExtra(SendSMS.PHONE_NUMBER, number);
           intent.setAction(SendSMS.ACTION_LEAVING);
           context.startService(intent);
            */
           }else if(center.getX() >= this.getWidth()-center.getWidth()*1.3 && middle <= this.getWidth() && middle >= this.getWidth() - center.getWidth() ){
           //the middle of the center FrameLayout must be less than the width and further
           // in distance from the edge by the width of the center
           //also ensure that the center view is actually there so they cant click on the edge to send

            vibrator.vibrate();

           /*
            Intent intent = new Intent(context, SendSMS.class);

            intent.putExtra(SendSMS.NAME, name);
            intent.putExtra(SendSMS.PHONE_NUMBER, number);
            intent.setAction(SendSMS.ACTION_HERE);
            context.startService(intent);
            */
        }



        ObjectAnimator.ofFloat(center,"x",
                (this.getWidth()/2) - (center.getWidth()/2)).start();

    }

    private class Vibrate{

        private Vibrator v;
        private final int DURATION = 200;
        public Vibrate(){v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);}

        public void vibrate(){
            v.vibrate(DURATION);
        }

    }


}
