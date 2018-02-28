package com.example.sushanth.drawcircle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import static com.example.sushanth.drawcircle.MainActivity.colorChoosen;
import static com.example.sushanth.drawcircle.MainActivity.modeChoosen;


/**
 * Created by sushanth on 2/25/2017.
 */

public class SimpleDrawing extends View implements View.OnTouchListener {


    static Paint blackThick, blueThick, greenThick, redThick ;

    static {
        blackThick = new Paint();
        blackThick.setColor(Color.BLACK);
        blackThick.setStyle(Paint.Style.STROKE);
        blackThick.setStrokeWidth(2.0f);


        blueThick = new Paint();
        blueThick.setColor(Color.BLUE);
        blueThick.setStyle(Paint.Style.STROKE);
        blueThick.setStrokeWidth(2.0f);

        greenThick = new Paint();
        greenThick.setColor(Color.GREEN);
        greenThick.setStyle(Paint.Style.STROKE);
        greenThick.setStrokeWidth(2.0f);

        redThick = new Paint();
        redThick.setColor(Color.RED);
        redThick.setStyle(Paint.Style.STROKE);
        redThick.setStrokeWidth(2.0f);

    }

    ArrayList<Circle> circles = new ArrayList<Circle>();
    ArrayList<Circle> circlesdelete = new ArrayList<Circle>();
    List<Integer> myList = new ArrayList<Integer>();
    public  boolean tempflag = false;
    public  boolean tempflag2 = false;
    private float speedCap = 8000;
    private float startX;
    private float startY;
    private float currentX;
    private float currentY;
    private float radius;
    private float deleteStartX;
    private float deleteStartY;
    private float deleteEndX;
    private float deleteEndY;
    private float moveStartX;
    private float moveStartY;
    private float moveEndX;
    private float moveEndY;
    private float deltax = 1;
    private float deltay=1;
    private MotionEvent lastEvent;

