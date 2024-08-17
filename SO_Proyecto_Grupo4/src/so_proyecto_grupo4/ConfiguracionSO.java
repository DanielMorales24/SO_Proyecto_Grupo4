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

public class ConfiguracionSO {
    private static ConfiguracionSO instance;
    private String memoriaVirtual;
    private String espacioDisco;
    private String metodoEjecucion;

    private ConfiguracionSO() {
        // Constructor privado para el patrón Singleton
    }

    public static ConfiguracionSO getInstance() {
        if (instance == null) {
            instance = new ConfiguracionSO();
        }
        return instance;
    }

    public void setMemoriaVirtual(String memoriaVirtual) {
        this.memoriaVirtual = memoriaVirtual;
    }

    public String getMemoriaVirtual() {
        return memoriaVirtual;
    }

    public void setEspacioDisco(String espacioDisco) {
        this.espacioDisco = espacioDisco;
    }

    public String getEspacioDisco() {
        return espacioDisco;
    }

    public void setMetodoEjecucion(String metodoEjecucion) {
        this.metodoEjecucion = metodoEjecucion;
    }

    public String getMetodoEjecucion() {
        return metodoEjecucion;
    }
}
