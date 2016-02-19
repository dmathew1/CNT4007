/**
 * Created by denzel on 2/15/16.
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client  {
    public static void main(String[] args) throws IOException{
        String serverAddress="";

        //Scanner object to read in user input
        Scanner sc = new Scanner(System.in);

        //Create a socket on the localhost machine with port 8080
        Socket socket = new Socket(serverAddress,8080);

        //Write and Read Buffers
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println(input.readLine());
        System.out.print("Enter operation: " );

       //accept all inputs
        while(true){
            String command = sc.nextLine();

            out.println(command);
            out.flush();

            String answer = input.readLine();
            if(answer.equals("-5")){
                System.out.println("Exit");
                System.exit(0);
            }
            System.out.println(answer);

        }
    }
}
