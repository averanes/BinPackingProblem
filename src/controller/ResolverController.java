/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import dao.CreateFileResult;
import dao.DataLoad;
import domain.Order;
import domain.SatelliteInTimeSlot;
import domain.Vehicle;
import domain.VeicType;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Adrian
 */
public class ResolverController {

    public int orders;//1..m;
    public int vehiclesCount;//1..o;
    public int timeslots;//1..p;
    public List<VeicType> VType;//1..numVTypes;
    public int numVTypes; //count of vehicles type
    public List<Vehicle> vehicles;

    public List<Order> orders_demand; //For each order i a demand di (demand[orders]).

    public List<SatelliteInTimeSlot> satellite_time_slot; //Capacity and Tarif of satellite in each timeslot (Depot Cap[timeslots] and Tarif[timeslots])

    public int cost_for_express = 1000;//ca the unit cost for an express delivery.

   

    private static ResolverController instance;

    public static ResolverController getInstance(File dataSet) {
        if (instance == null) {
            instance = new ResolverController();
        }

        DataLoad.loadData(instance, dataSet);
        return instance;
    }

    private ResolverController() {

    }
    
    //variables for show the logs of result
    public CreateFileResult c;
    public int runAndPrintSolution = 0; //0 only run, 1 run and update the value for excel and 2 run, update the value for excel and print in Console
    public Map<Integer, Integer> vehiclesCountByType;
    public Map<Integer, Integer> demandCountByTimeSlot;
    public String valuesOfObjFunc;

