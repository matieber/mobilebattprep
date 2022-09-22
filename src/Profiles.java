import java.io.*;

public class Profiles {
 	
		
	// model motorola_moto_g6
	public static int[][] model1_ScreenOn = new int[101][5];
	public static int[][] model1_ScreenOff = new int[101][5];
	public static int[][] model1_ScreenOn_Discharging = new int[101][5];
	public static int[][] model1_ScreenOff_Discharging = new int[101][5];
	
	
	// model samsung_SM_A305G
    public static int[][] model2_ScreenOn = new int[101][5];
    public static int[][] model2_ScreenOff = new int[101][5];
    public static int[][] model2_ScreenOn_Discharging = new int[101][5];
    public static int[][] model2_ScreenOff_Discharging = new int[101][5];
    
 
    // model Xiaomi_Mi_A2_Lite
	public static int[][] model3_ScreenOn = new int[101][5];
	public static int[][] model3_ScreenOff = new int[101][5];
	public static int[][] model3_ScreenOn_Discharging = new int[101][5];
	public static int[][] model3_ScreenOff_Discharging = new int[101][5];
	
	
    // model Xiaomi_Redmi_Note_7
	public static int[][] model4_ScreenOn = new int[101][5];
	public static int[][] model4_ScreenOff = new int[101][5];
	public static int[][] model4_ScreenOn_Discharging = new int[101][5];
	public static int[][] model4_ScreenOff_Discharging = new int[101][5];
	
	
	
	//----------------------------------	
		
	public static int get_CPU_index(String origen) {
		
		int index = -1;
		
		if (origen.contains("battery0"))
			index = 0;
		else
		if (origen.contains("battery30"))
	 		index = 1;
	 	else
	    if (origen.contains("battery50"))
	 		index = 2;
	    else
	    if (origen.contains("battery75"))
	 		index = 3;
	 	else
	 	if (origen.contains("battery100"))
	 		index = 4;
		
		return index;
	 			
	}
	
	
	//----------------------------------
	
