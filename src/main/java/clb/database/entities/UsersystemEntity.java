package clb.database.entities;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * The persistent class for the USERSYSTEM database table.
 * 
 */
@Document
public class UsersystemEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String userid;

	private String address;

	private String name;

	private String password;

	private String username;
	
	@DBRef
	private List<BuildingEntity> buildings;

	public UsersystemEntity() {
	}

	public String getUserid() {
		return this.userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

    public List<BuildingEntity> getBuildings() {
        return buildings;
    }

    public void setBuildings( List<BuildingEntity> buildings ) {
        this.buildings = buildings;
    }
	
	
}