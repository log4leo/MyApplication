package com.zjy.wukazhifu.entity;

public class Order {
	private String prdordno;
	private String ordamt;
	private String ordtime;
	private String PAYTYPE;// 03 快捷支付。04 微信支付。05 支付宝。
	private String ordstatus;// 00||06 未处理。01 成功。02 失败。03||04||05||07||08 处理中。

	public String getPrdordno() {
		return prdordno;
	}

	public void setPrdordno(String prdordno) {
		this.prdordno = prdordno;
	}

	public String getOrdamt() {
		return ordamt;
	}

	public void setOrdamt(String ordamt) {
		this.ordamt = ordamt;
	}

	public String getOrdtime() {
		return ordtime;
	}

	public void setOrdtime(String ordtime) {
		this.ordtime = ordtime;
	}

	public String getPAYTYPE() {
		return PAYTYPE;
	}

	public void setPAYTYPE(String pAYTYPE) {
		PAYTYPE = pAYTYPE;
	}

	public String getOrdstatus() {
		return ordstatus;
	}

	public void setOrdstatus(String ordstatus) {
		this.ordstatus = ordstatus;
	}

}
