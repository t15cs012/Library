package gaebook.library;

import java.util.logging.*;

import javax.jdo.*;
import javax.jdo.annotations.*;

import gaebook.util.PMF;

/**
 * 利用者情報を表現するクラス
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class UserInfo {
	static Logger logger = Logger.getLogger(BookInfo.class.getName());

	@Persistent
	@PrimaryKey
	private String userID; // ユーザID
	@Persistent
	private String password; // パスワード
	@Persistent
	private String name; // 利用者の氏名
	@Persistent
	private String eMail; // メールアドレス
	@Persistent
	private String phoneNum; // 電話番号

	public UserInfo() {
	}

    /* 指定したユーザIDが存在するかを判定．もし無ければ作成． */
	public static boolean createUserInfoIfNotExist(String userID, String password, String name, String eMail,
			String phoneNum) {
		logger.info("If not exist, create UserInfo.");
		
		PersistenceManager pm = null;
		Transaction tx = null;
		try {
			pm = PMF.get().getPersistenceManager();
			tx = pm.currentTransaction();
			tx.begin();
			try {
				pm.getObjectById(UserInfo.class, userID);
				// すでに存在する．
				tx.rollback();
				logger.info("This user id existed.");
				return false;
			} catch (JDOObjectNotFoundException e) {
				// なかった．
				UserInfo info = new UserInfo();
				info.setUserID(userID);
				info.setPassword(password);
				info.setName(name);
				info.setEMail(eMail);
				info.setPhoneNum(phoneNum);

				logger.info("Create new UserInfo entity.");
				pm.makePersistent(info);
				try {
					tx.commit();
					return true;
				} catch (JDOCanRetryException e2) {
					logger.log(Level.SEVERE, e2.getMessage(), e2);
					return false;
				}
			}
		} finally {
			if (tx != null && tx.isActive())
				tx.rollback();
			if (pm != null && !pm.isClosed())
				pm.close();
		}
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEMail(String eMail) {
		this.eMail = eMail;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getUserID() {
		return userID;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getEMail() {
		return eMail;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

}
