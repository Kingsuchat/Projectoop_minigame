import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 750; // กำหนดความกว้างของบอร์ดเกม
    int boardHeight = 550; // กำหนดความสูงของบอร์ดเกม

    // โหลดรูปภาพต่างๆ 
    Image morning; 
    Image afternoon;
    Image evening;
    Image Rockmanrun;  
    Image Rocman2;
    Image stone;
    Image bird;
    Image bird2;

    // กำหนดคลาส Block สำหรับเก็บข้อมูลของบล็อกต่างๆ เช่น ตำแหน่ง ขนาด และรูปภาพ
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

    // กำหนดค่าดิโนเสาร์
    int RockmanrunWidth = 88;
    int RockmanrunHeight = 94;
    int dinosaurX = 50;
    int dinosaurY = boardHeight - RockmanrunHeight; // ตั้งตำแหน่ง y ของดิโนเสาร์
    Block dinosaur;
    int health = 3; // กำหนดค่าพลังชีวิตเริ่มต้น

    // กำหนดค่าหินและนก
    int stoneWidth = 40;
    int birdWidth = 69;
    int bird2Width = 102;
    int cactusHeight = 70;
    int stoneX = 700; // ตำแหน่งเริ่มต้นของหิน
    int stoneY = boardHeight - cactusHeight;
    ArrayList<Block> cactusArray; // อาร์เรย์เก็บบล็อกหินและนก

    // ฟิสิกส์ของเกม
    int velocityX = -12;
    int velocityY = 0;
    int gravity = 1;

    boolean gameOver = false; // ตรวจสอบว่าเกมจบหรือไม่
    boolean gameStarted = false; // ตรวจสอบว่าเกมเริ่มหรือยัง
    int score = 0; // คะแนนของเกม

    Timer gameLoop; // ตั้งค่า Timer สำหรับเกม loop
    Timer placeCactusTimer; // ตั้ง Timer สำหรับการสร้างหินและนก
    int elapsedTime = 0; // ตัวแปรเก็บเวลาที่ผ่านไป
    JButton startButton; // ปุ่มเริ่มเกม
    JButton restartButton; // ปุ่มเริ่มเกมใหม่

    public ChromeDinosaur() {
        setPreferredSize(new Dimension(boardWidth, boardHeight)); // ตั้งขนาดบอร์ดเกม
        setBackground(Color.lightGray); // ตั้งสีพื้นหลัง
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

        dinosaur = new Block(dinosaurX, dinosaurY, RockmanrunWidth, RockmanrunHeight, Rockmanrun);
        cactusArray = new ArrayList<>(); // สร้างอาร์เรย์สำหรับเก็บหินและนก

        // สร้างปุ่ม Start
        startButton = new JButton("Start");
        startButton.setFont(new Font("Courier", Font.PLAIN, 24));
        startButton.setFocusable(false);
        startButton.setBounds(boardWidth / 2 - 60, boardHeight / 2 - 25, 120, 50);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(); // เริ่มเกมเมื่อกดปุ่ม
            }
        });
        add(startButton);

        // สร้างปุ่ม Restart
        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Courier", Font.PLAIN, 24));
        restartButton.setFocusable(false);
        restartButton.setBounds(boardWidth / 2 - 60, boardHeight / 2 + 50, 120, 50);
        restartButton.setVisible(false); // ซ่อนปุ่มนี้ในตอนเริ่มต้น
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame(); // เริ่มเกมใหม่เมื่อกดปุ่ม
            }
        });
        add(restartButton);

        // ตั้งค่า Timer สำหรับ loop ของเกม
        gameLoop = new Timer(1000 / 60, this);

        // ตั้งค่า Timer สำหรับการสร้างหิน
        placeCactusTimer = new Timer(1300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeCactus(); // สร้างหินและนกแบบสุ่ม
            }
        });
    }

    private void startGame() {
        gameStarted = true;
        gameOver = false;
        startButton.setVisible(false);
        restartButton.setVisible(false); // ซ่อนปุ่ม Restart เมื่อเริ่มเกมใหม่
        score = 0;
        elapsedTime = 0;
        health = 3;
        dinosaur.img = Rockmanrun;
        cactusArray.clear(); // เคลียร์อาร์เรย์หินเมื่อเริ่มเกมใหม่
        gameLoop.start();
        placeCactusTimer.start();
    }

    // ฟังก์ชันสำหรับเริ่มเกมใหม่เมื่อกดปุ่ม Restart
    private void restartGame() {
        startGame(); // เรียกใช้ฟังก์ชัน startGame() ที่มีอยู่เพื่อเริ่มเกมใหม่
    }

    void placeCactus() {
        if (gameOver) {
            return;
        }

        double placeCactusChance = Math.random();
        int cactusYShift = (int) (Math.random() * 10);
        int cactusYShiftHigher = cactusYShift + 180;

        // สุ่มขนาดสำหรับหินและนก
        int randomWidth = (int) ((Math.random() * 30) + stoneWidth); 
        int randomHeight = (int) ((Math.random() * 30) + cactusHeight); 
        int randomBirdWidth = (int) ((Math.random() * 50) + 60); 
        int randomBirdHeight = (int) ((Math.random() * 45) + 45); 

        if (placeCactusChance > .80) { 
            Block cactus = new Block(stoneX, stoneY - cactusYShift, bird2Width, cactusHeight, bird2);
            cactusArray.add(cactus);
        } else if (placeCactusChance > .50) { 
            Block cactus = new Block(stoneX, stoneY - cactusYShift, randomBirdWidth, randomBirdHeight, bird); 
            cactusArray.add(cactus);
        } else { 
            Block cactus = new Block(stoneX, stoneY - cactusYShift, randomWidth, randomHeight, stone); 
            cactusArray.add(cactus);
        }

        if (placeCactusChance > .70) {
            if (placeCactusChance > .90) {
                Block cactusHigher = new Block(stoneX + 230, stoneY - cactusYShiftHigher, bird2Width, cactusHeight, bird2);
                cactusArray.add(cactusHigher);
            } else if (placeCactusChance > .60) {
                Block cactusHigher = new Block(stoneX + 230, stoneY - cactusYShiftHigher, randomBirdWidth, randomBirdHeight, bird);
                cactusArray.add(cactusHigher);
            } else {
                Block cactusHigher = new Block(stoneX + 230, stoneY - cactusYShiftHigher, randomWidth, randomHeight, stone);
                cactusArray.add(cactusHigher);
            }
        }

        if (cactusArray.size() > 15) { // จำกัดขนาดของอาร์เรย์ cactus เพื่อไม่ให้มีบล็อกเยอะเกินไป
            cactusArray.remove(0);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g); // เรียกใช้ฟังก์ชัน draw() เพื่อวาดวัตถุทั้งหมดบนหน้าจอ
    }

    public void draw(Graphics g) {
        // เปลี่ยนภาพพื้นหลังตามคะแนน
        if (score >= 1500) {
            g.drawImage(evening, 0, 0, boardWidth, boardHeight, null); 
        } else if (score >= 1000) {
            g.drawImage(afternoon, 0, 0, boardWidth, boardHeight, null); 
        } else {
            g.drawImage(morning, 0, 0, boardWidth, boardHeight, null); 
        }

        if (!gameStarted) { // แสดงข้อความเมื่อยังไม่เริ่มเกม
            g.setColor(Color.black);
            g.setFont(new Font("Courier", Font.PLAIN, 32));
            g.drawString("Press Start       to play", boardWidth / 2 - 150, boardHeight / 2);
        } else { // แสดงตัวละครและหิน/นกเมื่อเกมเริ่ม
            g.drawImage(dinosaur.img, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);

            for (Block cactus : cactusArray) {
                g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
            }

            g.setColor(Color.black);
            g.setFont(new Font("Courier", Font.PLAIN, 32));
            g.drawString("Score: " + score, 10, 35);
            g.drawString("Time: " + formatElapsedTime(elapsedTime), 10, 80);
            g.drawString("Health: " + health, 10, 125);

            if (gameOver) { // แสดงข้อความเมื่อเกมจบ
                g.drawString("  Game Over", boardWidth / 2 - 80, boardHeight / 2);
                g.drawString("Press Restart to play again", boardWidth / 2 - 150, boardHeight / 2 + 40);
            }
        }
    }

    public void move() {
        velocityY += gravity; // ปรับความเร็วของดิโนเสาร์ตามแรงโน้มถ่วง
        dinosaur.y += velocityY;

        if (dinosaur.y > dinosaurY) { // รีเซ็ตความเร็วเมื่อดิโนเสาร์ตกถึงพื้น
            dinosaur.y = dinosaurY;
            velocityY = 0;
            dinosaur.img = Rockmanrun;
        }

        if (score >= 1500) {
            velocityX = -10; // ปรับความเร็วของหินตามคะแนน
        } else if (score >= 1000) {
            velocityX = -10;
        }

        for (Block cactus : cactusArray) { // เคลื่อนย้ายหิน
            cactus.x += velocityX;

            if (collision(dinosaur, cactus)) { // ตรวจสอบการชนกับหิน
                health--;
                cactusArray.remove(cactus);
                break;
            }
        }

        if (health <= 0) { // ตรวจสอบพลังชีวิต ถ้าพลังหมดจะจบเกม
            gameOver = true;
            dinosaur.img = Rockmanrun;
            placeCactusTimer.stop();
            gameLoop.stop();
            restartButton.setVisible(true); // แสดงปุ่ม Restart เมื่อเกมจบลง
        }

        score++; // เพิ่มคะแนน
    }

    boolean collision(Block a, Block b) { // ฟังก์ชันตรวจสอบการชนกันระหว่างดิโนเสาร์กับหิน
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move(); // เรียกใช้ฟังก์ชัน move() เพื่อขยับตำแหน่งวัตถุต่างๆ
        repaint();
        if (!gameOver) {
            elapsedTime++; // เพิ่มเวลา
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) { // ทำการกระโดดเมื่อกด space
            if (dinosaur.y == dinosaurY) {
                velocityY = -17;
                dinosaur.img = Rocman2;
            }

            if (gameOver) {
                startGame(); // เริ่มเกมใหม่ถ้ากด space เมื่อเกมจบแล้ว
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    private String formatElapsedTime(int totalSeconds) { // ฟังก์ชันแปลงเวลาเป็นนาทีและวินาที
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
