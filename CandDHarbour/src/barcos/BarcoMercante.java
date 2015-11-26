package barcos;

import sesionMonitores.ZonaMercancias;
import sesionPrimitivas.PuertaDeControl;

public class BarcoMercante extends Barco {

	/** Referencia a la zona de mercancias del barco mercante */
	ZonaMercancias zonaMercancias;

	/**
	 * Constructor parametrizado
	 * 
	 * @param _puerta
	 *            puerta por la que tiene que pasar el barco
	 * @param _zonaAbastecimiento
	 *            zona a la que abastece el barco
	 * @param _id
	 *            identificador del barco
	 */
	public BarcoMercante(PuertaDeControl _puerta,
			ZonaMercancias _zonaAbastecimiento, int _id) {
		super(_puerta, _id, 0);
		this.zonaMercancias = _zonaAbastecimiento;
	}

	@Override
	public void run() {

		// Intenta entrar como un barco normal
		puerta.quieroEntrar(this);
		for (int i = 0; i < 3; i++) {
			System.out.println("Barco " + id + " entrando");
		}
		puerta.yaHeEntrado(id);

		// Una vez dentro, se dispone a dejar todos los contenedores.
		for (int i = 0; i < 12; i++) {
			zonaMercancias.dejarContenedor(0);
		}
		for (int i = 0; i < 20; i++) {
			zonaMercancias.dejarContenedor(1);
		}
		for (int i = 0; i < 5; i++) {
			zonaMercancias.dejarContenedor(2);
		}

		// Cuando ha dejado todos los contenedores, lo notifica a la zona de
		// abastecimiento
		zonaMercancias.acabar();

		// Cuando termina de dejar contenedores, sale del puerto
		puerta.quieroSalir(this);
		for (int i = 0; i < 3; i++) {
			System.out.println("Barco " + id + " saliendo");
		}
		puerta.yaHeSalido(id);

	}

}
