package com.example.bubblefriend;

import java.util.ArrayList;
import java.util.List;

public class Coordinator implements Runnable {
    public Thread t;
    private List<Bubble> bubbles;
    private boolean start = true;

    Coordinator() {
        bubbles = new ArrayList<>();
        t = new Thread(this, "CoordinatorThread");
    }

    // вызывается при старте потока
    public void run() {
        while (start) {
            intersect();
            clearBubbles();
        }
    }

    public void start(){
        this.start = true;
        t.start();
    }
    public void stop(){
        this.start = false;
    }
    // добавление шаров
    public void addBubbles(Bubble bubble) {
        bubbles.add(bubble);
    }

    // очистка массива шаров
    public void clearBubbles() {
        this.bubbles.clear();
    }

    // проверяет столкновения между шарами
    private void intersect() {
        int n = bubbles.size();
        Bubble b1, b2;
        for (int i = 0, j = 1; i < n - 1; j++) {
            b1 = bubbles.get(i);
            b2 = bubbles.get(j);
            if (b1 == null || b2 == null )
                return;
            float dist = (float) (calcDist(b1, b2));
            if (dist < b1.radius + b2.radius) {
                correctionTrajectory(b1, b2, dist);
            }
            if (j % (n - 1) == 0) {
                i++;
                j = i;
            }
        }

    }

    // корректировка траектории
    private void correctionTrajectory(Bubble b1, Bubble b2, float dist) {
        float x = (b2.radius + b1.radius) - dist,
                cosa = (Math.abs(b1.x - b2.x) / dist),
                sina = (Math.abs(b1.y - b2.y) / dist),
                offsetX = x * cosa / 2,
                offsetY = x * sina / 2;
        if (b1.x < b2.x) {
            b1.setX(b1.x - offsetX);
            b2.setX(b2.x + offsetX);
        } else if (b1.x > b2.x) {
            b1.setX(b1.x + offsetX);
            b2.setX(b2.x - offsetX);
        }
        if (b1.y > b2.y) {
            b1.setY(b1.y + offsetY);
            b2.setY(b2.y - offsetY);
        } else if (b1.y < b2.y) {
            b1.setY(b1.y - offsetY);
            b2.setY(b2.y + offsetY);
        }

        if ((b1.velocityX > 0 && b2.velocityX < 0) ||
                (b1.velocityX < 0 && b2.velocityX > 0)) {
            b1.reverseX();
            b2.reverseX();
        }
        if ((b1.velocityY > 0 && b2.velocityY < 0) ||
                (b1.velocityY < 0 && b2.velocityY > 0)) {
            b1.reverseY();
            b2.reverseY();
        }
    }

    // функция расчета расстояния между двумя точками
    private double calcDist(Bubble b1, Bubble b2) {
        return Math.sqrt(Math.pow(b1.x - b2.x, 2) +
                Math.pow(b1.y - b2.y, 2));
    }
}
