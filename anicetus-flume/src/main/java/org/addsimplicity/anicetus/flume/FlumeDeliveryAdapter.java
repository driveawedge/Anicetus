package org.addsimplicity.anicetus.flume;

import org.addsimplicity.anicetus.entity.GlobalInfo;
import org.addsimplicity.anicetus.io.DeliveryAdapter;
import org.addsimplicity.anicetus.io.ExceptionHandler;
import org.springframework.beans.factory.DisposableBean;

public class FlumeDeliveryAdapter implements DeliveryAdapter, DisposableBean {

	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}

	public void sendTelemetry(GlobalInfo telemetry) {
		// TODO Auto-generated method stub

	}

	public void setExceptionHandler(ExceptionHandler handler) {
		// TODO Auto-generated method stub

	}

}
