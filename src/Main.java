import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;


public class Main {
	
  
  static int long_sec = 150;                         
  static int cant_parametros = 5;                    // number of components of a charge/discharge action
  
  static Integer estado_CPU[] = {0,30,50,75,100};    // possible values for CPU load (%)
  static Integer estado_pant[] = {0, 1};             // possible values for screen state (on/off)
  
  static int cant_gen = 2000;                        // Number of generations
  static int cant_pob = 100;                         // Population size
  static int cant_ind_torneo = 10;                   // Tournament size for tournament selection process
  static double prob_cruce = 1;                      // Crossover probability
  static double prob_cruce_uniforme = 0.5;           // Uniform crossover probability
  static double prob_mutacion;                       // Mutation probability
  static int cant_st = 50;                           // Replacement percentage for Steady-State selection process

  static ArrayList<ArrayList<Double>> poblacion = new ArrayList<>(); // Current Population
  static List<Integer> pob_padres = new ArrayList<>();               // Mating pool
  static ArrayList<ArrayList<Double>> pob_hijos = new ArrayList<>(); // New solutions pool

  
  static int cant_mobil;                             // Number of smartphones
  static String modelo[];                            // Model of each smartphone
  static Integer nivel_bat_act[];                    // Current battery level of each smartphone
  static Integer nivel_bat_obj[];                    // Start battery level of each smartphone
  static int[] secuencia_mobil;                      // Length of the sequence si of each smartphone (difference between current and start battery levels)
  static List<Double> target = new ArrayList<>();    // Optimal preparation time for each smartphone          
  
  
  //-------------------------------
  
  public static void setSequenceLength(){
      int aux=-1;
      secuencia_mobil= new int[cant_mobil];

      for(int i=0; i<cant_mobil; i++){
          aux= Math.abs(nivel_bat_obj[i]-nivel_bat_act[i]);
          
          if(aux < long_sec || aux == 0){
              secuencia_mobil[i]= aux;
          }
          else{
              secuencia_mobil[i]= long_sec;
          }
      }
  }
  
  
//-------------------------------
  
  public static void setProbMutation() {
  	
  	double cant_total_tareas = 0.0;
  	
  	for(int i=0; i<cant_mobil; i++){
          
  		cant_total_tareas = cant_total_tareas + secuencia_mobil[i];
         
      }
  	
  	prob_mutacion = 1 / (cant_total_tareas);     	
  	  	
  }
  
  
//-------------------------------
  
  public static void creatSolution(ArrayList<Double> solucion, double tarea, double bat_act, double bat_obj){        
      solucion.add(tarea);  
      solucion.add(bat_act);
      solucion.add(bat_obj);
      solucion.add((double)estado_CPU[(int)(estado_CPU.length*Math.random())]);
      solucion.add((double)estado_pant[(int)(estado_pant.length*Math.random())]);
  }

  
//-------------------------------
  
  public static void chargeDischarge(ArrayList<Double> solucion, int indice){
      int bat_inter_1, bat_inter_2, pos;
      
      List<Integer> niveles_bateria= new ArrayList<>();
      for(int i=0; i<=100; i++){ 
          niveles_bateria.add(i);
      }
      
      List<Integer> nivel_bat_aux= new ArrayList<>(niveles_bateria);
      nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_act[indice]+1, 101);
      
      pos= (int)(Math.random()*nivel_bat_aux.size());

      bat_inter_1= nivel_bat_act[indice];
      bat_inter_2= nivel_bat_aux.get(pos);
      
      creatSolution(solucion, 0.0, (double)bat_inter_1, (double)bat_inter_2); 
      creatSolution(solucion, 1.0, (double)bat_inter_2, (double)bat_inter_1); 
      secuencia_mobil[indice]= 2;
  }

  
