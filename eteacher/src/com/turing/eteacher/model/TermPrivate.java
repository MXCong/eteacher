package com.turing.eteacher.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import com.turing.eteacher.base.CreateTimeModel;

@Entity
@Table(name="T_TERM_PRIVATE")
public class TermPrivate extends CreateTimeModel implements Serializable{
	
	private static final long serialVersionUID = 4817634677207818875L;

	private String tpId;
	private String startDate;
	private String endDate;
	private Integer weekCount;
	private String userId;
	private String termName;
	private Integer status;
	
	@Id
	@GeneratedValue(generator="customId")
	@GenericGenerator(name="customId", strategy="com.turing.eteacher.util.CustomIdGenerator")
	@Column(name="TP_ID")
	public String getTpId() {
		return tpId;
	}
	public void setTpId(String tpId) {
		this.tpId = tpId;
	}
	
	@Column(name="START_DATE")
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	@Column(name="END_DATE")
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	@Column(name="WEEK_COUNT")
	public Integer getWeekCount() {
		return weekCount;
	}
	public void setWeekCount(Integer weekCount) {
		this.weekCount = weekCount;
	}
	
	@Column(name="USER_ID")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@Column(name="TERM_NAME")
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	
	@Column(name = "STATUS")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
