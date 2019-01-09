/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controller.ResolverController;
import domain.Order;
import domain.SatelliteInTimeSlot;
import domain.Vehicle;
import domain.VeicType;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Adrian
 */
public class DataLoad {
    
    public static String fileAddress;

    public static void loadData() {
        try {
            ResolverController m = ResolverController.getInstance();

            Properties p = new Properties();
            p.load(new FileReader(fileAddress));

            //cargando los datos
            m.orders = getInt(p.getProperty("m"));
            m.vehiclesCount = getInt(p.getProperty("o"));
            m.timeslots = getInt(p.getProperty("p"));

            //cargando las demandas de las ordenes
           List<Integer> order  = evalList(p.getProperty("demand"));
           m.orders_demand = new ArrayList<>();
            for (int i = 0; i < order.size(); i++) {
                m.orders_demand.add(new Order(order.get(i)));
            }
                   
            //cargando el satelite con su capacidad y tarifa por cada instante de tiempo
            List<Integer> depot_Cap = evalList(p.getProperty("Depot_Cap"));
            List<Integer> tarif = evalList(p.getProperty("Tarif"));
            m.satellite_time_slot = new ArrayList<SatelliteInTimeSlot>();
            for (int i = 0; i < depot_Cap.size(); i++) {
                m.satellite_time_slot.add(new SatelliteInTimeSlot(depot_Cap.get(i), tarif.get(i)));
            }

            //Cargando los tipos de vehiculos
            List<Integer> veicCost = evalList(p.getProperty("VeicCost"));
            List<Integer> veicVolume = evalList(p.getProperty("VeicVolume"));
            m.VType = new ArrayList<VeicType>();
            for (int i = 0; i < veicCost.size(); i++) {
                m.VType.add(new VeicType(veicCost.get(i), veicVolume.get(i)));
            }

             //cargando elect_vehicle (Cost per Stop)
            ArrayList<ArrayList<Integer>> elect_vehicle = evalMatrix(p.getProperty("Elect_vehicle"));
            
            
            //cargando los vehiculos y asociandole su tipo
            List<Integer> veicType = evalList(p.getProperty("VeicType"));
            m.vehicles = new ArrayList<Vehicle>();
            for (int i = 0; i < m.vehiclesCount; i++) {
                m.vehicles.add(new Vehicle(i,elect_vehicle.get(i), m.VType.get(veicType.get(i)-1)));
            }

            /*for (Object valor : p.keySet()) {
                System.out.println(valor +" : "+ p.get(valor));
            }*/
        } catch (Exception ex) {
            Logger.getLogger(DataLoad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static int getInt(String value) {
        value = value.replace(" ", "").replace("=", "").replace(":", "").replace(";", "");
        return Integer.parseInt(value);
    }

    /**
     * Ej de valor que recibe: "[1, 7, 3, 5, 4, 9]"
     */
    public static List<Integer> evalList(String arrayPrimitiveSimple) {

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");

            String arraySimple = "java.util.Arrays.asList(" + arrayPrimitiveSimple.substring(0, arrayPrimitiveSimple.length() - 1) + ")";

            Object result = engine.eval(arraySimple);

            List<Integer> a = (List<Integer>) result;

            /*for (int i = 0; i < a.size(); i++) {
            System.out.println(a.get(i));
            }*/
            return a;

        } catch (ScriptException ex) {
            Logger.getLogger(ex.getMessage());
        }

        return null;
    }

    /**
     * Ej de valor que recibe: "[[1, 7, 3], [5, 4, 9]];"
     */
    public static ArrayList<ArrayList<Integer>> evalMatrix(String arrayPrimitiveSimple) {

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");

            jdk.nashorn.api.scripting.ScriptObjectMirror resultInt = (jdk.nashorn.api.scripting.ScriptObjectMirror) engine.eval(arrayPrimitiveSimple);

            ArrayList<ArrayList<Object>> lista = new ArrayList<ArrayList<Object>>();

            for (int i = 0; i < resultInt.size(); i++) {
                jdk.nashorn.api.scripting.ScriptObjectMirror filaI = (jdk.nashorn.api.scripting.ScriptObjectMirror) resultInt.get(i+"");

                lista.add(new ArrayList<Object>(filaI.values()));

                /* for (int j = 0; j < filaI.size(); j++) {
                    System.out.print(filaI.get(j) + " "+filaI.get(j).getClass());
                }
                System.out.println();*/
            }

            ArrayList<ArrayList<Integer>> result = (ArrayList<ArrayList<Integer>>) (Object) lista;
            return result;

        } catch (ScriptException ex) {
            Logger.getLogger(ex.getMessage());
        }

        return null;
    }

}
