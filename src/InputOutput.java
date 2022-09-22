import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class InputOutput {
	
  static String instancia; 	
  static String rutaInstancia = "./instances/";
  static String rutaInstanciaResultados = "./";
	
		
//----------------------------------  
      
  public static void readMoviles() throws Exception {
	  
  	ArrayList<String> modelo = new ArrayList<>();
  	ArrayList<Integer> nivel_bat_act = new ArrayList<>();
  	ArrayList<Integer> nivel_bat_obj = new ArrayList<>();
  	int cant_mobil = 0;
  	
  	  	
  	String nombre = rutaInstancia + instancia;
  	
  	File moviles = new File(nombre);
  	  	
	BufferedReader movilesRead = new BufferedReader(new FileReader(moviles));
  	
	String linea;
	    
	while ((linea = movilesRead.readLine()) != null)
	{	            
		String[] lineaTemp = linea.split(" ");
			
		modelo.add(lineaTemp[1]);
		nivel_bat_act.add(Integer.parseInt(lineaTemp[2]));
		nivel_bat_obj.add(Integer.parseInt(lineaTemp[3]));
		cant_mobil++;	
	}			
		
	Main.cant_mobil = cant_mobil;
	Main.modelo = new String[cant_mobil];
	Main.nivel_bat_act = new Integer[cant_mobil];
	Main.nivel_bat_obj = new Integer[cant_mobil];
	    
	for (int i = 0; i < cant_mobil; i++) {
	    	
	    Main.modelo[i] = modelo.get(i);
	    Main.nivel_bat_act[i] = nivel_bat_act.get(i);
	    Main.nivel_bat_obj[i] = nivel_bat_obj.get(i);
	        	
	}
	
	movilesRead.close();
	
  }
  
  
//----------------------------------
    
 public static void writeBestSolutionJson(ArrayList<Double> solucion, long timeEA) throws IOException {
  	
  	String nombre = rutaInstanciaResultados + "EA_Solution_Json_" + instancia;
  	
  	File file = new File(nombre);
  	
  	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
  	
  	String linea;
  	int cant_moviles = Main.cant_mobil;
  	
  	//-----------start Json-----------
  	
  	linea = "{" + "\n"; 
  	writer.write(linea);
  	
  	//-----------start solution:------
  	
  	linea = "    solution: [" + "\n";  
  	writer.write(linea);
  	
  	int posicion = 0;
  	
  	for (int j=0; j < cant_moviles; j++) {
  		
  		linea = "            {" + "\n"; 
  		writer.write(linea);
  		
  		linea = "                \"" + Main.modelo[j] +"\": [" + "\n"; 
  		
  		writer.write(linea);    		  		
  		
  		for(int i=1; i<=Main.secuencia_mobil[j]; i++){ 
			for(int p=0; p<Main.cant_parametros; p++){ 
			
			   int valor_posicion;
  			
  			   if(p==0) {
  				linea = "                        {" + "\n";
  				writer.write(linea);
  				valor_posicion = solucion.get(posicion).intValue();
  				if (valor_posicion == 0)
  				    linea = "                         \"batt_state\":\"charging_ac\"," + "\n";
  				else
  					linea = "                         \"batt_state\":\"discharging_ac\"," + "\n";
  				writer.write(linea);    								
  			   }
  			   else
  			   if(p==1) {
  				valor_posicion = solucion.get(posicion).intValue();
  				double valor = valor_posicion / 100.0;
  				linea = "                         \"initial_level\": " + valor + "," + "\n";
  				writer.write(linea);    				
  			   }
  			   else
      		   if(p==2) {
      			valor_posicion = solucion.get(posicion).intValue();
      			double valor = valor_posicion / 100.0;
      			linea = "                         \"target_level\": " + valor + "," + "\n";
      			writer.write(linea);
      		   }	
      		   else
      		   if(p==3) {
      			valor_posicion = solucion.get(posicion).intValue();
      			double valor = valor_posicion / 100.0;
      			linea = "                         \"target_cpu\": " + valor + "," + "\n";
      			writer.write(linea);        			
      		   }
      		   else
      		   if(p==4) {
      			valor_posicion = solucion.get(posicion).intValue();
      			String estado = "off";
      			if(valor_posicion == 1)
      				estado = "on";        			      			
      			linea = "                         \"screen_state:\":" + "\"" + estado + "\"" + "\n";
      			writer.write(linea);
      			
      			if (i< Main.secuencia_mobil[j])
      				linea = "                        }," + "\n";
      			else 
      		    if (i==Main.secuencia_mobil[j])
      		    	linea = "                        }" + "\n";
      			writer.write(linea);		
      			
      		   }
  			 
  			   posicion++;
  		    } 
  		
  		}
  		
  		
  		linea = "                ]" + "\n";     		
  		writer.write(linea);
  		
  		
  		if (j < cant_moviles-1)    
  			linea = "            }," + "\n";
  		else
  		if (j == cant_moviles-1)
  			linea = "            }" + "\n";
  		writer.write(linea);
  		
  		    		
  	} 
  	  	
  	linea = "    ]" + "\n";  
  	writer.write(linea);
  	
  	//--------end solution:----------------
  	
  	//--------start extra:-----------------
  	  	
  	linea = "    extra: {" + "\n"; 
  	writer.write(linea);
  	
  	//----------EA Solution - Times t(si)--
  	
  	int comienzo_tiempos = posicion;
  	
  	linea = "        EA Solution - Times t(si) (milliseconds): [";
  	
  	int pos, valor, k;
  	
  	for (k = 0; k < cant_moviles; k++) {
  		pos = comienzo_tiempos + k;
  		valor = solucion.get(pos).intValue();
  		linea = linea + valor;
  		if (k < cant_moviles-1)
  			linea = linea + ",";
  	}
  	
  	linea = linea + "]," + "\n";  
  	writer.write(linea);
  	
  	//----------M Solution - Times t(si)----
  	
  	linea = "        M Solution - Times t(si) (milliseconds): [";
  	
  	for(int i=0; i<Main.target.size(); i++){
  		valor = Main.target.get(i).intValue();
  		linea = linea + valor;
  		if (i < Main.target.size()-1)
  			linea = linea + ",";
  	}
  	
  	linea = linea + "]," + "\n";  
  	writer.write(linea);
  	
  	//----------EA Solution - MAPE (%)-------
  	
  	double max_target= Collections.max(Main.target);   
  	double sumatoria = 0.0;
  	
  	for (k = 0; k < cant_moviles; k++) {
  		
  		pos = comienzo_tiempos + k;
  		valor = solucion.get(pos).intValue();
  		
  		sumatoria = sumatoria + (Math.abs(max_target - valor) / max_target);
  	
  	}
  	
  	sumatoria = sumatoria * (100.0 / cant_moviles);
  	
  	linea = "        EA Solution - MAPE(%): [" + sumatoria + "]," + "\n";
  	writer.write(linea);  	
  	
  	//----------M Solution - MAPE (%)--------
  	
  	sumatoria = 0.0;
  	
  	for(int i=0; i<Main.target.size(); i++){
  		
  		valor = Main.target.get(i).intValue();
  		
  		sumatoria = sumatoria + (Math.abs(max_target - valor) / max_target);
  		
  	}
  	
      sumatoria = sumatoria * (100.0 / cant_moviles);
  	
  	linea = "        M Solution - MAPE(%): [" + sumatoria + "]," + "\n";
  	writer.write(linea);  	
  	
  	//----------EA Solution - bud(i)---------
  	
  	int max_moviles_prep_time = solucion.get(comienzo_tiempos).intValue();
  	  	
    for (k = 0; k < cant_moviles; k++) {
  		
  		pos = comienzo_tiempos + k;
  		valor = solucion.get(pos).intValue();
  		if(valor > max_moviles_prep_time)
  			max_moviles_prep_time = valor;
  	
  	}
  	    
    linea = "        EA Solution - bud(i): [";
    
    int unidades_perdidas_solucion[] = new int[cant_moviles];
   
    for (k = 0; k < cant_moviles; k++) {
    	
    	pos = comienzo_tiempos + k;
  		valor = solucion.get(pos).intValue();
  		
  		int tiempo_ocioso = max_moviles_prep_time - valor;
    	
    	int nivel_bat_objetivo = Main.nivel_bat_obj[k];
    	
    	boolean termine = false;
    	int tiempo_unidad=0;
    	int unidades_perdidas=0;
    	
    	while (nivel_bat_objetivo > 0 && !termine) {
    		
    		switch(Main.modelo[k]){
            case "motorola_moto_g6":
            	tiempo_unidad += Profiles.model1_ScreenOff_Discharging[nivel_bat_objetivo-1][0] - Profiles.model1_ScreenOff_Discharging[nivel_bat_objetivo][0];
                break;
            case "samsung_SM_A305G":
            	tiempo_unidad += Profiles.model2_ScreenOff_Discharging[nivel_bat_objetivo-1][0] - Profiles.model2_ScreenOff_Discharging[nivel_bat_objetivo][0];
                break;
            case "Xiaomi_Mi_A2_Lite":
            	tiempo_unidad += Profiles.model3_ScreenOff_Discharging[nivel_bat_objetivo-1][0] - Profiles.model3_ScreenOff_Discharging[nivel_bat_objetivo][0];
                break;
            case "Xiaomi_Redmi_Note_7":
            	tiempo_unidad += Profiles.model4_ScreenOff_Discharging[nivel_bat_objetivo-1][0] - Profiles.model4_ScreenOff_Discharging[nivel_bat_objetivo][0];
                break;
            }
    		
    		if(tiempo_ocioso < tiempo_unidad)
    			termine=true;
    		else
    		if(tiempo_ocioso >= tiempo_unidad) {
    			unidades_perdidas++;
    			nivel_bat_objetivo--;    			
    		}
    	}
    	
    	unidades_perdidas_solucion[k] = unidades_perdidas;
    	
    	if (k == cant_moviles -1)
    	  linea = linea + unidades_perdidas + "]," + "\n";
    	else
    	  linea = linea + unidades_perdidas + ", ";
    	    	
    }
    
    writer.write(linea);
        
    //-----------M Solution - bud(i) --------
    
   	max_moviles_prep_time = Collections.max(Main.target).intValue(); 
   	  	
   	linea = "        M Solution - bud(i): [";
   	
   	int unidades_perdidas_solucion_manual[] = new int[cant_moviles];
    
    for (k = 0; k < cant_moviles; k++) {
    	
    	valor = Main.target.get(k).intValue();
  		
  		int tiempo_ocioso = max_moviles_prep_time - valor;
  		
  		
    	int nivel_bat_objetivo = Main.nivel_bat_obj[k];
    	
    	boolean termine = false;
    	int tiempo_unidad=0;
    	int unidades_perdidas=0;
    	
    	while (nivel_bat_objetivo > 0 && !termine) {
    		
    		switch(Main.modelo[k]){
            case "motorola_moto_g6":
            	tiempo_unidad += Profiles.model1_ScreenOff_Discharging[nivel_bat_objetivo-1][0] - Profiles.model1_ScreenOff_Discharging[nivel_bat_objetivo][0];
                break;
            case "samsung_SM_A305G":
            	tiempo_unidad += Profiles.model2_ScreenOff_Discharging[nivel_bat_objetivo-1][0] - Profiles.model2_ScreenOff_Discharging[nivel_bat_objetivo][0];
                break;
            case "Xiaomi_Mi_A2_Lite":
            	tiempo_unidad += Profiles.model3_ScreenOff_Discharging[nivel_bat_objetivo-1][0] - Profiles.model3_ScreenOff_Discharging[nivel_bat_objetivo][0];
                break;
            case "Xiaomi_Redmi_Note_7":
            	tiempo_unidad += Profiles.model4_ScreenOff_Discharging[nivel_bat_objetivo-1][0] - Profiles.model4_ScreenOff_Discharging[nivel_bat_objetivo][0];
                break;
            }
    		
    		if(tiempo_ocioso < tiempo_unidad)
    			termine=true;
    		else
    		if(tiempo_ocioso >= tiempo_unidad) {
    			unidades_perdidas++;
    			nivel_bat_objetivo--;    			
    		}
    	}
    	
    	unidades_perdidas_solucion_manual[k] = unidades_perdidas;
    	
    	if (k == cant_moviles -1)
    	  linea = linea + unidades_perdidas + "]," + "\n";
    	else
    	  linea = linea + unidades_perdidas + ", ";
    	    	
    }
    
    writer.write(linea);
    
    //------- EA Solution - RPD(%) --------
    
    double ahorro_movil = 0.0;
    
    for (k = 0; k < cant_moviles; k++) {
    	
    	double target = unidades_perdidas_solucion_manual[k];
    	double observado = unidades_perdidas_solucion[k];
    	
    	if(target > 0)
    	    ahorro_movil = ahorro_movil + ((target - observado)/target)*100;
    	else
    	    ahorro_movil = ahorro_movil + 0.0;	
    		
    	
    }
    
    ahorro_movil = ahorro_movil / cant_moviles;
    
    linea = "        EA Solution - RPD(%): [" + ahorro_movil + "],\n";
    writer.write(linea);
    
    //------- EA Solution - Computing Time ---
    
    linea = "        EA Solution - Computing time (seconds): [" + timeEA + "]\n";
    writer.write(linea);
    
    //---------------------------------------
    
  	linea = "    }" + "\n" + "\n";
  	writer.write(linea);
  	
  	//------------end extra:-----------------
  	
  	linea = "}";  
  	writer.write(linea);
  	
  	//-----------end Json--------------------
  	
  	writer.close(); 	
  	
  	
  }

 

//----------------------------------  
  
    
}    
