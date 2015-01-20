package trashbox.domain;

import java.io.Serializable;

public class UserProfile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2817798689479516718L;

	UserProfile(){};
	
private boolean isAdmin;

/**
 * @return the isAdmin
 */
public boolean isAdmin() {
	return isAdmin;
}

/**
 * @param isAdmin the isAdmin to set
 */
public void setAdmin(boolean isAdmin) {
	this.isAdmin = isAdmin;
}

}
