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

    public static double getDistanciaTotal(int ruta[], double[][] matdistancias) {

        double distanciaTotal = 0;

        for (int i = 0; i < ruta.length - 1; i++) {

            int ciudadActual = ruta[i];
            int ciudadSiguiente = ruta[i + 1];
            distanciaTotal += matdistancias[ciudadActual][ciudadSiguiente];
        }

        return distanciaTotal;
    }

    public static double getDistanciaParcial(int ruta[], double[][] matdistancias, int pos) {

        double distanciaParcial = 0;

        for (int i = 0; i < pos; i++) {

            int ciudadActual = ruta[i];
            int ciudadSiguiente = ruta[i + 1];
            distanciaParcial += matdistancias[ciudadActual][ciudadSiguiente];
        }

        return distanciaParcial;
    }

    public static int[] getVectorInicial(int numeroCiudades) {

        int v[] = new int[numeroCiudades];

        for (int i = 0; i < numeroCiudades; i++) {

            v[i] = i;

        }

        return v;
    }

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

    public static double[][] GenerarMatriz() throws FileNotFoundException, IOException {
        BufferedReader bf = new BufferedReader(new FileReader("prueba.tsp"));
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

    //metodos de resolucion con vuelta atras
    public static int[] resolverVueltaAtras(double[][] matdistancias) {
        int[] rutaActual = getVectorInicial(matdistancias.length);
        int[] rutaResul = Arrays.copyOf(rutaActual, matdistancias.length);
        rutaAleatoria(rutaResul);
        arbolBacktracking(rutaActual, rutaResul, matdistancias, 1);
        return rutaResul;
    }

    public static void arbolBacktracking(int[] rutaActual, int[] rutaResul, double[][] matdistancias, int pos) {
        if (pos < rutaActual.length) {

            for (int i = pos + 1; i < rutaActual.length; i++) {

                int aux = rutaActual[i];
                rutaActual[i] = rutaActual[pos];
                rutaActual[pos] = aux;

                double distanciaActual = getDistanciaTotal(rutaResul, matdistancias);
                double distanciaParcial = getDistanciaParcial(rutaActual, matdistancias, pos);
                if (distanciaActual > distanciaParcial) {
                    arbolBacktracking(rutaActual, rutaResul, matdistancias, pos + 1);
                }
                if (pos == rutaActual.length - 1) {
                    if (getDistanciaTotal(rutaResul, matdistancias) > getDistanciaTotal(rutaActual, matdistancias)) {
                        rutaResul = Arrays.copyOf(rutaActual, rutaActual.length);
                    }
                }

                aux = rutaActual[i];
                rutaActual[i] = rutaActual[pos];
                rutaActual[pos] = aux;
            }
        }
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

    public static void matrizDV(int[] rutaActual, int pos, int[][] matDV, int[] j) {
        if (pos < rutaActual.length) {

            for (int i = pos; i < rutaActual.length; i++) {

                int aux = rutaActual[i];
                rutaActual[i] = rutaActual[pos];
                rutaActual[pos] = aux;

                if (pos != i) {
                    System.arraycopy(rutaActual, 0, matDV[j[0]], 0, rutaActual.length);
                    j[0]++;
                }
                matrizDV(rutaActual, pos + 1, matDV, j);

                aux = rutaActual[i];
                rutaActual[i] = rutaActual[pos];
                rutaActual[pos] = aux;
            }
        }
    }

    public static int[][] matrizDV(int[] ruta) {
        int n = ruta.length - 1;
        int f = 1;
        while (n != 0) {
            f = f * n;
            n--;
        }
        int[][] mat = new int[f][ruta.length];
        int[] j = {0};
        System.arraycopy(ruta, 0, mat[j[0]], 0, ruta.length);
        j[0]++;
        matrizDV(ruta, 1, mat, j);
        return mat;
    }

    public static int[] resolverDV() throws IOException {
        
        double[][] matdistancias = GenerarMatriz();

        int mat[][] = matrizDV(getVectorInicial(matdistancias.length));
        
        return funcionDV(mat, matdistancias, 0, matdistancias.length - 1);
    }

    public static int[] funcionDV(int[][] matRutas, double[][] matdistancias, int ini, int fin) {
        int[] rutaActual;
        if (ini == fin) {
            rutaActual = matRutas[ini];
        } else {
            int[] rutaLeft = funcionDV(matRutas, matdistancias, ini, (fin + ini) / 2);
            int[] rutaRight = funcionDV(matRutas, matdistancias, (fin + ini) / 2 + 1, fin);
            double left = getDistanciaTotal(rutaLeft, matdistancias);
            double right = getDistanciaTotal(rutaRight, matdistancias);

            if (left < right) {
                rutaActual = rutaLeft;
            } else {
                rutaActual = rutaRight;
            }
        }
        return rutaActual;
    }
  
    public static void main(String[] args) throws IOException {
        /* double[][] matriz = GenerarMatriz();
        //para cambiar el fichero de prueba hay que cambiar el nombre al principio de GenerarMatriz()
        int[] resultado = resolverVueltaAtras(matriz);
        System.out.print("[");
        for (int i = 0; i < resultado.length; i++) {
            System.out.print(resultado[i] + ", ");
        }
        System.out.print("]");
         */
        int[] resultado = resolverDV();
        for(int i =0;i<resultado.length;i++){
            System.out.print(resultado[i] + ", ");
        }
        System.out.println("tusmula");
    }
    /*
    [0 1 2 3] inicio
    [0 1 3 2]
    [0 2 1 3]
    [0 2 3 1]
    [0 3 2 1]
    [0 3 1 2] fin   
     */
}
