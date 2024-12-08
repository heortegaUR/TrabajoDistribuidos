package Ahorcado_AdriánHéctor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private static final String HOST = "localhost";
    private static final int PORT = 9000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            System.out.println(in.readLine()); // ¡Empieza la partida!
            System.out.println(in.readLine()); // Eres el jugador N

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);

                if (line.startsWith("¡Es tu turno!")) {
                    String letra;
                    
                    do {
                    	
                        System.out.print("Introduce una letra: ");
                        letra = sc.nextLine();
                        
                    } while (letra.length() != 1);
                    
                    out.println(letra);
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el cliente:");
            e.printStackTrace();
        }
    }
}
