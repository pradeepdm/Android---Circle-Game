package com.assignment.three.circles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Pradeep Mallikarjun.
 * RED ID 822032361
 */

public class CircleView extends View implements View.OnTouchListener {


    private static final int CIRCLE_COUNT_LIMIT = 15;
    private static float screenWidth;
    private static float screenHeight;
    private static final Paint redFill;

    private float currentCircleXVelocity, currentCircleYVelocity;
    private VelocityTracker currentCircleVelocityTracker;
    private List<CircleModel> circles = new ArrayList<>();
    private CircleModel currentCircle;
    private StartCircleAnimation circleGrowTask;
    private boolean isMoveMode;

    static {
        redFill = new Paint();
        redFill.setColor(Color.RED);
        redFill.setStyle(Paint.Style.STROKE);
        redFill.setStrokeWidth(5f);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    private static float getDistanceBetweenPoints(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                return actionDown(motionEvent);
            case MotionEvent.ACTION_MOVE:
                return actionMove(motionEvent);
            case MotionEvent.ACTION_UP:
                return actionUp();
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP: {
                return false;
            }
        }
        return false;
    }

    // Identifying the current circle that user wants to move.
    private void identifyCircleToMove(float touchDownX, float touchDownY) {
        for (int i = 0; i < circles.size(); i++) {

            CircleModel circleToCheck = circles.get(i);

            if (circleToCheck.getCurrentRadius() >= getDistanceBetweenPoints(
                    circleToCheck.getCenterX(),
                    circleToCheck.getCenterY(),
                    touchDownX,
                    touchDownY)
                    ) {
                currentCircle = circleToCheck;
                break;
            }
            currentCircle.getCircleMovementDirectionModel().setDeltaX(1);
            currentCircle.getCircleMovementDirectionModel().setDeltaY(1);
        }
    }

    private boolean actionUp() {
        if (!isMoveMode) {
            circleGrowTask.cancel();
        } else {
            initiateCircleMovement();
        }
        return true;

    }

    private void initiateCircleMovement() {
        currentCircleVelocityTracker.computeCurrentVelocity(1000);
        currentCircleXVelocity = currentCircleVelocityTracker.getXVelocity();
        currentCircleYVelocity = currentCircleVelocityTracker.getYVelocity();
        moveCircle();
        velocityMax();
        currentCircleVelocityTracker.recycle();
        currentCircleVelocityTracker = null;
        invalidate();
    }

    private void moveCircle() {

        if (currentCircleXVelocity != 0 || currentCircleYVelocity != 0) {

            //Checking for collision with the boundaries
            handleCircleCollision(currentCircle);
            currentCircle.setCenterX((float) (currentCircle.getCenterX() +
                    (0.02 * currentCircleXVelocity * currentCircle.getCircleMovementDirectionModel().getDeltaX())));
            currentCircle.setCenterY((float) (currentCircle.getCenterY() +
                    (0.02 * currentCircleYVelocity * currentCircle.getCircleMovementDirectionModel().getDeltaY())));

            if (currentCircleXVelocity == 0 || currentCircleYVelocity == 0) {
                if (currentCircle.getCircleMovementDirectionModel().getDeltaX() == -1) {
                    currentCircle.getCircleMovementDirectionModel().setDeltaX(1);
                }
                if (currentCircle.getCircleMovementDirectionModel().getDeltaY() == -1) {
                    currentCircle.getCircleMovementDirectionModel().setDeltaY(1);
                }
            }
            if (!(currentCircleXVelocity > 30 ||
                    currentCircleXVelocity < -30 ||
                    currentCircleYVelocity > 30 ||
                    currentCircleYVelocity < -30)
                    ) {
                currentCircleXVelocity = 0;
                currentCircleYVelocity = 0;
            } else {
                this.postDelayed(new Mover(), 1);
            }
        }

    }

