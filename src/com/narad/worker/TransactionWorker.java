package com.narad.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.narad.dataaccess.DataAccess;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;

public class TransactionWorker implements INaradWorker {

	private static final Logger logger = LoggerFactory.getLogger(TransactionWorker.class);

	private String threadName;
	private boolean isStarted = false;

	public TransactionWorker() {
		super();
		Thread.currentThread().getName();
	}

	@Override
	public String getName() {
		return "TransactionWorker";
	}

	public void beginTransaction() {
		isStarted = true;
		logger.debug("Starting transaction: {} ", threadName);
		DataAccess.getInstance().beginTransaction();
		logger.debug("Started transaction: {} ", threadName);
	}

	public void transactionSuccess() {
		if (isStarted) {
			logger.debug("Starting transaction success: {} ", threadName);
			DataAccess.getInstance().completeTransaction(Conclusion.SUCCESS);
			logger.debug("Transaction success completed: {} ", threadName);
		} else {
			logger.debug("Transaction not started ", threadName);
		}
	}

	public void transactionFail() {
		if (isStarted) {
			logger.debug("Starting transaction fail: {} ", threadName);
			DataAccess.getInstance().completeTransaction(Conclusion.FAILURE);
			logger.debug("Transaction fail completed: {} ", threadName);
		} else {
			logger.debug("Transaction not started ", threadName);
		}
	}

}