//-------------------------------
  
  public static void dischargeCharge(ArrayList<Double> solucion, int indice){
      int bat_inter_1,bat_inter_2,pos;
      
      List<Integer> niveles_bateria= new ArrayList<>();
      for(int i=0; i<=100; i++){ 
          niveles_bateria.add(i);
      }

      List<Integer> nivel_bat_aux= new ArrayList<>(niveles_bateria);
      nivel_bat_aux= nivel_bat_aux.subList(0, nivel_bat_act[indice]);

      pos= (int)(Math.random()*nivel_bat_aux.size());

      bat_inter_1= nivel_bat_act[indice];
      bat_inter_2= nivel_bat_aux.get(pos);
      
      creatSolution(solucion, 1.0, (double)bat_inter_1, (double)bat_inter_2); 
      creatSolution(solucion, 0.0, (double)bat_inter_2, (double)bat_inter_1); 
      secuencia_mobil[indice]= 2;
  }

  
//-------------------------------
  
  public static void initialPopulation(){
      
	  ArrayList<Double> solucion;
      int bat_inter_1,bat_inter_2,pos;
      
      List<Integer> nivel_bat_intermedio;
      List<Integer> nivel_bat_aux;
      List<Integer> niveles_bateria= new ArrayList<>();
      
      for(int i=0; i<=100; i++){ 
          niveles_bateria.add(i);
      }

      
      for(int j=0; j<cant_pob; j++){
          solucion= new ArrayList<>();

          for(int i=0; i<cant_mobil; i++){
              if(nivel_bat_act[i] == nivel_bat_obj[i]){
              	
                  if(nivel_bat_act[i] < 50)
                      chargeDischarge(solucion, i);
                  else 
                  if(nivel_bat_act[i] >= 50)
                      dischargeCharge(solucion, i);
                  
              }
              else if(secuencia_mobil[i] == 1){
                  if(nivel_bat_act[i] < nivel_bat_obj[i]){                        
                      creatSolution(solucion, 0.0, (double)nivel_bat_act[i], (double)nivel_bat_obj[i]);
                  }
                  else{
                      creatSolution(solucion, 1.0, (double)nivel_bat_act[i], (double)nivel_bat_obj[i]);
                  }
              }
              else{
                  nivel_bat_aux= new ArrayList<>(niveles_bateria);
                  nivel_bat_intermedio= new ArrayList<>();

                  if(nivel_bat_act[i] < nivel_bat_obj[i]){  
                      nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_act[i]+1, nivel_bat_obj[i]);
                      
                      
                      for(int k=0; k<secuencia_mobil[i]-1; k++){
                          pos= (int)(Math.random()*nivel_bat_aux.size());
                          nivel_bat_intermedio.add(nivel_bat_aux.get(pos));
                          nivel_bat_aux.remove(pos);
                      }
                      Collections.sort(nivel_bat_intermedio);
                      
                      bat_inter_1= nivel_bat_act[i];
                      
                      for(int k=0; k<secuencia_mobil[i]-1; k++){
                          bat_inter_2= nivel_bat_intermedio.get(k);
                          creatSolution(solucion, 0.0, (double)bat_inter_1, (double)bat_inter_2);
                          bat_inter_1= bat_inter_2;
                      }
                      creatSolution(solucion, 0.0, (double)bat_inter_1, (double)nivel_bat_obj[i]);
                  }
                  else{ 
                      nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_obj[i]+1, nivel_bat_act[i]);

                      for(int k=0; k<secuencia_mobil[i]-1; k++){
                          pos= (int)(Math.random()*nivel_bat_aux.size());
                          nivel_bat_intermedio.add(nivel_bat_aux.get(pos));
                          nivel_bat_aux.remove(pos);
                      }
                      nivel_bat_intermedio.sort(Comparator.reverseOrder());
                      
                      bat_inter_1= nivel_bat_act[i];
                      
                      for(int k=0; k<secuencia_mobil[i]-1; k++){
                          bat_inter_2= nivel_bat_intermedio.get(k);
                          creatSolution(solucion, 1.0, (double)bat_inter_1, (double)bat_inter_2);
                          bat_inter_1= bat_inter_2;
                      }
                      creatSolution(solucion, 1.0, (double)bat_inter_1, (double)nivel_bat_obj[i]);
                  }
              }
          }
          poblacion.add(solucion);
      }
  }
  
  
