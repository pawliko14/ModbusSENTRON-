import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class IniFile 
	{ 
	public static String LICZBA_POMIAROW= "" ;
	public static String CZESTOTLIWOSC = "";
	public static String register = "";
	public static String Offset= "" ;
	public static String Directory= "";
	
	  public static void Ini() throws IOException 
	  { 
	  File file = new File("ini.txt");   
	  BufferedReader br = new BufferedReader(new FileReader(file));   
	  String st; 
	  int temp = 0;
	  int x = 0;
	  
	  while ((st = br.readLine()) != null) {
	  		for(int i = 0; i< st.length();i++)
	  		{
	  			if(st.charAt(i) == '=')
	  			{
	  			i++;
		  			if(x == 0)
		  				LICZBA_POMIAROW = st.substring(i, st.length());
		  			else if (x == 1)
		  				CZESTOTLIWOSC = st.substring(i, st.length());
		  			else if (x == 2)
		  				register = st.substring(i, st.length());
		  			else if (x == 3)
		  				Offset = st.substring(i, st.length());
		  			else if (x == 4)
		  				Directory = st.substring(i, st.length());			
	  			}		
	  		} 
	  		x++;
	      }   
	  } 
	} 

