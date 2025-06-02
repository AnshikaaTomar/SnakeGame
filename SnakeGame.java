package SnakeGame;

import java.awt.*;             
import java.awt.event.*;       
import java.util.ArrayList;    
import java.util.Random;      
import javax.swing.*;          

public class SnakeGame extends JPanel implements ActionListener, KeyListener{ 

    private class Tile {
        int x;             
        int y; 

        Tile(int x, int y){ 
            this.x = x;
            this.y = y;
        }
        
    }
    //to create a graphical background with green bubbles
    private class LeafParticle {
        int x, y, size;
        Color color;

        LeafParticle() {
            x = random.nextInt(boardWidth);
            y = random.nextInt(boardHeight);
            size = random.nextInt(10) + 5;
            
            color = new Color(0, 255, 0, 80);
        }

        void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, size, size);
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;     

    Tile snakeHead;              
    ArrayList<Tile> snakeBody;   

    Tile food; 
    Random random; 

    Timer gameLoop;  
    int velocityX;
    int velocityY;
     
    boolean gameOver = false;
     
    int highScore = 0;

    ArrayList<LeafParticle> leaves = new ArrayList<>();

    SnakeGame(int boardWidth, int boardHeight){
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;

        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5,5); 
        snakeBody = new ArrayList<Tile>(); 

        food = new Tile(10, 10); 
        random = new Random();
        placeFood(); 

        velocityX = 1;
        velocityY = 0; 

        for (int i = 0; i < 30; i++) {
            leaves.add(new LeafParticle());
        }
        
        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g){  
        super.paintComponent(g);
        draw(g); 
    }

    public void draw(Graphics g){
        //the background of the game
        g.setColor(Color.BLACK); // #107720
        g.fillRect(0, 0, boardWidth, boardHeight);

        //static leaves 
        for(LeafParticle leaf : leaves){
            leaf.draw(g);
        }

        //snake head
        g.setColor(new Color(89, 54, 28));
        g.fillOval(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        //food
        g.setColor(new Color(105, 4, 4));
        g.fillOval(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        //snake body 
        g.setColor(new Color(126, 78, 44));
        for(int i = 0; i < snakeBody.size(); i++){  //iterates through the array list to create the body
            Tile snakePart = snakeBody.get(i);
            g.fillOval(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }

        //score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if(gameOver){
            g.setColor(Color.RED);
            g.drawString("Game Over ", tileSize - 16, tileSize);
        }
        else{
            g.setColor(Color.GREEN);
            g.drawString("Score: " + String.valueOf(snakeBody.size()) , tileSize - 16, tileSize);
        }

        //highscore 
        g.setColor(Color.CYAN);
        g.drawString("High Score: " + highScore, tileSize - 16, tileSize + 20);
    }

    public void placeFood(){
        food.x = random.nextInt(boardWidth/tileSize); //random unit between 0-24 (600/25)
        food.y = random.nextInt(boardHeight/tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2){ 
        return tile1.x == tile2.x && tile1.y == tile2.y; 
    }

    public void move(){
        //eat the food 
        if(collision(snakeHead, food)){
            snakeBody.add(new Tile(snakeHead.x, snakeHead.y));
            placeFood();
        }

        //body moves along with the head 
        for(int i = snakeBody.size() - 1; i>=0; i--){    
            Tile snakePart = snakeBody.get(i);
            if(i == 0){
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else{
                Tile prevSnakePart = snakeBody.get(i-1);  //follow the snake head 
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY; 
       

        //game over conditions
        for(int i = 0; i < snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);

            if(collision(snakeHead, snakePart)){
                gameOver = true;
            }
        }

        //if the snake touch one of the four walls
        if(snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > boardWidth ||
           snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > boardHeight){
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move(); 
        repaint(); 
        if(gameOver){
            gameLoop.stop();
            if (snakeBody.size() > highScore) {
                highScore = snakeBody.size();
            }

            //let the user retry by restarting the game
            int retry = JOptionPane.showConfirmDialog(this, "Game Over! Your score: " + snakeBody.size() + "\nDo you want to retry?", "Game Over", JOptionPane.YES_NO_OPTION);

            if (retry == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0); // exit game if user presses No
            }
        }
    }

    //to control the movement of the snake throught the keys 
    @Override
    public void keyPressed(KeyEvent e) {
        
        if( e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1){
            velocityX = 0;
            velocityY = 1;
        }
        else if( e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1){
            velocityX = 0;
            velocityY = -1;
        }
        else if( e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1){
            velocityX = -1;
            velocityY = 0;
        }
        else if( e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1){
            velocityX = 1;
            velocityY = 0;
        }
    }

    
    @Override
    public void keyTyped(KeyEvent e) { 
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    //to restart the game 
    public void restartGame() {
    snakeHead = new Tile(5, 5);
    snakeBody.clear();
    velocityX = 1;
    velocityY = 0;
    placeFood();
    gameOver = false;
    gameLoop.start();
    repaint();
    }

}



