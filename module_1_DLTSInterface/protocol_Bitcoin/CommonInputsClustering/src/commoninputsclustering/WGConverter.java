/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commoninputsclustering;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.martiansoftware.jsap.JSAPException;

public class WGConverter {

	//Each file has .arcs and contains the sorted list of arcs
	private static final String[] FILE_BASENAME_LIST={"prova"};
	private static final String PREFIX="bv";
	
	public static void fromArcListToWebgraph( String input, String output ) throws SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IOException, JSAPException{
		////////it.unimi.dsi.webgraph.BVGraph.main( ("-g ArcListASCIIGraph "+ input + ".arcs " + output).split( " ") );
		it.unimi.dsi.webgraph.BVGraph.main( ("-g ArcListASCIIGraph "+ input + " " + output).split( " ") );
	}
	
	public static void convert( String[] args ) throws SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IOException, JSAPException{
		for ( String fileName : args ){
			WGConverter.fromArcListToWebgraph( fileName, PREFIX+fileName );
		}
	}
        public static void convert(String in) throws SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IOException, JSAPException{
            //WGConverter.fromArcListToWebgraph(in, in.substring(0, in.lastIndexOf("/")+1)+PREFIX+in.substring(in.lastIndexOf("/")+1,in.length()-5));
            WGConverter.fromArcListToWebgraph(in, in);
	}
        public static void convert(String in,String out) throws SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IOException, JSAPException{
            WGConverter.fromArcListToWebgraph(in,out+".arcs");
	}
}