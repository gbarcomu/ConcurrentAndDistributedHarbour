package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ContadorColaBarcos extends Remote {
	
	/*
	 * Metodo que indica que hay un barco mas esperando para entrar.
	 */
	public void incrementarEsperaEntrada (int id) throws RemoteException;
	
	/*
	 * Metodo que indica que hay un barco menos esperando para entrar.
	 */
	public void incrementarEsperaSalida (int id) throws RemoteException;
	
	/*
	 * Metodo que indica que hay un barco mas esperando para salir.
	 */
	public void decrementarEsperaEntrada (int id) throws RemoteException;
	
	/*
	 * Metodo que indica que hay un barco menos esperando para salir.
	 */
	public void decrementarEsperaSalida (int id) throws RemoteException;
	
	/*
	 * Metodo al que se llama al finalizar la ejecucion.
	 * Guarda en fichero el resultado de la misma y reinicia el valor de las variables.
	 */
	public void finComunicacionPuerto () throws RemoteException;

	/*
	 * Metodo que indica que un barco ha salido sin esperar.
	 */
	public void saleBarco(int id) throws RemoteException;

	/*
	 * Metodo que indica que un barco ha entrado sin esperar.
	 */
	public void entraBarco(int id) throws RemoteException;

}
