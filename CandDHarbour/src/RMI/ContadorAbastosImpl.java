package RMI;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import pcd.util.Ventana;

public class ContadorAbastosImpl implements ContadorAbastos {

	private int cargamentosAzucar;
	private int cargamentosHarina;
	private int cargamentosSal;

	private Ventana v;

	public ContadorAbastosImpl(Ventana _v) {

		cargamentosAzucar = 0;
		cargamentosHarina = 0;
		cargamentosSal = 0;		
		v = _v;
	}

	@Override
	public void dejarAzucar() {

		cargamentosAzucar++;
	}

	@Override
	public void dejarHarina() {

		cargamentosHarina++;
	}

	@Override
	public void dejarSal() {

		cargamentosSal++;
	}

	@Override
	public void finDescarga() {

		try {

			Date date = new Date();

			FileWriter fichero = new FileWriter("registroMercancia.log", true);
			PrintWriter pw = new PrintWriter(fichero);

			pw.println("Comunicacion recibida de puerto");
			pw.println("IP: " + java.rmi.server.RemoteServer.getClientHost());
			pw.println("Fecha " + date);
			pw.println("Se han descargado: ");
			pw.println(cargamentosAzucar + " containers de Azucar");
			pw.println(cargamentosHarina + " containers de Harina");
			pw.println(cargamentosSal + " containers de Sal");

			pw.println();

			pw.close();

			v.addText("\nEn el puerto con IP : "
					+ java.rmi.server.RemoteServer.getClientHost());
			
			v.addText("Se han descargado: ");
			v.addText(cargamentosAzucar + " containers de Azucar");
			v.addText(cargamentosHarina + " containers de Harina");
			v.addText(cargamentosSal + " containers de Sal");
			
			v.addText("\n---------------------------------------- ");

		} catch (Exception e) {

			e.printStackTrace();
		}

		cargamentosAzucar = 0;
		cargamentosHarina = 0;
		cargamentosSal = 0;
	}

}
