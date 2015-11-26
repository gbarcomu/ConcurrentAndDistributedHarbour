package sesionSemaforos;


public class ReponedorAceite extends Reponedor {

	/**
	 * Constructor parametrizado
	 * 
	 * @param _zonaCarga
	 *            zona donde recargan los barcos
	 */
	ReponedorAceite(ZonaReabastecimiento _zonaCarga) {
		
		super(_zonaCarga);
	}

	@Override
	public void reponer() throws InterruptedException {
	
		while (!hanRecargadoTodos) {
			zonaCarga.reponerAceite();
		}
		
	}

}
