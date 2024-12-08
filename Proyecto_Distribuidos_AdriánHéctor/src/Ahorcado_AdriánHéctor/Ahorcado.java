package Ahorcado_AdriánHéctor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class Ahorcado {
    private static final String[] PALABRAS = {"programacion", "java", "ahorcado", "winamax", "satoru gojo", "internet"};
    private static final int INTENTOS_MAXIMOS = 15;

    private int turnoJugador;
    private String palabraAdivinar;
    private StringBuilder progreso;
    private Set<Character> letrasUsadas;
    private int intentosRestantes;
    //private int turno; variable para contar los turnos?

    public Ahorcado() {

    	this.palabraAdivinar = obtenerPalabraDeAPI();

    	if (this.palabraAdivinar == null || this.palabraAdivinar.isEmpty()) {
    	    Random random = new Random();
    	    this.palabraAdivinar = PALABRAS[random.nextInt(PALABRAS.length)];
    	}

    	this.progreso = new StringBuilder("_ ".repeat(this.palabraAdivinar.length()));
    	this.letrasUsadas = new HashSet<>();
    	this.intentosRestantes = INTENTOS_MAXIMOS;
    	this.turnoJugador = 0;

    }
    
    public int getTurno() {
        return this.turnoJugador;
    }

    public void aumentarTurno() {
        this.turnoJugador = (this.turnoJugador + 1) % 3; // Ciclo entre 0, 1, 2
    }

    public boolean intentarLetra(char letra) {
        if (this.letrasUsadas.contains(letra)) {
            return false;
        }

        this.letrasUsadas.add(letra);

        if (this.palabraAdivinar.indexOf(letra) >= 0) {
        	this.actualizarProgreso(letra);
            return true;
        } else {
        	this.intentosRestantes--;
            return false;
        }
    }

    private void actualizarProgreso(char letra) {
        for (int i = 0; i < this.palabraAdivinar.length(); i++) {
            if (this.palabraAdivinar.charAt(i) == letra) {
            	this.progreso.setCharAt(i * 2, letra);
            }
        }
    }

    public boolean estaCompleto() {
        return (this.progreso.indexOf("_") == -1); //Si se nos acaban los intentos mal
    }

    public boolean tieneIntentos() {
        return this.intentosRestantes > 0;
    }

    public String getProgreso() {
        return this.progreso.toString();
    }

    public String getPalabraAdivinar() {
        return this.palabraAdivinar;
    }

    public int getIntentosRestantes() {
        return this.intentosRestantes;
    }

    public Set<Character> getLetrasUsadas() {
        return this.letrasUsadas;
    }
    
    public String anuncioTurno() {
    	return "Es el turno del jugador " + (this.getTurno() + 1) + ":";
    }
    
    public String anuncioDatos() {
    	StringBuilder datos = new StringBuilder();
    	datos.append("¡Quedan " + this.getIntentosRestantes() + " intentos para completar el juego!\r\n")
    	.append("El progreso es " + this.getProgreso() + "\r\n").append("Las letras que habéis usado son " + this.getLetrasUsadas().toString() + "\r\n")
    	.append("----------------------------------\r\n").append("\r\n");
    	
    	return datos.toString();
    }
    
    private String obtenerPalabraDeAPI() {
        String host = "random-word-api.herokuapp.com";
        String endpoint = "/word?number=1";
        int port = 80;

        try (Socket s = new Socket(host, port);
             PrintWriter out = new PrintWriter(s.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

            out.println("GET " + endpoint + " HTTP/1.1");
            out.println("Host: " + host);
            out.println("Connection: close");
            out.println();
            out.flush();

            String line;
            boolean jsonStarted = false;
            StringBuilder jsonResponse = new StringBuilder();
            while ((line = in.readLine()) != null) {
                if (jsonStarted) {
                    jsonResponse.append(line);
                }
                if (line.isEmpty()) {
                    jsonStarted = true;
                }
            }

            // Procesa JSON
            return jsonResponse.toString().replaceAll("[\\[\\]\"]", "");

        } catch (IOException e) {
            System.err.println("Error al obtener palabra de la API. Usando palabra local.");
            return null;
        }
    }

}
