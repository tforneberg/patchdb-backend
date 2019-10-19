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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import de.tforneberg.patchdb.security.HttpPATCHAllowed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name="users")
@Data @AllArgsConstructor @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonView(User.DefaultView.class)
public class User {
	
	@JsonIgnoreProperties public static interface BriefView {}
	@JsonIgnoreProperties public static interface DefaultView extends BriefView {}
	@JsonIgnoreProperties public static interface CompleteView extends DefaultView {}
	
	public static enum UserStatus { admin, mod, user, blockedUser };
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	@HttpPATCHAllowed(roles = {User.UserStatus.mod, User.UserStatus.admin})
	private UserStatus status;
	
	@Column(name="image")
	private String image;
	
	@Column(name="email")
	@JsonIgnore
	private String email;
	
	@Column(name="password")
	@JsonIgnore
	private String password;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="collections",
		joinColumns=@JoinColumn(name="user_id"),
		inverseJoinColumns=@JoinColumn(name="patch_id"))
	@JsonIgnore
	private List<Patch> patches;
	
	@ElementCollection(fetch=FetchType.LAZY)
	@CollectionTable(name="collections", joinColumns=@JoinColumn(name="user_id"))
	@Column(name="patch_id")
	@JsonView(CompleteView.class)
	public List<Integer> patchIDs;

	public User(int id, String name, UserStatus status, String email) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.email = email;
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
