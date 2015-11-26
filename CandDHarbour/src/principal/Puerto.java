package principal;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;

import sesionMonitores.ZonaMercancias;
import sesionPrimitivas.PuertaDeControl;
import sesionSemaforos.ZonaReabastecimiento;
import ampliacionID.EstacionDeContado;
import barcos.Barco;
import barcos.BarcoMercante;
import barcos.BarcoPetrolero;

public class Puerto {

	private final int BARCOS_NORM = 4; // SI SE PUEDE CAMBIAR
	private final int BARCOS_PETROL = 5; // NO TOCAR
	private final int BARCOS_MERCA = 1; // NO TOCAR
	private final int MAX_BARCOS = BARCOS_NORM + BARCOS_PETROL + BARCOS_MERCA;

	private Thread[] barcos = new Thread[MAX_BARCOS];
	private PuertaDeControl puerta;
	private ZonaReabastecimiento zonaReabastecimiento;
	private ZonaMercancias zonaMercancias;
	private EstacionDeContado estacion;

	/**
	 * Le notifica a la puerta que ya han pasado todos y que cese su ejecucion.
	 */
	public void hanPasadoTodos() {

		synchronized (puerta) {
			puerta.hanPasadoTodos();
			puerta.notify();
		}

		try {
			puerta.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Al retornar, estamos seguros de que la puerta ha finalizado su
		// ejecucion y que no esta esperando a mas barcos.
	}

	public Puerto(String ip, String nombreServicio1, String nombreServicio2) {

		Random r = new Random();
		puerta = new PuertaDeControl(ip, nombreServicio1);
		puerta.start();
		System.out.println("La puerta es el " + puerta.getName());

		zonaMercancias = new ZonaMercancias(ip, nombreServicio2);
		zonaReabastecimiento = new ZonaReabastecimiento();
		estacion = new EstacionDeContado();

		for (int i = 0; i < BARCOS_NORM; i++) {
			Barco barco = new Barco(puerta, i + 1, r.nextInt(2));
			barcos[i] = new Thread(barco);
		}

		for (int j = BARCOS_NORM; j < BARCOS_NORM + BARCOS_PETROL; j++) {
			/*
			 * idPetrolero es (j - BARCOS_NORM) porque servira para acceder al
			 * array en el que almacenamos los contenedores de petroleo. Es
			 * unicamente para empezar en el identificador 0.
			 */
			Barco barco = new BarcoPetrolero(puerta, -(j - BARCOS_NORM + 1), j
					- BARCOS_NORM, zonaReabastecimiento, estacion);
			barcos[j] = new Thread(barco);
		}

		for (int j = BARCOS_NORM + BARCOS_PETROL; j < MAX_BARCOS; j++) {
			Barco barco = new BarcoMercante(puerta, zonaMercancias, 0);
			barcos[j] = new Thread(barco);
		}
	}

	/** Comprueba que hilos se estan ejecutando todavia.. */
	public static void hilosEjecutandose() {

		/*
		 * A veces, aunque no se produzca interbloqueo, el programa no termina
		 * porque hay un Thread esperando.
		 */
		System.out.println("------ HILOS EN EJECUCION AL FINAL ------");
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread thread : threadSet) {
			System.out.println(thread.getName());
		}
	}

	/** Comprueba si hay interbloqueos. */
	public static void cazarDeadlocks() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		long[] threadIds = bean.findDeadlockedThreads();

		if (threadIds != null) {

			ThreadInfo[] infos = bean.getThreadInfo(threadIds);
			for (ThreadInfo info : infos) {

				StackTraceElement[] stack = info.getStackTrace();
				for (StackTraceElement stackTraceElement : stack) {
					System.out
							.println("Ha habido interbloqueo en el siguiente metodo.");
					System.out.println(stackTraceElement.getMethodName());
				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {

		Puerto puerto = null;
		try {
			
			/*
			 * Los parametros corresponden a la IP del servidor y al nombre de los servicios.
			 */
			puerto = new Puerto(args[0], args[1], args[2]);
		} catch (IndexOutOfBoundsException i) {
			System.err
					.println("Error en los argumentos. Deben ser <ip servidor> <nombre objeto contador cola> <nombre objeto contador mercancias>");
			System.exit(0);
		}

		for (Thread barco : puerto.barcos) {
			barco.start();
		}

		for (Thread barco : puerto.barcos) {
			try {
				/*
				 * Espero 10s, si el Thread no ha terminado seguramente es
				 * porque haya habido interbloqueo, por lo que lo compruebo.
				 */
				barco.join(10000);
				// Comentar para mejorar rendimiento.
				Puerto.cazarDeadlocks();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/*
		 * Todos los hilos "Barco" han finalizado. NOTA: para evitar complicar
		 * mas el codigo tambien vamos a esperar a que los BarcosPetrolero
		 * terminen de contar los identificadores que tienen en comun. No
		 * importa si la puerta (o gruas) sigue dormida (nunca va a reclamar la
		 * CPU). El objetvio es que todos los hilos acaben su ejecucion antes de
		 * finalizar el programa.
		 */
		// Despertar a la Puerta.
		puerto.hanPasadoTodos();

		// Ultima comprobacion.
		Puerto.hilosEjecutandose();
		System.out.println();
		System.out
				.println("                                                                                   ^ ");
		System.out
				.println("                                                                                 /   \\  ");
		System.out
				.println("                                                                             \\  *******");
		System.out
				.println("                                                                             -  |C|=|=|");
		System.out
				.println("                                                                             /  ~~~~~~~");
		System.out
				.println("                                               Antonio                          | wwww|");
		System.out
				.println("                  __    __    __               Fernando                         | wwww|");
		System.out
				.println("                 |==|  |==|  |==|              Guillermo                        |     |");
		System.out
				.println("               __|__|__|__|__|__|_             Marcos                           |wwwww|");
		System.out
				.println("            __|___________________|___                                          | wwww|");
		System.out
				.println("         __|__[]__[]__[]__[]__[]__[]__|___                                      |     |");
		System.out
				.println("        |............................o.../                                     /^\\    |");
		System.out
				.println("        \\.............................../                                     |()||www|");
		System.out
				.println("  ,~')_,~')_,~')_,~')_,~')_,~')_,~')_,~')/,~')_,~')_,~')_,~')_,~')_,~')_,~'888888888888888");
	}
}
