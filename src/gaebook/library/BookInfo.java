package gaebook.library;

import gaebook.util.ImageEntity;
import gaebook.util.PMF;
import java.util.logging.*;

import javax.jdo.*;
import javax.jdo.annotations.*;

/**
 * ブログを表現するクラス
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class BookInfo {
	static Logger logger = Logger.getLogger(BookInfo.class.getName());
	static String defaultPostFix = "mail";

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private int key; // ISBNコード
	@Persistent
	private String name; // 書籍名
	@Persistent
	private String author; // 著者名
	@Persistent
	private String publisher; // 出版社名
	@Persistent
	private ImageEntity image; // 表紙の画像

	public BookInfo() {
		this.image = null;
	}

	public static boolean createBookInfoIfNotExist(int ISBN, String name, String author, String publisher,
			ImageEntity image) {
		PersistenceManager pm = null;
		Transaction tx = null;
		try {
			pm = PMF.get().getPersistenceManager();
			tx = pm.currentTransaction();
			tx.begin();
			try {
				pm.getObjectById(UserInfo.class, ISBN);
				// すでに存在する．
				tx.rollback();
				return false;
			} catch (JDOObjectNotFoundException e) {
				// なかった．
				BookInfo info = new BookInfo();
				info.setName(name);
				info.setAuthor(author);
				info.setPublisher(publisher);
				info.setImage(image);

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

	public void setKey(int ISBN) {
		this.key = ISBN;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setImage(ImageEntity image) {
		this.image = image;
	}

}
