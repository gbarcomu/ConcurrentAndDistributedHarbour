package sesionPrimitivas;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;

import RMI.ContadorColaBarcos;
import barcos.Barco;

public class PuertaDeControl extends Thread {

	/** Numero de barcos que estan dentro del canal y entrando. */
	private int barcosEntrando;
	/** Numero de barcos que estan dentro del canal y saliendo. */
	private int barcosSaliendo;
	/** Numero de barcos esperando que se les permita entrar. */
	private LinkedList<Barco> colaEntrada;
	/** Numero de barcos esperando que se les permita salir. */
	private LinkedList<Barco> colaSalida;
	/** Indica si todos los barcos (del programa) ya han pasado. */
	private boolean todosHanPasado;

	/**
	 * Servicio para controlar el numero de barcos que esperan para salir, para
	 * entrar, así como el numero total de barcos que ha entrado o salido del
	 * Puerto.
	 **/
	private ContadorColaBarcos contCola;
	/** IP del servidor donde se alojan los servicios. **/
	private String ipServer;
	/**
	 * Nombre del servicio que se desea usar. Se buscara en el Registro RMI en
	 * la ip 'ipServer'.
	 **/
	private String nombreServicio;

	public PuertaDeControl(String _ip, String _nombreServicio) {

		barcosEntrando = 0;
		barcosSaliendo = 0;
		colaEntrada = new LinkedList<Barco>();
		colaSalida = new LinkedList<Barco>();
		todosHanPasado = false;

		// Estos parametro vienen de los argumentos al ejecutar el main de Puerto.
		ipServer = _ip;
		nombreServicio = _nombreServicio;

		/*
		 * Localiza mediante la IP el registro donde se encuentra el servicio. 
		 * Obtiene la interfaz del servicio para poder acceder a los metodos remotos. 
		 */
		try {
			Registry registry = LocateRegistry.getRegistry(ipServer);
			contCola = (ContadorColaBarcos) registry.lookup(nombreServicio);
		} catch (Exception e) {
			System.out
					.println("Excepcion en Puerta de Control al obtener ContadorColaBarcos");
			e.printStackTrace();
		}
	}

	/**
	 * Todos los barcos han pasado por lo que la puerta debe cesar su actividad.
	 */
	public void hanPasadoTodos() {

		/*
		 * No es synchronized. Dado que justamente despues de este metodo se
		 * notifica a la puerta, antes de llamarlo se adquirira el cerrojo sobre
		 * la misma.
		 */
		todosHanPasado = true;
	}

	/**
	 * Realiza la llamada al metodo que gestiona la entrada y salida de barcos
	 * hasta que todos los barcos hayan salido.
	 */
	@Override
	public void run() {

		actividadPuerta();
	}