//-------------------------------
  
  public static void setTarget(){
      double target_inter= -1.0;

      for(int i=0; i<cant_mobil; i++){
          switch(modelo[i]){
              case "motorola_moto_g6":
              	if(nivel_bat_act[i] < nivel_bat_obj[i])
              		target_inter= Profiles.model1_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model1_ScreenOff[nivel_bat_act[i]][0];
              	else	
              		target_inter= Profiles.model1_ScreenOn_Discharging[nivel_bat_obj[i]][4] - Profiles.model1_ScreenOn_Discharging[nivel_bat_act[i]][4];
                  break;
              case "samsung_SM_A305G":
              	if(nivel_bat_act[i] < nivel_bat_obj[i])
              		target_inter= Profiles.model2_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model2_ScreenOff[nivel_bat_act[i]][0];
              	else
                      target_inter= Profiles.model2_ScreenOn_Discharging[nivel_bat_obj[i]][4] - Profiles.model2_ScreenOn_Discharging[nivel_bat_act[i]][4];
                  break;
              case "Xiaomi_Mi_A2_Lite":
              	if(nivel_bat_act[i] < nivel_bat_obj[i])
              		target_inter= Profiles.model3_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model3_ScreenOff[nivel_bat_act[i]][0];
              	else                	
                      target_inter= Profiles.model3_ScreenOn_Discharging[nivel_bat_obj[i]][4] - Profiles.model3_ScreenOn_Discharging[nivel_bat_act[i]][4];
                  break;
              case "Xiaomi_Redmi_Note_7":
              	if(nivel_bat_act[i] < nivel_bat_obj[i])
              		target_inter= Profiles.model4_ScreenOff[nivel_bat_obj[i]][0] - Profiles.model4_ScreenOff[nivel_bat_act[i]][0];
              	else                	
                      target_inter= Profiles.model4_ScreenOn_Discharging[nivel_bat_obj[i]][4] - Profiles.model4_ScreenOn_Discharging[nivel_bat_act[i]][4];
                  break;
              default:
                  System.out.println("Smartphone Model Unknown");
                  break;
          }
          target.add(target_inter);
      }
  }

  
