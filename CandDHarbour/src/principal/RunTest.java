package principal;

public class RunTest {

	/*
	 * Se ve toda la salida por pantalla de, como maximo, 6 ejecuciones.
	 */
	private final int MAX_EJEC = 10;

	public RunTest() {

	}

	/**
	 * Ejecuta el programa principal MAX_EJEC veces. No asegura, por muchas
	 * ejecuciones que se hagan, que el programa funcione correctamente.
	 */
	public void variasEjecuciones(String[] args) {

		for (int i = 1; i <= MAX_EJEC; i++) {
			try {
				Puerto.main(args);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("------ FIN EJECUCION " + i + " ------");
		}
	}

	public static void main(String[] args) {

		RunTest test = new RunTest();
		test.variasEjecuciones(args);
	}
}