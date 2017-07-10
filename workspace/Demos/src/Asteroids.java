import java.awt.*;
import java.util.ArrayList;

public class Asteroids extends GameObject{

    int MAX_LEVEL;
    int START_NUM;
    final float START_SIZE;
    final float VARIATION = 0.8f;

    int windowWidth;
    int windowHeight;

    float start_speed;

    private ArrayList<Asteroid> myAsteroids;

    public Asteroids(int mx_lv, int start_num, float start_size, float start_speed, int Width, int Height){
        this.MAX_LEVEL = mx_lv;
        this.START_NUM = start_num;
        this.START_SIZE = start_size;
        this.windowWidth = Width;
        this.windowHeight = Height;
        this.start_speed = start_speed;

        myAsteroids = new ArrayList<Asteroid>((int)( start_num * Math.pow(MAX_LEVEL, MAX_LEVEL-1) ));
        for(int i = 0; i < start_num; i++){
            myAsteroids.add(new Asteroid(start_size, (float) Math.random() * Width, (float) Math.random() * Height, start_speed));
        }
    }

    public void reset(){
        MAX_LEVEL++;
        START_NUM++;
        myAsteroids = new ArrayList<Asteroid>((int)( START_NUM * Math.pow(MAX_LEVEL, MAX_LEVEL-1) ));
        for(int i = 0; i < START_NUM; i++){
            myAsteroids.add(new Asteroid(START_SIZE, (float) Math.random() * windowWidth, (float) Math.random() * windowHeight, start_speed));
        }
    }

    @Override
    public void update(float dt){
        for(Asteroid a : myAsteroids)
            a.update(dt);
        if(myAsteroids.size() == 0) {
            Game.SCORE *= 1.5;
            reset();
        }
    }

    @Override
    public void draw(Graphics2D g){
        for(Asteroid a : myAsteroids)
            a.draw(g);
    }

    @Override
    public void wrap(int Width, int Height){
        for(Asteroid a : myAsteroids)
            a.wrap(Width, Height);
    }

    @Override
    public boolean isColliding(GameObject o){
        for(int i = myAsteroids.size() - 1; i >= 0; i-- ){
            if(myAsteroids.get(i).isColliding(o)){
                ArrayList<Asteroid> children = myAsteroids.remove(i).getChildren();
                if(children != null) myAsteroids.addAll(children);
                o.takeHit();
            }
        }
        return false;
    }

    float getVariation(float value){
        return value * (float)(VARIATION + (Math.random() * VARIATION/2f));
    }


    private class Asteroid extends GameObject{

        float sz;
        private float ts;
        private int lv;

        Asteroid(float sz, float px, float py, float v){

            this.sz = getVariation(sz);

            this.px = px;
            this.py = py;
            this.d = (float) (Math.random() * Math.PI * 2f);
            this.vx = (float)Math.cos(d) * getVariation(v);
            this.vy = (float)Math.sin(d) * getVariation(v);

            this.lv = MAX_LEVEL;
        }

        Asteroid(Asteroid o){

            this.sz = getVariation(o.sz * 0.67f);

            this.px = o.px;
            this.py = o.py;

            this.d = getVariation(o.d);
            this.vx =  getVariation(o.vx * 2f);
            this.vy = getVariation(o.vx * 2f);

            this.lv = o.lv - 1;
        }

        ArrayList<Asteroid> getChildren(){
            if(lv < 1) return null;
            ArrayList<Asteroid> children = new ArrayList();
            for(int i = 0; i < MAX_LEVEL; i++){
                children.add(new Asteroid(this));
            }
            return children;
        }

        @Override
        public void update(float dt) {
            this.px += this.vx * dt;
            this.py += this.vy * dt;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(Color.CYAN);
            g.drawOval(getX() - (int)sz/2, getY() - (int)sz/2, (int)sz, (int)sz);
        }

        @Override
        public void wrap(int Width, int Height) {
            if(px > Width || px < 0) vx *= -1;
            if(py > Height || py < 0) vy *= -1;
        }


        @Override
        public boolean isColliding(GameObject o) {
            if(o instanceof Ship){
                if(Math.pow(px - o.px, 2) + Math.pow(py - o.py, 2) < Math.pow(sz/2 + ((Ship) o).h, 2)){
                    Game.SCORE += 25 * (MAX_LEVEL - lv + 1);
                    return true;
                }
            }
            if(o instanceof Ship.Bullet){
                if(Math.pow(px - o.px, 2) + Math.pow(py - o.py, 2) < Math.pow(sz/2 + ((Ship.Bullet) o).sz/2, 2)){
                    Game.SCORE += 50 * (MAX_LEVEL - lv + 1);
                    return true;
                }
            }
            return false;
        }
    }
}
