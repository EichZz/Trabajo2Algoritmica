/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajo2algoritmica;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author juani_000
 */
public class Trabajo2Algoritmica {

//Método auxiliar para obtener la distancia de un ruta.
    public static double getDistanciaTotal(int ruta[], double[][] matdistancias) {

        double distanciaTotal = 0;

        for (int i = 0; i < ruta.length - 1; i++) {

            int ciudadActual = ruta[i];
            int ciudadSiguiente = ruta[i + 1];
            distanciaTotal += matdistancias[ciudadActual][ciudadSiguiente];
        }

        return distanciaTotal;
    }

//Método auxiliar para obtener la Distancia PArcila de una ruta.
    public static double getDistanciaParcial(int ruta[], double[][] matdistancias, int pos) {

        double distanciaParcial = 0;

        for (int i = 0; i < pos; i++) {

            int ciudadActual = ruta[i];
            int ciudadSiguiente = ruta[i + 1];
            distanciaParcial += matdistancias[ciudadActual][ciudadSiguiente];
        }

        return distanciaParcial;
    }

//Método auxiliar para obtener un vector inicial
    public static int[] getVectorInicial(int numeroCiudades) {

        int v[] = new int[numeroCiudades];

        for (int i = 0; i < numeroCiudades; i++) {

            v[i] = i;

        }

        return v;
    }

//Método auxiliar para obtener coordenadas
    public static double[] devolverCoordenadas(String linea) {
        String x = "";
        String y = "";
        double[] coordenadas = new double[2];
        linea += " "; //añadimos un espacio para que se salga del ultimo bucle
        char[] l = linea.toCharArray();
        int i = 0;
        //se leen los primeros espacios en caso de que los haya
        while (l[i] == ' ') {
            i++;
        }
        //se lee el numero de la ciudad, no nos interesa
        while (l[i] != ' ') {
            i++;
        }
        //se leen los espacios entre el numero de ciudad y la coordenada x
        while (l[i] == ' ') {
            i++;
        }
        //se lee la coordenada x
        while (l[i] != ' ') {
            x += l[i];
            i++;
        }
        //se leen los espacios entre la coordenada x y la y
        while (l[i] == ' ') {
            i++;
        }
        //se lee la coordenada y
        while (l[i] != ' ') {
            y += l[i];
            i++;
        }
        coordenadas[0] = Double.parseDouble(x);
        coordenadas[1] = Double.parseDouble(y);

        return coordenadas;
    }

//Método auxiliar para obtener la matriz de distancias
    public static double[][] GenerarMatriz() throws FileNotFoundException, IOException {
        BufferedReader bf = new BufferedReader(new FileReader("vm1084.tsp"));
        String dimension = "";
        while (!dimension.startsWith("DIMENSION")) {
            dimension = bf.readLine();
        }
        int indice = dimension.indexOf(": ");
        dimension = dimension.substring(indice + 1);
        int dim = Integer.parseInt(dimension.trim());

        while (!dimension.startsWith("NODE")) {
            dimension = bf.readLine();
        }

        double[][] ciudades = new double[dim][2];
        for (int i = 0; i < dim; i++) {
            double[] ciudad = devolverCoordenadas(bf.readLine());

            ciudades[i][0] = ciudad[0];
            ciudades[i][1] = ciudad[1];

        }

        double x1, y1, x2, y2;
        double[][] matdistancias = new double[dim][dim];

        for (int i = 0; i < dim; i++) {
            x1 = ciudades[i][0];
            y1 = ciudades[i][1];

            matdistancias[i][i] = 0;

            for (int j = i + 1; j < dim; j++) {
                x2 = ciudades[j][0];
                y2 = ciudades[j][1];

                matdistancias[i][j] = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
                matdistancias[j][i] = matdistancias[i][j];
            }
        }
        return matdistancias;
    }

//Métodos de resolucion con Vuelta Atrás   
    public static int[] resolverVueltaAtras(int minutos) throws IOException {
        double[][] matdistancias = GenerarMatriz();
        int[] rutaMejor = getVectorInicial(matdistancias.length);
        int[] ruta = new int[rutaMejor.length];

        long[] n = {System.currentTimeMillis()};

        vueltaAtras(ruta, rutaMejor, 0, matdistancias, n, n[0] + minutos * 60000);
        return rutaMejor;
    }

//Algoritmo de Vuelta Atrás
    public static void vueltaAtras(int[] ruta, int[] rutaMejor, int pos, double[][] matdistancias, long[] start, long stop) {
        if (pos == ruta.length) {
            if (getDistanciaTotal(rutaMejor, matdistancias) > getDistanciaTotal(ruta, matdistancias)) {
                System.arraycopy(ruta, 0, rutaMejor, 0, ruta.length);
            }
        } else {
            int ciudad = 0;
            while (ciudad < ruta.length && start[0] < stop) {
                if (!contains(ciudad, ruta, pos)) {
                    ruta[pos] = ciudad;
                    if (getDistanciaTotal(rutaMejor, matdistancias) > getDistanciaParcial(ruta, matdistancias, pos)) {
                        vueltaAtras(ruta, rutaMejor, pos + 1, matdistancias, start, stop);
                    }
                }
                ciudad++;
                start[0] = System.currentTimeMillis();
            }
        }
    }

// Algoritmo voraz para iniciar la búsqueda local.
    public static int[] voraz(double[][] matDistancias) {
        int dim = matDistancias[0].length;
        int[] ruta = new int[dim];
        int origen = 0, destino = 1;
        for (int i = 0; i < dim; i++) {
            for (int j = i + 1; j < dim; j++) {
                if (matDistancias[origen][destino] > matDistancias[i][j]) {
                    origen = i;
                    destino = j;
                }
            }
        }
        ruta[0] = origen;
        ruta[1] = destino;

        origen = destino;
        dim--;
        int pos = 2;
        while (dim > 1) {
            destino = obtenerDestino(ruta, pos);
            for (int i = 1; i < dim; i++) {
                if (!contains(i, ruta, pos)) {
                    if (matDistancias[origen][destino] > matDistancias[origen][i]) {
                        destino = i;
                    } else if (matDistancias[origen][destino] == 0) {
                        destino = i;
                    }
                }
            }
            ruta[pos] = destino;
            pos++;

            origen = destino;
            dim--;
        }
        return ruta;
    }

//Algoritmo Búsqueda Local
    public static int[] resolverBusquedaLocal(double[][] matdistancias, int criterioParada) {
        int[] mejorRuta = voraz(matdistancias);
        int[] rutaActual = Arrays.copyOf(mejorRuta, mejorRuta.length);

        for (int i = 0; i < criterioParada; i++) {
            int[] rutaVecina = generaVecino(rutaActual, matdistancias);
            if (acepta(rutaActual, rutaVecina, matdistancias)) {
                System.arraycopy(rutaVecina, 0, rutaActual, 0, rutaActual.length);
            }
            if (getDistanciaTotal(mejorRuta, matdistancias) > getDistanciaTotal(rutaActual, matdistancias)) {
                System.arraycopy(rutaActual, 0, mejorRuta, 0, mejorRuta.length);
            }
        }
        return mejorRuta;
    }

//Método auxiliar contains
    public static boolean contains(int ciudad, int[] ruta, int pos) {
        boolean enc = false;
        int i = 0;
        while (i < pos && !enc) {
            if (ruta[i] == ciudad) {
                enc = true;
            }
            i++;
        }
        return enc;
    }

//Método auxiliar obtener Destino (BL)
    public static int obtenerDestino(int[] ruta, int pos) {
        int i = 0;
        boolean enc = false;
        while (i < ruta.length && !enc) {
            if (!contains(i, ruta, pos)) {
                enc = true;
            } else {
                i++;
            }
        }
        return i;
    }

//Método auxiliar generaVecino (BL)
    public static int[] generaVecino(int[] rutaActual, double[][] matdistancias) {
        int origen = rutaActual[0], destino = rutaActual[1], aux;
        int[] rutaVecina = Arrays.copyOf(rutaActual, rutaActual.length);
        int[] intercambios = new int[(rutaActual.length / 100 + 1) * 2];
        intercambios[0] = 0;
        intercambios[1] = 1;

        for (int j = 0; j < intercambios.length; j = j + 2) {

            for (int i = 1; i < rutaVecina.length - 1; i++) {
                int origenAux = rutaActual[i];
                int destinoAux = rutaActual[i + 1];
                if (matdistancias[origen][destino] < matdistancias[origenAux][destinoAux] && !contains(i, intercambios, j + 1)) {
                    origen = origenAux;
                    destino = destinoAux;
                    intercambios[j] = i;
                    intercambios[j + 1] = i + 1;
                }
            }
        }

        int[] intercAux = Arrays.copyOf(intercambios, intercambios.length);
        for (int i = 0; i < rutaVecina.length; i++) {
            rutaAleatoria(intercAux);
            getDistanciaLocal(intercambios, rutaVecina, matdistancias);
            if (getDistanciaLocal(intercambios, rutaVecina, matdistancias) > getDistanciaLocal(intercAux, rutaVecina, matdistancias)) {
                System.arraycopy(intercAux, 0, intercambios, 0, intercambios.length);
            }
        }

        for (int i = 0; i < intercambios.length - 1; i = i + 2) {
            aux = rutaVecina[intercambios[i]];
            rutaVecina[intercambios[i]] = rutaVecina[intercambios[i + 1]];
            rutaVecina[intercambios[i + 1]] = aux;
        }

        return rutaVecina;
    }

