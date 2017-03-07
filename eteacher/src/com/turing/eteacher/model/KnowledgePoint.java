package com.turing.eteacher.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.turing.eteacher.base.BaseModel;

@Entity
@Table(name = "T_KNOWLEDGE_POINT")
public class KnowledgePoint extends BaseModel implements Serializable {

	private static final long serialVersionUID = 3661742127555951619L;

	private String knowledgeId;
	private String knowledgeName;
	private String typeId;
	
	@Id
	@GeneratedValue(generator = "customId")
	@GenericGenerator(name = "customId", strategy = "com.turing.eteacher.util.CustomIdGenerator")
	@Column(name = "KNOWLEDGE_ID")
	public String getKnowledgeId() {
		return knowledgeId;
	}
	public void setKnowledgeId(String knowledgeId) {
		this.knowledgeId = knowledgeId;
	}
	
	@Column(name = "KNOWLEDGE_NAME")
	public String getKnowledgeName() {
		return knowledgeName;
	}
	public void setKnowledgeName(String knowledgeName) {
		this.knowledgeName = knowledgeName;
	}
	
	@Column(name = "TYPE_ID")
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	
		
}
