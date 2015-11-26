package barcos;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import ampliacionID.EstacionDeContado;
import ampliacionRecargasThread.Manguera;
import ampliacionRecargasThread.MangueraAceite;
import ampliacionRecargasThread.MangueraPetroleo;
import sesionPrimitivas.PuertaDeControl;
import sesionSemaforos.ZonaReabastecimiento;

public class BarcoPetrolero extends Barco {

	/** Referencia a la zona donde se reabastece el barco */
	private ZonaReabastecimiento zonaReabastecimiento;

	/** Objecto donde guarda el aceite y el petroleo */
	private Cargamento cargamento;

	private EstacionDeContado estacion;

	/** Identificador especifico de los barcos de tipo petrolero */
	// Se utiliza para acceder por indice a los contenedores de petroleo.
	// Mas tarde se reutiliza en la ampliacion opcional.
	private int idPetrolero;

	private int igualesQueYo; // ampliacion opcional.

	private int idRecibidos; // ampliacion opcional.

	/** Dos mangueras. Una para el aceite y otra para el petroleo. */
	private Manguera mangueraAceite;
	private Manguera mangueraPetroleo;

	/** Indica a las mangueras que deben empezar a recargar */
	CountDownLatch startSignal;
	/** Las mangueras han recargado el barco, puede continuar */
	CountDownLatch doneSignal;

	/**
	 * Constructor parametrizados
	 * 
	 * @param _puerta
	 *            puerta por la que tiene que pasar el barco
	 * @param _id
	 *            identificador del barco
	 * @param _idPetrolero
	 *            identificador del barco petrolero
	 * @param _zonaCarga
	 *            zona donde recarga el barco petrolero
	 */
	public BarcoPetrolero(PuertaDeControl _puerta, int _id, int _idPetrolero,
			ZonaReabastecimiento _zonaReabastecimiento,
			EstacionDeContado _estacion) {
		super(_puerta, _id, 0);
		zonaReabastecimiento = _zonaReabastecimiento;
		cargamento = new Cargamento();
		idPetrolero = _idPetrolero;
		igualesQueYo = 0;
		idRecibidos = 0;
		estacion = _estacion;
		startSignal = new CountDownLatch(1);
		doneSignal = new CountDownLatch(2); // tienen que acabar las dos
											// mangueras.
		mangueraAceite = new MangueraAceite(startSignal, doneSignal, this,
				zonaReabastecimiento);
		mangueraPetroleo = new MangueraPetroleo(startSignal, doneSignal, this,
				zonaReabastecimiento);

		mangueraAceite.start();
		mangueraPetroleo.start();
	}

	public Cargamento devolverCargamento() {
		return cargamento;
	}

	public int devolverIdPetrolero() {
		return idPetrolero;
	}

	public void setIdPetrolero(int _idPetrolero) {
		idPetrolero = _idPetrolero;
	}

	@Override
	public void run() {

		Random random = new Random();

		// Los barcos petroleros son siempre de entrada
		puerta.quieroEntrar(this);
		for (int i = 0; i < 3; i++) {
			System.out.println("Barco " + id + " entrando");
		}
		puerta.yaHeEntrado(id);

		// Una vez dentro, intentan recargar
		try {
			zonaReabastecimiento.quieroRecargar(this);
			startSignal.countDown();
			doneSignal.await();
			zonaReabastecimiento.yaHeRecargado();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Cuando terminan de recargar, salen del puerto
		puerta.quieroSalir(this);
		for (int i = 0; i < 3; i++) {
			System.out.println("Barco " + id + " saliendo");
		}
		puerta.yaHeSalido(id);

		// Se les adjudica un identificador aleatorio
		setIdPetrolero(random.nextInt(3) + 1);

		estacion.heLlegado(this); // aqui se bloquea si no han llegado todos.
		estacion.avisarAlResto(this);
	}

	/**
	 * Compara si el id pasado es igual que el de este barco. En caso
	 * afirmativo, incrementa en una unidad un acumulador.
	 */
	public void compararId(int idOtroBarco) {

		idRecibidos++;
		if (idOtroBarco == idPetrolero) {
			igualesQueYo++;
		}

		if (idRecibidos == 4) {
			System.out.println("El barco: " + id + " comparte id con "
					+ igualesQueYo + " barcos.");
		}
	}
}
