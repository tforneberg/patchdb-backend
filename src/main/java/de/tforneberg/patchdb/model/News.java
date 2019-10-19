package de.tforneberg.patchdb.model;

import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name="news")
@Data @AllArgsConstructor @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonView(News.DefaultView.class)
public class News {
	
	@JsonIgnoreProperties public static interface BriefView {}
	@JsonIgnoreProperties public static interface DefaultView extends BriefView {}
	@JsonIgnoreProperties public static interface CompleteView extends DefaultView {}
    
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="title")
	private String title;
	
	@Column(name="content")
	private String content;
	
	@Column(name="date_created")
	private Date created;
	
	@OneToOne(fetch=FetchType.LAZY, cascade= {CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="created_by")
	private User creator;

}