//-------------------------------
  
  public static void fitness(ArrayList<ArrayList<Double>> pob){
      
      int suma, bloque, bloque_ant, fila1, fila2, columna;
      double sumatoria;
      ArrayList<Integer> sumados= new ArrayList<>();
      double max_target= Collections.max(target);
      ArrayList<Double> solucion;

      for(int i=0; i<cant_pob; i++){ 
          solucion= pob.get(i);
          sumatoria= 0.0;
          bloque_ant= 0;
          
          for(int j=0; j<cant_mobil; j++){ 
              suma= 0;

              for(int k=0; k<secuencia_mobil[j]; k++){ 
                  bloque= k*cant_parametros + bloque_ant;
                  fila1= solucion.get(bloque+2).intValue();
                  fila2= solucion.get(bloque+1).intValue();
                  columna= Arrays.asList(estado_CPU).indexOf(solucion.get(bloque+3).intValue());

                  if ( (solucion.get(bloque) == 0.0) && (solucion.get(bloque+4) == 0.0) )  {
                      
                      switch(modelo[j]){
                          case "motorola_moto_g6":
                              suma += Profiles.model1_ScreenOff[fila1][columna] - Profiles.model1_ScreenOff[fila2][columna];
                              break;
                          case "samsung_SM_A305G":
                              suma += Profiles.model2_ScreenOff[fila1][columna] - Profiles.model2_ScreenOff[fila2][columna];
                              break;
                          case "Xiaomi_Mi_A2_Lite":
                              suma += Profiles.model3_ScreenOff[fila1][columna] - Profiles.model3_ScreenOff[fila2][columna];
                              break;
                          case "Xiaomi_Redmi_Note_7":
                              suma += Profiles.model4_ScreenOff[fila1][columna] - Profiles.model4_ScreenOff[fila2][columna];
                              break;
                      }
                  }
                  else
                  if ( (solucion.get(bloque) == 0.0) && (solucion.get(bloque+4) == 1.0) ){
                      switch(modelo[j]){
                          case "motorola_moto_g6":
                              suma += Profiles.model1_ScreenOn[fila1][columna] - Profiles.model1_ScreenOn[fila2][columna];
                              break;
                          case "samsung_SM_A305G":
                              suma += Profiles.model2_ScreenOn[fila1][columna] - Profiles.model2_ScreenOn[fila2][columna];
                              break;
                          case "Xiaomi_Mi_A2_Lite":
                              suma += Profiles.model3_ScreenOn[fila1][columna] - Profiles.model3_ScreenOn[fila2][columna];
                              break;
                          case "Xiaomi_Redmi_Note_7":
                              suma += Profiles.model4_ScreenOn[fila1][columna] - Profiles.model4_ScreenOn[fila2][columna];
                              break;
                      }
                  }
                  else
                  if ( (solucion.get(bloque) == 1.0) && (solucion.get(bloque+4) == 0.0) )  {
                          
                       switch(modelo[j]){
                           case "motorola_moto_g6":
                               suma += Profiles.model1_ScreenOff_Discharging[fila1][columna] - Profiles.model1_ScreenOff_Discharging[fila2][columna];
                               break;
                           case "samsung_SM_A305G":
                               suma += Profiles.model2_ScreenOff_Discharging[fila1][columna] - Profiles.model2_ScreenOff_Discharging[fila2][columna];
                               break;
                           case "Xiaomi_Mi_A2_Lite":
                               suma += Profiles.model3_ScreenOff_Discharging[fila1][columna] - Profiles.model3_ScreenOff_Discharging[fila2][columna];
                               break;
                           case "Xiaomi_Redmi_Note_7":
                               suma += Profiles.model4_ScreenOff_Discharging[fila1][columna] - Profiles.model4_ScreenOff_Discharging[fila2][columna];
                               break;
                      }
                  }	
                  else
                  if ( (solucion.get(bloque) == 1.0) && (solucion.get(bloque+4) == 1.0) ){
                       switch(modelo[j]){
                           case "motorola_moto_g6":
                               suma += Profiles.model1_ScreenOn_Discharging[fila1][columna] - Profiles.model1_ScreenOn_Discharging[fila2][columna];
                               break;
                           case "samsung_SM_A305G":
                               suma += Profiles.model2_ScreenOn_Discharging[fila1][columna] - Profiles.model2_ScreenOn_Discharging[fila2][columna];
                               break;
                           case "Xiaomi_Mi_A2_Lite":
                               suma += Profiles.model3_ScreenOn_Discharging[fila1][columna] - Profiles.model3_ScreenOn_Discharging[fila2][columna];
                               break;
                           case "Xiaomi_Redmi_Note_7":
                               suma += Profiles.model4_ScreenOn_Discharging[fila1][columna] - Profiles.model4_ScreenOn_Discharging[fila2][columna];
                               break;
                       }
                  }
              }  
              bloque_ant += secuencia_mobil[j]*cant_parametros;
              sumados.add(suma);
              sumatoria += Math.pow(suma - max_target, 2);
          }
          
          for(int j=0; j<sumados.size(); j++){
              solucion.add((double)sumados.get(j));
          }
          solucion.add(Math.sqrt(sumatoria/cant_mobil));
          sumados.clear();
      }
  }

  
