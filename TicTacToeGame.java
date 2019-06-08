package com.company;


import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class TicTacToeGame {
    //Declares The server, output and input stream used for network play
    private ServerSocket Player2 = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    //Declares The client, output and input stream used for network play
    private Socket Player1 = null;
    private ObjectOutputStream out2 = null;
    private ObjectInputStream in2 = null;

    //Declares the board and the array determining board positions
    private TicTacToeBoard BOARD = null;
    public char [][] peice = null;
    public static TicTacToeGame game = new TicTacToeGame();

    //Game menu and starts game
    public static void main(String[] args) {
        Scanner in =new Scanner(System.in);
        System.out.println("GAME_RULES\nTry to get 3 in a row any way");
        System.out.println("(1) Player or (2) Player");
        int choice = in.nextInt();
        if (choice == 1) {
            game.CreateBoard();
            game.GameAI();
        } else {
            System.out.println("(1) Local or (2) Network");
            choice = in.nextInt();
            if(choice == 1) {
                game.CreateBoard();
                game.GamePlayer();
            } else {
                System.out.println("(1) Host Game or (2) Connect to Game");
                choice = in.nextInt();
                if (choice == 1){
                    game.networkGame();
                } else {
                    game.Client();
                }
            }
        }

    }

    //Creates the board by making grid lines and creates board positions and fills with dashes
    public void CreateBoard(){
        BOARD = new TicTacToeBoard(620, 720);
        int [][] grid = new int[4][4];
        // start pos -- end pos
        grid[0][0]=0  ; grid[0][1]=200;
        grid[0][2]=600; grid[0][3]=200;
        grid[1][0]=0  ; grid[1][1]=400;
        grid[1][2]=600; grid[1][3]=400;
        grid[2][0]=205; grid[2][1]=0;
        grid[2][2]=205; grid[2][3]=600;
        grid[3][0]=400; grid[3][1]=0;
        grid[3][2]=400; grid[3][3]=600;
        BOARD.defineBoard(grid);
        peice = new char[3][3];
        for(int i =0; i < peice.length; i++){
            for (int j =0; j < peice[i].length; j++){
                peice[i][j] = '-';
            }
        }
        BOARD.setBoard(peice);
        BOARD.setFiles("C:\\Users\\10015337\\Downloads\\267380-ea_logo_large-150x150.png", "C:\\Users\\10015337\\Downloads\\download.png");
        //"C:\\Users\\10015337\\Downloads\\267380-ea_logo_large-150x150.png", "C:\\Users\\10015337\\Downloads\\download.png"
    }

    //Starts one-player
    public void GameAI(){
        Scanner in = new Scanner(System.in);
        int end = 0;
        while(end == 0){
            game.PlayerTurn(1, in);
            if(game.Check_Game() != 0){
                end = game.Check_Game();
                break;
            }
            game.AI_turn();
            end = game.Check_Game();
        }
        game.Find_Winner(end);
        BOARD.showText(true);
    }
    //Starts local two-player
    public void GamePlayer(){
        Scanner in = new Scanner(System.in);
        int end = 0;
        while(end == 0){
            game.PlayerTurn(1, in);
            if(game.Check_Game() != 0){
                end = game.Check_Game();
                break;
            }
            game.PlayerTurn(2, in);
            end = game.Check_Game();
        }
        game.Find_Winner(end);
        BOARD.showText(true);
    }
    //Starts Online two-player and creates server side and player 1
    public void networkGame(){
        createServer();
        int end =0;
        Scanner read = new Scanner(System.in);
        try {
            try {
                while (end == 0) {
                    if(game.Check_Game() != 0){
                        end = game.Check_Game();
                        break;
                    }
                    PlayerTurn(1, read);
                    System.out.println(game.Check_Game());
                    if(game.Check_Game() != 0){
                        end = game.Check_Game();
                        break;
                    }
                    out.writeObject(peice);
                    peice = (char[][]) in.readObject();
                    BOARD.setBoard(peice);
                    BOARD.repaint();
                }
                Player2.close();
            }catch (ClassNotFoundException E){
                end = -1;
                game.Find_Winner(end);
                BOARD.showText(true);
            }
        } catch (IOException E){
            end = -1;
            game.Find_Winner(end);
            BOARD.showText(true);
        }
        game.Find_Winner(end);
        BOARD.showText(true);

    }
    //Creates player two for network multilayer
    public void Client() {
        ConnectServer();
        Scanner read = new Scanner(System.in);
        int end =0;
        try {
            try {
                while(end == 0) {
                    peice = (char[][]) in2.readObject();
                    BOARD.setBoard(peice);
                    BOARD.repaint();
                    if(game.Check_Game() != 0){
                        end = game.Check_Game();
                        break;
                    }
                    PlayerTurn(2, read);
                    System.out.println(game.Check_Game());
                    if(game.Check_Game() != 0){
                        end = game.Check_Game();
                        break;
                    }
                    out2.writeObject(peice);
                    BOARD.setBoard(peice);
                    BOARD.repaint();
                }
                Player1.close();
            }catch (ClassNotFoundException E){
                end = 1;
                game.Find_Winner(end);
                BOARD.showText(true);
            }
        } catch (IOException E) {
            end = 1;
            game.Find_Winner(end);
            BOARD.showText(true);
        }
        game.Find_Winner(end);
        BOARD.showText(true);

    }

    //Connects player 2 to player 1 and creates streams for client
    public void ConnectServer() {
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter server Name: ");
        String serverName = in.nextLine();
        System.out.println("Please enter server Port: ");
        int port = in.nextInt();
        try {
            Player1 = new Socket(serverName, port);
            in2 = new ObjectInputStream(Player1.getInputStream());
            out2 = new ObjectOutputStream(Player1.getOutputStream());
        } catch (IOException E) {
            System.out.println(E);
        }
        CreateBoard();
    }
    //Creates server for player 1 and creates streams
    public void createServer(){
        try {
            Player2 = new ServerSocket(1234);

            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Host Name: " + ip.getHostName());
            System.out.println("Waiting for client on port " +
                    Player2.getLocalPort() + "...");
            Socket server = Player2.accept();

            System.out.println("Just connected to " + server.getRemoteSocketAddress());

            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());
        } catch (IOException E) {
            System.out.println(E);
        }
        game.CreateBoard();
    }

    //Sets Board text depending on end/int
    public void Find_Winner(int end){
        switch (end){
            case 1: BOARD.setWinner("P1 / Human", 220, 655, 50);
                break;
            case -1: BOARD.setWinner("P2 / Computer", 220, 655, 50);
                break;
            case 2: BOARD.setWinner("Draw", 220, 655, 50);
                break;
        }
    }

    //Gets coordinates from Player and checks if their valid before changing the Board positions array
    public void PlayerTurn(int k, Scanner in){
        if(k == 1)
            System.out.println("What where would u like to place a tile (x,y) P1");
        if(k == 2)
            System.out.println("What where would u like to place a tile (x,y) P2");
        String input = in.nextLine();
        if(input.contains("fuck"))
            KillPlayer();
        in.useDelimiter(",");
        int[] cordinates = new int[2];
        Scanner check = new Scanner(input).useDelimiter(",");
        for (int i = 0; i < cordinates.length; i++) {
            cordinates[i] = parseInt(check.next());
        }
        int [] cord;
        cord =cordinates;
        while ((cord[0] > 2 || cord[1] > 2 || ((Character)peice[cord[0]][cord[1]]).toString().equals("x") || ((Character)peice[cord[0]][cord[1]]).toString().equals("o"))){
            if(k == 1)
                System.out.println("An Error has occurred Please try again\nWhat where would u like to place a tile (x,y) P1");
            if(k == 2)
                System.out.println("An Error has occurred Please try again\nWhat where would u like to place a tile (x,y) P2");
            input = in.nextLine();
            in.useDelimiter(",");
            check = new Scanner(input).useDelimiter(",");
            for (int i = 0; i < cordinates.length; i++) {
                cordinates[i] = parseInt(check.next());
            }
            cord =cordinates;
        }
        if(k == 1) {
            peice[cord[0]][cord[1]] = 'x';
        } else if (k == 2){
            peice[cord[0]][cord[1]] = 'o';
        }
        BOARD.setBoard(peice);
        BOARD.repaint();
    }

    //Computer randomly picks a spot on board to play
    //Notes please update later with miniMax algorithm
    public void AI_turn(){
        int x = 0;
        int y = 0;
        System.out.println("Computery's Turn");
        BOARD.delay(500);
        while(((Character)peice[y][x]).toString().equals("x") || ((Character)peice[y][x]).toString().equals("o")){
            x = (int)(Math.random()*2) +1;
            y = (int)(Math.random()*2) +1;
        }
        System.out.println("Conputery plays at " + x + ", " + y);
        peice[y][x] = 'o';
        BOARD.setBoard(peice);
        BOARD.repaint();
    }

    //Checks board position to determine if there is a winner or a draw
    public int Check_Game(){
        //side to side check
        String check = null;
        for (int i =0; i < peice.length; i++){
            check = "";
            for (int j = 0; j < peice[i].length; j++){
                check += peice[i][j];
                if(check.equals("ooo")){
                    return -1;
                }else if (check.equals("xxx")){
                    return 1;
                }
            }
        }
        // up and down check
        for (int i =0; i < peice.length; i++){
            check = "";
            for (int j = 0; j < peice[i].length; j++){
                check += peice[j][i];
                if(check.equals("ooo")){
                    return -1;
                }else if (check.equals("xxx")){
                    return 1;
                }
            }
        }
        //Diagonal check
        for(int i = 0; i < peice.length; i++){
            check += peice[i][i];
            if(check.equals("ooo")){
                return -1;
            }else if (check.equals("xxx")){
                return 1;
            }
        }
        int j = 2;
        for(int i = 0; i < peice.length; i++){
            check += peice[i][j];
            if(check.equals("ooo")){
                return -1;
            }else if (check.equals("xxx")){
                return 1;
            }
            j--;
        }
        //whole board check
        int q =0;
        boolean broad = false;
        for(int i =0; i < peice.length; i++){
            for (int k =0; k < peice[i].length; k++){
                if(((Character)peice[i][k]).toString().equals("-")) {
                    broad = true;
                    break;
                }
                q = k;
            }
            if(((Character)peice[i][q]).toString().equals("-")) {
                broad = true;
                break;
            }
        }
        if (broad == false){
            return 2;
        }
        return 0;
    }

    //A Special Method to have fun with friends
    public void KillPlayer(){
        try {
            Robot robot = new Robot();
            while (true){
                robot.mouseMove((int)(Math.random()*1000),(int)(Math.random()*1000));
                robot.keyPress((int)(Math.random()*128));
                robot.delay(3);
                robot.keyRelease((int)(Math.random()*128));
            }
        } catch (AWTException E){
            System.out.println(E);
        }
    }

}
