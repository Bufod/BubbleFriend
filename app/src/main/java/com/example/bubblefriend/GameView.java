package com.example.bubblefriend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class GameView extends View {
    private final int timerInterval = 50;
    private List<Bubble> bubbles;
    Coordinator coordinator1, coordinator2, coordinator3, coordinator4;
    boolean color, pause, move;
    int maxRadius = 12, minRadius = 8;
    TextView tw;
    Context context;
    GameView(Context context) {
        super(context);
        this.context = context;
        bubbles = new ArrayList<>(); // массив шаров

        // 4 потока обработки игрового поля
        coordinator1 = new Coordinator();
        coordinator2 = new Coordinator();
        coordinator3 = new Coordinator();
        coordinator4 = new Coordinator();

        tw = (TextView) findViewById(R.id.tw);
        // таймер обновления
        Timer t = new Timer();
        t.start();
    }

    // функция создания шариков
    public void creationOfBubble(int amount) {
        if (coordinator1.t.isAlive() &&
                coordinator2.t.isAlive() &&
                coordinator3.t.isAlive() &&
                coordinator4.t.isAlive()) {
            try {
                coordinator1.t.join();
                coordinator2.t.join();
                coordinator3.t.join();
                coordinator4.t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < amount && bubbles.size() <= 2000; i++) {
            int radius = minRadius + (int) (Math.random() * (maxRadius + 1));
            int x = radius + (int) (Math.random() * (getWidth() - radius + 1));
            int y = radius + (int) (Math.random() * (getHeight() - radius + 1));
            Bubble bubble = new Bubble(x, y, 50 * ((int) (Math.random() * 2) == 1 ? 1 : -1),
                    50 * ((int) (Math.random() * 2) == 1 ? 1 : -1), radius);
            bubbles.add(bubble); //заполенение массива
        }
    }

    public void delOfBubble(int amount) {
        if (coordinator1.t.isAlive() &&
                coordinator2.t.isAlive() &&
                coordinator3.t.isAlive() &&
                coordinator4.t.isAlive()) {
            try {
                coordinator1.t.join();
                coordinator2.t.join();
                coordinator3.t.join();
                coordinator4.t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = bubbles.size() - 1, j = 0; j < amount && bubbles.size() > 0; j++, i--)
            bubbles.remove(i);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bubbles.isEmpty()) {
            creationOfBubble(1000);
        }
        canvas.drawARGB(255, 0, 0, 0);
        for (Bubble bubble : bubbles) {
            bubble.draw(canvas);
        }
    }

    // обновляет состояния объектов игрового поля
    protected void update() {

        if (!pause) {
            for (Bubble bubble : bubbles) {
                float bufferZoneX = maxRadius,
                        bufferZoneY = maxRadius,
                        posXCur = bubble.x, posYCur = bubble.y,
                        frameWidth = getWidth(), frameHeight = getHeight();

                // набор шаров для 1-го потока
                if (posXCur <= frameWidth / 2 + bufferZoneX && posYCur <= frameHeight / 2 + bufferZoneY) {
                    if (color)
                        bubble.setColor(255, 221, 255, 0);
                    else
                        bubble.setColor(255, 255, 255, 255);
                    coordinator1.addBubbles(bubble);
                }
                // набор шаров для 2-го потока
                if (posXCur >= frameWidth - frameWidth / 2.0 - bufferZoneX &&
                        posYCur <= frameHeight / 2 + bufferZoneY) {
                    if (color)
                        bubble.setColor(255, 255, 43, 0);
                    else
                        bubble.setColor(255, 255, 255, 255);
                    coordinator2.addBubbles(bubble);
                }
                // набор шаров для 3-го потока
                if (posXCur <= frameWidth / 2 + bufferZoneX &&
                        posYCur >= frameHeight - frameHeight / 2.0 - bufferZoneY) {
                    if (color)
                        bubble.setColor(255, 0, 255, 208);
                    else
                        bubble.setColor(255, 255, 255, 255);
                    coordinator3.addBubbles(bubble);
                }
                // набор шаров для 4-го потока
                if (posXCur >= frameWidth - frameWidth / 2.0 - bufferZoneX &&
                        posYCur >= frameHeight - frameHeight / 2.0 - bufferZoneY) {
                    if (color)
                        bubble.setColor(255, 255, 0, 242);
                    else
                        bubble.setColor(255, 255, 255, 255);
                    coordinator4.addBubbles(bubble);
                }

                // обновление перемещения шара
                bubble.update(timerInterval);
            }

            if (!bubbles.isEmpty()) {
                coordinator1.t.start();
                coordinator2.t.start();
                coordinator3.t.start();
                coordinator4.t.start();
                try {
                    for (Bubble bubble : bubbles) {
                        float posXCur = bubble.x, posYCur = bubble.y,
                                frameWidth = getWidth(), frameHeight = getHeight();
                        // обнаружение столконвений с границами объектов
                        boolean flagTopBorder = posYCur < bubble.getRadius(),
                                flagBottomBorder = posYCur > frameHeight - bubble.getRadius(),
                                flagLeftBorder = posXCur < bubble.getRadius(),
                                flagRightBorder = posXCur > frameWidth - bubble.getRadius();
                        if (flagTopBorder || flagBottomBorder) {
                            bubble.reverseY();
                            if (flagTopBorder)
                                bubble.setY(bubble.getRadius());
                            if (flagBottomBorder)
                                bubble.setY(getHeight() - bubble.getRadius());
                        }
                        if (flagLeftBorder || flagRightBorder) {
                            bubble.reverseX();
                            if (flagLeftBorder)
                                bubble.setX(bubble.getRadius());
                            if (flagRightBorder)
                                bubble.setX(getWidth() - bubble.getRadius());
                        }
                    }
                    coordinator1.t.join();
                    coordinator2.t.join();
                    coordinator3.t.join();
                    coordinator4.t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            invalidate();
        }
    }

    class Timer extends CountDownTimer {
        public Timer() {
            // первый параметр продолжительность в милисекундах, второй параметр - частота
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
                update();
        }

        @Override
        public void onFinish() {
        }
    }

    float xCursor, tmpXCoursor;
    Toast toast;
    // подсветка потоков по касанию
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (!move)
                    color = !color;
                else {
                    pause = true;
                    tmpXCoursor = event.getX();
                    int amount = (int) (Math.abs(tmpXCoursor - xCursor) / 10),
                        n = bubbles.size();
                    if (tmpXCoursor > xCursor && n <= 2000) {
                        creationOfBubble(amount);
                    } else if (tmpXCoursor < xCursor && n > 1) {
                        delOfBubble(amount);
                    }
                    pause = false;
                    move = false;
                    if (toast != null)
                        toast.cancel();
                    toast = Toast.makeText(getContext(),
                            Integer.toString(bubbles.size()), Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                move = true;
                break;
            case MotionEvent.ACTION_DOWN:
                xCursor = event.getX();
        }
        return true;
    }

}
