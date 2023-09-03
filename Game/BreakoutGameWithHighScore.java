import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class BreakoutGameWithHighScore extends JPanel implements ActionListener, KeyListener {
    private int ballX = 250;
    private int ballY = 350;
    private int ballDirectionX = 1;
    private int ballDirectionY = 2;

    private int paddleX = 200;
    private int paddleY = 380;

    private boolean[] bricks;
    private int brickRows = 5;
    private int brickCols = 10;
    private int brickWidth = 50;
    private int brickHeight = 20;

    private int score = 0;
    private int highestScore = 0;

    private Timer timer;

    public BreakoutGameWithHighScore() {
        bricks = new boolean[brickRows * brickCols];
        for (int i = 0; i < bricks.length; i++) {
            bricks[i] = true;
        }

        loadHighestScore();

        timer = new Timer(10, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw bricks
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                int index = i * brickCols + j;
                if (bricks[index]) {
                    g.setColor(Color.blue);
                    g.fillRect(j * brickWidth, i * brickHeight, brickWidth, brickHeight);
                }
            }
        }

        // Draw paddle
        g.setColor(Color.red);
        g.fillRect(paddleX, paddleY, 100, 10);

        // Draw ball
        g.setColor(Color.green);
        g.fillOval(ballX, ballY, 20, 20);

        // Draw score
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 30);

        // Draw highest score
        g.drawString("Highest Score: " + highestScore, 10, 60);
    }

    public void actionPerformed(ActionEvent e) {
        // Move the ball
        ballX += ballDirectionX;
        ballY += ballDirectionY;

        // Ball collision with walls
        if (ballX <= 0 || ballX >= 480) {
            ballDirectionX = -ballDirectionX;
        }
        if (ballY <= 0) {
            ballDirectionY = -ballDirectionY;
        }

        // Ball collision with paddle
        if (ballY >= paddleY && ballY <= paddleY + 10 && ballX >= paddleX && ballX <= paddleX + 100) {
            ballDirectionY = -ballDirectionY;
        }

        // Ball collision with bricks
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                int index = i * brickCols + j;
                if (bricks[index]) {
                    int brickX = j * brickWidth;
                    int brickY = i * brickHeight;
                    if (ballX >= brickX && ballX <= brickX + brickWidth && ballY >= brickY && ballY <= brickY + brickHeight) {
                        bricks[index] = false;
                        ballDirectionY = -ballDirectionY;
                        score += 10;
                    }
                }
            }
        }

        // Ball out of bounds
        if (ballY >= 400) {
            // Game over
            timer.stop();
            checkHighestScore();
            displayGameOver();
        }

        repaint();
    }

    private void loadHighestScore() {
        try {
            File file = new File("highest_score.txt");
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();
                if (line != null) {
                    highestScore = Integer.parseInt(line);
                }
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveHighestScore() {
        try {
            File file = new File("highest_score.txt");
            FileWriter fw = new FileWriter(file);
            fw.write(Integer.toString(highestScore));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkHighestScore() {
        if (score > highestScore) {
            highestScore = score;
            saveHighestScore();
        }
    }

    private void displayGameOver() {
        JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + score + "\nHighest Score: " + highestScore, "Game Over", JOptionPane.PLAIN_MESSAGE);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            if (paddleX > 0) {
                paddleX -= 20;
            }
        } else if (key == KeyEvent.VK_RIGHT) {
            if (paddleX < 400) {
                paddleX += 20;
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout Game");
        BreakoutGameWithHighScore game = new BreakoutGameWithHighScore();
        frame.add(game);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}