    public static double getDistanciaLocal(int intercambios[], int[] ruta, double[][] matdistancias) {

        double distanciaTotal = 0;

        for (int i = 0; i < intercambios.length - 1; i = i + 2) {

            int ciudadActual = ruta[intercambios[i]];
            int ciudadSiguiente = ruta[intercambios[i + 1]];
            distanciaTotal += matdistancias[ciudadActual][ciudadSiguiente];
        }

        return distanciaTotal;
    }

    public static void rutaAleatoria(int[] rutaActual) {
        Random r = new Random();
        int indiceAleatorio_1, indiceAleatorio_2, aux;

        for (int i = 0; i < rutaActual.length; i++) {
            indiceAleatorio_1 = r.nextInt(rutaActual.length);
            indiceAleatorio_2 = r.nextInt(rutaActual.length);

            aux = rutaActual[indiceAleatorio_1];
            rutaActual[indiceAleatorio_1] = rutaActual[indiceAleatorio_2];
            rutaActual[indiceAleatorio_2] = aux;
        }

    }
//Método auxiliar acepta (BL)

    private static boolean acepta(int[] rutaActual, int[] rutaVecina, double[][] matdistancias) {
        return 0.9 * getDistanciaTotal(rutaVecina, matdistancias) < getDistanciaTotal(rutaActual, matdistancias);
    }

//main
    public static void main(String[] args) throws IOException {
        testBL();
    }

    

    public static void testBL() throws IOException {
        int[] repeticiones = {100, 500, 1000, 2500, 5000};
        double[][] mat = GenerarMatriz();
        int[] resul = new int[mat.length];
        System.out.println("\nResultados de pruebas para algoritmo de búsqueda local:");
        for (int i = 0; i < repeticiones.length; i++) {
            long ini = System.nanoTime();
            for (int j = 0; j< 1;j++){
            resul = resolverBusquedaLocal(mat, repeticiones[i]);
            }
            long fin = System.nanoTime();
            System.out.println("\tPara " + repeticiones[i] + " reps, " + (fin - ini)/1 + "ns, " + getDistanciaTotal(resul, mat));
        }
    }

    public static void testVA() throws IOException {
        int[] minutos = {1, 5, 10, 25};
        double[][] mat = GenerarMatriz();

        System.out.println("Resultados de pruebas para algoritmo de vuelta atrás:");
        for (int i = 0; i < minutos.length; i++) {
            int[] resul = resolverVueltaAtras(minutos[i]);
            System.out.println("\tPara " + minutos[i] + "s, " + getDistanciaTotal(resul, mat));
        }
    }
}
