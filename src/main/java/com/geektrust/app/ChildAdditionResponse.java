package com.geektrust.app;

import org.json.simple.JSONObject;

public class ChildAdditionResponse {
	public JSONObject jsonObj;
	public String addResponse;

	public JSONObject getJsonObj() {
		return jsonObj;
	}
	public void setJsonObj(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
	public String getAddResponse() {
		return addResponse;
	}
	public void setAddResponse(String addResponse) {
		this.addResponse = addResponse;
	}
}
