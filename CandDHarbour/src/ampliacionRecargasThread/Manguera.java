package ampliacionRecargasThread;

import java.util.concurrent.CountDownLatch;

import sesionSemaforos.ZonaReabastecimiento;
import barcos.BarcoPetrolero;

public abstract class Manguera extends Thread {

	CountDownLatch startSignal;
	CountDownLatch doneSignal;

	BarcoPetrolero barcoAlQueSirvo;
	ZonaReabastecimiento zonaCarga;

	public Manguera(CountDownLatch _startSignal, CountDownLatch _doneSignal,
			BarcoPetrolero _barcoAlQueSirvo, ZonaReabastecimiento _zonaCarga) {

		startSignal = _startSignal;
		doneSignal = _doneSignal;
		barcoAlQueSirvo = _barcoAlQueSirvo;
		zonaCarga = _zonaCarga;
	}

	public abstract void reponer();

	@Override
	public void run() {

		try {
			startSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		reponer();
	}
}
