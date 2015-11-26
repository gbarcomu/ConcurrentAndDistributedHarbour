package sesionSemaforos;


public class ReponedorPetroleo extends Reponedor {

	/**
	 * Constructor parametrizado
	 * 
	 * @param _zonaCarga
	 *            zona donde recargan los barcos
	 */
	ReponedorPetroleo(ZonaReabastecimiento _zonaCarga) {
		
		super(_zonaCarga);
	}

	@Override
	public void reponer() throws InterruptedException {

		while (!hanRecargadoTodos) {
			zonaCarga.reponerPetroleo();
		}
		
	}

}
