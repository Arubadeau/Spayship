import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

public class Game extends JFrame implements KeyListener{

    //window vars
    private final int MAX_FPS;
    private final int WIDTH;
    private final int HEIGHT;

    //game states
    public enum GAME_STATES{
        MENU,
        GAME,
        SCORE
    }
    public GAME_STATES GameState;

    //player score
    public static int SCORE;

    //pause
    public boolean isPaused = false;

    //menu UI elements
    private JPanel Menu;
    private JButton Menu_Play;
    private JButton Menu_Exit;

    //score UI elements
    private JPanel Score;
    private JButton Score_Play;
    private JButton Score_Menu;
    private JLabel Score_Label;
    private JLabel Score_Score;

    //double buffer
    private BufferStrategy strategy;

    //loop variables
    private boolean isRunning = true;
    private long rest = 0;

    //timing variables
    private float dt;
    private long lastFrame;
    private long startFrame;
    private int fps;

    //game object handlers
    private Ship player;
    private Asteroids asteroidHandler;

    //constructor, sets up window
    public Game(int width, int height, int fps){
        super("Asteroids Demo");
        this.MAX_FPS = fps;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    //init, creates and sets up UI and game vars
    void init(){
        //initialize JFrame
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setBounds(0, 0, WIDTH, HEIGHT);

        //set initial last frame value
        lastFrame = System.currentTimeMillis();


        /////////////MENU UI/////////////////////////////

        //set up Menu panel
        Menu = new JPanel(new GridLayout(2, 1));
        Menu.setPreferredSize(new Dimension(WIDTH, 250));

        //initialize buttons
        Menu_Play = new JButton("Play!"){{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Menu.setVisible(false);
                    GameState = GAME_STATES.GAME;
                }
            });
        }};
        Menu_Exit = new JButton("Exit"){{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }};

        //add buttons to panel
        Menu.add(Menu_Play);
        Menu.add(Menu_Exit);

        //set visible and add to frame
        Menu.setVisible(true);
        this.getContentPane().add(Menu, BorderLayout.SOUTH);

        /////////////////////////SCORE UI/////////////////////
        //set up Score panel
        Score = new JPanel(new BorderLayout());

        //set up sub panels
        JPanel Score_Top = new JPanel(new GridLayout(1, 2));
        Score_Top.setPreferredSize(new Dimension(WIDTH, 200));

        JPanel Score_Bottom = new JPanel(new GridLayout(2, 1));
        Score_Bottom.setPreferredSize(new Dimension(WIDTH, 250));

        //add sub panels to main panel
        Score.add(Score_Top, BorderLayout.NORTH);
        Score.add(Score_Bottom, BorderLayout.SOUTH);

        //initialize labels and add them to top panel
        Score_Label = new JLabel("Your Score: ", SwingConstants.CENTER);
        Score_Score = new JLabel(Integer.toString(SCORE), SwingConstants.CENTER);
        Score_Top.add(Score_Label);
        Score_Top.add(Score_Score);

        //initialize buttons
        Score_Play = new JButton("Play Again!"){{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Score.setVisible(false);
                    GameState = GAME_STATES.GAME;
                }
            });
        }};
        Score_Menu = new JButton("Back to Menu"){{
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Score.setVisible(false);
                    Menu.setVisible(true);
                    GameState = GAME_STATES.MENU;
                }
            });
        }};

        //add buttons to bottom panel
        Score_Bottom.add(Score_Play);
        Score_Bottom.add(Score_Menu);

        //add main panel to frame
        Score.setVisible(false); //hide frame at first
        this.getContentPane().add(Score);

        //finish initializing the frame
        setResizable(false);
        setVisible(true);

        this.pack();
        setIgnoreRepaint(true);

        //add key listener
        addKeyListener(this);
        setFocusable(true);

        //create double buffer strategy
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        //initialize game components
        ResetGame();

        GameState = GAME_STATES.MENU;
    }


    private void update(){
        //update current fps
        fps = (int)(1f/dt);

        switch(GameState){
            case MENU:
                break;
            case GAME:

                //update game objects
                asteroidHandler.update(dt);
                asteroidHandler.wrap(WIDTH, HEIGHT);

                player.update(dt);
                player.wrap(WIDTH, HEIGHT);

                //hit detection and resolution
                if(player.isColliding(asteroidHandler)) player.takeHit();

                //if game over, set state to score, set score visible, reset game vars
                if(player.isGameOver()){
                    GameState = GAME_STATES.SCORE;
                    Score_Score.setText(Integer.toString(SCORE));
                    Score.setVisible(true);

                    ResetGame();
                }

                break;
            case SCORE:
                break;
        }
    }

    private void draw(){

        switch(GameState){
            case MENU:
                break;
            case GAME:
                //get canvas
                Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

                //clear screen
                g.setColor(Color.black);
                g.fillRect(0,0,WIDTH, HEIGHT);

                //draw images
                asteroidHandler.draw(g);
                player.draw(g);

                //draw fps
                g.setColor(Color.GREEN);
                g.drawString(Long.toString(fps), 10, 40);

                g.setColor(Color.WHITE);
                g.drawString("Num Lives: " + player.NumLives, 10, 80);
                g.drawString("Score: " + SCORE, 10, 120);

                //release resources, show the buffer
                g.dispose();
                strategy.show();
                break;
            case SCORE:
                break;
        }

    }

    public void ResetGame(){
        player = new Ship(WIDTH/2, HEIGHT/2, Color.PINK);
        asteroidHandler = new Asteroids(2, 4, 250f, 50f, WIDTH, HEIGHT);

        SCORE = 0;
    }

    public void run(){
        init();

        while(isRunning){
            //new loop, clock the start
            startFrame = System.currentTimeMillis();
            //calculate delta time
            dt = (float)(startFrame - lastFrame)/1000;
            //log the current time
            lastFrame = startFrame;

            //call update and draw methods
            update();
            draw();

            //dynamic thread sleep, only sleep the time we need to cap the framerate
            rest = (1000/MAX_FPS) - (System.currentTimeMillis() - startFrame);
            if(rest >0){
                try{ Thread.sleep(rest); }
                catch (InterruptedException e){ e.printStackTrace(); }
            }
        }

    }

    @Override
    public void keyPressed(KeyEvent e){
        switch(e.getKeyCode()){
            case KeyEvent.VK_UP:
                player.isThrust = true;
                break;
            case KeyEvent.VK_LEFT:
                player.turnLeft();
                break;
            case KeyEvent.VK_RIGHT:
                player.turnRight();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e){
        switch(e.getKeyCode()){
            case KeyEvent.VK_UP:
                player.isThrust = false;
                break;
            case KeyEvent.VK_SPACE:
                player.fire();
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e){}

    public static void main(String[] args){
        Game game = new Game(800, 600, 60);
        game.run();
    }

}
