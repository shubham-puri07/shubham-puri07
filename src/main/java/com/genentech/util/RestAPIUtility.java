package com.genentech.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.io.File;
import java.util.Map;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
/**
 * @Description : This is utility to post the JSON object .
 * @author Anushree Kharwade
 * @Created Date : 29/03/2022
 * @Updated By : Anushree Kharwade/ Sagar Rai
 * @Updated Date: 06/06/2023
 * @param 
 * @Comments: Added new methods in the util
 */

public class RestAPIUtility {
	
	protected Logger log = LogManager.getLogger(this.getClass());
	RequestSpecification req;


	public int getResponseCode(String url) {
		
		int getresponsecode = RestAssured.given().when().get(url).getStatusCode();
		log.info("The response status from getResponseBodyRest is "+getresponsecode);
		return getresponsecode;
	}
	
	public int updatePatchResponseCode(String url, String jsonBody){
	
		int updateresponsecode = RestAssured.given().contentType(ContentType.JSON).body(jsonBody).when().patch(url).getStatusCode();
		log.info("The response status from updatePatchResponseCode is "+updateresponsecode);
		return updateresponsecode;
	}

	public int postResponseCode(String url, String jsonBody){
		
		int postResponsecode = RestAssured.given().contentType(ContentType.JSON).body(jsonBody).when().post(url).getStatusCode();
		log.info("The response status from postResponseCode is "+postResponsecode);
		return postResponsecode;
	}
	
	public int putResponseCode(String url, String jsonBody) {
	
		int putResponseCode = RestAssured.given().contentType(ContentType.JSON).body(jsonBody).when().put(url).getStatusCode();
		log.info("The response status from putResponseCode is "+putResponseCode);
		return putResponseCode;
	}	
	
	public int deleteResponseCode(String url, String jsonBody) {
	  
		int deleteResponseCode = RestAssured.given().contentType(ContentType.JSON).body(jsonBody).when().delete(url).getStatusCode();
		log.info("The response status from deleteResponseCode is "+deleteResponseCode);
		return deleteResponseCode; 
	 }
	 
	
	
	public Response launchGetRequest(String url) {
		//Response res = RestAssured.given().log().all().get(url);
		Response res = RestAssured.given().get(url);
		//System.out.println("launchGetRequest(String url) method ran successfully");
		return res;
	}
	public Response launchGetRequest(String url, String header, String headerValue) {
		Response res = RestAssured.given().header(header,headerValue).log().headers().get(url);
		//System.out.println("launchGetRequest(String url, String header, String headerValue) launched successfully");
		return res;
	}
	public Response launchGetRequest(String url,Map<String,Object> m) {
		Response res = RestAssured.given().headers(m).log().headers().get(url);
		//System.out.println("launch successful");
		return res;
	}
	
	public Response launchPostRequest(String url, String body) 
	{
		Response res = RestAssured.given().body(body).post(url);
		return res;
	}
	public Response launchPostRequest(String url, String body,Map<String,Object> m) {
		Response res = RestAssured.given().body(body).headers(m).post(url);
		return res;
	}	
	
	public int getStatusCode(Response res) {
		System.out.println("statuscode  successful");
		return res.getStatusCode();
	}
	public boolean compareStatusCode(Response res, int sc) {
		boolean a = false;
		if (res.getStatusCode() == sc) {
			a = true;
			System.out.println("status code matches to required one");
		}
		return a;
	}
	
	public String getValueOfGivenTag(String path, Response res) {
		JsonPath jsonpath = res.jsonPath();
		System.out.println(jsonpath.getString(path));
		return jsonpath.getString(path);
	}
	public boolean compareValueOfGivenTag(String path, Response res, String tagValue) {
		boolean valueFound=false;
		JsonPath jsonpath = res.jsonPath();
		System.out.println(jsonpath.getString(path));
		String s =  jsonpath.getString(path);
		if(s.equalsIgnoreCase(tagValue)) 
		{
			valueFound=true;
		}
		return valueFound;
	}

	public void logResponse(Response res)
	{
		res.then().log().all();
	}
	public void logRequest(RequestSpecification reqSpec)
	{
		reqSpec.log().all();
	}
	
	public String convertResponseToString(Response res) {
		
		return res.asPrettyString();
	}

	//********************************Creating Request***************************//
	
	public RequestSpecification addBaseUri(String baseUri) {
		req = RestAssured.given().baseUri(baseUri);
		return req;
	}
	public RequestSpecification addBasePath(RequestSpecification reqSpec, String basePath) {
		req = reqSpec.basePath(basePath);
		return req;
	}
	public RequestSpecification addQueryParameter(RequestSpecification reqSpec, Map<String, String>m) {
		req = reqSpec.queryParams(m);
		return req;
	}
	public RequestSpecification addHeaders(RequestSpecification reqSpec, Map<String, String>m) {
		req = reqSpec.headers(m);
		return req;
	}
	public RequestSpecification addBody(RequestSpecification reqSpec, String body) {
		req = reqSpec.body(body);
		return req;
	}
	public Response launchGetRequestSpecification(RequestSpecification reqSpec )
	{
		Response res = reqSpec.given().log().all().when().get();
		//Response res = reqSpec.get();
		return res;
	}
	public Response launchPostRequestSpecification(RequestSpecification reqSpec )
	{
		Response res = reqSpec.post();
		return res;
	}
	
	  public void validateSchema(Response res, String schemaFileLocation) { 
		  /*Use
	  https://www.liquid-technologies.com/online-json-to-schema-converter to create
	  schema for a response AND then put that response in a json file, here
	  schema.json and then pass that //location while calling this method
	  */
	  System.out.println("Validating Schema");
	  res.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(new File(schemaFileLocation)));
	  
	  }
	 
	
	  
}
