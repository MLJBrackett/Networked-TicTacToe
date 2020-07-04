import java.io.*;
import java.net.*;

public class TicTacToeServer{

    private ServerSocket ss;
    private int numPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private int turnsMade;
    private int maxTurns;
    private int player1ButtonNum;
    private int player2ButtonNum;


    public TicTacToeServer() {
        System.out.println("Game Server");
        numPlayers=0;
        turnsMade=0;
        maxTurns=10;
        try{
            ss = new ServerSocket(8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectClient(){
        try{
            System.out.println("Waiting for connections");
            while (numPlayers<2){
                Socket s = ss.accept();
                numPlayers++;
                System.out.println("Player #"+numPlayers+" has connected");
                ServerSideConnection ssc = new ServerSideConnection(s, numPlayers);
                if(numPlayers==1){
                    player1=ssc;
                }else{
                    player2=ssc;
                }
                Thread t = new Thread(ssc);
                t.start();
            }
            System.out.println("Two players have joined. No longer accepting connections");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerSideConnection implements Runnable{
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;

        public ServerSideConnection(Socket s, int id){
            socket = s;
            playerID =  id;
            try{
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try{
                dataOut.writeInt(playerID);
                dataOut.writeInt(maxTurns);
                dataOut.flush();
                while(true){
                    if(playerID==1){
                        player1ButtonNum = dataIn.readInt();
                        System.out.println("Player 1 clicked button number: "+player1ButtonNum);
                        player2.sendButtonNum(player1ButtonNum);
                    }else{
                        player2ButtonNum = dataIn.readInt();
                        System.out.println("Player 2 clicked button number: "+player2ButtonNum);
                        player1.sendButtonNum(player2ButtonNum);
                    }
                    turnsMade++;
                    if(turnsMade==maxTurns){
                        System.out.println("Max turns has been reached");
                        break;
                    }
                }
                player1.closeConnection();
                player2.closeConnection();
            }catch (IOException e){
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
        public void closeConnection(){
            try{
                socket.close();
                System.out.println("Connection closed");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TicTacToeServer ttts = new TicTacToeServer();
        ttts.connectClient();
    }

}
