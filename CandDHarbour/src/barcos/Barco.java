package barcos;

import sesionPrimitivas.PuertaDeControl;

public class Barco implements Runnable {

	/** Referencia a la puerta por la que tiene que pasar el barco */
	protected PuertaDeControl puerta;

	/** Identificador del barco **/
	protected int id;

	/** Dos tipos posibles, barco que entra (0) y barco que sale (1) **/
	protected int tipo;

	/** Nos indica si el barco tiene que esperar para pasar por la puerta */
	protected boolean esperandoParaPasar;

	/**
	 * Constructor parametrizado de la clase puerta
	 * 
	 * @param _puerta
	 *            puerta por la que tiene que pasar el barco
	 * @param _id
	 *            identificador del barco
	 * @param _tipo
	 *            indica si es de entrada o de salida
	 */
	public Barco(PuertaDeControl _puerta, int _id, int _tipo) {
		
		puerta = _puerta;
		id = _id;
		tipo = _tipo;
		esperandoParaPasar = false;			
	}

	public boolean isEsperandoParaPasar() {

		return esperandoParaPasar;
	}

	public void setEsperandoParaPasar(boolean esperandoParaPasar) {

		this.esperandoParaPasar = esperandoParaPasar;
	}

	public int getId() {

		return id;
	}

	@Override
	public void run() {
		
		if (tipo == 0) { // El barco es de entrada
			puerta.quieroEntrar(this);
			for (int i = 0; i < 3; i++) {
				System.out.println("Barco " + id + " entrando");
			}
			puerta.yaHeEntrado(id);
		} else { // El barco es de salida
			puerta.quieroSalir(this);
			for (int i = 0; i < 3; i++) {
				System.out.println("Barco " + id + " saliendo");
			}
			puerta.yaHeSalido(id);
		}
	}
}
