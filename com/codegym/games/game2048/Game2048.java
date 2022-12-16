package com.codegym.games.game2048;

import com.codegym.engine.cell.*;

public class Game2048 extends Game {
    
    private static final int SIDE = 4;
    private static final int GOAL_VALUE = 2048;
    
    private int score;
    private boolean isGameStopped = false;
    
    private int[][] gameField; // for storing the current state
    
    private void createGame() {
        score = 0;
        setScore(score);
        gameField = new int[SIDE][SIDE];
        createNewNumber();
        createNewNumber();
    }
    
    private void drawScene() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellColoredNumber(j, i, gameField[i][j]);
            }
        }
    }
    
    private void createNewNumber() {
        if (getMaxTileValue() == GOAL_VALUE) {
            win();
            return;
        }
        
        int x, y;
        
        do {
            x = getRandomNumber(SIDE);
            y = getRandomNumber(SIDE);
        } while (gameField[x][y] != 0);
        
        int r = getRandomNumber(10);
        if (r == 9) {
            gameField[x][y] = 4;
        } else {
            gameField[x][y] = 2;
        }
    }
    
    private Color getColorByValue(int value) {
        Color color = Color.WHITE;
        
        switch (value) {
            case 2    : color = Color.BLUE; break;
            case 4    : color = Color.PURPLE; break;
            case 8    : color = Color.PINK; break;
            case 16   : color = Color.ORANGE; break;
            case 32   : color = Color.LIGHTBLUE; break;
            case 64   : color = Color.RED; break;
            case 128  : color = Color.TURQUOISE; break;
            case 256  : color = Color.GREY; break;
            case 512  : color = Color.MAGENTA; break;
            case 1024 : color = Color.GREEN; break;
            case 2048 : color = Color.VIOLET; break;
        }
        
        return color;
    }
    
    private void setCellColoredNumber(int x, int y, int value) {
        Color color = getColorByValue(value);
        String val = value == 0 ? "" : Integer.toString(value);
        setCellValueEx(x, y, color, val);
    }
    
    private boolean compressRow(int[] row) {
        int[] newRow = new int[SIDE];
        int k = 0;
        boolean result = false;
        
        for (int i = 0; i < SIDE; i++) {
            if (row[i] != 0) {
                newRow[k++] = row[i];
            }
        }
        
        for (int i = 0; i < SIDE; i++) {
            if (row[i] != newRow[i]) {
                result = true;
                row[i] = newRow[i];
            }
        }
        
        return result;
    }
    
    private boolean mergeRow(int[] row) {
        boolean result = false;
        
        for (int i = 0; i < SIDE - 1; i++) {
            if (row[i] != 0 && row[i] == row[i + 1]) {
                row[i++] *= 2;
                row[i] = 0;
                score += row[i - 1];
                result = true;
                setScore(score);
            }
        }
        
        return result;
    }
    
    private void moveLeft() {
        boolean movement = false;
        
        for (int i = 0; i < SIDE; i++) {
            movement = compressRow(gameField[i]) || movement;
            movement = mergeRow(gameField[i]) || movement;
            movement = compressRow(gameField[i]) || movement;
        }
        
        if (movement) {
            createNewNumber();
        }
    }
    
    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }
    
    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }
    
    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }
    
    private void rotateClockwise() {
        int[][] temp = new int[SIDE][SIDE];
        
        for (int i = 0; i < SIDE; i++) {
            int col = SIDE - 1 - i;
            for (int j = 0; j < SIDE; j++) {
                temp[j][col] = gameField[i][j];
            }
        }
        
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                gameField[i][j] = temp[i][j];
            }
        }
    }
    
    private int getMaxTileValue() {
        int max = 0;
        
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (max < gameField[i][j]) {
                    max = gameField[i][j];
                }
            }
        }
        
        return max;
    }
    
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.GREEN, "Congrats! You won!", Color.WHITE, 70);
    }
    
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.RED, "Game over! Press SPACE to restart!", Color.WHITE, 25);
    }
    
    private int numberOfZeroElements() {
        int number = 0;
        
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] == 0) {
                    number++;
                }
            }
        }
        
        return number;
    }
    
    private boolean hasAdjacentCells() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (j + 1 < SIDE && gameField[i][j] == gameField[i][j + 1]) {
                    return true;
                }
                
                if (i + 1 < SIDE && gameField[i][j] == gameField[i + 1][j]) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean canUserMove() {
        if (numberOfZeroElements() > 0) {
            return true;
        }
        
        if (hasAdjacentCells()) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }
    
    @Override
    public void onKeyPress(Key key) {
        if (isGameStopped) {
            if (key == Key.SPACE) {
                isGameStopped = false;
                createGame();
                drawScene();
                return;
            }
            
        } else {
            
            if (!canUserMove()) {
                gameOver();
                return;
            }
            
            if (key == Key.LEFT) {
                moveLeft();
                drawScene();
            } else if (key == Key.RIGHT) {
                moveRight();
                drawScene();
            } else if (key == Key.UP) {
                moveUp();
                drawScene();
            } else if (key == Key.DOWN) {
                moveDown();
                drawScene();
            }
        }
        
    }
    
}
