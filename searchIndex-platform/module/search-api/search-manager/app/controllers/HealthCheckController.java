package controllers;

import managers.PlaySearchManager;
import play.libs.F.Promise;
import play.mvc.Result;

import com.ilimi.common.dto.Request;

public class HealthCheckController extends SearchBaseController {
	
	PlaySearchManager mgr = new PlaySearchManager();

	public Promise<Result> search() {
		String apiId = "search-service.health";
		Request request = getRequest(null, apiId, request().uri());
		return mgr.health(request);
	}
	
}
