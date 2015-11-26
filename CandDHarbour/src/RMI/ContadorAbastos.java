package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ContadorAbastos extends Remote {
	
	/*
	 * Incrementa en uno el contador de contenedores de azucar descargados
	 */
	public void dejarAzucar () throws RemoteException;
	
	/*
	 * Incrementa en uno el contador de contenedores de harina descargados
	 */
	public void dejarHarina ( ) throws RemoteException;
	
	/*
	 * Incrementa en uno el contador de contenedores de sal descargados
	 */
	public void dejarSal () throws RemoteException;
	
	/*
	 * Se debe llamar al finzalizar cada ejecucion.
	 * Guarda en fichero el resultado de la simulacion, muestra los contenedores descargados y
	 * restablece los valores para una nueva ejecucion.
	 */
	public void finDescarga() throws RemoteException;
}
