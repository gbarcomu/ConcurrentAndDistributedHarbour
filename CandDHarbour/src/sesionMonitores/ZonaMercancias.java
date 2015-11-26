package sesionMonitores;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import RMI.ContadorAbastos;


public class ZonaMercancias {

	/** Indica si el ultimo contenedor puesto por el barco ha sido recogido */
	boolean ultimoPuestoRecogido = true;

	/** Indica si el barco ha terminado de descargar toda la mercancia */
	boolean barcoDescargado = false;

	/** Monitor encargado de la gestion de la plataforma */
	Lock monitor = new ReentrantLock();

	/** Condicion de espera de la grua que recoge azucar */
	Condition esperaAzucar = monitor.newCondition();

	/** Condicion de espera de la grua que recoge sal */
	Condition esperaSal = monitor.newCondition();

	/** Condicion de espera de la grua que recoge harina */
	Condition esperaHarina = monitor.newCondition();

	/** Condicion de espera a que se recoja el ultimo contenedor. */
	Condition esperaUltimaCaja = monitor.newCondition();

	/**
	 * Condicion de espera del barco mercante mientras haya mercancia en la
	 * plataforma
	 */
	Condition esperaRecoger = monitor.newCondition();

	/**
	 * El barco ha descargado todos sus contenedores.
	 */
	private boolean yaHeDescargadoTodos;
	/** IP del servidor donde se alojan los servicios. **/
	private String ip;
	/**
	 * Nombre del servicio que se desea usar.
	 **/
	private String nombreServicio;
	/**
	 * Servicio para controlar la cantidad de contenedores descargados en cada ejecucion.
	 */
	private ContadorAbastos contAbastos;
	
	/** Constructor por defecto */
	public ZonaMercancias(String _ip, String _nombreServicio) {
		
		ip = _ip;
		nombreServicio = _nombreServicio;

		for (int i = 0; i < 3; i++) {
			Grua grua = new Grua(i, this);
			System.out.println("La grua " + i + " es el " + grua.getName());
			grua.start();
		}
		
		/*
		 * Localiza mediante la IP el registro donde se encuentra el servicio. 
		 * Obtiene la interfaz del servicio para poder acceder a los metodos remotos. 
		 */
		try {			
			Registry registry = LocateRegistry.getRegistry(ip);
			contAbastos = (ContadorAbastos) registry.lookup(nombreServicio);
		} catch (Exception e) {
			System.out.println("Excepcion en Puerta de Control al obtener ContadorAbastos");
			e.printStackTrace();
		}
	}

	/** El barco mercante notifica que ha depositado toda la mercancia */
	public void acabar() {

		monitor.lock();
		yaHeDescargadoTodos = true;

		if (!ultimoPuestoRecogido) {
			try {
				esperaUltimaCaja.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		barcoDescargado = true;

		esperaAzucar.signal();
		esperaSal.signal();
		esperaHarina.signal();

		try {
			contAbastos.finDescarga();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		monitor.unlock();
	}

	public boolean haAcabado() {

		return barcoDescargado;
	}

	/**
	 * El barco mercante deja mercancia de un tipo concreto en la plataforma en
	 * el caso de que esta este libre
	 * 
	 * @param tipo
	 */
	public void dejarContenedor(int tipo) {
		monitor.lock();

		if (!ultimoPuestoRecogido) { // Si la plataforma no esta libre, se
										// espera
			try {
				esperaRecoger.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		ultimoPuestoRecogido = false;

		switch (tipo) {
		case 0:
			System.out.println("												" + "Dejando contendor de azucar.");
			esperaAzucar.signal();
			break;
		case 1:
			System.out.println("												" + "Dejando contendor de sal.");
			esperaSal.signal();
			break;
		case 2:
			System.out.println("												" + "Dejando contendor de harina.");
			esperaHarina.signal();
			break;
		}

		monitor.unlock();
	}

	/**
	 * La grua recoge mercancia de su tipo, siempre y cuando esta en la
	 * plataforma
	 * 
	 * @param tipoGrua
	 */
	public void gruaRecogeContenedor(int tipoGrua) {
		monitor.lock();
		while (!haAcabado()) {
			// Si la plataforma esta vacia, la grua se espera.
			if (ultimoPuestoRecogido) { // if porque solo una de cada tipo.
				switch (tipoGrua) {
				case 0:
					try {
						esperaAzucar.await();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 1:
					try {
						esperaSal.await();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 2:
					try {
						esperaHarina.await();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}

			// Si el barco no ha terminado de descargar y hay mercancia en la
			// plataforma, la recoge
			if (!barcoDescargado && !ultimoPuestoRecogido) {

				ultimoPuestoRecogido = true;

				switch (tipoGrua) {
				case 0:
					System.out.println("												"
							+ "Cogiendo contendor de azucar.");
					
					// Llamada metodo remoto
					
					try {
						contAbastos.dejarAzucar();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					
					break;
				case 1:
					System.out.println("												"
							+ "Cogiendo contendor de sal.");
					
					// Llamada metodo remoto
					
					try {
						contAbastos.dejarSal();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					break;
				case 2:
					System.out.println("												"
							+ "Cogiendo contendor de harina.");
					
					// Llamada metodo remoto
					
					try {
						contAbastos.dejarHarina();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					break;
				}

				if (yaHeDescargadoTodos) {
					barcoDescargado = true;
					esperaUltimaCaja.signal();
					// No queda nada en la plataforma.
				} else {
					esperaRecoger.signal();
				}
			}
		} // fin while
		monitor.unlock();

	}
}