    //Optimizado en base a disminuir el costo por parada
    public int heuristicResolverPerfect() {

        //Creamos una lista de instantes de tiempo 
        List<Integer> timeSlotOrganized = new ArrayList<Integer>();
        for (int i = 0; i < timeslots; i++) {
            timeSlotOrganized.add(i);
        }

        //organizamos los instantes de tiempo con respecto a la Tarifa
        timeSlotOrganized.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return satellite_time_slot.get(o1).getTarif() - satellite_time_slot.get(o2).getTarif();
            }
        });
        //timeSlotOrganized es siempre [1, 2, 0] con los juegos de datos que tenemos

        //Valor 1 genera Ordenamiento Ascendente y -1 Descendente
        int tipoDeOrdenamientoPrueba = -1; //Esta variable es solo para probar si es mejor ordenar ascendente o descendente
        //if(timeslots>1 && satellite_time_slot.get(timeSlotOrganized.get(0).pos).getTarif() > satellite_time_slot.get(timeSlotOrganized.get(1).pos).getTarif() )
        // tipoDeOrdenamientoPrueba = 1;

        //ordenamos la lista de ordenes de forma $tipoDeOrdenamientoPrueba con respecto a la capacidad
        orders_demand.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return (o1.getDemand() - o2.getDemand()) * tipoDeOrdenamientoPrueba;
            }
        });

        //ordenamos la lista de vehiculos de forma creciente con respecto a los costos x parada en el 1er instante de tiempo q estan ordenados y si tienen el mismo valor con respecto a la proporcion Volume / Cost, si todo esto sigue siendo igual se ordena creciente con respecto a los costos x parada en el 2do y 3er instante 
        vehicles.sort(new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle o1, Vehicle o2) {

                int comparator = o1.getCost_per_stop().get(timeSlotOrganized.get(0)) - o2.getCost_per_stop().get(timeSlotOrganized.get(0));

                if (comparator == 0) {
                    comparator = -1 * ((int) (((float) o1.getVtype().getVeicVolume() / (float) o1.getVtype().getVeicCost()) * 100)) - (int) (((float) o2.getVtype().getVeicVolume() / (float) o2.getVtype().getVeicCost()) * 100);
                }

                if (comparator == 0 && timeSlotOrganized.size() > 1) {
                    comparator = o1.getCost_per_stop().get(timeSlotOrganized.get(1)) - o2.getCost_per_stop().get(timeSlotOrganized.get(1));
                }

                if (comparator == 0 && timeSlotOrganized.size() > 2) {
                    comparator = o1.getCost_per_stop().get(timeSlotOrganized.get(2)) - o2.getCost_per_stop().get(timeSlotOrganized.get(2));
                }

                return comparator;
            }
        });

        
        int resultSumMinFirst = 0; //Result for the Greddy solution 1
        int[] demandInTime = new int[timeslots];//Se utiliza para controlar el (CONSTRAIN 1)
        int[][] vehicleUseCapacityInTime = new int[vehiclesCount][timeslots];//Se utiliza para controlar el (CONSTRAIN 2)
        boolean orderIsSatisfied = false; //Se utiliza para controlar el (CONSTRAIN 3)

        HashMap<Integer, String> ordersByVehicleOnlyForTestSolution = new HashMap<Integer, String>();//Est variable es solo para almacenar datos para probar la solucion
        valuesOfObjFunc = "";

        //CALCULANDO LA FUNCION OBJETIVO
        for (int i = 0; i < orders; i++) {
            Order order = orders_demand.get(i);
            orderIsSatisfied = false;

            for (int h1 = 0; h1 < timeslots && orderIsSatisfied == false; h1++) {
                int h = timeSlotOrganized.get(h1);

                //(CONSTRAIN 1) Las sumas de las demandas que se envian en un instante de tiempo tienen que ser menor o igual a la capacidad del satelite en ese instante
                if (demandInTime[h] + order.getDemand() <= satellite_time_slot.get(h).getCapacity()) {

                    //recorremos los vehiculos para ver cual puede satisfacer la orden en el tiempo h mientras que la orden no sea satisfecha
                    for (int k = 0; k < vehiclesCount; k++) {
                        Vehicle vehic = vehicles.get(k);

                        //(CONSTRAIN 2) Si la orden i puede ser tomada por el vehiculo k en el instante de tiempo h
                        if (vehicleUseCapacityInTime[k][h] + order.getDemand() <= vehic.getVtype().getVeicVolume()) {

                            //AGREGAMOS EL COSTO DE LA DEMANDA POR LA TARIFA EN EL INSTANTE DE TIEMPO(Di X Th)
                            resultSumMinFirst += order.getDemand() * satellite_time_slot.get(h).getTarif();

                            //agregamos el costo por parada para el vehicle k en el instante de tiempo h
                            resultSumMinFirst += vehic.getCost_per_stop().get(h);

                            if (runAndPrintSolution == 2){
                                 valuesOfObjFunc += " + ( "+order.getDemand()+" * "+satellite_time_slot.get(h).getTarif();
                                 valuesOfObjFunc += " + "+vehic.getCost_per_stop().get(h);
                                }
                            
                            //AGREGAMOS EL COSTO FIJO DE USAR EL VEHICULO (Si es la primera demanda para el vehiculo en este instante de tiempo)
                            if (vehicleUseCapacityInTime[k][h] == 0) { //Si esto no es 0 es porque ya se sumo el vehiculo k en el instante de tiempo h
                                resultSumMinFirst += vehic.getVtype().getVeicCost();
                                
                                if (runAndPrintSolution == 2){
                                valuesOfObjFunc += " + "+vehic.getVtype().getVeicCost()+" ) ";
                                }
                            }else{
                            if (runAndPrintSolution == 2){
                                valuesOfObjFunc += " ) ";
                                }
                            }

                            //LA SUMATORIA DE LOS ENVIOS EXPRESS SE REALIZA AL FINAL SI NO SE PUDO ENVIAR DE FORMA NORMAL
                            vehicleUseCapacityInTime[k][h] += order.getDemand(); //Agregamos la demanda al vehiculo k en el instante de tiempo h
                            orderIsSatisfied = true; //marcamos que la orden fue satisfecha
                            demandInTime[h] += order.getDemand(); //Agregamos la demanda al instante de tiempo h

                            //estos datos solo se guardan para realizar las pruebas de la solucion
                            if (runAndPrintSolution != 0) {
                                String ordenes = "";
                                if (ordersByVehicleOnlyForTestSolution.containsKey(k)) {
                                    ordenes += ordersByVehicleOnlyForTestSolution.get(k) + ", ";
                                }
                                ordenes += "[" + i + " : " + order.getDemand() + "] ";
                                ordersByVehicleOnlyForTestSolution.put(k, ordenes);
                            }
                            break;
                        }
                    }
                }

            }

            //si no se puede enviar con ningun vehiculo, se envia express
            if (!orderIsSatisfied) {
                resultSumMinFirst += cost_for_express; //si no se puede enviar con ningun vehiculo se envia por express

                               if (runAndPrintSolution == 2){
                                 valuesOfObjFunc += " [ "+cost_for_express+" ] ";
                                }
                
                System.out.println("Orden " + i + " Vehicle EXPRESS");
            }
        }

        if (runAndPrintSolution != 0) {
            
            demandCountByTimeSlot = new HashMap<Integer, Integer>();
            //comprobando Satelite en instante de tiempo (capacidad con usado)
            for (int h = 0; h < timeslots; h++) {
                demandCountByTimeSlot.put(h, demandInTime[h]);

                if (runAndPrintSolution == 2) {
                    print(c, "Satelite TimeSlot " + h + " Capacity Total " + satellite_time_slot.get(h).getCapacity() + " Used " + demandInTime[h]);
                }
            }
            print(c, "");

            //comprobando ordenes en vehiculos
            int total = 0;
            for (int i = 0; i < orders; i++) {
                total += orders_demand.get(i).getDemand();
            }

            if (runAndPrintSolution == 2) {
                print(c, "Sum of demands " + total);
            }

            for (Map.Entry<Integer, String> en : ordersByVehicleOnlyForTestSolution.entrySet()) {
                Integer key = en.getKey();
                String value = en.getValue();

                if (runAndPrintSolution == 2) {
                    print(c, "Vehicle: " + vehicles.get(key).getPosition() + " [Orders, Demands]: " + value);
                }
            }

            if (runAndPrintSolution == 2) {
                print(c, "");
            }

            vehiclesCountByType = new HashMap<Integer, Integer>();
            //comprobando vehiculos (capacidad total y capacidad usada)
            total = 0;
            int vehicleUsedCount = 0;
            for (int i = 0; i < vehiclesCount; i++) {
                boolean isUsedVehic = false;
                for (int j = 0; j < timeslots; j++) {
                    total += vehicleUseCapacityInTime[i][j];

                    if (vehicleUseCapacityInTime[i][j] != 0) {

                        if (runAndPrintSolution == 2) {
                            print(c, "Vehicle " + vehicles.get(i).getPosition() + " Type " + vehicles.get(i).getVtype().getNumOfVeicType() + " Capacity Total " + vehicles.get(i).getVtype().getVeicVolume() + " TimeSlot " + j + ": Total Demands " + vehicleUseCapacityInTime[i][j]);
                        }

                        isUsedVehic = true;
                    }

                }
                if (isUsedVehic) {
                    int vehicleUsedCountTemp = 0;
                    if (vehiclesCountByType.containsKey(vehicles.get(i).getVtype().getNumOfVeicType())) {
                        vehicleUsedCountTemp = vehiclesCountByType.get(vehicles.get(i).getVtype().getNumOfVeicType());
                    }

                    vehiclesCountByType.put(vehicles.get(i).getVtype().getNumOfVeicType(), vehicleUsedCountTemp + 1);
                    vehicleUsedCount++;
                }
            }

            if (runAndPrintSolution == 2) {
                print(c, "");
                print(c, "Used vehicles " + vehicleUsedCount + " Sum of occupied capacities " + total);
                
            valuesOfObjFunc = "Obj Fuction ( d1*Th + Chk [+ &hk] [+ Ca]) "+valuesOfObjFunc.substring(2);     
            print(c, valuesOfObjFunc);
            }

        }
        

        return resultSumMinFirst;

    }

    public static void print(CreateFileResult c, String text) {
        c.setValueToPrint(text);
        //System.out.println(text);
    }

    //Optimizado en base a disminuir el costo por parada
   /* public int heuristicResolverOld() {
        int resultSumMin = 0;

        //El costo por parada aporta la mayor variacion de valor, entonces optimizando con respecto a esto se obtienen los mejores resultados
        //Por cada instante de tiempo sumamos el total de los costos por parada
        List<PosValue> timeSlotOrganized = new ArrayList<PosValue>();
        for (int i = 0; i < timeslots; i++) {
            timeSlotOrganized.add(new PosValue(i, 0));
        }

        for (int i = 0; i < vehiclesCount; i++) {
            for (int j = 0; j < timeslots; j++) {
                timeSlotOrganized.get(j).sumValue(vehicles.get(i).getCost_per_stop().get(j));
            }
        }

        //organizamos los instantes de tiempo con respecto a la suma de costos por parada (ascendente)
        timeSlotOrganized.sort(new Comparator<PosValue>() {
            @Override
            public int compare(PosValue o1, PosValue o2) {
                return o1.value - o2.value;
            }
        });
        //timeSlotOrganized es siempre [2, 0, 1] con los juegos de datos que tenemos

        //Valor 1 genera Ordenamiento Ascendente y -1 Descendente
        int tipoDeOrdenamientoPrueba = -1; //Esta variable es solo para probar si es mejor ordenar ascendente o descendente
        //if(timeslots>1 && satellite_time_slot.get(timeSlotOrganized.get(0).pos).getTarif() > satellite_time_slot.get(timeSlotOrganized.get(1).pos).getTarif() )
        // tipoDeOrdenamientoPrueba = 1;

        //ordenamos la lista de ordenes de forma $tipoDeOrdenamientoPrueba con respecto a la capacidad
        orders_demand.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return (o1.getDemand() - o2.getDemand()) * tipoDeOrdenamientoPrueba;
            }
        });

        //ordenamos la lista de vehiculos de forma creciente con respecto a costos x parada en los instantes de tiempo q estan ordenados, al costo y al volumen del vehiculo descendente
        vehicles.sort(new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle o1, Vehicle o2) {

                int comparator = o1.getCost_per_stop().get(timeSlotOrganized.get(0).pos) - o2.getCost_per_stop().get(timeSlotOrganized.get(0).pos);

                
                if (comparator == 0) {
                    comparator = -1 * ((int) (((float) o1.getVtype().getVeicVolume() / (float) o1.getVtype().getVeicCost()) * 100)) - (int) (((float) o2.getVtype().getVeicVolume() / (float) o2.getVtype().getVeicCost()) * 100);

                }

                return comparator;
            }
        });

        int[] demandInTime = new int[timeslots];//Se utiliza para controlar el (CONSTRAIN 1)
        int[][] vehicleUseCapacityInTime = new int[vehiclesCount][timeslots];//Se utiliza para controlar el (CONSTRAIN 2)
        boolean orderIsSatisfied = false; //Se utiliza para controlar el (CONSTRAIN 3)

        HashMap<Integer, String> ordersByVehicleOnlyForTestSolution = new HashMap<Integer, String>();//Est variable es solo para almacenar datos para probar la solucion

        //CALCULANDO LA FUNCION OBJETIVO
        for (int i = 0; i < orders; i++) {
            Order order = orders_demand.get(i);
            orderIsSatisfied = false;

            for (int h1 = 0; h1 < timeslots && orderIsSatisfied == false; h1++) {
                int h = timeSlotOrganized.get(h1).pos;

                //(CONSTRAIN 1) Las sumas de las demandas que se envian en un instante de tiempo tienen que ser menor o igual a la capacidad del satelite en ese instante
                if (demandInTime[h] + order.getDemand() <= satellite_time_slot.get(h).getCapacity()) {

                    //recorremos los vehiculos para ver cual puede satisfacer la orden en el tiempo h mientras que la orden no sea satisfecha
                    for (int k = 0; k < vehiclesCount; k++) {
                        Vehicle vehic = vehicles.get(k);

                        //(CONSTRAIN 2) Si la orden i puede ser tomada por el vehiculo k en el instante de tiempo h
                        if (vehicleUseCapacityInTime[k][h] + order.getDemand() <= vehic.getVtype().getVeicVolume()) {

                            //AGREGAMOS EL COSTO DE LA DEMANDA POR LA TARIFA EN EL INSTANTE DE TIEMPO(Di X Th)
                            resultSumMin += order.getDemand() * satellite_time_slot.get(h).getTarif();

                            //agregamos el costo por parada para el vehicle k en el instante de tiempo h
                            resultSumMin += vehic.getCost_per_stop().get(h);

                            //AGREGAMOS EL COSTO FIJO DE USAR EL VEHICULO (Si es la primera demanda para el vehiculo en este instante de tiempo)
                            if (vehicleUseCapacityInTime[k][h] == 0) { //Si esto no es 0 es porque ya se sumo el vehiculo k en el instante de tiempo h
                                resultSumMin += vehic.getVtype().getVeicCost();
                            }

                            //LA SUMATORIA DE LOS ENVIOS EXPRESS SE REALIZA AL FINAL SI NO SE PUDO ENVIAR DE FORMA NORMAL
                            vehicleUseCapacityInTime[k][h] += order.getDemand(); //Agregamos la demanda al vehiculo k en el instante de tiempo h
                            orderIsSatisfied = true; //marcamos que la orden fue satisfecha
                            demandInTime[h] += order.getDemand(); //Agregamos la demanda al instante de tiempo h

                            //estos datos solo se guardan para realizar las pruebas de la solucion
                            String ordenes = "";
                            if (ordersByVehicleOnlyForTestSolution.containsKey(k)) {
                                ordenes += ordersByVehicleOnlyForTestSolution.get(k) + ", ";
                            }
                            ordenes += i + " demand: " + order.getDemand();
                            ordersByVehicleOnlyForTestSolution.put(k, ordenes);

                            break;
                        }
                    }
                }

            }

            //si no se puede enviar con ningun vehiculo, se envia express
            if (!orderIsSatisfied) {
                resultSumMin += cost_for_express; //si no se puede enviar con ningun vehiculo se envia por express

                System.out.println("Orden " + i + " Vehicle EXPRESS");
            }
        }

       
        return resultSumMin;
    }*/

    //Optimizado en base a disminuir la Tarifa por la demanda (di x Th) MEJORADO
   /* public int heuristicResolverGoodSolutionButBadResult() {
        int resultSumMin = 0;

        //Valor 1 genera Ordenamiento Ascendente y -1 Descendente
        int tipoDeOrdenamientoPrueba = -1; //Esta variable es solo para probar si es mejor ordenar ascendente o descendente

        //ordenamos la lista de ordenes de forma $tipoDeOrdenamientoPrueba con respecto a la capacidad
        orders_demand.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return (o1.getDemand() - o2.getDemand()) * tipoDeOrdenamientoPrueba;
            }
        });

        //ordenamos la lista de vehiculos de forma creciente con respecto al precio y $tipoDeOrdenamientoPrueba con respecto a la capacidad
        vehicles.sort(new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle o1, Vehicle o2) {
                int compareByCost = o1.getVtype().getVeicCost() - o2.getVtype().getVeicCost();
                return compareByCost == 0 ? (o1.getVtype().getVeicVolume() - o2.getVtype().getVeicVolume()) * tipoDeOrdenamientoPrueba : compareByCost;
            }
        });

        //ordenamos la lista de satellite_time_slot con respecto a la tarifa, de forma diferente al ordenamiento de las demandas de las ordenes
        satellite_time_slot.sort(new Comparator<SatelliteInTimeSlot>() {
            @Override
            public int compare(SatelliteInTimeSlot o1, SatelliteInTimeSlot o2) {
                return (o1.getTarif() - o2.getTarif()) * (tipoDeOrdenamientoPrueba * -1);
            }
        });

        int[] demandInTime = new int[timeslots];//Se utiliza para controlar el (CONSTRAIN 1)
        int[][] vehicleUseCapacityInTime = new int[vehiclesCount][timeslots];//Se utiliza para controlar el (CONSTRAIN 2)
        boolean orderIsSatisfied = false; //Se utiliza para controlar el (CONSTRAIN 3)

        //CALCULANDO LA FUNCION OBJETIVO
        for (int i = 0; i < orders; i++) {
            Order order = orders_demand.get(i);
            orderIsSatisfied = false;

            for (int hSatellite = 0; hSatellite < timeslots && orderIsSatisfied == false; hSatellite++) {
                int h = satellite_time_slot.get(hSatellite).getTimeSlot(); //Real Time Slot ordered by Sattelite Tarif

                //(CONSTRAIN 1) Las sumas de las demandas que se envian en un instante de tiempo tienen que ser menor o igual a la capacidad del satelite en ese instante
                if (demandInTime[h] + order.getDemand() <= satellite_time_slot.get(hSatellite).getCapacity()) {

                    //recorremos los vehiculos para ver cual puede satisfacer la orden mientras que la orden no sea satisfecha
                    for (int k = 0; k < vehiclesCount; k++) {
                        Vehicle vehic = vehicles.get(k);

                        //(CONSTRAIN 2) Si la orden i puede ser tomada por el vehiculo k en el instante de tiempo h
                        if (vehicleUseCapacityInTime[k][h] + order.getDemand() <= vehic.getVtype().getVeicVolume()) {

                            //AGREGAMOS EL COSTO DE LA DEMANDA POR LA TARIFA EN EL INSTANTE DE TIEMPO(Di X Th)
                            resultSumMin += order.getDemand() * satellite_time_slot.get(hSatellite).getTarif();

                            //agregamos el costo por parada para el vehicle k en el instante de tiempo h
                            resultSumMin += vehic.getCost_per_stop().get(h);

                            //AGREGAMOS EL COSTO FIJO DE USAR EL VEHICULO (Si es la primera vez que se utiliza el vehiculo en este instante de tiempo)
                            if (vehicleUseCapacityInTime[k][h] == 0) { //Si esto no es 0 es porque ya se sumo
                                resultSumMin += vehic.getVtype().getVeicCost();
                            }

                            //LA SUMATORIA DE LOS ENVIOS EXPRESS SE REALIZA AL FINAL SI NO SE PUDO ENVIAR DE FORMA NORMAL
                            vehicleUseCapacityInTime[k][h] += order.getDemand(); //Agregamos la demanda al vehiculo k en el instante de tiempo h
                            orderIsSatisfied = true; //marcamos que la orden fue satisfecha
                            demandInTime[h] += order.getDemand(); //Agregamos la demanda al instante de tiempo h
                            break;
                        }
                    }
                }

            }

            //si no se puede enviar con ningun vehiculo se envia express
            if (!orderIsSatisfied) {
                resultSumMin += cost_for_express; //si no se puede enviar con ningun vehiculo se envia por express

                System.out.println("Orden " + i + " Vehicle EXPRESS");
            }
        }

        return resultSumMin;
    }*/

    //Grasp basandonos en las primeras versiones del greedy, ya no se acerca a la solucion factible
   /* public int GRASP_metaHeuristicResolver() {
        int resultSumMin = 0;

        //Valor 1 genera Ordenamiento Ascendente y -1 Descendente
        int tipoDeOrdenamientoPrueba = -1; //Esta variable es solo para probar si es mejor ordenar ascendente o descendente

        //ordenamos la lista de ordenes de forma $tipoDeOrdenamientoPrueba con respecto a la capacidad
        orders_demand.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return (o1.getDemand() - o2.getDemand()) * tipoDeOrdenamientoPrueba;
            }
        });

        //ordenamos la lista de vehiculos de forma creciente con respecto al precio y $tipoDeOrdenamientoPrueba con respecto a la capacidad
        vehicles.sort(new Comparator<Vehicle>() {
            @Override
            public int compare(Vehicle o1, Vehicle o2) {
                int compareByCost = o1.getVtype().getVeicCost() - o2.getVtype().getVeicCost();
                return compareByCost == 0 ? (o1.getVtype().getVeicVolume() - o2.getVtype().getVeicVolume()) * tipoDeOrdenamientoPrueba : compareByCost;
            }
        });

        int[] demandInTime = new int[timeslots];//Se utiliza para controlar el (CONSTRAIN 1)
        int[][] vehicleUseCapacityInTime = new int[vehiclesCount][timeslots];//Se utiliza para controlar el (CONSTRAIN 2)

        //CALCULANDO LA FUNCION OBJETIVO
        for (int i = 0; i < orders; i++) {
            Order order = orders_demand.get(i);

            List<Vehicle> vehiclesCandidates = new ArrayList<Vehicle>();
            List<Integer> timeAvailableForEachVehicle = new ArrayList<Integer>(); //instante de tiempo disponible por cada posible vehiculo

            //recorremos los vehiculos para ver cual puede satisfacer la orden mientras que la orden no sea satisfecha
            for (int k = 0; k < vehiclesCount && vehiclesCandidates.size() < 3; k++) {
                Vehicle vehic = vehicles.get(k);

                //buscamos el 1er vehiculo que tenga capacidad para satisfacer la demanda de la orden i
                if (vehic.getVtype().getVeicVolume() >= order.getDemand()) {

                    for (int hSatellite = 0; hSatellite < timeslots; hSatellite++) {
                        int h = satellite_time_slot.get(hSatellite).getTimeSlot(); //Real Time Slot ordered by Sattelite Tarif

                        //(CONSTRAIN 1) Las sumas de las demandas que se envian en un instante de tiempo tienen que ser menor o igual a la capacidad del satelite en ese instante
                        if (demandInTime[h] + order.getDemand() <= satellite_time_slot.get(hSatellite).getCapacity()) {

                            //(CONSTRAIN 2) Si la orden i puede ser tomada por el vehiculo k en el instante de tiempo h
                            if (vehicleUseCapacityInTime[vehic.getPosition()][h] + order.getDemand() <= vehic.getVtype().getVeicVolume()) {
                                //capturo el instante en que el vehicle k puede satisfcer la demanda
                                vehiclesCandidates.add(vehic);
                                timeAvailableForEachVehicle.add(h);
                                break;
                            }
                        }
                    }

                }
            }

            //Si existen posibles vehiculos para satisfacer la demanda
            if (vehiclesCandidates.size() > 0) {
                int ramdomValue = new Random().nextInt(vehiclesCandidates.size());

                Vehicle vehicleBetterRandom = vehiclesCandidates.get(ramdomValue); //seleccionamos un vehiculo aleatoriamente
                int timeAvailable = timeAvailableForEachVehicle.get(ramdomValue); //tiempo disponible del vehiculo seleccionado aleatoriamente

                //AGREGAMOS EL COSTO DE LA DEMANDA POR LA TARIFA EN EL INSTANTE DE TIEMPO h=timeAvailable (Di X Th)
                resultSumMin += order.getDemand() * satellite_time_slot.get(timeAvailable).getTarif();

                //agregamos el costo por parada para el vehicle k en el instante de tiempo h
                resultSumMin += vehicleBetterRandom.getCost_per_stop().get(timeAvailable);

                //AGREGAMOS EL COSTO FIJO DE USAR EL VEHICULO (Si es la primera vez que se utiliza el vehiculo en este instante de tiempo)
                if (vehicleUseCapacityInTime[vehicleBetterRandom.getPosition()][timeAvailable] == 0) { //Si esto no es 0 es porque ya se sumo
                    resultSumMin += vehicleBetterRandom.getVtype().getVeicCost();
                }

                //LA SUMATORIA DE LOS ENVIOS EXPRESS SE REALIZA AL FINAL SI NO SE PUDO ENVIAR DE FORMA NORMAL
                vehicleUseCapacityInTime[vehicleBetterRandom.getPosition()][timeAvailable] += order.getDemand(); //Agregamos la demanda al vehiculo en su posicion y en el instante de tiempo timeAvailable
                demandInTime[timeAvailable] += order.getDemand(); //Agregamos la demanda al instante de tiempo timeAvailable

            } else {//si no se puede enviar con ningun vehiculo se envia express
                resultSumMin += cost_for_express; //si no se puede enviar con ningun vehiculo se envia por express

            }
        }

        
        return resultSumMin;
    }*/
}
