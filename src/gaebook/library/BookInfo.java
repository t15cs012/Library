package gaebook.library;

import gaebook.util.ImageEntity;
import gaebook.util.PMF;

import java.util.List;
import java.util.logging.*;

import javax.jdo.*;
import javax.jdo.annotations.*;

/**
 * 図書情報を表現するクラス
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class BookInfo {
	static Logger logger = Logger.getLogger(BookInfo.class.getName());
	static String defaultPostFix = "mail";

	@Persistent
	@PrimaryKey
	private String ISBN; // ISBNコード
	@Persistent
	private String name; // 書籍名
	@Persistent
	private String kanaName; // 書籍名のふりがな
	@Persistent
	private String author; // 著者名
	@Persistent
	private String kanaAuthor; // 著者名のふりがな
	@Persistent
	private String publisher; // 出版社名
	@Persistent
	private String kanaPublisher; // 出版社名のふりがな

	@Persistent(mappedBy = "info")
	@Element(dependent = "true")
	private List<ImageEntity> images; // 表紙の画像

	public BookInfo() {
		images = null;
	}

	/* 指定したISBNコードが存在するかを判定．もし無ければ作成． */
	public static boolean createBookInfoIfNotExist(String ISBN, String name, String kanaName, String author,
			String kanaAuthor, String publisher, String kanaPublisher, List<ImageEntity> images) {
		logger.info("If not exist, create BookInfo.");

		PersistenceManager pm = null;
		Transaction tx = null;
		try {
			pm = PMF.get().getPersistenceManager();
			tx = pm.currentTransaction();
			tx.begin();
			try {
				pm.getObjectById(BookInfo.class, ISBN);
				// すでに存在する．
				tx.rollback();
				logger.info("This ISBN code existed.");
				return false;
			} catch (JDOObjectNotFoundException e) {
				// なかった．
				BookInfo info = new BookInfo();
				info.setISBN(ISBN);
				info.setName(name);
				info.setKanaName(kanaName);
				info.setAuthor(author);
				info.setKanaAuthor(kanaAuthor);
				info.setPublisher(publisher);
				info.setKanaPublisher(kanaPublisher);
				info.setImages(images);

				logger.info("Create new BookInfo entity.");
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

	public void setISBN(String ISBN) {
		this.ISBN = ISBN;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setKanaName(String kanName) {
		this.kanaName = kanName;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void setKanaAuthor(String kanaAuthor) {
		this.kanaAuthor = kanaAuthor;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	public void setKanaPublisher(String kanPublisher) {
		this.kanaPublisher = kanPublisher;
	}

	public void setImages(List<ImageEntity> images) {
		this.images = images;
	}

	public String getISBN() {
		return ISBN;
	}

	public String getName() {
		return name;
	}

	public String getKanaName() {
		return kanaName;
	}

	public String getAuthor() {
		return author;
	}
	public String getKanaAuthor() {
		return kanaAuthor;
	}

	public String getPublisher() {
		return publisher;
	}
	
	public String getKanaPublisher() {
		return kanaPublisher;
	}

	public List<ImageEntity> getImages() {
		return images;
	}









}
