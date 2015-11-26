package RMI;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.Date;

import pcd.util.Ventana;

public class ContadorColaBarcosImpl implements ContadorColaBarcos {

	private int contadorSalida;
	private int contadorEntrada;

	private int totalBarcosEntrada;
	private int totalBarcosSalida;

	private Ventana v;

	public ContadorColaBarcosImpl(Ventana _v) {

		v = _v;
		contadorEntrada = 0;
		contadorSalida = 0;
		totalBarcosEntrada = 0;
		totalBarcosSalida = 0;
	}
	
	@Override
	public synchronized void entraBarco(int id)
			throws RemoteException {
		
		totalBarcosEntrada++;
		
		try {
			v.addText("\nLlamada desde IP : "
					+ java.rmi.server.RemoteServer.getClientHost()
					+ " - Barco : " + id + " entra sin esperar");
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void saleBarco(int id)
			throws RemoteException {
		
		totalBarcosSalida++;
		
		try {
			v.addText("\nLlamada desde IP : "
					+ java.rmi.server.RemoteServer.getClientHost()
					+ " - Barco : " + id + " sale sin esperar");
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
	}
		

	@Override
	public synchronized void incrementarEsperaEntrada(int id)
			throws RemoteException {

		contadorEntrada++;

		try {
			v.addText("\nLlamada desde IP : "
					+ java.rmi.server.RemoteServer.getClientHost()
					+ " - Barco : " + id + " encolandose para entrar");
			v.addText("Total barcos esperando para entrar: " + contadorEntrada);
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}

	}

	@Override
	public synchronized void incrementarEsperaSalida(int id)
			throws RemoteException {

		contadorSalida++;

		try {
			v.addText("\nLlamada desde IP : "
					+ java.rmi.server.RemoteServer.getClientHost()
					+ " - Barco : " + id + " encolandose para salir");
			v.addText("Total barcos esperando para salir: " + contadorSalida);
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void decrementarEsperaEntrada(int id)
			throws RemoteException {

		contadorEntrada--;
		totalBarcosEntrada++;

		try {
			v.addText("\nLlamada desde IP : "
					+ java.rmi.server.RemoteServer.getClientHost()
					+ " - Barco : " + id + " desencolandose y entrando");
			v.addText("Total barcos esperando para entrar: " + contadorEntrada);
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void decrementarEsperaSalida(int id)
			throws RemoteException {

		contadorSalida--;
		totalBarcosSalida++;

		try {
			v.addText("\nLlamada desde IP : "
					+ java.rmi.server.RemoteServer.getClientHost()
					+ " - Barco : " + id + " desencolandose para salir");
			v.addText("Total barcos esperando para salir: " + contadorSalida);
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void finComunicacionPuerto() throws RemoteException {

		try {
			Date date = new Date();

			FileWriter fichero = new FileWriter("registroPuerta.log", true);
			PrintWriter pw = new PrintWriter(fichero);

			pw.println("Comunicacion recibida de puerto");
			pw.println("IP: " + java.rmi.server.RemoteServer.getClientHost());
			pw.println("Fecha " + date);
			pw.println("Han entrado: " + totalBarcosEntrada);
			pw.println("Han salido: " + totalBarcosSalida);
			pw.println();

			pw.close();

			v.addText("\nTotal barcos han salido: " + totalBarcosSalida);
			v.addText("Total barcos han entrado: " + totalBarcosEntrada);
			
			v.addText("Fin comunicacion con puerto "
					+ java.rmi.server.RemoteServer.getClientHost() + "\n");

		} catch (IOException e) {
			System.out
					.println("Excepcion de entrada/salida en ContadorColaBarcosImpl");
			e.printStackTrace();
		} catch (ServerNotActiveException e) {
			System.out
			.println("Excepcion servidor no activo en ContadorColaBarcosImpl");
			e.printStackTrace();
		}

		totalBarcosEntrada = 0;
		totalBarcosSalida = 0;

	}

}
