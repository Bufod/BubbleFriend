package com.example.bubblefriend;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Bubble {
    float x, y, velocityX, velocityY;
    int radius;
    private Paint p;

    Bubble(float x, float y, float velocityX, float velocityY, int radius) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.radius = radius;
        p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);
    }

    synchronized void setX(float x) {
        this.x = x;
    }

    synchronized void setY(float y) {
        this.y = y;
    }

    // цвет шаров
    void setColor(int alpha, int red, int green, int blue) {
        p.setColor(Color.argb(alpha, red, green, blue));
    }

    // возвращает радиус
    int getRadius() {
        return radius;
    }

    // разворот по Х
    synchronized void reverseX() {
        this.velocityX = -this.velocityX;
    }

    // разворот по У
    synchronized void reverseY() {
        this.velocityY = -this.velocityY;
    }

    // отрисовка на холсте
    void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, p);
    }

    // обновление координат с учетом ускорения
    synchronized void update(int ms) {

        x = x + velocityX * (float) (ms / 1000.0);
        y = y + velocityY * (float) (ms / 1000.0);

    }
}
