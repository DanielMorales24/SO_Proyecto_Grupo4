/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so_proyecto_grupo4;

/**
 *
 * @author henri
*/
public class Proceso {
    private String nombre;
    private int tiempoCPU;
    private int prioridad;
    private int boletos;

    // Constructor para prioridad
    public Proceso(String nombre, int tiempoCPU, int prioridad) {
        this.nombre = nombre;
        this.tiempoCPU = tiempoCPU;
        this.prioridad = prioridad;
    }

    // Constructor para sorteo
    public Proceso(String nombre, int boletos) {
        this.nombre = nombre;
        this.boletos = boletos;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTiempoCPU() {
        return tiempoCPU;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public int getBoletos() {
        return boletos;
    }
}