	/** Metodo encargado de controlar la entrada y salida de los barcos. */
	public synchronized void actividadPuerta() {

		while (!todosHanPasado) {

			/*
			 * Debe dar preferencia a los barcos que salen. No le importa si hay
			 * barcos en la cola de entrada, solo ve que el canal no esta
			 * ocupado y que hay barcos que quieren salir.
			 */
			if (barcosEntrando == 0 && !colaSalida.isEmpty()) {

				while (!colaSalida.isEmpty()) {

					Barco barcoPrimeroS = colaSalida.peek();
					synchronized (barcoPrimeroS) {

						System.out.println("Barco " + barcoPrimeroS.getId()
								+ " zarpa para salir.");
						
						// Llamada metodo remoto
						try {
							
							contCola.decrementarEsperaSalida(barcoPrimeroS
									.getId());
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						colaSalida.poll().notify();
						barcosSaliendo++;
					}
				}
			}

			/*
			 * No hay barcos saliendo ni esperando para salir, por lo que pueden
			 * entrar barcos que estan esperando para entrar.
			 */
			else if (barcosSaliendo == 0 && colaSalida.isEmpty()
					&& !colaEntrada.isEmpty()) {

				Barco barcoPrimeroE = colaEntrada.peek();
				synchronized (barcoPrimeroE) {

					// Llamada metodo remoto
					try {
						contCola.decrementarEsperaEntrada(barcoPrimeroE.getId());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					System.out.println("Barco " + barcoPrimeroE.getId()
							+ " zarpa para entrar.");
					colaEntrada.poll().notify();
					barcosEntrando++;
				}
			}

			/*
			 * No hay barcos en ninguna cola. No obstante el canal puede estar
			 * ocupado (el controlador, la puerta, no debe hacer nada).
			 */
			else {

				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} // fin while(!todosHanPasado)

		// Llamada metodo remoto de finalizacion
		try {
			contCola.finComunicacionPuerto();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	} // fin actividadPuerta()

	/**
	 * Un barco llega e intenta entrar. Si el canal esta libre, pasa
	 * directamente. Si hay algun barco saliendo o esperando para salir, se
	 * encolara y se dormira
	 */
	public void quieroEntrar(Barco barco) {

		synchronized (barco) {
			synchronized (this) {

				System.out.println("Barco " + barco.getId() + " ("
						+ Thread.currentThread().getName() + ")"
						+ " quiere entrar.");

				// Comprueba si el canal esta libre
				if (barcosSaliendo != 0 || !colaSalida.isEmpty()
						|| !colaEntrada.isEmpty()) {

					barco.setEsperandoParaPasar(true);
					colaEntrada.add(barco);
					
					// Llamada metodo remoto
					try {
						contCola.incrementarEsperaEntrada(barco.getId());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					if (colaEntrada.size() == 1) {
						notify();
					}

				} else {

					barco.setEsperandoParaPasar(false);
					System.out.println("Barco " + barco.getId()
							+ " zarpa para entrar.");
					
					// Llamada metodo remoto
					try {
						contCola.entraBarco(barco.getId());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					barcosEntrando++;
				}
			} // fin synchronized(this)

			// Si se ha encolado es porque tiene que esperar, luego se duerme
			if (barco.isEsperandoParaPasar()) {

				try {
					barco.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} // fin synchronized(barco)
	}

	/**
	 * Un barco llega e intenta salir. Si el canal esta libre, pasa
	 * directamente. Si hay algun barco entrando, se encolara y se dormira
	 */
	public void quieroSalir(Barco barco) {

		synchronized (barco) {
			synchronized (this) {

				System.out.println("Barco " + barco.getId() + " ("
						+ Thread.currentThread().getName() + ")"
						+ " quiere salir.");

				// Comprueba si el canal esta libre
				if (barcosEntrando != 0 || !colaSalida.isEmpty()) {

					barco.setEsperandoParaPasar(true);
					colaSalida.add(barco);
					
					// Llamada metodo remoto
					try {
						contCola.incrementarEsperaSalida(barco.getId());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					if (colaSalida.size() == 1) {
						// El unico Thread esperando es la propia puerta.
						notify();
					}

				} else {

					barco.setEsperandoParaPasar(false);
					System.out.println("Barco " + barco.getId()
							+ " zarpa para salir.");
					
					// Llamada metodo remoto
					try {
						contCola.saleBarco(barco.getId());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					barcosSaliendo++;
				}
			} // fin synchronized(this)

			// Si se ha encolado es porque tiene que esperar, luego se duerme
			if (barco.isEsperandoParaPasar()) {

				try {
					barco.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}// fin synchronized(barco)
	}

	/**
	 * Un barco ha terminado de entrar por lo que, si es el �ltimo, debe
	 * notificar a la puerta que tiene que volver a intervenir.
	 */
	public synchronized void yaHeEntrado(int id) {

		System.out.println("Barco " + id + " ya ha entrado.");
		barcosEntrando--;

		// Soy el ultimo, que vea la puerta si hay barcos esperando.
		if (barcosEntrando == 0)
			notify();
	}

	/**
	 * Un barco ha termiando de salir por lo que, si es el �ltimo, debe
	 * notificar a la puerta que tiene que volver a intervenir.
	 */
	public synchronized void yaHeSalido(int id) {

		System.out.println("Barco " + id + " ya ha salido.");
		barcosSaliendo--;

		// Soy el ultimo, que vea la puerta si hay barcos esperando.
		if (barcosSaliendo == 0)
			notify();
	}
}