    private boolean actionMove(MotionEvent motionEvent) {
        if (isMoveMode) {
            currentCircleVelocityTracker.addMovement(motionEvent);
            return true;

        }
        return false;
    }

    private boolean actionDown(MotionEvent motionEvent) {

        float touchDownX = motionEvent.getX();
        float touchDownY = motionEvent.getY();

        isMoveMode = checkForMoveAction(touchDownX, touchDownY);
        if (isMoveMode) {

            currentCircleVelocityTracker = VelocityTracker.obtain();
            currentCircleVelocityTracker.addMovement(motionEvent);
            identifyCircleToMove(touchDownX, touchDownY);

        } else {

            if(circles.size() < CIRCLE_COUNT_LIMIT) {

                currentCircle = new CircleModel(touchDownX, touchDownY);
                circles.add(currentCircle);
                currentCircle.setCurrentRadius(10);
                circleGrowTask = new StartCircleAnimation();
                Timer timer = new Timer(true);
                timer.scheduleAtFixedRate(circleGrowTask, 0, 50);

            } else {

                Toast.makeText(this.getContext(), R.string.circle_limit, Toast.LENGTH_SHORT).show();
            }

        }

        return true;
    }

    private boolean checkForMoveAction(float moveDownX, float moveDownY) {

        isMoveMode = false;
        for (CircleModel each : circles) {
            float centerToTouchDownDistance = getDistanceBetweenPoints(
                    each.getCenterX(), each.getCenterY(),
                    moveDownX, moveDownY);

            if (centerToTouchDownDistance < each.getCurrentRadius()) {
                isMoveMode = true;
                break;
            }
        }
        return isMoveMode;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        screenWidth = canvas.getWidth();
        screenHeight = canvas.getHeight();
        super.onDraw(canvas);

        for (CircleModel each : circles) {
            canvas.drawCircle(each.getCenterX(), each.getCenterY(), (float) each.getCurrentRadius(), redFill);
        }

        if (currentCircle != null) {
            canvas.drawCircle(currentCircle.getCenterX(), currentCircle.getCenterY(), (float) currentCircle.getCurrentRadius(), redFill);
        }
        invalidate();
    }

    private boolean isXOutOfBound(CircleModel circle) {
        return circle.getCenterX() - circle.getCurrentRadius() < 0 ||
                (circle.getCenterX() + circle.getCurrentRadius() > screenWidth);
    }

    private boolean isYOutOfBound(CircleModel circle) {
        return circle.getCenterY() - circle.getCurrentRadius() < 0 ||
                circle.getCenterY() + circle.getCurrentRadius() > screenHeight;
    }

    private void handleCircleCollision(CircleModel c) {
        if (isXOutOfBound(c)) {
            c.getCircleMovementDirectionModel().setDeltaX(c.getCircleMovementDirectionModel().getDeltaX() * -1);
        }
        if (isYOutOfBound(c)) {
            c.getCircleMovementDirectionModel().setDeltaY(c.getCircleMovementDirectionModel().getDeltaY() * -1);
        }
    }

    private void velocityMax() {
        if (currentCircleXVelocity > 8000) {
            currentCircleXVelocity = 8000;
        } else if (currentCircleXVelocity < -8000) {
            currentCircleXVelocity = -8000;
        }

        if (currentCircleYVelocity > 8000) {
            currentCircleYVelocity = 8000;
        } else if (currentCircleYVelocity < -8000) {
            currentCircleYVelocity = -8000;
        }
    }

    // Post delayed approach to move the circles.
    class Mover implements Runnable {
        public void run() {
            currentCircleXVelocity = (float) (currentCircleXVelocity * 0.99);
            currentCircleYVelocity = (float) (currentCircleYVelocity * 0.99);
            moveCircle();
        }
    }

    class StartCircleAnimation extends TimerTask {
        @Override
        public void run() {
            int baseRadius = 10;
            currentCircle.setCurrentRadius(currentCircle.getCurrentRadius() + baseRadius);
        }
    }
}
