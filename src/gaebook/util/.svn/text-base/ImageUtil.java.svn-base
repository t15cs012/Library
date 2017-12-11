package gaebook.util;

import gaebook.blog.BlogEntry;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.users.User;


public class ImageUtil {
    static Logger logger = Logger.getLogger(ImageUtil.class.getName());
    
    public static String putImage(byte [] bytes, String name){
       PersistenceManager pm = PMF.get().getPersistenceManager();
       ImageEntity entity = new ImageEntity(bytes, name);
       try {
            pm.makePersistent(entity);
        } finally {
            pm.close();
        }
        return entity.getKeyAsString();
    }
    public static byte[] readBytes(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        byte[] buf = new byte[10 * 1024];
        while ((len = is.read(buf)) >= 0) {
            bos.write(buf, 0, len);
        }
        return bos.toByteArray();
    }
    
    public static ImageEntity getImageEntity(String keyString) {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
            return pm.getObjectById(ImageEntity.class, KeyFactory.stringToKey(keyString));
        } catch (javax.jdo.JDOObjectNotFoundException e) {
            logger.warning("cannot find ImageEntry for id " + keyString);
        } finally {
            pm.close();
        }
        return null;
    }

    public static List<ImageEntity> getImageEntities() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(ImageEntity.class);

        return (List<ImageEntity>) query.execute();
    }

    public static Image readImage(String filename) throws IOException {
        File f = new File(filename);
        InputStream is = new FileInputStream(f);
        int length = (int) f.length();
        byte [] buffer = new byte[length];
        
        int current = 0;
        while (current < length) {
            int len = is.read(buffer, current, length - current);
            if (len < 0)
                throw new IOException("could not fully read the file");
            current += len;
        }
        return ImagesServiceFactory.makeImage(buffer);
    }

    public static boolean delete(Long imageId) {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
            ImageEntity image = pm.getObjectById(ImageEntity.class, imageId);
            pm.deletePersistent(image);
            return true;
        } catch (javax.jdo.JDOObjectNotFoundException e) {
            logger.warning("cannot find ImageEntry for id " + imageId);
        } finally {
            pm.close();
        }
        return false;
    }
    
}
