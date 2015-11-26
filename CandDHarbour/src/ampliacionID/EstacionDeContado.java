package ampliacionID;

import barcos.BarcoPetrolero;

public class EstacionDeContado {

	private BarcoPetrolero[] barcosPetroleros;
	int barcosListos; // actua como indice del anterior array.

	public EstacionDeContado() {

		barcosListos = 0;
		barcosPetroleros = new BarcoPetrolero[5];
	}

	/**
	 * Espera a que todos los barcos esten listos para contar sus id's entre
	 * ellos
	 */
	public synchronized void heLlegado(BarcoPetrolero barcoPetrol) {

		barcosPetroleros[barcosListos++] = barcoPetrol;

		/*
		 * Espero a que todos los barcos esten listos para que empiecen a
		 * mandarse mensajes entre ellos.
		 */
		if (barcosListos < 5) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		notify(); // Desbloqueo encadenado (de los petroleros).
	}

	/**
	 * Manda mensajes al resto de barcos para comparar su id con el de los demas
	 */
	public void avisarAlResto(BarcoPetrolero barcoP) {

		for (int i = 0; i < barcosPetroleros.length; i++) {

			// Para evitar que un barco sea preguntado por varios a la vez.
			synchronized (barcosPetroleros[i]) {

				if (barcoP.getId() != barcosPetroleros[i].getId()) {

					System.out.println("barco " + barcoP.getId() + " con ID "
							+ barcoP.devolverIdPetrolero() + " compara con "
							+ "barco " + barcosPetroleros[i].getId()
							+ " con ID "
							+ barcosPetroleros[i].devolverIdPetrolero());

					// Aqui es donde se compara realmente, lo anterior es salida
					// por pantalla
					barcosPetroleros[i]
							.compararId(barcoP.devolverIdPetrolero());
				}
			}
		}
	}
}
