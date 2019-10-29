package de.tforneberg.patchdb.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import de.tforneberg.patchdb.security.HttpPATCHAllowed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="patches")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonView(Patch.DefaultView.class)
public class Patch extends Patchable {
	
	@JsonIgnoreProperties public static interface BriefView {}
	@JsonIgnoreProperties public static interface DefaultView extends BriefView {}
	@JsonIgnoreProperties public static interface CompleteView extends DefaultView {}
	
	public static enum PatchType { Woven, Stitched, Printed };
	public static enum PatchState { approved, notApproved };
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	protected int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="date_inserted")
	private Date dateInserted;
	
	@OneToOne(fetch=FetchType.LAZY, cascade= {CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="user_inserted")
	@JsonView(CompleteView.class)
	private User userInserted;
	
	//TODO:
	//private User[] usersChanged;
	//private String[] datesChanged;
	//evtl Klasse PatchChange (extends Change?)... dann PatchChange[] changes
	
	@ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="band_id")
	@JsonView(CompleteView.class)
	private Band band;
	
	@Column(name="description")
	@JsonView(CompleteView.class)
	private String description;
	
	@Column(name="image")
	private String image;
	
	@Column(name="type")
	@Enumerated(EnumType.STRING)
	private PatchType type;
	
	@Column(name="state")
	@Enumerated(EnumType.STRING)
	@HttpPATCHAllowed(roles = {User.UserStatus.mod, User.UserStatus.admin})
	private PatchState state;
	
	@Column(name="num_of_copies")
	@JsonView(CompleteView.class)
	private Integer numOfCopies;
	
	@Column(name="release_date")
	@JsonView(CompleteView.class)
	private Date releaseDate;
	
	@Column(name="manufacturer")
	@JsonView(CompleteView.class)
	private String manufacturer;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="collections",
		joinColumns=@JoinColumn(name="patch_id"),
		inverseJoinColumns=@JoinColumn(name="user_id"))
	@JsonView(CompleteView.class)
	private List<User> users;
	
	//does not work at the moment (NullPointer) with native queries used in repository (bc of collection table) .. see https://hibernate.atlassian.net/browse/HHH-7525
	//@Formula("(SELECT COUNT(*) FROM collections WHERE collections.patch_id = id)")
	//private Integer amountUsers;
	
	//workaround for Hibernate bug, see comment above
	@Column(name="amount_users", insertable = false, updatable = false)
	@ColumnTransformer(read = "(SELECT COUNT(*) FROM collections WHERE collections.patch_id = id)")
	private Integer amountUsers;
	
	public Patch(int id, String title, Date dateInserted, User userInserted) {
		this.id = id;
		this.name = title;
		this.dateInserted = dateInserted;
		this.userInserted = userInserted;
	}
	
	public void addUser(User user) {
		if (users == null) {
			users = new ArrayList<>();
		}
		users.add(user);
	}
	
}