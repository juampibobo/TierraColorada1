/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.tierracolorada1.vistas;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;


import com.mycompany.tierracolorada1.controladores.BalanzaControlador;
import com.mycompany.tierracolorada1.controladores.ProcesoControlador;
import com.mycompany.tierracolorada1.controladores.AuditoriaControlador;


import com.mycompany.tierracolorada1.modelos.Lote;
import com.mycompany.tierracolorada1.modelos.Etapa;
import com.mycompany.tierracolorada1.modelos.Auditoria;

public class Principal {


    private static List<Lote> baseDatosLotes = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

    try {
        System.out.println("[JPA] Inicializando fábrica de conexiones y generando tablas...");
        com.mycompany.tierracolorada1.persistencia.JPAUtil.getEntityManagerFactory();
        System.out.println("[JPA] Conexión establecida y tablas sincronizadas en MySQL.");
    } catch (Exception e) {
        System.out.println("[JPA] Error al conectar a la base de datos en el arranque.");
        e.printStackTrace();
    }
    
    BalanzaControlador balanzaCtrl = new BalanzaControlador();
    ProcesoControlador procesoCtrl = new ProcesoControlador();
    AuditoriaControlador seguridadCtrl = new AuditoriaControlador();

    System.out.println("    SISTEMA DE TRAZABILIDAD - TIERRA COLORADA     ");
    

        boolean loginExitoso = false;
        while (!loginExitoso) {
            System.out.println("INICIO DE SESIÓN");
            System.out.print("Ingrese Usuario (admin123): ");
            String usuarioInput = scanner.nextLine();
            System.out.print("Ingrese Contrasenia (yerba2026): ");
            String claveInput = scanner.nextLine();

            loginExitoso = seguridadCtrl.iniciarSesion(usuarioInput, claveInput);

            if (!loginExitoso) {
                System.out.println("Credenciales incorrectas. Intente de nuevo.");
            }
        }

        System.out.println("¡Login Exitoso!" 
                + seguridadCtrl.getUsuarioAutenticado().getNombreCompleto() 
                + " [" + seguridadCtrl.getUsuarioAutenticado().getRol() + "]");



        System.out.println(" PUESTO DE BALANZA: REGISTRO DE HOJA VERDE       ");

        
        System.out.print("Ingrese el número/ID del nuevo Lote: ");
        int idLote = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nombre del Productor: ");
        String nomProd = scanner.nextLine();
        System.out.print("CUIT del Productor: ");
        String cuitProd = scanner.nextLine();
        System.out.print("Numero de telefono de Productor: ");
        String nroProd = scanner.nextLine();

        System.out.print("Patente del Camión: ");
        String patente = scanner.nextLine();
        System.out.print("Distancia recorrida desde el Yerbal (Km): ");
        double km = scanner.nextDouble();
        System.out.print("Kilos Iniciales de Hoja Verde: ");
        double kilosIni = scanner.nextDouble();
        System.out.print("Porcentaje de Humedad Inicial (%): ");
        double humedad = scanner.nextDouble();

        System.out.println("Tipo de Transporte:");
        System.out.println("1. Camión Propio (Logística Interna)");
        System.out.println("2. Flete Tercerizado (Fletero Particular)");
        System.out.print("Seleccione una opción: ");
        int tipoFlete = scanner.nextInt();

        Lote nuevoLote = null;

        if (tipoFlete == 1) {
       
            nuevoLote = balanzaCtrl.registrarIngresoCamionPropio(
                    idLote, cuitProd, nomProd, nroProd, patente, "Mercedes-Benz", "Atego", 
                    150000.0, km, kilosIni, humedad
            );
        } else {
            System.out.print("Ingrese la Tarifa pactada por Kilómetro ($): ");
            double tarifa = scanner.nextDouble();
            
            nuevoLote = balanzaCtrl.registrarIngresoFleteTercerizado(
                    idLote, cuitProd, nomProd, nroProd, patente, "Ford", "Cargo", 
                    tarifa, km, kilosIni, humedad
            );
        }

    
        baseDatosLotes.add(nuevoLote);
        System.out.println("¡Lote " + nuevoLote.getIdLote() + " registrado con éxito en Balanza!");
        System.out.println("Costo calculado del viaje: $" + balanzaCtrl.obtenerCostoFleteTotal(nuevoLote));

    
    
        System.out.println(" PLANTA DE SECADO: CIRCUITO TECNOLÓGICO ");
    
        
        System.out.println("Pesaje actual del Lote en planta: " + procesoCtrl.consultarPesoActualEnPlanta(nuevoLote) + " kg.");
        System.out.println("Cambiando lote al sector de [SAPECADO]...");
        
    
        procesoCtrl.iniciarNuevaEtapa(nuevoLote, Etapa.SAPECADO);
        
        System.out.print("Ingrese los kilos de MERMA detectados en el Sapecado: ");
        double kilosMerma = scanner.nextDouble();

        double pesoAntesDeModificar = procesoCtrl.consultarPesoActualEnPlanta(nuevoLote);

        try {
        
            procesoCtrl.registrarMermaEnEtapaActual(nuevoLote, kilosMerma);
            double pesoNuevo = procesoCtrl.consultarPesoActualEnPlanta(nuevoLote);
            System.out.println("Merma aplicada. Nuevo peso remanente del lote: " + pesoNuevo + " kg.");

            Auditoria log = seguridadCtrl.registrarAuditoriaDeCambio(nuevoLote, pesoAntesDeModificar, pesoNuevo);
            
            System.out.println(" Registro de Auditoría N° " + log.getIdAuditoria() + " creado.");
            System.out.println(" Operario responsable: " + log.getUsuario().getNombreCompleto());
            System.out.println(" Fecha/Hora del registro: " + log.getFechaHora());

        } catch (IllegalArgumentException e) {
            System.out.println(" Error en el modelo: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(" Error de flujo: " + e.getMessage());
        }

    
        System.out.println(" SALIDA DE INFORMACIÓN: CONTROL DE CALIDAD ");

        

        System.out.println(nuevoLote.obtenerResumen());
        

        System.out.println(" SIMULACIÓN FINALIZADA CORRECTAMENTE ");
        
        scanner.close();
    }
}

    

 


