package cn.org.rapid_framework.ibatis.spring;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.impl.SqlMapClientImpl;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;

public class SqlMapClientFactoryBean extends org.springframework.orm.ibatis.SqlMapClientFactoryBean{
	 private static final Log logger = LogFactory.getLog(SqlMapClientFactoryBean.class);  
	private SqlExecutor sqlExecutor;
	
	public SqlExecutor getSqlExecutor() {
		return sqlExecutor;
	}

	public void setSqlExecutor(SqlExecutor sqlExecutor) {
		this.sqlExecutor = sqlExecutor;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		SqlMapClient c = (SqlMapClient)getObject();
		if(sqlExecutor != null && c instanceof SqlMapClientImpl) {
			SqlMapClientImpl client = (SqlMapClientImpl)c;
			SqlMapExecutorDelegate delegate = client.getDelegate();
			try {
				ReflectUtil.setFieldValue(delegate, "sqlExecutor", SqlExecutor.class, sqlExecutor);
				logger.info("[ibatis] set ibatis SqlMapClient.sqlExecutor as "+sqlExecutor.getClass());
			}catch(Exception e) {
				logger.warn("[ibatis] error,cannot set ibatis SqlMapClient.sqlExecutor as "+sqlExecutor.getClass()+" cause:"+e);
			}
		}
	}
	
	static class ReflectUtil {  
		  
	    private static final Log logger = LogFactory.getLog(ReflectUtil.class);  
	  
	    public static void setFieldValue(Object target, String fname, Class ftype,  
	            Object fvalue) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {  
	        if (target == null  
	                || fname == null  
	                || "".equals(fname)  
	                || (fvalue != null && !ftype.isAssignableFrom(fvalue.getClass()))) {  
	            return;  
	        }  
	        
	        Class clazz = target.getClass();  
	        try {  
	            Method method = clazz.getDeclaredMethod("set"  
	                    + Character.toUpperCase(fname.charAt(0))  
	                    + fname.substring(1), ftype);  
	            if (!Modifier.isPublic(method.getModifiers())) {  
	                method.setAccessible(true);  
	            }  
	            method.invoke(target, fvalue);  
	        } catch (Exception me) {  
	            if (logger.isDebugEnabled()) {  
	                logger.debug(me);  
	            }  
                Field field = clazz.getDeclaredField(fname);  
                if (!Modifier.isPublic(field.getModifiers())) {  
                    field.setAccessible(true);  
                }  
                field.set(target, fvalue);  
	        }  
	    }  
	}
	
}
