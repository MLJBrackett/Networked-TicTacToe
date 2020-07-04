import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class TicTacToeClient extends JFrame {
    private int width;
    private int height;
    private JFrame contentPane = new JFrame();
    private JPanel buttonArea = new JPanel();
    private JTextArea message;
    private JButton[] buttons = new JButton[9];
    private int playerID;
    private int otherPlayer;
    private int maxTurns;
    private int turnsMade;

    private boolean buttonEnabled;

    private ClientSideConnection csc;

    public TicTacToeClient(int w,int h) {
        width=w;
        height=h;
        message= new JTextArea();
        buttons[0]= new JButton("1");
        buttons[1]= new JButton("2");
        buttons[2]= new JButton("3");
        buttons[3]= new JButton("4");
        buttons[4]= new JButton("5");
        buttons[5]= new JButton("6");
        buttons[6]= new JButton("7");
        buttons[7]= new JButton("8");
        buttons[8]= new JButton("9");
        turnsMade=0;
    }

    public void setUpGUI(){
        contentPane.setSize(width,height);
        contentPane.setTitle("Player #"+playerID);
        contentPane.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane.setResizable(true);
        contentPane.setLayout(new GridLayout(1,2));
        buttonArea.setLayout(new GridLayout(3,3));
        contentPane.add(message);
        contentPane.add(buttonArea);
        message.setText("Creating a simple tictactoe");
        message.setWrapStyleWord(true);
        message.setLineWrap(true);
        message.setEditable(false);
        buttonArea.add(buttons[0]);
        buttonArea.add(buttons[1]);
        buttonArea.add(buttons[2]);
        buttonArea.add(buttons[3]);
        buttonArea.add(buttons[4]);
        buttonArea.add(buttons[5]);
        buttonArea.add(buttons[6]);
        buttonArea.add(buttons[7]);
        buttonArea.add(buttons[8]);
        if(playerID==1){
            message.setText("You are player X. You go first.\nClick on one of the buttons to turn it into an X");
            otherPlayer=2;
            buttonEnabled=true;
        }else{
            message.setText("You are player O. Wait for your turn. \nClick on one of the buttons to turn it into an O");
            otherPlayer=1;
            buttonEnabled=false;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    updateTurn();
                }
            });
            t.start();
        }
        toggleButtons();
        contentPane.setVisible(true);
    }

    public void connectToServer(){
        csc = new ClientSideConnection();
    }

    public void setUpButtons(){
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JButton b = (JButton) actionEvent.getSource();
                int bNum = Integer.parseInt(b.getText());
                message.setText("You clicked button number: " +bNum +" Waiting for input from other player...");
                if(playerID==1){
                    b.setText("X");
                }else{
                    b.setText("O");
                }
                turnsMade++;
                System.out.println("Turns made: "+ turnsMade);
                if(playerID==1&&turnsMade==5){
                    message.setText("TIE");
                }else if(playerID==2&&turnsMade==4){
                    message.setText("TIE");
                }
                buttonEnabled=false;
                toggleButtons();
                csc.sendButtonNum(bNum);
                if(playerID==2 && turnsMade>=3){
                    checkWinner();
                }
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateTurn();
                    }
                });
                t.start();
            }
        };
        buttons[0].addActionListener(al);
        buttons[1].addActionListener(al);
        buttons[2].addActionListener(al);
        buttons[3].addActionListener(al);
        buttons[4].addActionListener(al);
        buttons[5].addActionListener(al);
        buttons[6].addActionListener(al);
        buttons[7].addActionListener(al);
        buttons[8].addActionListener(al);
    }

    public void toggleButtons(){
        for(JButton b: buttons){
            if(b.getText().equals("X") || b.getText().equals("O")){
                b.setEnabled(false);
            }else{
                b.setEnabled(buttonEnabled);
            }
        }
    }

    public void updateTurn(){
        int n = csc.recieveButtonNum();
        message.setText("Other player clicked "+n+" Your turn");
        if(playerID==1&&turnsMade==5){
            message.setText("TIE");
        }else if(playerID==2&&turnsMade==4){
            message.setText("TIE");
        }else if(playerID==1 && turnsMade>=3){
            checkWinner();
        }
        buttonEnabled = true;
        toggleButtons();
    }

    //I was going to implement the MAGIC SQUARES algorithm but I have many other assignments to do so I don't have the time
    private void checkWinner(){
        buttonEnabled=false;
        int counter=0;
        if(playerID==1){
            if(buttons[0].getText().equals("X")&&buttons[1].getText().equals("X")&&buttons[2].getText().equals("X")||
               buttons[0].getText().equals("X")&&buttons[3].getText().equals("X")&&buttons[6].getText().equals("X")||
               buttons[0].getText().equals("X")&&buttons[4].getText().equals("X")&&buttons[8].getText().equals("X")||
               buttons[1].getText().equals("X")&&buttons[4].getText().equals("X")&&buttons[7].getText().equals("X")||
               buttons[2].getText().equals("X")&&buttons[5].getText().equals("X")&&buttons[8].getText().equals("X")||
               buttons[2].getText().equals("X")&&buttons[4].getText().equals("X")&&buttons[6].getText().equals("X")||
               buttons[3].getText().equals("X")&&buttons[4].getText().equals("X")&&buttons[5].getText().equals("X")||
               buttons[6].getText().equals("X")&&buttons[7].getText().equals("X")&&buttons[8].getText().equals("X")){
                message.setText("You won");
                csc.closeConnection();
            }else if(buttons[0].getText().equals("O")&&buttons[1].getText().equals("O")&&buttons[2].getText().equals("O")||
                    buttons[0].getText().equals("O")&&buttons[3].getText().equals("O")&&buttons[6].getText().equals("O")||
                    buttons[0].getText().equals("O")&&buttons[4].getText().equals("O")&&buttons[8].getText().equals("O")||
                    buttons[1].getText().equals("O")&&buttons[4].getText().equals("O")&&buttons[7].getText().equals("O")||
                    buttons[2].getText().equals("O")&&buttons[5].getText().equals("O")&&buttons[8].getText().equals("O")||
                    buttons[2].getText().equals("O")&&buttons[4].getText().equals("O")&&buttons[6].getText().equals("O")||
                    buttons[3].getText().equals("O")&&buttons[4].getText().equals("O")&&buttons[5].getText().equals("O")||
                    buttons[6].getText().equals("O")&&buttons[7].getText().equals("O")&&buttons[8].getText().equals("O")){
                message.setText("You Lost");
                csc.closeConnection();
            }
        }else if(playerID==2){
            if(buttons[0].getText().equals("O")&&buttons[1].getText().equals("O")&&buttons[2].getText().equals("O")||
               buttons[0].getText().equals("O")&&buttons[3].getText().equals("O")&&buttons[6].getText().equals("O")||
               buttons[0].getText().equals("O")&&buttons[4].getText().equals("O")&&buttons[8].getText().equals("O")||
               buttons[1].getText().equals("O")&&buttons[4].getText().equals("O")&&buttons[7].getText().equals("O")||
               buttons[2].getText().equals("O")&&buttons[5].getText().equals("O")&&buttons[8].getText().equals("O")||
               buttons[2].getText().equals("O")&&buttons[4].getText().equals("O")&&buttons[6].getText().equals("O")||
               buttons[3].getText().equals("O")&&buttons[4].getText().equals("O")&&buttons[5].getText().equals("O")||
               buttons[6].getText().equals("O")&&buttons[7].getText().equals("O")&&buttons[8].getText().equals("O")){
                message.setText("You won");
                csc.closeConnection();
            }else if(buttons[0].getText().equals("X")&&buttons[1].getText().equals("X")&&buttons[2].getText().equals("X")||
                    buttons[0].getText().equals("X")&&buttons[3].getText().equals("X")&&buttons[6].getText().equals("X")||
                    buttons[0].getText().equals("X")&&buttons[4].getText().equals("X")&&buttons[8].getText().equals("X")||
                    buttons[1].getText().equals("X")&&buttons[4].getText().equals("X")&&buttons[7].getText().equals("X")||
                    buttons[2].getText().equals("X")&&buttons[5].getText().equals("X")&&buttons[8].getText().equals("X")||
                    buttons[2].getText().equals("X")&&buttons[4].getText().equals("X")&&buttons[6].getText().equals("X")||
                    buttons[3].getText().equals("X")&&buttons[4].getText().equals("X")&&buttons[5].getText().equals("X")||
                    buttons[6].getText().equals("X")&&buttons[7].getText().equals("X")&&buttons[8].getText().equals("X")){
                message.setText("You Lost");
                csc.closeConnection();
            }
        }
    }

    private class ClientSideConnection{
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection(){
            System.out.println("Client");
            try{
                socket = new Socket("localhost",8000);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt();
                maxTurns = dataIn.readInt();
                System.out.println("maxTurns: "+maxTurns);
                System.out.println("Connected to server as player: "+playerID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void sendButtonNum(int n){
            try{
                dataOut.writeInt(n);
                dataOut.flush();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        public int recieveButtonNum(){
            int n = -1;
            try{
                n = dataIn.readInt();
                if(playerID==1){
                    buttons[n-1].setText("O");
                }else{
                    buttons[n-1].setText("X");
                }
                buttons[n-1].setEnabled(false);
                System.out.println("Player number: "+otherPlayer+" clicked button: "+n);
            }catch(IOException e){
                e.printStackTrace();
            }
            return n;
        }
        public void closeConnection(){
            try{
                socket.close();
                System.out.println("Connection Closed");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TicTacToeClient p = new TicTacToeClient(800,400);
        p.connectToServer();
        p.setUpGUI();
        p.setUpButtons();
    }
}
