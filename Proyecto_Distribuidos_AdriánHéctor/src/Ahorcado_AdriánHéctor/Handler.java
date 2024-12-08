package Ahorcado_AdriánHéctor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Handler implements Runnable {
    private Socket socket;
    private CyclicBarrier startBarrier;
    private Ahorcado juego;
    private int jugadorId;
    private CyclicBarrier turnBarrier;

    public Handler(Socket socket, CyclicBarrier startBarrier, Ahorcado juego, int jugadorId, CyclicBarrier turnBarrier) {
        this.socket = socket;
        this.startBarrier = startBarrier;
        this.juego = juego;
        this.jugadorId = jugadorId;
        this.turnBarrier = turnBarrier;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Esperar a que todos los jugadores estén listos
            startBarrier.await();
            
            out.println("¡Empieza la partida!");
            out.println("Eres el jugador " + jugadorId);
           
            System.out.println("La palabra es " + this.juego.getPalabraAdivinar());
            
            while (!juego.estaCompleto() && this.juego.tieneIntentos()) {
                synchronized (juego) {
                   //System.out.println(juego.anuncioTurno());
                   out.write(juego.anuncioDatos());
                   out.flush();
                   out.write("\r\n");
                   out.flush();
                   out.write(juego.anuncioTurno() + "\r\n");
                   out.flush();
                   
                    if (juego.anuncioTurno().contains("jugador " + jugadorId)) {
                        out.println("¡Es tu turno!\r\n");
                        out.flush();
                        
                        String input = in.readLine();

                        if (input != null && input.length() == 1) {
                            juego.intentarLetra(input.charAt(0));
                        } else {
                            out.println("Entrada inválida. Intenta de nuevo.\r\n");
                            out.flush();
                        }
                        //System.out.println("Turno: " + juego.getTurno());
                        juego.aumentarTurno();
                        //System.out.println("Turno:" + juego.getTurno());
                    } else {
                        out.println("Espera tu turno...");
                    }
                }

                // Sincronizar turnos
                turnBarrier.await();
            }

            out.println("¡El juego ha terminado!");
            
            if(this.juego.tieneIntentos()) {
            	out.println("El ganador es el jugador " + (juego.getTurno() + 3) + "!");
            	out.println("Teníais " + this.juego.getIntentosRestantes() + " intentos restantes");
            }
            
            out.println("La palabra era " + this.juego.getPalabraAdivinar());
            out.println("¡Adiós!");
            
            this.socket.shutdownOutput();

        } catch (IOException | InterruptedException | BrokenBarrierException e) {
            System.err.println("Error en el Handler del jugador " + jugadorId);
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error cerrando socket para el jugador " + jugadorId);
            }
        }
    }
}
