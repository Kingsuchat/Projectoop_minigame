import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {
    // กำหนดขนาดบอร์ดเกม
    int boardWidth = 750;
    int boardHeight = 550;

    // รูปภาพสำหรับพื้นหลังและตัวละคร
    Image morning;
    Image afternoon;
    Image evening;
    Image Rockmanrun;  
    Image Rocman2;
    Image stone;
    Image bird;
    Image bird2;

    // คลาส Block ใช้แทนตัวละครและสิ่งกีดขวาง
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    // ข้อมูลของตัวละคร
    int RockmanrunWidth = 88;
    int RockmanrunHeight = 94;
    int dinosaurX = 50;
    int dinosaurY = boardHeight - RockmanrunHeight;
    Block dinosaur;
    int health = 3;

    // ข้อมูลสิ่งกีดขวาง (เช่น stone และ bird)
    int stoneWidth = 40;
    int birdWidth = 69;
    int bird2Width = 102;
    int cactusHeight = 70;
    int stoneX = 700;
    int stoneY = boardHeight - cactusHeight;
    ArrayList<Block> cactusArray;

    // ฟิสิกส์ของการเคลื่อนที่
    int velocityX = -12;
    int velocityY = 0;
    int gravity = 1;

    // สถานะของเกม
    boolean gameOver = false;
    boolean gameStarted = false;
    int score = 0;

    // ตัวจับเวลาและปุ่มต่าง ๆ
    Timer gameLoop;
    Timer placeCactusTimer;
    int elapsedTime = 0;
    JButton startButton;
    JButton restartButton;

    // Constructor ของเกม
    public ChromeDinosaur() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.lightGray);
        setFocusable(true);
        setLayout(null);
        addKeyListener(this);

        // โหลดรูปภาพพื้นหลังและตัวละคร
        morning = new ImageIcon(getClass().getResource("./img/morning.png")).getImage();
        afternoon = new ImageIcon(getClass().getResource("./img/afternoon.png")).getImage();
        evening = new ImageIcon(getClass().getResource("./img/evening.png")).getImage();
        Rockmanrun = new ImageIcon(getClass().getResource("./img/Rockmanrun.gif")).getImage();
        Rocman2 = new ImageIcon(getClass().getResource("./img/Rocman2.png")).getImage();
        stone = new ImageIcon(getClass().getResource("./img/stone.png")).getImage();
        bird = new ImageIcon(getClass().getResource("./img/bird.png")).getImage();
        bird2 = new ImageIcon(getClass().getResource("./img/bird2.png")).getImage();

        // กำหนดค่าตัวละคร
        dinosaur = new Block(dinosaurX, dinosaurY, RockmanrunWidth, RockmanrunHeight, Rockmanrun);
        cactusArray = new ArrayList<>();

        // สร้างปุ่ม Start และเพิ่ม Action Listener
        startButton = new JButton("Start");
        startButton.setFont(new Font("Courier", Font.PLAIN, 24));
        startButton.setFocusable(false);
        startButton.setBounds(boardWidth / 2 - 60, boardHeight / 2 - 25, 120, 50);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        add(startButton);

        // สร้างปุ่ม Restart และเพิ่ม Action Listener
        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Courier", Font.PLAIN, 24));
        restartButton.setFocusable(false);
        restartButton.setBounds(boardWidth / 2 - 60, boardHeight / 2 + 50, 120, 50);
        restartButton.setVisible(false); // ซ่อนปุ่มนี้ในตอนเริ่มต้น
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        add(restartButton);

        // ตั้งค่า Timer สำหรับการอัพเดทเกม
        gameLoop = new Timer(1000 / 60, this);

        // ตั้งค่า Timer สำหรับการสร้างสิ่งกีดขวาง
        placeCactusTimer = new Timer(1300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeCactus();
            }
        });
    }

    // เริ่มเกมใหม่
    private void startGame() {
        gameStarted = true;
        gameOver = false;
        startButton.setVisible(false);
        restartButton.setVisible(false);
        score = 0;
        elapsedTime = 0;
        health = 3;
        dinosaur.img = Rockmanrun;
        cactusArray.clear();
        gameLoop.start();
        placeCactusTimer.start();
    }

    // รีสตาร์ทเกม
    private void restartGame() {
        startGame();
    }

    // สร้างสิ่งกีดขวางใหม่
    void placeCactus() {
        if (gameOver) return;

        double placeCactusChance = Math.random();
        int cactusYShift = (int) (Math.random() * 10);
        int cactusYShiftHigher = cactusYShift + 180;

        // สร้างสิ่งกีดขวางตามโอกาสสุ่ม
        if (placeCactusChance > .80) {
            cactusArray.add(new Block(stoneX, stoneY - cactusYShift, bird2Width, cactusHeight, bird2));
        } else if (placeCactusChance > .50) {
            cactusArray.add(new Block(stoneX, stoneY - cactusYShift, birdWidth, cactusHeight, bird));
        } else {
            cactusArray.add(new Block(stoneX, stoneY - cactusYShift, stoneWidth, cactusHeight, stone));
        }

        // จำกัดจำนวนสิ่งกีดขวางในอาร์เรย์
        if (cactusArray.size() > 15) {
            cactusArray.remove(0);
        }
    }

    // ฟังก์ชันวาดหน้าจอ
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // ฟังก์ชันวาดองค์ประกอบเกม
    public void draw(Graphics g) {
        // เปลี่ยนภาพพื้นหลังตามคะแนน
        if (score >= 1500) {
            g.drawImage(evening, 0, 0, boardWidth, boardHeight, null); 
        } else if (score >= 1000) {
            g.drawImage(afternoon, 0, 0, boardWidth, boardHeight, null); 
        } else {
            g.drawImage(morning, 0, 0, boardWidth, boardHeight, null); 
        }

        if (!gameStarted) {
            g.drawString("Press Start to play", boardWidth / 2 - 150, boardHeight / 2);
        } else {
            g.drawImage(dinosaur.img, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);
            for (Block cactus : cactusArray) {
                g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
            }
            g.drawString("Score: " + score, 10, 35);
            g.drawString("Health: " + health, 10, 125);
            if (gameOver) {
                g.drawString("Game Over", boardWidth / 2 - 80, boardHeight / 2);
            }
        }
    }

    // ฟังก์ชันการเคลื่อนที่และการชน
    public void move() {
        velocityY += gravity;
        dinosaur.y += velocityY;

        // ตรวจสอบการตกถึงพื้น
        if (dinosaur.y > dinosaurY) {
            dinosaur.y = dinosaurY;
            velocityY = 0;
            dinosaur.img = Rockmanrun;
        }

        for (Block cactus : cactusArray) {
            cactus.x += velocityX;
            if (collision(dinosaur, cactus)) {
                health--;
                cactusArray.remove(cactus);
                break;
            }
        }

        // ถ้าพลังชีวิตหมด เกมจะจบ
        if (health <= 0) {
            gameOver = true;
            placeCactusTimer.stop();
            gameLoop.stop();
            restartButton.setVisible(true);
        }

        score++;
    }

    // ตรวจสอบการชนกันระหว่างตัวละครกับสิ่งกีดขวาง
    boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (!gameOver) elapsedTime++;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (dinosaur.y == dinosaurY) {
                velocityY = -17;
                dinosaur.img = Rocman2;
            }

            if (gameOver) {
                startGame();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    // ฟังก์ชันช่วยในการแปลงเวลาที่ผ่านไป
    private String formatElapsedTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
