package sesionSemaforos;

public abstract class Reponedor extends Thread {

	/** Referencia a la zona donde cargan los barcos */
	protected ZonaReabastecimiento zonaCarga;

	/** Indicador de si han recargado todos los barcos */
	protected boolean hanRecargadoTodos;

	/**
	 * Constructor parametrizado
	 * 
	 * @param _zonaReabastecimiento
	 *            zona donde recargan los barcos
	 */
	Reponedor(ZonaReabastecimiento _zonaReabastecimiento) {

		zonaCarga = _zonaReabastecimiento;
		hanRecargadoTodos = false;
	}

	/**
	 * Metodo a traves del cual los reponedores cumplen sus funciones dentro de
	 * la zona de carga. Dependiendo del tipo de reponedor, actuaran de una
	 * forma u otra
	 * 
	 * @throws InterruptedException
	 */
	public abstract void reponer() throws InterruptedException;

	@Override
	public void run() {

		try {
			reponer();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void hanRecargadoTodos() {

		hanRecargadoTodos = true;
	}
}
