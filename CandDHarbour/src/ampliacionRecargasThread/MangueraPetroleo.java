package ampliacionRecargasThread;

import java.util.concurrent.CountDownLatch;

import sesionSemaforos.ZonaReabastecimiento;
import barcos.BarcoPetrolero;

public class MangueraPetroleo extends Manguera {

	public MangueraPetroleo(CountDownLatch _startSignal,
			CountDownLatch _doneSignal, BarcoPetrolero _barcoAlQueSirvo, ZonaReabastecimiento _zonaCarga) {

		super(_startSignal, _doneSignal, _barcoAlQueSirvo, _zonaCarga);
	}

	@Override
	public void reponer() {
		
		while (!barcoAlQueSirvo.devolverCargamento().estaLlenoPetroleo()) {
			try {
				zonaCarga.recargoPetroleo(barcoAlQueSirvo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		doneSignal.countDown();
	}
}
