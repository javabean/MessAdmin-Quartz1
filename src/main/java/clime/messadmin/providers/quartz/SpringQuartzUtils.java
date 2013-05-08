/**
 *
 */
package clime.messadmin.providers.quartz;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletContext;

import org.quartz.Scheduler;

/**
 * @author C&eacute;drik LIME
 */
final class SpringQuartzUtils {

	private static transient Method getWebApplicationContext;
	private static transient Class schedulerFactoryBeanClass;
	private static transient Method getBeansOfType;
	private static transient Method getObject;
	private static transient Method isRunning;

	static {
		try {
			Class webApplicationContextUtilsClass = Class.forName("org.springframework.web.context.support.WebApplicationContextUtils");
			getWebApplicationContext = webApplicationContextUtilsClass.getMethod("getWebApplicationContext", new Class[] {ServletContext.class});
			Class webApplicationContextClass = Class.forName("org.springframework.web.context.WebApplicationContext");
			schedulerFactoryBeanClass = Class.forName("org.springframework.scheduling.quartz.SchedulerFactoryBean");
			getBeansOfType = webApplicationContextClass.getMethod("getBeansOfType", new Class[] {Class.class});
			getObject = schedulerFactoryBeanClass.getMethod("getObject", null);
			try {
				// @since Spring 2.0
				isRunning = schedulerFactoryBeanClass.getMethod("isRunning", null);
			} catch (NoSuchMethodException oldSpring1Version) {
			}
		} catch (LinkageError e) {
		} catch (ClassNotFoundException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	private SpringQuartzUtils() {
	}

	public static Collection/*<SchedulerFactoryBean>*/ getSchedulerFactoryBeans(ServletContext context) {
		Map/*<String, SchedulerFactoryBean>*/ schedulerBeans = Collections.EMPTY_MAP;
		//WebApplicationContext webContext = WebApplicationContextUtils.getWebApplicationContext(context);
		//Map/*<String, SchedulerFactoryBean>*/ schedulerBeans = webContext.getBeansOfType(SchedulerFactoryBean.class);
		try {
			Object webContext = getWebApplicationContext.invoke(null, new Object[] {context});
			schedulerBeans = (Map) getBeansOfType.invoke(webContext, new Object[] {schedulerFactoryBeanClass});
		} catch (Exception ignore) {
		}
		return schedulerBeans.values();
	}

	public static boolean isRunning(Object/*SchedulerFactoryBean*/ schedulerFactoryBean) {
		// return schedulerFactoryBean.isRunning();
		boolean schedulerBeanIsRunning = true;// default value for Spring 1.x
		if (isRunning != null) {
			try {
				schedulerBeanIsRunning = ((Boolean) isRunning.invoke(schedulerFactoryBean, null)).booleanValue();
			} catch (Exception ignore) {}
		}
		return schedulerBeanIsRunning;
	}

	public static Scheduler getScheduler(Object/*SchedulerFactoryBean*/ schedulerFactoryBean) {
		// return (Scheduler) schedulerBean.getObject();
		Scheduler scheduler = null;
		try {
			scheduler = (Scheduler) getObject.invoke(schedulerFactoryBean, null);
		} catch (Exception ignore) {
		}
		return scheduler;
	}

}