package com.focaplo.mylocal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RequestResult<E>{
	public static String good = "good";
	public static String error = "error";
	
	private String status;
	private List<E> data = new ArrayList<E>();
	private String errorMessage;
	private String input;
	
	public void setGood(){
		this.setStatus(good);
	}
	public void setError(Exception e){
		this.setStatus(error);
		this.setErrorMessage(e.getLocalizedMessage());
	}
	public void setError(String message){
		this.setStatus(error);
		this.setErrorMessage(message);
	}
	public boolean isGood(){
		return good.equalsIgnoreCase(status);
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public List<E> getData() {
		return data;
	}
	public void setData(List<E> data) {
		this.data = data;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	
}
