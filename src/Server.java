import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


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

        public boolean isNumeric(String s) {
            return s.matches("[-+]?\\d*\\.?\\d+");
        }

        //What this thread should do once spawned.
        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

                //Welcome the new users
                out.println("Greetings Traveller!");

                //Read in the input
                main: while(true) {
                    String input = in.readLine();
                    String[] test = input.split(" ");
                    int[] operands = new int[test.length-1];
                    int errorCode = 0;

                    //Store the operation in here
                    String operation = test[0];

                    /**
                     * -1 = incorrect operation command (misspelled)
                     * -2 = number of inputs is less than two
                     * -3 = number of inputs is greater than four
                     * -4 = one or more inputs contain non-numbers
                     * -5 = exit
                     */
                    if((!operation.equals("add") && !operation.equals("subtract") && !operation.equals("multiply") && !operation.equals("terminate") && !operation.equals("bye"))){
                        errorCode = -1;
                        out.println(errorCode);
                        out.flush();
                        continue main;
                    }else if(operands.length < 2 && !operation.equals("terminate") && !operation.equals("bye")){
                        errorCode = -2;
                        out.println(errorCode);
                        out.flush();
                        continue main;
                    }else if(operands.length > 4){
                        errorCode = -3;
                        out.println(errorCode);
                        out.flush();
                        continue main;
                    }



                    //Create an array of arguments
                    for(int i=0;i<operands.length;i++){
                        try {
                            operands[i] = Integer.parseInt(test[i + 1]);
                        }catch(NumberFormatException e){
                            errorCode = -4;
                            out.println(errorCode);
                            out.flush();
                            continue main;
                        }
                    }


                    //Compute Operations
                    if(operation.equals("add")){
                        int answer = 0;
                        for(int i=0;i<operands.length;i++){
                            answer += operands[i];
                            System.out.println(answer);
                        }
                        out.println(answer);
                        out.flush();
                        answer = 0;

                    }else if(operation.equals("subtract")){
                        int answer = operands[0];
                        for(int i=1;i<operands.length;i++){
                            System.out.println(answer);
                            answer -= operands[i];
                        }
                        out.println(answer);
                        out.flush();
                        answer = 0;

                    }else if(operation.equals("multiply")){
                        int answer = 1;
                        for(int i=0;i<operands.length;i++){
                            System.out.println(answer);
                            answer *= operands[i];
                        }
                        out.println(answer);
                        out.flush();
                        answer = 1;

                    }else if(operation.equals("bye")) {
                        errorCode = -5;
                        out.println(errorCode);
                        out.flush();

                    }else if(operation.equals("terminate")){
                        errorCode = -5;
                        out.println(errorCode);
                        out.flush();
                        socket.close();
                        System.exit(0);
                    }

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
