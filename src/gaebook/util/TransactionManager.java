package gaebook.util;

import gaebook.util.ErrorPage.ErrorPageException;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.JDOCanRetryException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;


public class TransactionManager {
    static Logger logger = Logger.getLogger(TransactionManager.class.getName());

    /* トランザクション内で実行するコードセグメントを表すインナークラスのインターフェイス */
    public static interface Body { 
        void run(PersistenceManager pm, Transaction tx) 
        throws ErrorPageException;
    }
    
    public static boolean start(int retryCount, Body runnable) 
    throws ErrorPageException{ 
        return start(retryCount, null, runnable);
    }
    
    public static boolean start(
            int retryCount, 
            PersistenceManager pm, 
            Body runnable) throws ErrorPageException{
        boolean pmSupplied = false;
        if (pm != null)
            pmSupplied = true;
        while (retryCount-- >= 0) {
            Transaction tx = null;
            try {
                if (!pmSupplied)
                    pm = PMF.get().getPersistenceManager();
                tx = pm.currentTransaction();
                tx.begin();
                runnable.run(pm, tx);

                try {
                    tx.commit();
                } catch (JDOCanRetryException e) {
                    logger.warning("commit failed, retry!");
                }
                return true;
            } finally {
                if (tx != null && tx.isActive())
                    tx.rollback();
                if (pm != null && !pmSupplied && !pm.isClosed())
                    pm.close();
            }
        }
        return false; // failed to commit eventually.

    }
}    

