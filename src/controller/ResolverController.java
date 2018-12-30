/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.DataLoad;
import domain.Order;
import domain.SatelliteInTimeSlot;
import domain.Vehicle;
import domain.VeicType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Adrian
 */
public class ResolverController {

    public int orders;//1..m;
    public int vehiclesCount;//1..o;
    public int timeslots;//1..p;
    public List<VeicType> VType;//1..numVTypes;
    public List<Vehicle> vehicles;

    public List<Order> orders_demand; //For each order i a demand di (demand[orders]).

    public List<SatelliteInTimeSlot> satellite_time_slot; //Capacity and Tarif of satellite in each timeslot (Depot Cap[timeslots] and Tarif[timeslots])

    public int cost_for_express = 1000;//ca the unit cost for an express delivery.

    public ArrayList<ArrayList<Integer>> elect_vehicle;

    private static ResolverController instance;

    public static ResolverController getInstance() {
        if (instance == null) {
            instance = new ResolverController();
            DataLoad.loadData();
        }
        return instance;
    }

    private ResolverController() {

    }

    public int heuristicResolver() {
        int resultSumMin = 0;

        //Valor 1 genera Ordenamiento Ascendente y -1 Descendente
        int tipoDeOrdenamientoPrueba = -1; //Esta variable es solo para probar si es mejor ordenar ascendente o descendente
        
        //ordenamos la lista de ordenes de forma $tipoDeOrdenamientoPrueba con respecto a la capacidad
        orders_demand.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return (o1.getDemand() - o2.getDemand())*tipoDeOrdenamientoPrueba;
            }
        });

        //ordenamos la lista de vehiculos de forma $tipoDeOrdenamientoPrueba con respecto a la capacidad y creciente con respecto al precio
        vehicles.sort(new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle o1, Vehicle o2) {
                int compareByCost = o1.getVtype().getVeicCost() - o2.getVtype().getVeicCost();
                return compareByCost == 0 ?  (o1.getVtype().getVeicVolume() - o2.getVtype().getVeicVolume())*tipoDeOrdenamientoPrueba : compareByCost;
            }
        });

        int[] demandInTime = new int[timeslots];//Se utiliza para controlar el (CONSTRAIN 1)
        int[][] vehicleUseCapacityInTime = new int[vehiclesCount][timeslots];//Se utiliza para controlar el (CONSTRAIN 2)
        boolean orderIsSatisfied = false; //Se utiliza para controlar el (CONSTRAIN 3)

        //CALCULANDO LA FUNCION OBJETIVO
        for (int i = 0; i < orders; i++) {
            Order order = orders_demand.get(i);
            orderIsSatisfied = false;

            //recorremos los vehiculos para ver cual puede satisfacer la orden mientras que la orden no sea satisfecha
            for (int k = 0; k < vehiclesCount && orderIsSatisfied == false; k++) {
                Vehicle vehic = vehicles.get(k);

                //buscamos el 1er vehiculo que tenga capacidad para satisfacer la demanda de la orden i
                if (vehic.getVtype().getVeicVolume() >= order.getDemand()) {

                    for (int h = 0; h < timeslots; h++) {

                        //(CONSTRAIN 1) Las sumas de las demandas que se envian en un instante de tiempo tienen que ser menor o igual a la capacidad del satelite en ese instante
                        if (demandInTime[h] + order.getDemand() <= satellite_time_slot.get(h).getCapacity()) {

                            //(CONSTRAIN 2) Si la orden i puede ser tomada por el vehiculo k en el instante de tiempo h
                            if (vehicleUseCapacityInTime[k][h] + order.getDemand() <= vehic.getVtype().getVeicVolume()) {

                                //AGREGAMOS EL COSTO DE LA DEMANDA POR LA TARIFA EN EL INSTANTE DE TIEMPO(Di X Th)
                                resultSumMin += order.getDemand() * satellite_time_slot.get(h).getTarif();

                                //(FALTA BUSCAR CUAL ES EL COSTO X PARADA) 
                                resultSumMin += 0; //agregamos el costo por parada

                                //AGREGAMOS EL COSTO FIJO DE USAR EL VEHICULO (FALTA REVISAR PORQUE DEBEN TENER UN COSTO DIFERENTE EN CADA INSTANTE DE TIEMPO)
                                //EN CASO DE QUE EL COSTO SEA elect_vehicle[][] se tedria que sumar elect_vehicle[vehic.getPosition()][h]
                                resultSumMin += vehic.getVtype().getVeicCost();

                                //LA SUMATORIA DE LOS ENVIOS EXPRESS SE REALIZA AL FINAL SI NO SE PUDO ENVIAR DE FORMA NORMAL
                                
                                vehicleUseCapacityInTime[k][h] += order.getDemand(); //Agregamos la demanda al vehiculo k en el instante de tiempo h
                                orderIsSatisfied = true; //marcamos que la orden fue satisfecha
                                demandInTime[h] += order.getDemand(); //Agregamos la demanda al instante de tiempo h
                            }
                        }
                    }
                }
            }

            //si no se puede enviar con ningun vehiculo se envia express
            if (!orderIsSatisfied) {
                resultSumMin += cost_for_express; //si no se puede enviar con ningun vehiculo se envia por express
            }
        }

        return resultSumMin;
    }

}
