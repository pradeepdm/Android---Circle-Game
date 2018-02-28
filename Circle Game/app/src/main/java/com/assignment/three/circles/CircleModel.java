package com.assignment.three.circles;

/**
 * Created by Pradeep Mallikarjun.
 * RED ID 822032361
 */

// Class to handle basic circle details
public class CircleModel {

    private float centerX;
    private float centerY;
    private double currentRadius;

    public void setCurrentRadius(double currentRadius) {
        this.currentRadius = currentRadius;
    }

    public CircleMovementDirectionModel getCircleMovementDirectionModel() {
        return circleMovementDirectionModel;
    }

    private CircleMovementDirectionModel circleMovementDirectionModel;

    public CircleModel(float touchDownX, float touchDownY) {
        circleMovementDirectionModel = new CircleMovementDirectionModel( 1, 1);
        this.centerX = touchDownX;
        this.centerY = touchDownY;
    }

    public double getCurrentRadius() {
        return currentRadius;
    }

    public void setCurrentRadius(float currentRadius) {
        this.currentRadius = currentRadius;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    // Class to handle Circle Velocity data
    public class CircleMovementDirectionModel {

        private float deltaX;
        private float deltaY;

        public CircleMovementDirectionModel(float deltaX, float deltaY) {
            this.deltaX = deltaX;
            this.deltaY = deltaY;
        }

        public float getDeltaX() {
            return deltaX;
        }

        public void setDeltaX(float deltaX) {
            this.deltaX = deltaX;
        }

        public float getDeltaY() {
            return deltaY;
        }

        public void setDeltaY(float deltaY) {
            this.deltaY = deltaY;
        }
    }
}
