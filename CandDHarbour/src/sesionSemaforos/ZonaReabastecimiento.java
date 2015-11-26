package sesionSemaforos;

import java.util.concurrent.Semaphore;

import barcos.BarcoPetrolero;
import barcos.Cargamento;

public class ZonaReabastecimiento {

	/** Reponedor que se encarga de reponer los depositos de petroleo */
	Reponedor reponedorPetroleo;

	/** Reponedor que se encarga de reponer el deposito de aceite */
	Reponedor reponedorAceite;

	/** Semaforo en el cual esperan los barcos hasta que llegan todos */
	Semaphore hanLlegado;

	/** Semaforo con el que se garantiza la exclusion mutua en cada momento */
	Semaphore mutex;

	/** Semaforo que garantiza que el aceite se carga de uno en uno */
	Semaphore mutexAceite;

	/** Array con los depositos individuales de cada barco petrolero */
	Semaphore[] petroleo;

	/**
	 * Semaforo en el que se queda bloqueado el barco que llama al reponedor de
	 * aceite
	 */
	Semaphore esperaReponerAceite;

	/**
	 * Semaforo en el que espera el reponedor de petroleo hasta que tenga que
	 * reponer los depositos
	 */
	Semaphore reponedorP;

	/**
	 * Semaforo en el que espera el reponedor de aceite hasta que tenga que
	 * reponer el deposito
	 */
	Semaphore reponedorA;

	/** Cantidad de barcos esperando para comenzar a cargar */
	int barcosEsperando;

	/** Cantidad de recargas pendientes hasta que tenga que actuar el reponedor */
	int recargasPendientesP;

	/** Cantidad de barcos que han terminado de cargar */
	int barcosHanRecargado;

	/** Cantidad de aceite disponible en cada momento */
	int recargasPendientesA;

	/** Constructor por defecto */
	public ZonaReabastecimiento() {

		reponedorPetroleo = new ReponedorPetroleo(this);
		reponedorAceite = new ReponedorAceite(this);

		hanLlegado = new Semaphore(0);
		mutex = new Semaphore(1);
		mutexAceite = new Semaphore(1);
		petroleo = new Semaphore[5];
		esperaReponerAceite = new Semaphore(0);
		reponedorP = new Semaphore(0);
		reponedorA = new Semaphore(0);

		barcosEsperando = 0;
		recargasPendientesP = 10;
		recargasPendientesA = 15;
		barcosHanRecargado = 0;

		for (int i = 0; i < petroleo.length; i++) {
			petroleo[i] = new Semaphore(2);
		}

		reponedorPetroleo.start();
		reponedorAceite.start();
	}

	/**
	 * Cuando los barcos llegan a la zona de carga, deben esperar unos por otros
	 * hasta estar todos, momento en el cual pueden empezar a cargar. Hasta
	 * entonces se deben quedar bloqueados en este metodo
	 * 
	 * @param barco
	 *            barco que invoca el metodo
	 * @throws InterruptedException
	 */
	public void quieroRecargar(BarcoPetrolero barco)
			throws InterruptedException {

		System.out.println("						" + "Barco " + barco.getId()
				+ " quiere recargar");

		mutex.acquire();

		// Si el barco no es el ultimo en llegar, se espera
		if (barcosEsperando < 4) {
			barcosEsperando++;
			mutex.release();
			hanLlegado.acquire(); // Se debe quedar bloqueado aqui
			barcosEsperando--;
		}

		System.out.println("						" + "Barco " + barco.getId()
				+ " empieza a recargar");

		// Si queda algun barco esperando, lo despierta
		if (barcosEsperando > 0) {
			hanLlegado.release();
		} else {
			mutex.release();
		}

	}

