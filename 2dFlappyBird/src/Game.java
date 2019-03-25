import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import game2D.*;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. By default GameCore
// will handle the 'Escape' key to quit the game but you should
// override this with your own event handler.

/**
 * @author David Cairns
 *
 */
@SuppressWarnings("serial")

public class Game extends GameCore 
{
	// Useful game constants
	static int screenWidth = 900;
	static int screenHeight = 600;

    float 	lift = 0.005f;
    float	gravity = 0.0001f;
    
    Image background;
    
    // Game state flags
    boolean up = false;
    
    boolean right = false;
    
    boolean left = false;
    
    boolean down = false;
    
    int xo;
    int yo;
    int yoffset;
    int xoffset;
    int xpTop;
    int ypTop;
    int xpBottom;
    int ypBottom;
    int xpLeft;
    int ypLeft;
    int xpRight;
    int ypRight;
    String loadedMap = "map";
    

    // Game resources
    Animation flyRight;
    Animation flyLeft;
    Animation enemy;
    
    
    Sprite enemySprite = null;
    Sprite enemySprite2 =null;
    Sprite enemySprite3 = null;
    Sprite enemySprite4 = null;
    Sprite	player = null;
    ArrayList<Sprite> clouds = new ArrayList<Sprite>();
    

    TileMap tmap = new TileMap();	// Our tile map, note that we load it in init()
    
    long total;         			// The score will be the total time elapsed since a crash


    /**
	 * The obligatory main method that creates
     * an instance of our class and starts it running
     * 
     * @param args	The list of parameters this program might use (ignored)
     */
    public static void main(String[] args) {

        Game gct = new Game();
        gct.init();
        // Start in windowed mode with the given screen height and width
        gct.run(false,screenWidth,screenHeight);
    }

