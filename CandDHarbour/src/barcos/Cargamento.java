package barcos;

public class Cargamento {

	/** Cantidad de aceite que contiene el cargamento */
	private int depositoAceite;

	/** Cantidad de petroleo que contiene el cargamento */
	private int depositoPetroleo;

	/** Constructor por defecto */
	public Cargamento() {
		depositoAceite = 0;
		depositoPetroleo = 0;
	}

	/**
	 * Metodo para comprobar si el deposito de aceite esta lleno
	 * 
	 * @return true si el deposito de aceite esta lleno
	 * @return false en caso contrario
	 */
	public boolean estaLlenoAceite() {
		return (depositoAceite == 5000);
	}

	/** Aumenta la cantidad almacenada de aceite en 1000 */
	public void recargarAceite() {
		depositoAceite += 1000;
	}

	/**
	 * Metodo para comprobar si el deposito de petroleo esta lleno
	 * 
	 * @return true si el deposito de petroleo esta lleno
	 * @return false en caso contrario
	 */
	public boolean estaLlenoPetroleo() {
		return (depositoPetroleo == 3000);
	}

	/** Aumenta la cantidad almacenada de petroleo en 1000 */
	public void recargarPetroleo() {
		depositoPetroleo += 1000;
	}
}