	/**
	 * El barco en cuestion intenta recargar aceite, siempre y cuando no lo este
	 * haciendo otro barco o no haya aceite en el deposito
	 * 
	 * @param barco
	 * @throws InterruptedException
	 */
	public void recargoAceite(BarcoPetrolero barco) throws InterruptedException {

		Cargamento cargamento = barco.devolverCargamento();

		// Aqui garantizamos que solo recarguen aceite de uno en uno
		mutexAceite.acquire();

		cargamento.recargarAceite();

		for (int i = 0; i < 3; i++) {
			System.out.println("						" + "Barco " + barco.getId()
					+ " recargando aceite..." + (i + 1));
		}

		recargasPendientesA--;

		// Si no queda aceite despues de la ultima recarga, se llama al
		// reponedor
		if (recargasPendientesA == 0) {

			System.out.println("						"
					+ "Hace falta aceite. Llamando al reponedor.");
			reponedorA.release();

			// El barco se queda bloqueado aqui hasta que el
			// reponedor finalice su tarea
			esperaReponerAceite.acquire();
		}

		mutexAceite.release();
	}

	/**
	 * El barco intenta recargar petroleo siempre y cuando haya disponible en el
	 * deposito
	 * 
	 * @param barco
	 * @throws InterruptedException
	 */
	public void recargoPetroleo(BarcoPetrolero barco)
			throws InterruptedException {

		Cargamento cargamento = barco.devolverCargamento();

		// Si queda petroleo disponible en el deposito, lo cargara. En caso
		// contrario, se quedara bloqueado hasta que el reponedor lo recargue
		petroleo[barco.devolverIdPetrolero()].acquire();

		cargamento.recargarPetroleo();

		for (int i = 0; i < 3; i++) {
			System.out.println("						" + "Barco " + barco.getId()
					+ " recargando petroleo..." + (i + 1));
		}

		mutex.acquire();
		recargasPendientesP--;

		// Si todos los depositos de petroleo estan vacios, se llama al
		// reponedor
		if (recargasPendientesP == 0) {
			System.out.println("						"
					+ "Hace falta petroleo. Llamando al reponedor.");
			reponedorP.release();
		}
		mutex.release();
	}

	/**
	 * Una vez que los barcos han recargado, lo comunican a traves de este
	 * metodo. El ultimo barco en terminar de recargar se lo notifica a los
	 * reponedores para que finalicen sus ejecuciones
	 * 
	 * @throws InterruptedException
	 */
	public void yaHeRecargado() throws InterruptedException {

		mutex.acquire();

		if (barcosHanRecargado == 4) {
			reponedorPetroleo.hanRecargadoTodos();
			reponedorAceite.hanRecargadoTodos();
			reponedorP.release();
			reponedorA.release();
		}
		barcosHanRecargado++;

		mutex.release();
	}

	/**
	 * Metodo invocado por el reponedor de petroleo, en el que se quedara
	 * dormido y solo sera despertado cuando todos los depositos de petroleo
	 * estan vacios, en cuyo caso los recargara todos
	 * 
	 * @throws InterruptedException
	 */
	public void reponerPetroleo() throws InterruptedException {

		int i = 0; // numero de contenedor de petroleo.

		reponedorP.acquire(); // El reponedor espera aqui a que se vacian los
								// depositos

		mutex.acquire();
		if (barcosHanRecargado != 5) { // Cuando todos los barcos hayan
										// terminado no sera necesario que
										// recargue los depositos
			for (Semaphore semPetroleo : petroleo) {
				System.out.println("						"
						+ "Recargando contenedor de petroleo " + i);
				i++;
				recargasPendientesP += 2;
				semPetroleo.release(2);
			}
		}
		mutex.release();
	}

	/**
	 * Metodo invocado por el reponedor de aceite, en el que se quedara dormido
	 * y solo sera despertado cuando el deposito de aceite este vacio
	 * 
	 * @throws InterruptedException
	 */
	public void reponerAceite() throws InterruptedException {

		reponedorA.acquire(); // El reponedor espera aqui a que se vacie el
								// deposito
		mutex.acquire();
		if (barcosHanRecargado != 5) { // Cuando todos los barcos hayan
										// terminado no sera necesario recargar
										// el deposito de aceite
			for (int i = 0; i < 3; i++) {

				System.out
						.println("						" + "Recargando contenedor de aceite");
			}

			recargasPendientesA = 15;

		}
		mutex.release();
		esperaReponerAceite.release();
	}
}
