/**
 * Created by denzel on 2/15/16.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class Client  {
    public static void main(String[] args) throws IOException{
        Scanner sc = new Scanner(System.in);

        //Create a socket on the localhost machine with port 8080
        Socket socket = new Socket("localhost",8080);

        PrintWriter data = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.print("Enter operation: " );

        //operation
        String operation = sc.next();

        //operand 1
        int a = sc.nextInt();

        //operand 2
        int b = sc.nextInt();

        //Send it to the network
        data.println(operation);
        data.println(a);
        data.println(b);

        data.flush();

        String answer = input.readLine();
        //Print answer
        System.out.println(answer);
    }
}