//-------------------------------
  
  public static int tournament(){
      List<Integer> lista_id= new ArrayList<>();
      double sol_intermedia;                        
      double mejor_sol= Double.POSITIVE_INFINITY;   
      int indice_int_lista;                         
      int indice_int_pob;                           
      int indice_mejor_sol= -1;                     
      int indice_valor_fitness= poblacion.get(0).size()-1; 
      
      
      for(int i=0; i<cant_pob; i++){ 
          lista_id.add(i);
      }

      for(int i=0; i<cant_ind_torneo; i++){
          indice_int_lista= (int)(Math.random()*lista_id.size());
          indice_int_pob= lista_id.get(indice_int_lista);
          sol_intermedia= poblacion.get(indice_int_pob).get(indice_valor_fitness);

          if(sol_intermedia < mejor_sol){
              indice_mejor_sol= indice_int_pob;
              mejor_sol= sol_intermedia;
          }
          
          lista_id.remove(indice_int_lista);
      }
      lista_id= null; 

      return indice_mejor_sol;
  }

  
//-------------------------------
  
  public static void parentSelection(){
      
      for(int i= 0; i<cant_pob; i++){
          pob_padres.add(tournament());
      }
  }

  
//-------------------------------
  
  public static void crossPositionOfParents(ArrayList<Double> hijo1, ArrayList<Double> hijo2, int pos){
      double valor_intermedio= hijo1.get(pos);
      hijo1.set(pos, hijo2.get(pos));
      hijo2.set(pos, valor_intermedio);
  }
  
  
//-------------------------------
    
  public static void crossover(){
      
      int inicio_bloque, bloque_anterior, pos;
      double prob_aleatoria;
      int[] lista_pos_cruce= {2,3,4}; 
      ArrayList<Double> hijo1;
      ArrayList<Double> hijo2;

      for(int i=0; i<cant_pob; i= i+2){   
          hijo1= new ArrayList<>(poblacion.get(pob_padres.get(i)));
          hijo2= new ArrayList<>(poblacion.get(pob_padres.get(i+1)));
          bloque_anterior= 0;
          prob_aleatoria= Math.random();

          if(prob_aleatoria < prob_cruce){    
                            
              for(int j=0; j<cant_mobil; j++){    
                  
                  int bandera= -1; 

                  for(int k=0; k<secuencia_mobil[j]; k++){    
                      inicio_bloque= k*cant_parametros + bloque_anterior;

                      for(int l=0; l<lista_pos_cruce.length; l++){    
                          prob_aleatoria= Math.random();
                          
                          switch(lista_pos_cruce[l]){
                              case 2:
                                  if(bandera == -1){
                                      if(prob_aleatoria < prob_cruce_uniforme){
                                          bandera= 1;
                                      }
                                      else{
                                          bandera= 0;
                                      }
                                  }

                                  if(bandera == 1){
                                      pos= inicio_bloque+1;
                                      for(int ll=0;ll<2; ll++){
                                          crossPositionOfParents(hijo1, hijo2, pos);
                                          pos++;
                                      }
                                  }
                                  break;
                              case 3:
                                  if(prob_aleatoria < prob_cruce_uniforme){
                                      pos= inicio_bloque+3;
                                      crossPositionOfParents(hijo1, hijo2, pos);
                                  }
                                  break;
                              case 4:
                                  if(prob_aleatoria < prob_cruce_uniforme){
                                      pos= inicio_bloque+4;
                                      crossPositionOfParents(hijo1, hijo2, pos);
                                  }
                                  break;
                          }
                      }
                  }
                  bloque_anterior += secuencia_mobil[j]*cant_parametros;
              }
          }
          
          int max_indice= hijo1.size()-1;
          for(int j=0; j<cant_mobil+1; j++){
              hijo1.remove(max_indice);
              hijo2.remove(max_indice);
              max_indice--;
          }

          pob_hijos.add(hijo1);
          pob_hijos.add(hijo2);
      }
      pob_padres.clear();
  }

  
//-------------------------------
  
  public static void randomBatteryLevels(List<Integer> nivel_bat_aux, List<Integer> nivel_bat_intermedio, int indice){
      int pos;

      for(int i=0; i<secuencia_mobil[indice]-1; i++){
          pos= (int)(Math.random()*nivel_bat_aux.size());
          nivel_bat_intermedio.add(nivel_bat_aux.get(pos));
          nivel_bat_aux.remove(pos);
      }
  }

  
