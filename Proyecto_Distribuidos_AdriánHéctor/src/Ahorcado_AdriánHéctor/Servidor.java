package Ahorcado_AdriánHéctor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    private final int PORT = 9000;
    private static final int JUGADORES_POR_SALA = 3;

    public static void main(String[] args) {
        new Servidor().start();
    }

    public void start() {
        System.out.println("Servidor de Ahorcado iniciado...");
        ExecutorService pool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (!Thread.interrupted()) {
                System.out.println("Esperando jugadores...");

                CyclicBarrier startBarrier = new CyclicBarrier(JUGADORES_POR_SALA + 1);
                CyclicBarrier turnBarrier = new CyclicBarrier(JUGADORES_POR_SALA);
                Ahorcado juego = new Ahorcado();

                Socket[] jugadores = new Socket[JUGADORES_POR_SALA];
                for (int i = 0; i < JUGADORES_POR_SALA; i++) {
                    jugadores[i] = serverSocket.accept();
                    System.out.println("Jugador " + (i + 1) + " conectado.");
                }

                for (int i = 0; i < JUGADORES_POR_SALA; i++) {
                    pool.execute(new Handler(jugadores[i], startBarrier, juego, (i + 1), turnBarrier));
                }

                startBarrier.await();
                System.out.println("¡Partida iniciada!");
            }
        } catch (Exception e) {
            System.err.println("Error en el servidor:");
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
