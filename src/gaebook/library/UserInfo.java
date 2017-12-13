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

	@PrimaryKey @Persistent
	private String key; // ユーザID
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

	public static boolean createUserIfNotExist(String userID, String password, String name, String eMail,
			String phoneNum) {
		
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
				return false;
			} catch (JDOObjectNotFoundException e) {
				// なかった．
				UserInfo info = new UserInfo();
				info.setKey(userID);
				info.setPassword(password);
				info.setName(name);
				info.setEMail(eMail);
				info.setPhoneNum(phoneNum);

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

	public void setKey(String userID) {
		this.key = userID;
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

	public String getKey() {
		return key;
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

	/* 指定された図書情報を返す. PersistentManager は外部で管理する． */
	// public boolean checkPassword(String password) {
	// try {
	// if (this.password == password)
	// return true;
	// return false;
	// } catch (JDOObjectNotFoundException e) {
	// return false;
	// }
	// }

}