    /**
     * Initialise the class, e.g. set up variables, load images,
     * create animations, register event handlers
     */
    public void init()
    {         
        Sprite s;	// Temporary reference to a sprite
        
        xoffset = 128;
        yoffset = screenHeight/2;
        
        try {
			background = ImageIO.read(new File("images/bgImage.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("maps", "map.txt");

        // Create a set of background sprites that we can 
        // rearrange to give the illusion of motion
        flyLeft = new Animation();
        flyRight = new Animation();
        enemy = new Animation();
        flyRight.loadAnimationFromSheet("images/bird-sprite.png", 4, 2, 40);
        flyLeft.loadAnimationFromSheet("images/bird-sprite-back.png", 4, 2, 40);
        enemy.loadAnimationFromSheet("images/enemy-bird.png", 4, 2, 40);
        
        
        // Initialise the player with an animation
        player = new Sprite(flyRight);
        
        //Initialise Enemy Sprite
        enemySprite = new Sprite(enemy);
        enemySprite2 = new Sprite(enemy);
        enemySprite3 = new Sprite(enemy);
        enemySprite4 = new Sprite(enemy);
        
        
        
        // Load a single cloud animation
        Animation ca = new Animation();
        ca.addFrame(loadImage("images/cloud.png"), 1000);
        
        // Create 10 clouds at random positions off the screen
        // to the right
        for (int c=0; c<10;c++)
        {
        	s = new Sprite(ca);
        	s.setX(screenWidth + (int)(Math.random()*200.0f));
        	s.setY(30 + (int)(Math.random()*400.0f));
        	s.setVelocityX(-0.02f);
        	s.show();
        	clouds.add(s);
        }

        initialiseGame();
      		
        System.out.println(tmap);
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it to restart
     * the game.
     */
    public void initialiseGame()
    {
    	total = 0;
    	      
        player.setX(64);
        player.setY(100);
        player.setVelocityX(0);
        player.setVelocityY(0);
        player.show();
        
        
        enemySprite.setX(380);
        enemySprite.setY(150);
        enemySprite.setVelocityX(0);
        enemySprite.setVelocityY(0.1f);
        enemySprite.show();
        
        enemySprite2.setX(1280);
        enemySprite2.setY(90);
        enemySprite2.setVelocityX(0);
        enemySprite2.setVelocityY(0.1f);
        enemySprite2.show();
        
        enemySprite3.setX(2080);
        enemySprite3.setY(200);
        enemySprite3.setVelocityX(0);
        enemySprite3.setVelocityY(0.1f);
        enemySprite3.show();
        
        enemySprite4.setX(3000);
        enemySprite4.setY(250);
        enemySprite4.setVelocityX(0);
        enemySprite4.setVelocityY(0.1f);
        enemySprite4.show();
        
    }
    
    /**
     * Draw the current state of the game
     */
    public void draw(Graphics2D g)
    {    	
    	// Be careful about the order in which you draw objects - you
    	// should draw the background first, then work your way 'forward'

    	// First work out how much we need to shift the view 
    	// in order to see where the player is.
        xo = 0;
        yo = 0;

        // If relative, adjust the offset so that
        // it is relative to the player

        // ...?
        g.drawImage(background, 0, 0, null);
        //g.setColor(Color.white);//
       //g.fillRect(0, 0, getWidth(), getHeight());
        xo = (int)-player.getX()+xoffset;
        yo = (int)-player.getY()+yoffset;
        
        if(yo>0)
        	yo=0;
        
        if(yo<screenHeight - tmap.getPixelHeight())
        	yo = screenHeight - tmap.getPixelHeight();
        
        
        if(xo>0)
        	xo=0;
        
        if (xo<screenWidth - tmap.getPixelWidth())
        	xo = screenWidth - tmap.getPixelWidth();
        
        //g.setColor(Color.red);
//         g.drawRect(Math.round(player.getX()) , Math.round(player.getY()), player.getWidth(), player.getHeight());
        
        // Apply offsets to sprites then draw them
        for (Sprite s: clouds)
        {
//        	s.setOffsets(xo,yo);
        	s.draw(g);
        }

        // Apply offsets to player and draw 
        player.setOffsets(xo, yo);
        player.draw(g);
        
        enemySprite.setOffsets(xo, yo);
        enemySprite.draw(g);
        
        enemySprite2.setOffsets(xo, yo);
        enemySprite2.draw(g);
        
        enemySprite3.setOffsets(xo, yo);
        enemySprite3.draw(g);
        
        enemySprite4.setOffsets(xo, yo);
        enemySprite4.draw(g);
                
        // Apply offsets to tile map and draw  it
        tmap.draw(g,xo,yo);    
        
        // Show score and status information
        String msg = String.format("Score: %d", total/100);
        g.setColor(Color.orange);
        g.setFont(new Font("Latin Wide",Font.BOLD,20));
        g.drawString(msg, getWidth() - 100, 50);
        

    }

    /**
     * Update any sprites and check for collisions
     * 
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */    
    public void update(long elapsed)
    {
    	
        // Make adjustments to the speed of the sprite due to gravity
        player.setVelocityY(player.getVelocityY()+(gravity*elapsed));
    	    	
       	player.setAnimationSpeed(0f);
       	
       	if (up) 
       	{
       		player.shiftY(-1);
       		player.setAnimationSpeed(1.8f);
       		player.setVelocityY(-0.1f);
       		player.setAnimation(flyRight);
       	}
       	

       	else if (left) {
       		player.shiftX(-1);
       		player.setAnimationSpeed(1.8f);
       		player.setVelocityX(-0.1f);
       		player.setAnimation(flyLeft);
       	}
       	
       	else if (right) {
       		player.shiftX(1);
       		player.setAnimationSpeed(1.8f);
       		player.setVelocityX(0.1f);
       		player.setAnimation(flyRight);
       	}
       	else 
       	{
       		player.setVelocityX(0);
       		
       	}
                
       	for (Sprite s: clouds)
       		s.update(elapsed);
       	
       	
       	if (enemySprite.getY() <50)
       	{ enemySprite.setVelocityY(Math.abs(enemySprite.getVelocityY())); }
       	else if (enemySprite.getY() + enemySprite.getHeight() >= 500)
       	{ enemySprite.setVelocityY(-Math.abs(enemySprite.getVelocityY())); }
       	enemySprite.update(elapsed); // Update sprite position
       	
       	if (enemySprite2.getY() <50)
       	{ enemySprite2.setVelocityY(Math.abs(enemySprite2.getVelocityY())); }
       	else if (enemySprite2.getY() + enemySprite2.getHeight() >= 500)
       	{ enemySprite2.setVelocityY(-Math.abs(enemySprite2.getVelocityY())); }
       	enemySprite2.update(elapsed); // Update sprite position
       	
       	if (enemySprite3.getY() <50)
       	{ enemySprite3.setVelocityY(Math.abs(enemySprite3.getVelocityY())); }
       	else if (enemySprite3.getY() + enemySprite3.getHeight() >= 500)
       	{ enemySprite3.setVelocityY(-Math.abs(enemySprite3.getVelocityY())); }
       	enemySprite3.update(elapsed); // Update sprite position
       	
       	if (enemySprite4.getY() <50)
       	{ enemySprite4.setVelocityY(Math.abs(enemySprite4.getVelocityY())); }
       	else if (enemySprite4.getY() + enemySprite4.getHeight() >= 500)
       	{ enemySprite4.setVelocityY(-Math.abs(enemySprite4.getVelocityY())); }
       	enemySprite4.update(elapsed); // Update sprite position

        if(boundingBoxCollision( player, enemySprite) == true) {
        	 		
        	player.setVelocityY(0);
     		Sound cl = new Sound("sounds/caw.wav");
     		cl.start();
        	try {
    			Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

        	initialiseGame();
        }
        
        if(boundingBoxCollision2( player, enemySprite2) == true) {
	 		
        	player.setVelocityY(0);
     		Sound cl = new Sound("sounds/caw.wav");
     		cl.start();
        	try {
    			Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

        	initialiseGame();
        }
        	
        if(boundingBoxCollision3( player, enemySprite3) == true) {
	 		
        	player.setVelocityY(0);
     		Sound cl = new Sound("sounds/caw.wav");
     		cl.start();
        	try {
    			Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

        	initialiseGame();
        }
        
        if(boundingBoxCollision4( player, enemySprite4) == true) {
	 		
        	player.setVelocityY(0);
     		Sound cl = new Sound("sounds/caw.wav");
     		cl.start();
        	try {
    			Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

        	initialiseGame();
        }
     // Then check for any collisions that may have occurred
        handleTileMapCollisions(player,elapsed);
        
        // Now update the sprites animation and position
        player.update(elapsed);
       
        
        
         	
    }
    
    // Sound Fader Filter
    
   /** public int read(byte [] sample, int offset, int length)
    {
    int bytesRead = super.read(sample,offset,length);
    float change = 2.0f * (1.0f / (float)bytesRead);
    float volume = 1.0f;
    short amp=0;
    // Loop through the sample 2 bytes at a time
    for (int p=0; p<bytesRead; p = p + 2)
    {
    amp = getSample(sample,p);
    amp = (short)((float)amp * volume);
    setSample(sample,p,amp);
    volume = volume - change;
    }
    return length;
    }
    private void setSample(byte[] sample, int p, short amp) {
		// TODO Auto-generated method stub
		
	}

	private short getSample(byte[] sample, int p) {
		// TODO Auto-generated method stub
		return 0;
	}
	**/
    
    
    /**
     * Checks and handles collisions with the tile map for the
     * given sprite 's'. Initial functionality is limited...
     * 
     * @param s			The Sprite to check collisions for
     * @param elapsed	How time has gone by
     */
    public void handleTileMapCollisions(Sprite s, long elapsed)
    {
    	// This method should check actual tile map collisions. For
    	// now it just checks if the player has gone off the bottom
    	// of the tile map.
    	
    
       xpTop =(int)(player.getX()/tmap.getTileWidth()+0.5);
       ypTop =(int)((player.getY()+player.getHeight())/tmap.getTileHeight());
       
       xpBottom =(int)(player.getX()/tmap.getTileWidth()+0.5);
       ypBottom =(int)((player.getY()+player.getHeight())/tmap.getTileHeight()-1.2);
       
       xpLeft =(int)(player.getX()/tmap.getTileWidth()+1.7);
       ypLeft=(int)((player.getY()+player.getHeight())/tmap.getTileHeight()-0.6);
       
       xpRight=(int)(player.getX()/tmap.getTileWidth()+0.2);
       ypRight=(int)((player.getY()+player.getHeight())/tmap.getTileHeight()-0.6);
    	
    	
    //Collision with Bottom,ceiling and walls 	  	
    	
    if (tmap.getTileChar(xpTop, ypTop) == 'b' && player.getVelocityY() > 0) { //top edge of brick sprite
    	
    	player.setVelocityY(0);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    if (tmap.getTileChar(xpBottom, ypBottom) == 'b' ) {                       //bottom edge of brick sprite
    	
    	player.setVelocityY(0);
    	player.shiftY(1);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    if (tmap.getTileChar(xpLeft, ypLeft) == 'b' && player.getVelocityX() > 0) {   //left hand edge of brick sprite
    	
    	player.setVelocityX(0);
    	player.shiftX(-1);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    if (tmap.getTileChar(xpRight, ypRight) == 'b' && player.getVelocityX() < 0) { //right hand edge of brick sprite
    	
    	player.setVelocityX(0);
    	player.shiftX(1);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    
    //Collision with Pipes
    if (tmap.getTileChar(xpTop, ypTop) == 't' && player.getVelocityY() > 0) {    //top level of top pipe sprite
    	
    	player.setVelocityY(0);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    if (tmap.getTileChar(xpBottom, ypBottom) == 'p' ) {                          //bottom level of bottom pipe sprite
    	
    	player.setVelocityY(0);
    	player.shiftY(1);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    if (tmap.getTileChar(xpLeft, ypLeft) == 'm' && player.getVelocityX() > 0) {     //left hand side of middle pipe
    	
    	player.setVelocityX(0);
    	player.shiftX(-1);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    if (tmap.getTileChar(xpLeft, ypLeft) == 'p' && player.getVelocityX() > 0) {    //left hand side of bottom pipe
    	
    	player.setVelocityX(0);
    	player.shiftX(-1);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    if (tmap.getTileChar(xpLeft, ypLeft) == 't' && player.getVelocityX() > 0) {   //left hand side of top pipe
    	
    	player.setVelocityX(0);
    	player.shiftX(-1);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    if (tmap.getTileChar(xpRight, ypRight) == 'm' && player.getVelocityX() < 0) { //right hand side of middle pipe
    	
    	player.setVelocityX(0);
    	player.shiftX(1);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    if (tmap.getTileChar(xpRight, ypRight) == 'p' && player.getVelocityX() < 0) { //right hand side of bottom pipe
    	
    	player.setVelocityX(0);
    	player.shiftX(1);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    if (tmap.getTileChar(xpRight, ypRight) == 't' && player.getVelocityX() < 0) { //right hand side of top pipe
    	
    	player.setVelocityX(0);
    	player.shiftX(1);
 		Sound cl = new Sound("sounds/collision.wav");
 		cl.start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	initialiseGame();
       }
    
    // check coin collision
    
    if (tmap.getTileChar((xpTop), ypTop) == 'c' ) {                           //Top edge of coin tile
    	   	
         tmap.setTileChar('.', xpTop, ypTop);
         total += 100;
 		Sound c = new Sound("sounds/coin.wav");
 		c.start();
 
       }
    if (tmap.getTileChar(xpBottom, ypBottom) == 'c' ) {                       //bottom edge of coin tile
    	
        tmap.setTileChar('.', xpBottom, ypBottom);
        total += 100;
 		Sound c = new Sound("sounds/coin.wav");
 		c.start();
 

       }
    if (tmap.getTileChar(xpLeft, ypLeft) == 'c' ) {   //left hand edge of coin tile
    	
        tmap.setTileChar('.', xpLeft, ypLeft);
        total += 100;
 		Sound c = new Sound("sounds/coin.wav");
 		c.start();
 
 	   }
    if (tmap.getTileChar(xpRight, ypRight) == 'c' ) { //right hand edge of coin tile
    	
        tmap.setTileChar('.', xpRight, ypRight);
        total += 100;
 		Sound c = new Sound("sounds/coin.wav");
 		c.start();
 
       }

 
    
    
    // Check collision with finish line 
    
    if (tmap.getTileChar((xpTop), ypTop) == 'f'&& player.getVelocityY() > 0 ) {                           //Top edge finish line tile
 		Sound lc = new Sound("sounds/levelcomplete.wav");
 		lc.start();
 		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        level2();

      }
   if (tmap.getTileChar(xpBottom, ypBottom) == 'f' ) {                       //bottom edge of finish line tile
		Sound lc = new Sound("sounds/levelcomplete.wav");
		lc.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       level2();

      }
   if (tmap.getTileChar(xpLeft, ypLeft) == 'f'&& player.getVelocityX() > 0) {   //left hand edge of finish line tile
   	
		Sound lc = new Sound("sounds/levelcomplete.wav");
		lc.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       level2();
	   }
   if (tmap.getTileChar(xpRight, ypRight) == 'f' && player.getVelocityX() < 0) { //right hand edge of finish line tile
   	
		Sound lc = new Sound("sounds/levelcomplete.wav");
		lc.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       level2();
      }


   }
    		
    
    
     
    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     * 
     *  @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) 
    { 
    	int key = e.getKeyCode();
    	
    	if (key == KeyEvent.VK_ESCAPE) stop();
    	
    	if (key == KeyEvent.VK_UP) up = true;
    	
    	if (key == KeyEvent.VK_LEFT) left = true;
    	
    	if (key == KeyEvent.VK_RIGHT) right = true;
    	
    	if (key == KeyEvent.VK_DOWN) down = true;
    	   	
    	if (key == KeyEvent.VK_S)
    	{
    		// Example of playing a sound as a thread
    		Sound s = new Sound("sounds/caw.wav");
    		s.start();
    	}
    	
    }
    
    public void level2() {
    	
    	
    	
    	if(total > 2 && loadedMap.equals("map"))
    	{
    		tmap.loadMap("maps", "map2.txt");
    		loadedMap= "map2";
    		
    		total=0;
    		
            player.setX(64);
            player.setY(90);
            player.setVelocityX(0);
            player.setVelocityY(0);

    	
    	}
    	
    	else if (total> 100 && loadedMap.equals("map2")) {
    		stop();
    }
    			
    			
    			
    }

    public boolean boundingBoxCollision(Sprite player, Sprite enemySprite)
    {
    	return ((player.getX() + player.getImage().getWidth(null)> enemySprite.getX()) &&
    			(player.getX() < (enemySprite.getX() + enemySprite.getImage().getWidth(null)))
    			&&
    		   ((player.getY() + player.getImage().getHeight(null)> enemySprite.getY()) &&
    	        (player.getY() < (enemySprite.getY() + enemySprite.getImage().getHeight(null)))));
    }
    
    public boolean boundingBoxCollision2(Sprite player, Sprite enemySprite2)
    {
    	return ((player.getX() + player.getImage().getWidth(null)> enemySprite2.getX()) &&
    			(player.getX() < (enemySprite2.getX() + enemySprite2.getImage().getWidth(null)))
    			&&
    		   ((player.getY() + player.getImage().getHeight(null)> enemySprite2.getY()) &&
    	        (player.getY() < (enemySprite2.getY() + enemySprite2.getImage().getHeight(null)))));
    }
    
    public boolean boundingBoxCollision3(Sprite player, Sprite enemySprite3)
    {
    	return ((player.getX() + player.getImage().getWidth(null)> enemySprite3.getX()) &&
    			(player.getX() < (enemySprite3.getX() + enemySprite3.getImage().getWidth(null)))
    			&&
    		   ((player.getY() + player.getImage().getHeight(null)> enemySprite3.getY()) &&
    	        (player.getY() < (enemySprite3.getY() + enemySprite3.getImage().getHeight(null)))));
    }
    
    public boolean boundingBoxCollision4(Sprite player, Sprite enemySprite4)
    {
    	return ((player.getX() + player.getImage().getWidth(null)> enemySprite4.getX()) &&
    			(player.getX() < (enemySprite4.getX() + enemySprite4.getImage().getWidth(null)))
    			&&
    		   ((player.getY() + player.getImage().getHeight(null)> enemySprite4.getY()) &&
    	        (player.getY() < (enemySprite4.getY() + enemySprite4.getImage().getHeight(null)))));
    }
    


	public void keyReleased(KeyEvent e) { 

		int key = e.getKeyCode();

		// Switch statement instead of lots of ifs...
		// Need to use break to prevent fall through.
		switch (key)
		{
			case KeyEvent.VK_ESCAPE : stop(); break;
			case KeyEvent.VK_UP     : up = false; break;
			case KeyEvent.VK_RIGHT : right = false; break;
			case KeyEvent.VK_LEFT : left = false; break;
			case KeyEvent.VK_DOWN : down = false; break;
			case MouseEvent.BUTTON1: up = false; break;
			default :  break;
		}
	}
}
