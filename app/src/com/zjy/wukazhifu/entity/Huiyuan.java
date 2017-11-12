package com.zjy.wukazhifu.entity;

import java.io.Serializable;

public class Huiyuan  implements Serializable{
	private String custLogin;
	private String custId;
	private String merclass;
	private String referrer;
	private String custName;
	private String custStatus;
	private String custRegDatetime;
	public String getCustLogin() {
		return custLogin;
	}
	public void setCustLogin(String custLogin) {
		this.custLogin = custLogin;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getMerclass() {
		return merclass;
	}
	public void setMerclass(String merclass) {
		this.merclass = merclass;
	}
	public String getReferrer() {
		return referrer;
	}
	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustStatus() {
		return custStatus;
	}
	public void setCustStatus(String custStatus) {
		this.custStatus = custStatus;
	}
	public String getCustRegDatetime() {
		return custRegDatetime;
	}
	public void setCustRegDatetime(String custRegDatetime) {
		this.custRegDatetime = custRegDatetime;
	}
}
