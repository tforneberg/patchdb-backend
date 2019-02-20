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
import javax.persistence.Table;

@Entity
@Table(name="patches")
public class Patch {
	
	public static enum Type { woven, stitched, printed };
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="date_inserted")
	private Date dateInserted;
	
	@OneToOne(fetch=FetchType.EAGER, cascade= {CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="user_inserted")
	private User userInserted;
	
//	private User[] usersChanged;
//	private String[] datesChanged;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name="band_id")
	private Band band;
	
	@Column(name="description")
	private String description;
	
	@Column(name="image")
	private String image;
	
	@Column(name="type")
	@Enumerated(EnumType.STRING)
	private Type type;
	
	@Column(name="num_of_copies")
	private int numOfCopies;
	
	@Column(name="release_date")
	private Date releaseDate;
	
	@Column(name="manufacturer")
	private String manufacturer;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="collections",
		joinColumns=@JoinColumn(name="patch_id"),
		inverseJoinColumns=@JoinColumn(name="user_id"))
	private List<User> users;
	
	public Patch() {}
	
	public Patch(int id, String title, Date dateInserted, User userInserted) {
		this.id = id;
		this.name = title;
		this.dateInserted = dateInserted;
		this.userInserted = userInserted;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateInserted() {
		return dateInserted;
	}

	public void setDateInserted(Date dateInserted) {
		this.dateInserted = dateInserted;
	}

	public User getUserInserted() {
		return userInserted;
	}

	public void setUserInserted(User userInserted) {
		this.userInserted = userInserted;
	}

//	public User[] getUsersChanged() {
//		return usersChanged;
//	}
//
//	public void setUsersChanged(User[] usersChanged) {
//		this.usersChanged = usersChanged;
//	}
//
//	public String[] getDatesChanged() {
//		return datesChanged;
//	}
//
//	public void setDatesChanged(String[] datesChanged) {
//		this.datesChanged = datesChanged;
//	}

	public Band getBand() {
		return band;
	}

	public void setBand(Band band) {
		this.band = band;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getNumOfCopies() {
		return numOfCopies;
	}

	public void setNumOfCopies(int numOfCopies) {
		this.numOfCopies = numOfCopies;
	}

	public Date getDatePublished() {
		return releaseDate;
	}

	public void setDatePublished(Date datePublished) {
		this.releaseDate = datePublished;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public void addUser(User user) {
		if (users == null) {
			users = new ArrayList<>();
		}
		users.add(user);
	}
	
}