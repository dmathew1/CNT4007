
import jdk.internal.org.objectweb.asm.Handle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.RunnableFuture;

/**
 * Created by denzel on 2/15/16.
 */

//Need to make server multi-threaded to handle many clients.
public class Server {


    public static class Handler implements Runnable{
        //Listen to server by creating a socket based on the connection accepted
        private Socket socket;

        public Handler(Socket socket){
            this.socket = socket;
            System.out.println("Socket: " + socket.getInetAddress() + " created.");
            run();
        }

        //What this thread should do once spawned.
        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));


                //Read in the input
                String operation = in.readLine();
                String a = in.readLine();
                String b = in.readLine();

                if(operation.equals("add")) {
                    int answer = Integer.parseInt(a) + Integer.parseInt(b);
                    out.println(answer);
                    out.flush();
                }else if(operation.equals("subtract")){
                    int answer = Integer.parseInt(a) - Integer.parseInt(b);
                    out.println(answer);
                    out.flush();
                }else if(operation.equals("multiply")){
                    int answer = Integer.parseInt(a) * Integer.parseInt(b);
                    out.println(answer);
                    out.flush();
                }else if(operation.equals("divide")){
                    double answer = Double.parseDouble(a) / Double.parseDouble(b);
                    out.println(answer);
                    out.flush();
                }else{
                    out.println("Error parsing");
                }


            }catch(IOException e){
                System.out.println(e);
            }
        }
    }
    //main thread to create the sockets
    public static void main(String[] args) throws IOException{

        //Create server socket here
        ServerSocket server = new ServerSocket(8080);

        //After opening the connection, accept all incoming clients
        try{
            while(true){
                //create new Handlers
                Handler client = new Handler(server.accept());
            }
        }finally {
            server.close();
        }
    }
}