    VelocityTracker velocity;
    float xVelocity, yVelocity;
    int screenWidth;
    int screenHeight;
    int numberOfCircles = 0;
    public SimpleDrawing(Context context, AttributeSet xmlAttributes) {
        super(context, xmlAttributes);
        setOnTouchListener(this);
        screenWidth= context.getResources().getDisplayMetrics().widthPixels;
        screenHeight= context.getResources().getDisplayMetrics().heightPixels;
    }
    public boolean onTouch(View arg0, MotionEvent event) {
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        switch (actionCode)
        {
            case MotionEvent.ACTION_DOWN:
                if(modeChoosen == "draw")
                {
                    Log.i("dudh", "draw mode");
                    tempflag2 =true;
                    return handleActionDown(event);
                }
                else if(modeChoosen=="delete")
                {
                    deleteStartX = event.getX();
                    deleteStartY = event.getY();
                    invalidate();
                    return true;
                }
                else if(modeChoosen =="move"){
                    velocity = VelocityTracker.obtain();
                    velocity.addMovement(event);
                    return handleSwipeActionDown(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (modeChoosen == "draw")
                {
                    tempflag2 =true;
                    return handleActionMove(event);
                }
                else if(modeChoosen=="delete")
                {
                    invalidate();
                    return true;
                }
                else if(modeChoosen =="move"){
                    velocity.addMovement(event);
                    return handleSwipeActionMove(event);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (modeChoosen == "draw")
                {
                    tempflag2 =false;

                    return handleActionUp(event);
                }
                else if (modeChoosen == "delete")
                {

                    deleteEndX = event.getX();
                    deleteEndY = event.getY();
                    return deletecircles(event);
                }
                else if(modeChoosen =="move"){
                    velocity.computeCurrentVelocity(1000);
                    xVelocity = velocity.getXVelocity();
                    yVelocity = velocity.getYVelocity();

                    if(xVelocity > speedCap || xVelocity < -speedCap){
                        if (xVelocity > 0){xVelocity = speedCap;}
                        if (xVelocity < 0){xVelocity = -speedCap;}
                    }

                    if(yVelocity > speedCap || yVelocity < -speedCap){
                        if (yVelocity > 0){yVelocity = speedCap;}
                        if (yVelocity < 0){yVelocity = -speedCap;}
                    }


                    velocity.recycle();
                    velocity = null;
                    return handleSwipeActionUp(event);
                }
                break;
        }
        return false;
    }
    private boolean handleActionMove(MotionEvent event) {
        currentX = event.getX();
        currentY = event.getY();

        invalidate();

        return true;
    }
    private boolean handleActionDown(MotionEvent event) {
        startX = event.getX();
        startY = event.getY();
        numberOfCircles += 1;
        return true;
    }
    private boolean handleActionUp(MotionEvent event){
        currentX = event.getX();
        currentY = event.getY();
        invalidate();
        circles.add(new Circle(startX, startY, calcRadius(), selectColor(colorChoosen) ));
        radius = 0;
        return true;
    }

    private boolean handleSwipeActionDown(MotionEvent event){
        moveStartX = event.getX();
        moveStartY = event.getY();

        myList.clear();

        for (int i=0; i<circles.size(); i++) {
            Circle presentCircle = circles.get(i);
            float movex = presentCircle.startX;
            float movey = presentCircle.startY;
            float moveradius = presentCircle.radius;
            float distanceFromCircleDown = (float) (Math.sqrt(((moveStartX - movex) * (moveStartX - movex)) + ((moveStartY - movey) * (moveStartY - movey))));
            if(moveradius >= distanceFromCircleDown){
                System.out.println("the index value is" + i );
                myList.add(i);


            }
        }

        for (int i = 0; i<myList.size(); i++){
            Circle presentCircle = circles.get(myList.get(i));
            presentCircle.deltax =1;
            presentCircle.deltay =1;
        }
        return true;
    }

    private boolean handleSwipeActionMove(MotionEvent event){

        moveEndX = event.getX();
        moveEndY = event.getY();


        index =0;


        invalidate();
        return true;
    }

    private boolean handleSwipeActionUp(MotionEvent event){
        moveEndX = event.getX();
        moveEndY = event.getY();
        System.out.println("swipeupcalled");
        moveTheCircles();

        return true;
    }
    private boolean deletecircles(MotionEvent event)
    {
        float deletex;
        float deletey;
        float deleteradius;
        for (int i=0; i<circles.size(); i++) {
            Circle presentCircle = circles.get(i);
            deletex = presentCircle.startX;
            deletey = presentCircle.startY;
            deleteradius = presentCircle.radius;
            float distanceFromCircleDown = (float) (Math.sqrt(((deleteStartX - deletex) * (deleteStartX - deletex)) + ((deleteStartY - deletey) * (deleteStartY - deletey))));
            float distanceFromCircleUp = (float) (Math.sqrt(((deleteEndX - deletex) * (deleteEndX - deletex)) + ((deleteEndY - deletey) * (deleteEndY - deletey))));
            if ((deleteradius >= distanceFromCircleDown) && (deleteradius >= distanceFromCircleUp))  {
                circlesdelete.add(circles.get(i));
                invalidate();
                numberOfCircles -= 1;
            }
        }
        circles.removeAll(circlesdelete);
        invalidate();
        return true;
    }
    private int[] arrayToHoldCircles;
    private int index =0;

    private void moveTheCircles(){

        if (modeChoosen =="move") {
            System.out.println("MoveTheCircles is called");
            if (xVelocity != 0 || yVelocity != 0) {
                for (int i = 0; i < myList.size(); i++) {
                    Circle presentCircle = circles.get(myList.get(i));
                    changeOnCollison(presentCircle);
                    Log.i("rew", String.valueOf(xVelocity) + String.valueOf(yVelocity));
                    presentCircle.startX = (float) (presentCircle.startX + (presentCircle.deltax * 0.0022 * xVelocity));
                    presentCircle.startY = (float) (presentCircle.startY + (presentCircle.deltay * 0.0022 * yVelocity));
                    invalidate();
                    deltax = 1;
                    deltay = 1;

                }

                invalidate();
                if (xVelocity > 100 || xVelocity < -100 || yVelocity > 100 || yVelocity < -100) {
                    this.postDelayed(new Mover(), 1);
                } else {
                    xVelocity = 0;
                    yVelocity = 0;
                    myList.clear();
                }


            }
        }
    }
    private void changeOnCollison(Circle presentCircle){
        if (xIsOutOfBounds(presentCircle)) presentCircle.deltax = presentCircle.deltax * -1;
        if (yIsOutOfBounds(presentCircle)) presentCircle.deltay = presentCircle.deltay * -1;
    }
    private boolean xIsOutOfBounds(Circle presentCircle) {
        float x = presentCircle.startX;
        if (x - presentCircle.radius <0) return true;
        if (x + presentCircle.radius > screenWidth) return true;
        return false;
    }

    private boolean yIsOutOfBounds(Circle presentCircle) {
        float y = presentCircle.startY;
        if (y - presentCircle.radius <0) return true;
        if (y + presentCircle.radius + 190> screenHeight) return true;
        return false;
    }
    class Mover implements Runnable{
        @Override
        public void run() {
            xVelocity = (float)(xVelocity * 0.99);
            yVelocity = (float)(yVelocity * 0.99);
            System.out.println("mover class" );
            moveTheCircles();
        }
    }



    public static float getMinValue(float[] array) {
        float minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }
    private Paint selectColor( String colorChoosen ){
        Paint temp = blackThick;
        switch (colorChoosen){
            case "redThick":
                temp = redThick;
                break;
            case "greenThick":
                temp =  greenThick;
                break;
            case "blueThick":
                temp =  blueThick;
                break;
            case "blackThick":
                temp =  blackThick;
                break;
        }
        return temp;
    }
    private float calcRadius(){
        float leftDistance = startX;
        float rightDistance = screenWidth - startX;
        float topDistance = startY;
        float bottomDistance = screenHeight - startY - 190;
        float arrayOfAllValues[] ={leftDistance,rightDistance,topDistance, bottomDistance};
        radius = (float) (Math.sqrt(((currentX - startX) *(currentX - startX)) + ((currentY - startY) *(currentY - startY))));
         if((radius<leftDistance) && (radius<rightDistance) && (radius<topDistance) && (radius<bottomDistance)){
            return radius;
        }else{
            return radius = getMinValue(arrayOfAllValues);
        }
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GRAY);

       // canvas.drawCircle(startX, startY, calcRadius(),blackThick);
        for (Circle each : circles) {
            each.drawOn(canvas);
        }
        if (modeChoosen == "draw" && tempflag2 ){
            canvas.drawCircle(startX, startY, calcRadius(), selectColor(colorChoosen));
        }



    }


}

