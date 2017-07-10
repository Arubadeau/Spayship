import java.awt.*;

public abstract class GameObject {

    float px; //float position x
    float py; //float position y

    float vx; //velocity x
    float vy; //velocity y

    float d; //direction (rad)

    public abstract void update(float dt);
    public abstract void draw(Graphics2D g);

    public abstract void wrap(int Width, int Height);

    public boolean isColliding(GameObject o){
        return false;
    }

    public void takeHit(){
        return;
    }

    //returns the position x as an int
    public int getX(){
        return (int)px;
    }

    //returns the position y as an int
    public int getY(){
        return (int)py;
    }

}
