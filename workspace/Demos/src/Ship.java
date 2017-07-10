import java.awt.*;
import java.util.ArrayList;

public class Ship extends GameObject{

    private float ax; //acceleration
    private float ay;

    private float ix; //initial spawn location
    private float iy;

    private final float fr = 0.99f; //friction
    private final float th = 800.0f; //thrust
    private final float mx_th = 500.0f; //maximum acceleration from thrust
    private final float ts = 7.5f; //turning speed (degrees)

    private ArrayList<Bullet> Bullets; //bullet list
    private final int Max_Bullets = 5; //maximum number of bullets

    private final int StartLives = 3; //starting amount of lives
    public int NumLives; //current number of lives

    private float lf; //life timer for spawning invincibility
    private final float mx_lf = 1.5f;

    private Color c; //color of the ship
    private Polygon sprite; //ship sprite
    int h = 20; //height of ship
    int w = 30; //width of ship

    public boolean isThrust = false; //is currently accelerating

    public Ship(float x, float y, Color c){
        super(); //super gameobject constructor
        this.c = c;
        this.ix = x;
        this.iy = y;

        //create ship body
        int xPoly[] = {0, w, 0};
        int yPoly[] = {0, h/2, h};
        sprite = new Polygon(xPoly, yPoly, xPoly.length);

        //set initial lives
        this.NumLives = StartLives;
        spawn();
    }

    private void spawn(){
        //set position to initial spawn point
        //0 all movement vectors
        this.px = ix;
        this.py = iy;
        this.vx = 0f;
        this.vy = 0f;
        this.ax = 0f;
        this.ay = 0f;

        //reset direction to up
        this.d = (float)Math.toRadians(-90);

        //reset bullet array
        this.Bullets = new ArrayList<>(Max_Bullets);
        this.lf = 0f;
    }

    @Override
    public void update(float dt) {

        //apply thrust or reset acceleration
        if(isThrust) {
            ax += th * Math.cos(d) * dt;
            ay += th * Math.sin(d) * dt;
        }
        else{
            ax = 0;
            ay = 0;
        }

        //apply acceleration
        vx += ax * dt;
        vy += ay * dt;

        //limit velocity to maximum thrust
        //note, applies to general acceleration, not just thrust
        if((Math.pow(vx, 2) + Math.pow(vy, 2)) > Math.pow(mx_th, 2)) {
            vx *= mx_th / Math.sqrt((Math.pow(vx, 2) + Math.pow(vy, 2)));
            vy *= mx_th / Math.sqrt((Math.pow(vx, 2) + Math.pow(vy, 2)));
        }

        //apply friction to velocity
        vx *= fr; //note, not frame independent
        vy *= fr; //v -= ((v - (v * Fr) ) * dt;

        //apply velocity
        px += vx * dt;
        py += vy * dt;

        //update bullets
        for(int i = Bullets.size() - 1; i >= 0; i--){
            if(!Bullets.get(i).isActive()) Bullets.remove(i);
            else Bullets.get(i).update(dt);
        }

        //if invincibility timer isn't up, update timer
        if(lf < mx_lf) lf += dt;
    }

    @Override
    public void draw(Graphics2D g) {

        //set position and rotation to current position
        g.translate(getX(), getY());
        g.rotate(d, w/2, h/2); //center of ship
            g.setColor(c);
            g.fillPolygon(sprite);
            if(isThrust){ //draw thrust
                if(Math.random() > 0.5) {
                    g.setColor(Color.WHITE);
                    g.fillOval(-w, h/2 - w/8, w / 4, w / 4);
                    g.setColor(Color.YELLOW);
                    g.fillOval(-w * 2, h/2 - w/4, w / 2, w / 2);
                }
            }
        //reset position
        g.rotate(-d, w/2, h/2);
        g.translate(-(getX()), -(getY()));

        //draw bullets
        for(int i = 0; i < Bullets.size(); i++) Bullets.get(i).draw(g);

    }

    @Override
    public void wrap(int Width, int Height){
        //wrap ship position to other side of screen
        if(px < 0) px = Width;
        if(px > Width) px = 0;
        if(py < 0) py = Height;
        if(py > Height) py = 0;

        //wrap bullets
        for(Bullet b : Bullets){
            b.wrap(Width, Height);
        }
    }

    @Override
    public boolean isColliding(GameObject o){
        //forward collision handlers
        for(Bullet b : Bullets) o.isColliding(b);
        return o.isColliding(this);
    }

    @Override
    public void takeHit(){
        //take out a life if we're vulnerable
        if(lf < mx_lf) return;
        if(NumLives > 0) NumLives--;
        spawn();
    }

    //check endgame
    public boolean isGameOver(){
        return NumLives <= 0;
    }

    public void turnRight(){
        d += Math.toRadians(ts); //convert to radians
        d %= Math.PI * 2; //wrap value
    }

    public void turnLeft(){
        d -= Math.toRadians(ts);
        d %= Math.PI * 2;
    }

    public void fire(){
        if(Bullets.size() < Max_Bullets){
            float bpx = px + w/2 + (float)Math.cos(d) * w/2;
            float bpy = py + h/2 + (float)Math.sin(d) * w/2;
            Bullets.add(new Bullet(bpx, bpy, d));
        }
    }


    class Bullet extends GameObject{

        private float lf;
        private final float mx_lf = 1.5f;

        final float sz = 10;

        final float sp = 750.0f;

        Bullet(float px, float py, float d){
            this.px = px;
            this.py = py;
            this.d = d;

            this.vx = sp * (float)Math.cos(d);
            this.vy = sp * (float)Math.sin(d);

            this.lf = 0f;
        }


        @Override
        public void update(float dt) {
            this.px += vx * dt;
            this.py += vy * dt;

            this.lf += dt;
        }

        @Override
        public void draw(Graphics2D g) {
            g.setColor(Color.ORANGE);
            g.drawOval((int)(px - sz/2), (int)(py - sz/2), (int)sz, (int)sz);
        }

        @Override
        public void wrap(int Width, int Height) {
            if (px < 0) px = Width;
            if (px > Width) px = 0;
            if (py < 0) py = Height;
            if (py > Height) py = 0;
        }

        @Override
        public void takeHit(){
            this.lf = this.mx_lf;
        }

        boolean isActive(){
            return this.lf < this.mx_lf;
        }
    }

}