package de.tforneberg.patchdb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="users")
public class User {
	
	public static enum Status { admin, mod, user, blockedUser };
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@Column(name="image")
	private String image;
	
	@Column(name="email")
	@JsonIgnore //important for security: don't send email via JSON
	private String email;
	
	@Column(name="password")
	@JsonIgnore //important for security: don't send pw via JSON
	private String password;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="collections",
		joinColumns=@JoinColumn(name="user_id"),
		inverseJoinColumns=@JoinColumn(name="patch_id"))
	private List<Patch> patches;
	
	@ElementCollection(fetch=FetchType.LAZY)
	@CollectionTable(name="collections", joinColumns=@JoinColumn(name="user_id"))
	@Column(name="patch_id")
	public List<Integer> patchIDs;

	public User() {}

	public User(int id, String name, Status status, String email) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.email = email;
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Patch> getPatches() {
		return patches;
	}
	
	public List<Integer> getPatchIDs() {
		return patchIDs;
	}

	public void setPatches(List<Patch> patches) {
		this.patches = patches;
	}
	
	public void addPatch(Patch patch) {
		if (patches == null) {
			patches = new ArrayList<>();
		}
		patches.add(patch);
	}
	
	public void removePatch(Patch patch) {
		if (patches != null) {
			patches.remove(patch);
		}
	}
}
