package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import pcd.util.Ventana;

public class ServidorPuerto {

	// Creamos dos ventanas, una para cada apartado de la ampliacion
	
	private static Ventana v = new Ventana("Contador Barcos");
	private static Ventana v1 = new Ventana("Contador Mercancias");

	public static void main(String[] args) {

		String nombreObjetoContadorCola; // Para guardar el nombre del servicio1
		
		/*
		 * Aunque existe la posibilidad de facilitar el nombre del servicio por parametros,
		 * utilizaremos el por defecto
		 */
		try {

			nombreObjetoContadorCola = args[0];
		}

		catch (ArrayIndexOutOfBoundsException e) {
			
			nombreObjetoContadorCola = "ContadorCola";
			
			v.addText("\nNo se ha especificado nombre objeto servidor");
			v.addText("usando nombre por defecto " + nombreObjetoContadorCola + "\n");
		}
		
		String nombreObjetoContadorMercancia; // Para guardar el nombre del servicio2

		try {

			nombreObjetoContadorMercancia = args[1];
		}

		catch (ArrayIndexOutOfBoundsException e) {
			
			nombreObjetoContadorMercancia = "ContadorMercancia";
			
			v1.addText("\nNo se ha especificado nombre objeto servidor");
			v1.addText("usando nombre por defecto " + nombreObjetoContadorMercancia + "\n");
		}
		
		try {
			
			ContadorColaBarcosImpl contCola = new ContadorColaBarcosImpl(v); // Instanciamos la clase
			
			/*
			 * Obtenemos la interfaz
			 */
			ContadorColaBarcos stubContCola = (ContadorColaBarcos) UnicastRemoteObject.exportObject(contCola,0);

			
			Registry registry = LocateRegistry.getRegistry(); // Obtenemos la instancia del registro RMI
			
			/*
			 * Creamos el servicio1 usando nombre e interfaz
			 */
			registry.rebind(nombreObjetoContadorCola, stubContCola);
			
			v.addText("Servidor..... " + nombreObjetoContadorCola + "...... listo");
			/////////////////////////////////////////////////////////////////////////
			
			/*
			 * Se siguen los mismos pasos para el servicio2
			 */
			ContadorAbastosImpl contAbastos = new ContadorAbastosImpl(v1);
			ContadorAbastos stubContAbastos = (ContadorAbastos) UnicastRemoteObject.exportObject(contAbastos,0);
			
			registry.rebind(nombreObjetoContadorMercancia, stubContAbastos);
			v1.addText("Servidor..... " + nombreObjetoContadorMercancia + "...... listo");	
			
		}
		
		catch(Exception e) {
			
			System.err.println("Excepcion en servidor" + e.getMessage());
			e.printStackTrace();
		}
	}
}
