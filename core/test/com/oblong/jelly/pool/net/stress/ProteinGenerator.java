//package com.oblong.jelly.pool.net;
//
//import com.oblong.jelly.pool.net.stress.Receiver;
//import com.oblong.jelly.Protein;
//import com.oblong.jelly.util.ExceptionHandler;
//import com.oblong.util.Util;
//
//import java.util.Random;
//
//import static junit.framework.Assert.fail;
//
///**
// * Created with IntelliJ IDEA.
// * User: valeria
// * Date: 10/22/13
// * Time: 2:31 PM
// */
//public class ProteinGenerator extends Thread {
//
//
//	private final Random r =  new Random();
//	private final int sleepMs;
//
//
//	private final TestPoolSender connector;
//	private volatile boolean stopMe = false;
//
//
//	public ProteinGenerator(TestPoolSender connector) {
//		this.connector = connector;
//
//		this.sleepMs = ExternalTCPMultiProteinTestConfig.SLEEP_MS_SMALL;
//		ExternalTCPMultiProteinTest.logMessage("created generator");
//	}
//
//	@Override
//	public void run(){
//		ExternalTCPMultiProteinTest.logMessage("will start adding proteins");
//		while(!stopThread()){
//			createAndAddBatch();
//			try {
//				Util.randomSleep(r, sleepMs);
//			} catch (InterruptedException e) {
//				stopSelf();
//				ExternalTCPMultiProteinTest.logMessage("Thread interrupted");
//				Thread.currentThread().interrupt();
////				ExceptionHandler.handleException(e);
//				//fail("Error creating new protein(s)");
//			}
//		}
//	}
//
//
//	public void stopSelf(){
//		stopMe = true;
//		ExternalTCPMultiProteinTest.logMessage("Thread will be stopped, sent "+proteinCounter+" proteins");
//	}
//
//	private boolean stopThread() {
//		return (maxProteinNumber == ExternalTCPMultiProteinTestConfig.NO_LIMIT_PROTEIN_NUMBER) ?
//				stopMe :
//				(proteinCounter >= maxProteinNumber) && stopMe;
//	}
//
//
//
//
//
//
//}