	public static void set_milisegundos(String origen, int nivel_bat, int milisegundos) {
		
		if (origen.contains("motorola_moto_g6")) {
			if (origen.contains("screenOn"))
				model1_ScreenOn[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model1_ScreenOff[nivel_bat][get_CPU_index(origen)] = milisegundos;
		}
        else
        if (origen.contains("samsung_SM_A305G")) {
     	    if (origen.contains("screenOn"))
				model2_ScreenOn[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model2_ScreenOff[nivel_bat][get_CPU_index(origen)] = milisegundos;	
        }
        else	
        if (origen.contains("Xiaomi_Mi_A2_Lite")) {
     	    if (origen.contains("screenOn"))
				model3_ScreenOn[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model3_ScreenOff[nivel_bat][get_CPU_index(origen)] = milisegundos;	
        }
        else
        if (origen.contains("Xiaomi_Redmi_Note_7")) {
     	    if (origen.contains("screenOn"))
				model4_ScreenOn[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model4_ScreenOff[nivel_bat][get_CPU_index(origen)] = milisegundos;
        }		
		
	}
	
	
	//----------------------------------
	
	public static void filter(String origen, String destino) throws Exception {
		
		File profiles = new File(origen);
	    BufferedReader profilesRead = new BufferedReader(new FileReader(profiles));

	    File file = new File(destino);
             
        if (!file.exists()) {
           file.createNewFile();
        }
     
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
     	    
        String strng;
        String marca_comienzo = "ADD_NODE";
		String marca_final = "LEFT_NODE";
		int valor_anterior = -1;
     
        while ((strng = profilesRead.readLine()) != null)
	    {	            
	    		String[] linea = strng.split(";");
	    		
	    		if (!linea[0].equals(marca_comienzo) && !linea[0].equals(marca_final)) {
	    			
	    			int nivel_bat = Integer.parseInt(linea[3]);
	    			int nivel_bat_real = nivel_bat / 100000;
	    			
	    			if (nivel_bat_real != valor_anterior) {
	    				
	    				String contenido;
	    				
	    				if (nivel_bat_real < 100)
	    				     contenido = nivel_bat_real + " " + linea[1] + "\n" ;
	    				else
	    					contenido = nivel_bat_real + " " + linea[1] ;
	    		            
	                    bw.write(contenido);
	                    
	                    int milisegundos = Integer.parseInt(linea[1]);
			            
	                    set_milisegundos(origen, nivel_bat_real, milisegundos);
	                    	                    
	                    valor_anterior = nivel_bat_real;
	    		        				
	    			}
	    		}
	    }
    	
		profilesRead.close();
        bw.close();
	}
			
	
	//----------------------------------
	
	public static void filterProfiles( ) throws Exception{
		
		File profiles = new File("./profilesFiltered.txt");
	    BufferedReader profilesRead = new BufferedReader(new FileReader(profiles));
		
	    String strng;
		
	    while ((strng = profilesRead.readLine()) != null)
	    {	            
	    		String[] linea = strng.split(" ");
	    		
	    		filter(linea[0], linea[1]);
	        	
	    }			
		
		profilesRead.close();
		
	}	
	
	
	//----------------------------------
	
	public static void set_milisegundos_Discharging(String origen, int nivel_bat, int milisegundos) {
		
		if (origen.contains("motorola_moto_g6")) {
			if (origen.contains("screenOn"))
				model1_ScreenOn_Discharging[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model1_ScreenOff_Discharging[nivel_bat][get_CPU_index(origen)] = milisegundos;
		}
        else
        if (origen.contains("samsung_SM_A305G")) {
     	    if (origen.contains("screenOn"))
				model2_ScreenOn_Discharging[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model2_ScreenOff_Discharging[nivel_bat][get_CPU_index(origen)] = milisegundos;	
        }
        else	
        if (origen.contains("Xiaomi_Mi_A2_Lite")) {
     	    if (origen.contains("screenOn"))
				model3_ScreenOn_Discharging[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model3_ScreenOff_Discharging[nivel_bat][get_CPU_index(origen)] = milisegundos;	
        }
        else
        if (origen.contains("Xiaomi_Redmi_Note_7")) {
     	    if (origen.contains("screenOn"))
				model4_ScreenOn_Discharging[nivel_bat][get_CPU_index(origen)] = milisegundos;
			else
			if (origen.contains("screenOff"))
				model4_ScreenOff_Discharging[nivel_bat][get_CPU_index(origen)] = milisegundos;
        }		
		
	}

	
	//----------------------------------
	
    public static void filterDischarging(String origen, String destino) throws Exception {
		
		File profiles = new File(origen);
	    BufferedReader profilesRead = new BufferedReader(new FileReader(profiles));

	    File file = new File(destino);
             
        if (!file.exists()) {
           file.createNewFile();
        }
     
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
     	    
        String strng;
        String marca_comienzo = "ADD_NODE";
		String marca_final = "LEFT_NODE";
		
		int nivel_bat = 0;
		int nivel_bat_real = 0;
		int milisegundos = 0;
		String contenido;
		
		int nivel_bat_ant = 0;
		int nivel_bat_real_ant = 0;
		int milisegundos_ant = 0;
		
		     
        while ((strng = profilesRead.readLine()) != null)
	    {	            
	    		String[] linea = strng.split(";");
	    		
	    		if (linea[0].equals(marca_comienzo)) {
	    			
	    			strng = profilesRead.readLine();
	    			
	    			linea = strng.split(";");
	    			
	    			nivel_bat_ant = Integer.parseInt(linea[3]);
	    		    nivel_bat_real_ant = nivel_bat_ant / 100000;   			
	    		    milisegundos_ant = Integer.parseInt(linea[1]);
	    		    
	    		}
	    		else
	    		if (!linea[0].equals(marca_comienzo) && !linea[0].equals(marca_final)) {
	    			
	    			nivel_bat = Integer.parseInt(linea[3]);
	    		    nivel_bat_real = nivel_bat / 100000;
	    		    milisegundos = Integer.parseInt(linea[1]);
	    			
	    			if(nivel_bat_real != nivel_bat_real_ant) {
	    		      
	    				contenido = nivel_bat_real_ant + " " + milisegundos_ant + "\n" ;
	    				            
	                    bw.write(contenido);
	                    
	                    set_milisegundos_Discharging(origen, nivel_bat_real_ant, milisegundos_ant);
	    			}    
	                    
	    			milisegundos_ant = milisegundos;
	                nivel_bat_real_ant = nivel_bat_real;
			                       
	    		}
	    		else
	    		if (linea[0].equals(marca_final)) {
	    			
	    			contenido = nivel_bat_real_ant + " " + milisegundos_ant + "\n" ;
		            
                    bw.write(contenido);
                    
                    set_milisegundos_Discharging(origen, nivel_bat_real_ant, milisegundos_ant);
	    		    			
	    		}
	    	    		
	    }
        
        nivel_bat_real_ant = nivel_bat_real_ant - 1;
        
        while(nivel_bat_real_ant > 0) {
        	
        	contenido = nivel_bat_real_ant + " " + milisegundos_ant + "\n" ;
        	
            bw.write(contenido);
        	
        	set_milisegundos_Discharging(origen, nivel_bat_real_ant, milisegundos_ant);
        	
        	nivel_bat_real_ant = nivel_bat_real_ant - 1;    	
        }
        
        if(nivel_bat_real_ant == 0) {
        	
        	contenido = nivel_bat_real_ant + " " + milisegundos_ant ;
        	
            bw.write(contenido);
        	
        	set_milisegundos_Discharging(origen, nivel_bat_real_ant, milisegundos_ant);
               	
        }
        
        profilesRead.close();
        bw.close();
	}
	
    
  //----------------------------------
	
	public static void filterProfilesDischarging( ) throws Exception{
		
		File profiles = new File("./profilesFilteredDischarging.txt");
	    BufferedReader profilesRead = new BufferedReader(new FileReader(profiles));
		
	    String strng;
		
	    while ((strng = profilesRead.readLine()) != null)
	    {	            
	    		String[] linea = strng.split(" ");
	    		
	    		filterDischarging(linea[0], linea[1]);
	    
	    		
	    }			
		
		profilesRead.close();
				
		
	}
	
		
	//----------------------------------
	
	
	
}