//-------------------------------
  
  public static void mutation(){
      
      int inicio_bloque, bloque_anterior, pos;
      double prob_aleatoria, valor_intermedio;
      int[] lista_pos_mutar= {2,3,4}; 
      List<Integer> nivel_bat_aux, nivel_bat_intermedio, cpu_aux, niveles_bateria= new ArrayList<>();
      
      for(int i=0; i<=100; i++){ 
          niveles_bateria.add(i);
      }

      for(int i=0; i<pob_hijos.size(); i++){ 
          inicio_bloque= bloque_anterior= 0;

          for(int j=0; j<cant_mobil; j++){ 
              nivel_bat_aux= new ArrayList<>(niveles_bateria);
              nivel_bat_intermedio= new ArrayList<>();
              prob_aleatoria= Math.random();
              int bandera= 0; 
              
              if((prob_aleatoria < prob_mutacion) && (nivel_bat_act[j] == nivel_bat_obj[j])) { 
              	bandera = 1;
              	
              	if(nivel_bat_act[j] < 50) { 
              		nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_act[j]+1, 101);
              		randomBatteryLevels(nivel_bat_aux, nivel_bat_intermedio, j);   
              	}
              	else
              	if(nivel_bat_act[j] >= 50) { 
                      nivel_bat_aux= nivel_bat_aux.subList(0, nivel_bat_act[j]);
                  	randomBatteryLevels(nivel_bat_aux, nivel_bat_intermedio, j);     
                  }
              	
              }
              else
              if(prob_aleatoria < prob_mutacion && secuencia_mobil[j] > 1){
                  bandera= 1;

                  if(nivel_bat_act[j] < nivel_bat_obj[j]){  //Tarea de carga
                      nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_act[j]+1, nivel_bat_obj[j]);
                      randomBatteryLevels(nivel_bat_aux, nivel_bat_intermedio, j);
                      Collections.sort(nivel_bat_intermedio);
                  }
                  else{ 
                      nivel_bat_aux= nivel_bat_aux.subList(nivel_bat_obj[j]+1, nivel_bat_act[j]);
                      randomBatteryLevels(nivel_bat_aux, nivel_bat_intermedio, j);
                      nivel_bat_intermedio.sort(Comparator.reverseOrder());
                  }
              }

              for(int k=0; k<secuencia_mobil[j]; k++){    
                  inicio_bloque= k*cant_parametros + bloque_anterior;

                  for(int l=0; l<lista_pos_mutar.length; l++){    
                      prob_aleatoria= Math.random();
                      
                      switch(lista_pos_mutar[l]){
                          case 2: 
                              if(bandera == 1){
                                  if(k == 0){ 
                                      pos= inicio_bloque+2;
                                      valor_intermedio= (double)nivel_bat_intermedio.get(0);
                                      pob_hijos.get(i).set(pos, valor_intermedio);
                                      nivel_bat_intermedio.remove(0);
                                  }
                                  else if(k == secuencia_mobil[j]-1){ 
                                      pos= inicio_bloque+1;
                                      pob_hijos.get(i).set(pos, pob_hijos.get(i).get(pos-cant_parametros+1));
                                  }
                                  else{ 
                                      pos= inicio_bloque+1;
                                      pob_hijos.get(i).set(pos, pob_hijos.get(i).get(pos-cant_parametros+1));

                                      pos++;
                                      valor_intermedio= (double)nivel_bat_intermedio.get(0);
                                      pob_hijos.get(i).set(pos, valor_intermedio);
                                      nivel_bat_intermedio.remove(0);
                                  }
                              }
                              break;
                          case 3: 
                              if(prob_aleatoria < prob_mutacion){
                                  pos= inicio_bloque+3;
                                  cpu_aux= new ArrayList<>(Arrays.asList(estado_CPU));
                                  cpu_aux.remove(cpu_aux.indexOf(pob_hijos.get(i).get(pos).intValue()));
                                  valor_intermedio= cpu_aux.get((int)(cpu_aux.size()*Math.random()));
                                  pob_hijos.get(i).set(pos, (double)valor_intermedio);
                              }
                              break;
                          case 4: 
                              if(prob_aleatoria < prob_mutacion){
                                  pos= inicio_bloque+4;
                                  valor_intermedio= 0;
                                  if(pob_hijos.get(i).get(pos) == 0.0){
                                      valor_intermedio= 1;
                                  }
                                  pob_hijos.get(i).set(pos, (double)valor_intermedio);
                              }
                              break;
                      }
                  }
              }
              bloque_anterior += secuencia_mobil[j]*cant_parametros;
          }
      }
  }

  
