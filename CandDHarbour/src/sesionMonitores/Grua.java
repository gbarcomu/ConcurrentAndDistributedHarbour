package sesionMonitores;

public class Grua extends Thread {

	/** Referencia a la zona de abastecimiento del barco mercante */
	ZonaMercancias zonaAbastecimiento;

	/**
	 * Tipo de mercancía que recoge la grúa. 0: azúcar, 1: sal, 2: harina
	 */
	int tipo;

	/**
	 * Constructor parametrizado
	 * 
	 * @param _tipo
	 *            tipo de mercancía que recoge la grúa
	 * @param _zonaAbastecimiento
	 *            zona a la que abastece el barco
	 */
	public Grua(int _tipo, ZonaMercancias _zonaAbastecimiento) {
		tipo = _tipo;
		zonaAbastecimiento = _zonaAbastecimiento;
	}

	@Override
	public void run() {
			zonaAbastecimiento.gruaRecogeContenedor(tipo);
	}
}