//-------------------------------
  
  public static void quickSort(ArrayList<ArrayList<Double>> pob, int start, int end){
      int q;

      if(start<end){
          q = partition(pob, start, end);
          quickSort(pob, start, q);
          quickSort(pob, q+1, end);
      }
  }

  
//-------------------------------
  
  static int nextIntInRange(int min, int max, Random rng) {
      if (min > max) {
         throw new IllegalArgumentException("Rango no valido [" + min + ", " + max + "].");
      }
      int diff = max - min;
      if (diff >= 0 && diff != Integer.MAX_VALUE) {
         return (min + rng.nextInt(diff + 1));
      }
      int i;
      do {
         i = rng.nextInt();
      } while (i < min || i > max);
      return i;
   }

  
//-------------------------------
  
  static int partition(ArrayList<ArrayList<Double>> pob, int start, int end){
      int init = start;
      int length = end;
      int indice_valor_fitness= pob.get(0).size()-1; 
      ArrayList<Double> pivot;
      ArrayList<Double> temp;
      
      Random r = new Random();
      int pivotIndex = nextIntInRange(start,end,r);
      pivot = pob.get(pivotIndex);
              
      while(true){
          while(pob.get(length).get(indice_valor_fitness) > pivot.get(indice_valor_fitness) && length > start){
              length--;
          }
          
          while(pob.get(init).get(indice_valor_fitness) < pivot.get(indice_valor_fitness) && init < end){
              init++;
          }
          
          if(init<length){
              temp = pob.get(init);
              pob.set(init, pob.get(length));
              pob.set(length, temp);
              length--;
              init++;
          }
          else{
              return length;
          }
      } 
  }

  
//-------------------------------
  
  public static void survivalSelection(){
      int pos=cant_pob-1;
      
      quickSort(poblacion, 0, pos);

      quickSort(pob_hijos, 0, pos);

      double mejor_padre= poblacion.get(0).get(poblacion.get(0).size()-1);
      double mejor_hijo= pob_hijos.get(0).get(pob_hijos.get(0).size()-1);

      if(mejor_hijo < mejor_padre){
          mejor_padre= mejor_hijo;
      }

      for(int i=0; i<cant_st; i++){
          poblacion.set(pos, pob_hijos.get(i));
          pos--;
      }

      pob_hijos.clear();
      pob_padres.clear();

      
  }

  
//-------------------------------
    
  public static void main(String[] args) throws IOException {
	        
      InputOutput.instancia = args[0];
      
      try {
    	  
          Profiles.filterProfiles();
          Profiles.filterProfilesDischarging();
          InputOutput.readMoviles();
          
      } catch (Exception e) {
          e.printStackTrace();
      }

      setTarget();
      
      setSequenceLength();
            
      if(!IntStream.of(secuencia_mobil).anyMatch(x -> x > 0)){
          System.out.println("All smartphones are ready");
      }
      else
      {
          long startTime = System.currentTimeMillis();
          
          initialPopulation();  
          
          setProbMutation();  
          
          fitness(poblacion);
          
                   
          for(int i=0; i<cant_gen; i++){
              
        	  parentSelection();
              
              crossover();
                            
              mutation();
                            
              fitness(pob_hijos);
              
              survivalSelection();
              
          }
                    
          long endTime = System.currentTimeMillis();
          
          long time = ((endTime - startTime)/1000);
          
          InputOutput.writeBestSolutionJson(poblacion.get(0), time);
          
          
      }
      
  }
  
  
//-------------------------------
  
}

